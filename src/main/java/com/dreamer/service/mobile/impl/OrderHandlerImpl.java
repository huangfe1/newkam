package com.dreamer.service.mobile.impl;

import com.dreamer.domain.pmall.goods.GoodsStandard;
import com.dreamer.domain.pmall.goods.PmallGoods;
import com.dreamer.domain.pmall.order.*;
import com.dreamer.domain.pmall.shopcart.ShopCart;
import com.dreamer.domain.user.AccountsRecord;
import com.dreamer.domain.user.AddressClone;
import com.dreamer.domain.user.Agent;
import com.dreamer.domain.user.enums.AccountsType;
import com.dreamer.domain.wechat.WxConfig;
import com.dreamer.repository.mobile.OrderDao;
import com.dreamer.repository.mobile.OrderItemDao;
import com.dreamer.service.mobile.*;
import com.dreamer.service.mobile.factory.WxConfigFactory;
import com.dreamer.util.CommonUtil;
import com.dreamer.util.PreciseComputeUtil;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.wxjssdk.JSAPI;
import com.wxjssdk.dto.SdkResult;
import com.wxjssdk.util.XmlUtil;
import net.sf.json.JSONObject;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.mx.otter.exception.ApplicationException;
import ps.mx.otter.utils.SearchParameter;

import java.util.*;

/**
 * Created by huangfei on 11/07/2017.
 */
@Service
public class OrderHandlerImpl extends BaseHandlerImpl<Order> implements OrderHandler {


    @Override
    @Transactional
    public Order commitOrder(Agent agent, AddressClone addressClone, ShopCart shopCart, String remark) {
        Order order = new Order(agent, addressClone, remark);
        Set<OrderItem> items = bulidItems(order, shopCart);
        order.setItems(items);
        order.setAmount(PreciseComputeUtil.round(shopCart.getAmount()));//设置总金额
        order.setTicket(PreciseComputeUtil.round(shopCart.getTicket()));//设置总券
        order.setQuantity(shopCart.getQuantity());//设置总数量
        //提交订单
        commitOrder(order);
        //保存订单
        order = save(order);
        return order;
    }


    //生成payOpenid  并且去支付
    @Override
    @Transactional
    public String resetOpenIdToPay(String code, String orderId) {
        Order order = get(Integer.valueOf(orderId));
        if (order == null) {
            LOG.error("获取order为空,{}", orderId);
            throw new ApplicationException("订单不存在！请联系管理员！");
        }
        Agent agent = agentHandler.get(order.getUser().getId());
        WxConfig wxConfig = wxConfigFactory.getPayConfig();
        SdkResult sdkResult = JSAPI.getTokenAndOpenId(wxConfig.getAppid(), wxConfig.getSecret(), code);
        if (sdkResult.isSuccess()) {//获取openId成功了  就去支付
            JSONObject jsonObject = (JSONObject) sdkResult.getData();
            try {
//                if (agent.getPayOpenid() == null || !agent.getPayOpenid().equals(jsonObject.getString("openid"))) {
                agent.setPayOpenid(jsonObject.getString("openid"));
//                    agentHandler.merge(agent);
//                }
            } catch (Exception e) {
                System.out.println("异常:" + e.getMessage());
            }
            String jsApiParam = payHandler.toWxPay(wxConfig, agent.getPayOpenid(), order.getOrderNo(), order.getAmount(), WxConfig.PMALL_NOTICE_URL);
            return jsApiParam;
        } else {
            LOG.error("获取payOpenid失败:{}", sdkResult.getError());
            throw new ApplicationException("获取获取payOpenid失败失败ERROR:" + sdkResult.getError());
        }
    }


    //非代金券支付方法
    private void payOrder(Integer id, PaymentWay paymentWay, Integer cash) {
        Order order = get(id);
        Double ticket = order.getTicket();//管理员支付的时候没有金额
        if (cash != null) {
            //计算多支付的金额 就是抵扣积分
            ticket = (cash - order.getAmount() * 100) / 100;//获取抵扣了的积分
        }
        Double tem = order.getTicket() - ticket;//获取实际要支付的积分
        //获取自己的积分
        Double myA = order.getUser().getAccounts().getAccount(AccountsType.ADVANCE);
        if (myA < tem) {//券不够用
            payOrder(order, paymentWay, PaymentStatus.NOT_ENOUGH);//支付失败,不够
        } else {
            if (tem > 0) {//需要扣券
                payOrder(order, paymentWay, tem);//支付成功且支付券
            } else {
                payOrder(order, paymentWay, PaymentStatus.PAID);//成功支付
            }

        }
        //保存
        merge(order);
    }


    //支付 //接受微信支付回调 并支付  返回支付结果
    @Override
    @Transactional
    public String payOrderByWx(String body) {
        Map<String, String> xml = XmlUtil.xmlToMap(body);
        String out_trade_no = xml.get("out_trade_no");
        Integer cash_fee = Integer.valueOf(xml.get("cash_fee"));//金额
        Order order = get("orderNo", out_trade_no);
        try {
            payOrder(order.getId(), PaymentWay.WX, cash_fee);//微信支付
        } catch (Exception e) {
            LOG.error("微信支付回调支付异常,{}", e.getMessage());
            LOG.error("订单id,{}", order.getId());
//            payOrderWithError(order.getId(), PaymentWay.WX);
        }
        //返回支付结果
        Map<String, Object> map = new HashMap();
        map.put("return_code", "SUCCESS");
        map.put("return_msg", "OK");
        String result = XmlUtil.mapToXml(map);
        return result;
    }


    //管理员支付
    @Override
    @Transactional
    public void payOrderByAdmin(Integer id) {
        payOrder(id, PaymentWay.ADMIN, null);
    }


    private void delivery(Order order) {
        //设置状态
        order.setStatus(OrderStatus.SHIPPED);
        order.setShippingTime(new Date());
    }

    @Transactional
    private void payOrderWithError(Integer id, PaymentWay paymentWay) {
        Order order = get(id);
//        payOrder(order, paymentWay);
        payOrder(order, paymentWay, PaymentStatus.PAID_ERROR);
        merge(order);
    }

    //成功支付
    private void payOrder(Order order, PaymentWay paymentWay, Double ticket) {
        List<AccountsRecord> allRecords = new ArrayList<>();
        payOrder(order, paymentWay, PaymentStatus.PAID);//设置支付状态
        //支付成功后续动作
        AccountsRecord accountsRecord = accountsHandler.deductAccountAndRecord(AccountsType.ADVANCE, order.getUser(), order.getUser(), ticket, "置换券-优惠商城购物消费!");
        allRecords.add(accountsRecord);
        //减库存
        deductMallGoodsAccount(order);
        if(order.getAmount()>0){
            //增加消费
            AccountsRecord accountsRecord1 = accountsHandler.increaseAccountAndRecord(AccountsType.CONSUME, order.getUser(), order.getUser(), order.getAmount(), "消费值-优惠商城购物累积消费值");
            allRecords.add(accountsRecord1);
        }
        //返利
        List<AccountsRecord> records = rewardVoucher(order);
        allRecords.addAll(records);
        //保存返利记录
        accountsRecordHandler.saveList(allRecords);
        //通知返利
        noticeHandler.noticeAccountRecords(allRecords);
    }

    //设置支付状态
    private void payOrder(Order order, PaymentWay paymentWay, PaymentStatus paymentStatus) {
        if (order == null) {
            logger.error("订单不存在id:{}", order.getId());
            throw new ApplicationException("订单不存在!id:" + order.getId());
        }
        if (hasPaid(order)) {
            logger.error("订单已经支付!");
            throw new ApplicationException("订单已经支付!id:" + order.getId());

        }
        order.setPaymentStatus(paymentStatus);//设置支付状态
        order.setPaymentTime(new Date());
        order.setPaymentWay(paymentWay);
    }


    //扣减账户
    private AccountsRecord deductAccounts(Order order, AccountsType accountsType) {
//        if (accountsType == AccountsType.ADVANCE) {//置换券支付只能支付置换券类型的
//            if (!order.getCanAdvance()) {
//                throw new ApplicationException("包含不能置换券支付的产品!");
//            }
//        }
        Double amount;
        if (accountsType == AccountsType.ADVANCE) {
            amount = order.getTicket();
        } else {
            amount = order.getAmount();
        }
        String more = "购物-优惠商城购物支付";
        AccountsRecord accountsRecord = accountsHandler.deductAccountAndRecord(accountsType, order.getUser(), order.getUser(), amount, more);
        return accountsRecord;
    }


    //是否已经支付
    private boolean hasPaid(Order order) {
        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            return false;
        }
        return true;
    }

    //是否已经发货
    private boolean hasDelivery(Order order) {
        if (order.getStatus().equals(OrderStatus.NEW)) {
            return false;
        }
//        System.out.println(order.getLogistics()==null);
        return true;
    }

    //置换券支付
    @Override
    @Transactional
    public void payOrderByAccounts(Integer id, Integer payOrderByAccounts) {
        AccountsType accountsType = AccountsType.stateOf(payOrderByAccounts);
        Order order = get(id);
        //减少库存
        deductMallGoodsAccount(order);
        PaymentWay paymentWay;
        //设置为支付状态
        if (accountsType == AccountsType.ADVANCE) {//置换券
            paymentWay = PaymentWay.ADVANCE;
        } else {//代金券
            paymentWay = PaymentWay.VOUCHER;
        }
        List<AccountsRecord> records = new ArrayList<>();
        //减少券
        records.add(deductAccounts(order, accountsType));
        //增加消费
        if (order.getAmount() > 0) {
            AccountsRecord accountsRecord = accountsHandler.increaseAccountAndRecord(AccountsType.CONSUME, order.getUser(), order.getUser(), order.getAmount(), "累积-优惠商城购物累积消费值");
            //累积消费记录
            accountsRecordHandler.save(accountsRecord);
        }
        //支付状态
        payOrder(order, paymentWay, PaymentStatus.PAID);
        //返利
        records.addAll(rewardVoucher(order));
        //保存
        merge(order);
        //保存代金券记录
        accountsRecordHandler.saveList(records);

        //通知通知
        noticeHandler.noticeAccountRecords(records);
    }


    @Override
    public List<Order> findOrders(SearchParameter<Order> p) {
        DetachedCriteria dc = DetachedCriteria.forClass(Order.class);
        Example example = Example.create(p.getEntity()).enableLike(MatchMode.ANYWHERE);
        dc.add(example);
        //下单人搜索
        if (p.getEntity().getUser() != null) {
            DetachedCriteria user = dc.createCriteria("user");
            Example example2 = Example.create(p.getEntity().getUser());
            user.add(example2);
//            addRestraction(user,"realName",p.getEntity().getUser().getRealName());
//            addRestraction(user,"agentCode",p.getEntity().getUser().getAgentCode());
        }
        //地址条件
        AddressClone addressClone = p.getEntity().getAddressClone();
        if (addressClone != null) {
            DetachedCriteria address = dc.createCriteria("addressClone");
            Example example1 = Example.create(p.getEntity().getAddressClone()).enableLike(MatchMode.ANYWHERE);
            address.add(example1);
//            addRestraction(address, "mobile", addressClone.getMobile());
//            addRestraction(address, "consignee", addressClone.getConsignee());
        }
        //时间搜索
        if (p.getStartTime() != null && p.getEndTime() != null) {
            dc.add(Restrictions.between("orderTime", p.getStartTimeByDate(), p.getEndTimeByDate()));
        }
        dc.addOrder(org.hibernate.criterion.Order.desc("id"));
        return orderDao.searchByPage(p, dc);
    }


    @Override
    public List<Order> findOrdersWithChildren(SearchParameter<Order> p) {
        DetachedCriteria dc = DetachedCriteria.forClass(Order.class);
        Example example = Example.create(p.getEntity()).enableLike(MatchMode.ANYWHERE);
        dc.add(example);
        //下单人搜索
        if (p.getEntity().getUser() != null) {
            DetachedCriteria user = dc.createCriteria("user");
//            Example example2 = Example.create(p.getEntity().getUser());
//            user.add(example2);
//            addRestraction(user,"realName",p.getEntity().getUser().getRealName());
//            addRestraction(user,"agentCode",p.getEntity().getUser().getAgentCode());
            //自己的或者用户的
            user.add(Restrictions.or(Restrictions.eq("id", p.getEntity().getUser().getId()), Restrictions.eq("parent.id", p.getEntity().getUser().getId())));
        }

        //时间搜索
        if (p.getStartTime() != null && p.getEndTime() != null) {
            dc.add(Restrictions.between("orderTime", p.getStartTimeByDate(), p.getEndTimeByDate()));
        }
        dc.addOrder(org.hibernate.criterion.Order.desc("id"));
        return orderDao.searchByPage(p, dc);
    }


    //提交订单
    private void commitOrder(Order order) {
        order.setUpdateTime(new Date());
        order.setOrderTime(new Date());
        order.setOrderNo(CommonUtil.createNo());
        order.setPaymentStatus(PaymentStatus.UNPAID);//设置成未支付
        order.setStatus(OrderStatus.NEW);//设置为新下单
    }

    private Set<OrderItem> bulidItems(Order order, ShopCart shopCart) {
        Set<OrderItem> items = new HashSet<>();
        order.setCanAdvance(true);//首先默认可以置换
        shopCart.getItems().values().stream().forEach(p -> {
            //限制购买数量
            validateActitivty(p.getGoods(), order.getUser(), p.getQuantity());
            OrderItem item = new OrderItem();
            item.setPmallOrder(order);
            item.setPmallGoods(p.getGoods());
            item.setPrice(p.getGoods().getRetailPrice());
            item.setQuantity(p.getQuantity());
            Set<GoodsStandard> goodsStandards = new HashSet<>();
            for (GoodsStandard gs : p.getGoodsStandards()) {
                GoodsStandard tem = goodsStandardHandler.get(gs.getId());
                goodsStandards.add(tem);
            }
            item.setStandards(goodsStandards);
            if (!item.getPmallGoods().getCanAdvance()) {//存在一款不能代金券的
                order.setCanAdvance(false);//就都不能代金券
            }
            items.add(item);
        });
        return items;
    }


    /**
     * @param pmallGoods 购买产品
     * @param agent      购买人
     * @param quantity   购买数量
     */
    private void validateActitivty(PmallGoods pmallGoods, Agent agent, Integer quantity) {
        if (pmallGoods.getActivity() == false) return;
        List<OrderItem> orderItems = orderItemDao.findOrderItemByGoodsAndUserAndTime(pmallGoods.getId(), agent.getId(), pmallGoods.getStartTime());
        Integer sumQuantity = 0;
        for (OrderItem item : orderItems) {
            sumQuantity += item.getQuantity();
        }
        if (sumQuantity + quantity > pmallGoods.getLimitNumer()) {
            throw new ApplicationException("提交失败," + pmallGoods.getName() + "限购" + pmallGoods.getLimitNumer());
        }
    }

    //获取返利明细
    private HashMap<Agent, Double> getAgentsWithVoucher(Order order) {
        HashMap<Agent, Double> maps = new HashMap<>();
        Agent agent = order.getUser();
        //首先找出能返利的代理
        List<Agent> parents = new ArrayList<>();
        Agent parent = agent.getParent();
        while (parent != null && !parent.isMutedUser()) {
            if (agentHandler.canPmallReward(parent)) {
                parents.add(parent);//可以返利的上级
            }
            parent = parent.getParent();
        }
        order.getItems().forEach(item -> {
//            CommonUtil.putAll(maps, accountsHandler.rewardPmallVoucher(parents, item.getQuantity(), item.getPmallGoods().getProfit()));
        });
        return maps;
    }


    //返利
    private List<AccountsRecord> rewardVoucher(Order order) {
        List<AccountsRecord> accountsRecords = new ArrayList<>();//暂时不返利
//        StringBuffer sb = new StringBuffer();
//        sb.append("利润--");
//        sb.append(order.getUser().getRealName()).append("优惠商城购买:");
//        order.getItems().forEach(p -> {
//            if (p.getPmallGoods().getName().contains("避孕")) {
//                sb.append("******");
//            } else {
//                sb.append(p.getPmallGoods().getName());
//            }
//            sb.append("X").append(p.getQuantity()).append("  ");
//        });
//        Map<Agent, Double> map = getAgentsWithVoucher(order);
//        for (Agent agent : map.keySet()) {
//            accountsRecords.add(accountsHandler.increaseAccountAndRecord(AccountsType.VOUCHER, agent, order.getUser(), map.get(agent), sb.toString()));
//        }
        return accountsRecords;
    }


    /**
     * 减少产品库存
     *
     * @param order
     */
    private void deductMallGoodsAccount(Order order) {
        order.getItems().forEach(p -> {
            //减少总库存
            mallGoodsHandler.deductMallGoodsAccount(p.getPmallGoods(), p.getQuantity());
            //减少单品库存
            p.getStandards().forEach(st -> {
                st.deductCurrentStock(p.getQuantity());
            });
        });
    }


    @Override
    public Double getActualAmount(Order order) {
//        if (order.getCanAdvance()) {
        Double tem1 = getActualAdvance(order);//可用的券
        Double tem2 = order.getTicket();//需要的券
        //实际现金加上不够的券
        Double result = PreciseComputeUtil.add(order.getAmount(), tem2 - tem1);
        return PreciseComputeUtil.round(result);
//        } else {
//            return order.getAmount();
//        }
    }

    //实际置换券 所需置换券
    @Override
    public Double getActualAdvance(Order order) {
//        if (order.getCanAdvance()) {//如果可以置换
        Double tem1 = order.getTicket();//所需置换券
        Double tem2 = accountsHandler.getAvailableAccounts(order.getUser(), tem1, AccountsType.ADVANCE);
        return tem2;//返回可用的置换券
//        }
//        return 0.0;
    }

    //发货
    @Override
    @Transactional
    public void delivery(Integer id, String logistics, String logisticsCode, String operate) {
        Order order = get(id);
        if (hasDelivery(order)) {
            throw new ApplicationException("订单已经发货");
        }
        order.setLogisticsCode(logisticsCode);
        order.setLogistics(logistics);
        order.setShippingOperator(operate);
        order.setShippingTime(new Date());//发货时间
        order.setStatus(OrderStatus.SHIPPED);//已经发货
        merge(order);
        //通知
        noticeHandler.noticeOrder(order);
    }


    @Override
    public List<Order> findAllOrders(SearchParameter<Order> p) {

        return orderDao.findAllOrders(p);
    }

    @Override
    public List<Order> findNewOrders(Integer limit) {

        return orderDao.findNewOrders(limit);
    }

    //获取所有没有发货的订单总数
    @Override
    public List<Object[]> getOrdersItemCount(Integer limit) {
        return orderDao.getOrdersItemCount(limit);
    }

    private OrderDao orderDao;

    public OrderDao getOrderDao() {
        return orderDao;
    }

    @Autowired
    public void setOrderDao(OrderDao orderDao) {
        this.orderDao = orderDao;
        super.setBaseDao(orderDao);
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountsHandler accountsHandler;

    @Autowired
    private NoticeHandler noticeHandler;

    @Autowired
    private AgentHandler agentHandler;

    @Autowired
    private MallGoodsHandler mallGoodsHandler;

    @Autowired
    private GoodsStandardHandler goodsStandardHandler;

    @Autowired
    private WxConfigFactory wxConfigFactory;

    @Autowired
    private OrderItemDao orderItemDao;


    @Autowired
    private PayHandler payHandler;

    @Autowired
    private AccountsRecordHandler accountsRecordHandler;

    private Logger LOG = LoggerFactory.getLogger(this.getClass());
}
