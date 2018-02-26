package com.dreamer.service.mobile.impl;

import com.dreamer.domain.account.GoodsAccount;
import com.dreamer.domain.inter.Country;
import com.dreamer.domain.inter.CountryPrice;
import com.dreamer.domain.mall.delivery.DeliveryNote;
import com.dreamer.domain.mall.goods.*;
import com.dreamer.domain.mall.transfer.Transfer;
import com.dreamer.domain.mall.transfer.TransferItem;
import com.dreamer.domain.user.*;
import com.dreamer.domain.user.enums.AccountsType;
import com.dreamer.repository.mobile.AccountsRecordDao;
import com.dreamer.repository.mobile.AgentDao;
import com.dreamer.repository.mobile.GoodsDao;
import com.dreamer.repository.mobile.TransferDao;
import com.dreamer.service.inter.CountryHandler;
import com.dreamer.service.inter.CountryPriceHandler;
import com.dreamer.service.mobile.*;
import com.dreamer.util.CommonUtil;
import com.dreamer.util.PreciseComputeUtil;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.wxjssdk.util.DateUtil;
import org.apache.commons.collections.map.HashedMap;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.mx.otter.exception.ApplicationException;
import ps.mx.otter.utils.SearchParameter;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by huangfei on 02/07/2017.
 */
@Service
public class TransferHandlerImpl extends BaseHandlerImpl<Transfer> implements TransferHandler {
    /**
     * 返利
     *
     * @param transfer
     */
    private List<AccountsRecord> rewardVoucher(Transfer transfer) {
        List<AccountsRecord> records = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        sb.append("利润--");
        sb.append(transfer.getToAgent().getRealName()).append("购买:");
        Country country = countryHandler.get("name", transfer.getCountry());
        List<Double> cvs = new ArrayList<>();
        transfer.getItems().forEach(p -> {
            if (country != null) {
                CountryPrice cp = countryPriceHandler.getPrice(p.getGoods(), country);
                Double cv = cp.getProfit() * p.getQuantity();
                cvs.add(cv);
            }
            sb.append(p.getGoods().getName()).append("X").append(p.getQuantity()).append("  ");
        });
        String more = sb.toString();
        //获取整个国际的返利
        HashMap<Agent, Double> map = getAgentsWithVoucher(transfer.getToAgent(), transfer.getItems());
        Double cvsum = PreciseComputeUtil.round(cvs.stream().mapToDouble(c -> c).sum());
        if (country != null && cvsum != 0) {
            map.put(country.getAgent(), cvsum);
        }
        for (Agent agent : map.keySet()) {
            //增加返利库存
            records.add(accountsHandler.increaseAccountAndRecord(AccountsType.VOUCHER, agent, transfer.getToAgent(), map.get(agent), more));
        }
        return records;
    }


    /**
     * 追回代金券
     *
     * @param transfer
     * @return
     */
    private List<AccountsRecord> backVoucher(Transfer transfer) {
        List<AccountsRecord> records = new ArrayList<>();
        StringBuffer goodsInfo = new StringBuffer();
        transfer.getItems().forEach(p -> {
            goodsInfo.append(p.getGoods().getName()).append("X").append(p.getQuantity()).append("  ");
        });
        //全部退代金券
        Double voucher = transfer.getAmount();
        StringBuffer backMy = new StringBuffer();
        backMy.append("退回--退货退回代金券，货物:");
        backMy.append(goodsInfo).append("  ");
        records.add(accountsHandler.increaseAccountAndRecord(AccountsType.VOUCHER, transfer.getFromAgent(), muteUserHandler.getMuteUser(), voucher, backMy.toString()));
        //追回上级代金券
        StringBuffer sb = new StringBuffer();
        sb.append("追回--");
        sb.append(transfer.getFromAgent().getRealName()).append("退货追回代金券，货物:");
        sb.append(goodsInfo.toString());
        HashMap<Agent, Double> map = getAgentsWithVoucher(transfer.getFromAgent(), transfer.getItems());
        for (Agent agent : map.keySet()) {//减少上级的代金券 不验证 可以为负数
            records.add(accountsHandler.deductAccountAndRecord(AccountsType.VOUCHER, agent, transfer.getFromAgent(), map.get(agent), sb.toString(), false));
        }
        return records;
    }


    /**
     * 核心返利算法
     * 获取某个订单可以返利的总数
     *
     * @param transfer
     * @return
     */
    private HashMap<Agent, Double> getAgentsWithVoucher(Agent agent, Set<TransferItem> items) {
        HashMap<Agent, Double> maps = new HashMap<>();
        Agent toAgent = agent;
        //首先找出能返利的代理
        List<Agent> parents = new ArrayList<>();
        Agent parent = toAgent.getParent();
        while (parent != null && !parent.isMutedUser()) {
            if (agentHandler.canReward(parent)) {
                parents.add(parent);//可以返利的上级
            }
            parent = parent.getParent();
        }

//        Double countryVoucher = 0.0;//国际返利

        //开始返利
        items.forEach(
                item -> {
                    //装载所有返利 暂时不返利
//                    CommonUtil.putAll(maps, accountsHandler.rewardVoucher(parents, item.getGoods().getVoucher(), item.getQuantity()));
                }
        );

        return maps;
    }


    /**
     * 主动转出
     *
     * @param transfer
     */
    private void transfer(Transfer transfer) {
        transfer.setRemittance("主动转出");
        transfer.setStatus(GoodsTransferStatus.CONFIRM);//设置已完成状态
        //转出货物
        transferGoods(transfer);

    }

    /**
     * 申请退货
     *
     * @param transfer
     */
    private void applyBackTransfer(Transfer transfer) {
        if (!agentHandler.isVip(transfer.getFromAgent())) {//不是vip不能退货
            throw new ApplicationException("只有vip才可以退货！");
        }
        transfer.setRemittance("退货冻结");
        transfer.setStatus(GoodsTransferStatus.BACK);//设置已完成状态
        transfer.setApplyTime(new Date());
        sumAmount(transfer);
        sumQuantity(transfer);
        //转出货物
        transferGoods(transfer);
    }

    /**
     * 转出货物
     *
     * @param transfer
     */
    private void transferGoods(Transfer transfer) {
        Agent fromAgent = transfer.getFromAgent();
        Agent toAgent = transfer.getToAgent();
        GoodsAccount fromAgentMain = goodsAccountHandler.getMainGoodsAccount(fromAgent);
        GoodsAccount toAgentMain = goodsAccountHandler.getMainGoodsAccount(toAgent);

        transfer.setUpdateTime(new Date());//转货时间
        transfer.setOperator(fromAgent.getRealName());//操作员

        //拨库存
        transfer.getItems().stream().forEach(
                p -> {
                    //这里要判断是不是公司转出，或者公司转入
                    if (fromAgent.isMutedUser()) {//如果转出人是公司
                        goodsHandler.reduceBalacne(p.getGoods().getId(), p.getQuantity());//减少公司余额
                    } else if (toAgent.isMutedUser()) {//如果接收方是公司
                        goodsHandler.addBalance(p.getGoods().getId(), p.getQuantity());//增加公司余额
                    }
                    GoodsAccount fromGoodsAccount = goodsAccountHandler.getGoodsAccount(fromAgent, p.getGoods(), fromAgentMain);
                    goodsAccountHandler.deductGoodsAccount(fromGoodsAccount, p.getQuantity());//减少库存
                    GoodsAccount toGoodsAccount = goodsAccountHandler.getGoodsAccount(toAgent, p.getGoods(), toAgentMain);
                    goodsAccountHandler.increaseGoodsAccount(toGoodsAccount, p.getQuantity());//增加库存
                }
        );
    }

    public static void main(String[] args) {


    }


    /**
     * 找出某人的转账记录
     *
     * @param uid
     * @param toId
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<Transfer> findTransferRecords(Integer uid, Integer toId, Date startDate, Date endDate, GoodsTransferStatus status) {
        DetachedCriteria dc = DetachedCriteria.forClass(Transfer.class);
        dc.add(Restrictions.eq("fromAgent.id", uid));
        dc.add(Restrictions.eq("toAgent.id", toId));
        if (startDate != null && endDate != null) {
            dc.add(Restrictions.between("updateTime", startDate, endDate));//时间
        }
        if (status != null) {//类别
            dc.add(Restrictions.eq("status", status));
        }
//       dc.add(Restrictions.like("updateTime", DateUtil.formatStr(startDate,"yyyy-MM-dd")));
        dc.addOrder(Order.desc("id"));
        List<Transfer> records = transferDao.findByCriteria(dc, null, null);
        //统计
        if (records == null) {
            return new ArrayList<>();
        }
        return records;
    }


    @Override
    public List<Transfer> findTransfers(Integer uid, Integer nid) {
        DetachedCriteria dc = DetachedCriteria.forClass(Transfer.class);
        if (nid == null) {
            //收货人或者转货人是我
            dc.add(Restrictions.or(Restrictions.eq("fromAgent.id", uid), Restrictions.eq("toAgent.id", uid)));
        } else {
            //根据id查找
            dc.add(Restrictions.eq("id", nid));
        }
        //时间倒序  查询30条
        dc.addOrder(Order.desc("id"));
        List<Transfer> items = transferDao.findByCriteria(dc, 0, 30);
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }


    /**
     * 初始化一个transfer
     *
     * @return
     */
    private Transfer initTransfer(Integer fromUid, Integer toUid, Integer[] goodsIds, Integer[] amounts, String remark, String country) {
        Agent fromAgent = agentDao.get(fromUid);
        Agent toAgent = agentDao.get(toUid);
        //生成订单
        Transfer transfer = new Transfer(toAgent, fromAgent, new Timestamp(new Date().getTime()), remark);
        if (country == null) {
            country = "中国";
        }
        transfer.setCountry(country);
        buildItems(transfer, goodsIds, amounts);//组装货物
        return transfer;
    }


    /**
     * 获取总金额
     *
     * @return
     */
    private Double sumAmount(Transfer transfer) {
        //计算费用 总金额
        Double amount = transfer.getItems().stream().mapToDouble(item -> item.getAmount()).sum();
        transfer.setAmount(amount);
        return PreciseComputeUtil.round(amount);
    }

    /**
     * 获取总货物
     *
     * @return
     */
    private Integer sumQuantity(Transfer transfer) {
        //计算费用 总金额
        Integer quantity = transfer.getItems().stream().mapToInt(item -> item.getQuantity()).sum();
        transfer.setQuantity(quantity);
        return quantity;
    }


    private void buildItems(Transfer transfer, Integer[] goodsIds, Integer[] amounts) {
        String country = transfer.getCountry();
        Agent toAgent = transfer.getToAgent();
        Agent fromAgent = transfer.getFromAgent();
        Set<TransferItem> items = new HashSet<>();
        TransferItem item;
        for (int i = 0; i < goodsIds.length; i++) {
            Goods goods = goodsDao.get(goodsIds[i]);
            Integer quantity = amounts[i];
            Double dP;
            Price price;
            if (toAgent.isMutedUser()) {
                price = priceHandler.getPrice(fromAgent, goods);
            } else {
                price = priceHandler.getPrice(toAgent, goods);
            }
            dP = price.getPrice();
            if (country != null) {
                Country c = countryHandler.get("name", country);
                if (c != null) {
                    CountryPrice cp = countryPriceHandler.getPrice(goods, c);
                    dP += cp.getPrice();
                }
            }
            item = new TransferItem(transfer, quantity, dP, goods, price.getAgentLevel().getName());
            items.add(item);
        }
        transfer.setItems(items);
    }


    /**
     * 确认转货
     *
     * @param transfer
     */
    private void confirmTransfer(Transfer transfer) {
        transfer.setRemittance("订单已成交");
        transfer.setStatus(GoodsTransferStatus.CONFIRM);
        transferGoods(transfer);//转出货物
    }

    /**
     * 确认退货订单 退奖金  追回奖金  货物已经扣除不用管
     *
     * @param transfer
     */
    private List<AccountsRecord> confirmBackTransfer(Transfer transfer) {
        if (!transfer.getStatus().equals(GoodsTransferStatus.BACK)) {
            throw new ApplicationException("不可退货订单!当前订单状态为" + transfer.getStatus().getDesc());
        }
        //退奖金
        List<AccountsRecord> accountsRecord = backVoucher(transfer);

        //更新状态
        transfer.setRemittance("订单已退货!");
        transfer.setStatus(GoodsTransferStatus.CONFIRM);
        return accountsRecord;
    }

    /**
     * 主动转货
     *
     * @param fromUid
     * @param toUid
     * @param goodsIds
     * @param amounts
     * @param remark
     * @return
     */
    @Override
    @Transactional
    public void transfer(Integer fromUid, Integer toUid, Integer[] goodsIds, Integer[] amounts, String remark) {
        Transfer transfer = initTransfer(fromUid, toUid, goodsIds, amounts, remark, null);
        //验证
        validate(transfer.getFromAgent(), transfer.getToAgent());
        //提交订单
        applyTransfer(transfer);
        //主动转货
        transfer(transfer);
        //保存
        transfer = transferDao.merge(transfer);
        //通知 异步
        noticeHandler.noticeTransfer(transfer);
    }


    //验证 只能转给自己的股东  股东只能转给公司 或者自己的下家
    private void validate(Agent from, Agent to) {
        String ln = agentHandler.getLevelName(from);
        if (ln.equals(AgentLevelName.联合股东.toString())) {//股东转货，转给公司 或者自己的下级
            if (!to.isMutedUser()) {//如果不是转给公司
                Agent tem = agentHandler.findBoss(to.getId());
                if (tem == null) throw new ApplicationException("没有找到股东!");
                if (!tem.getId().equals(from.getId())) throw new ApplicationException("只能转给自己的团队!");
            }
        } else {//其它人转货,只能抓给自己的股东
//            Agent tem = agentHandler.findBoss(from.getId());
//            if(tem==null)throw new ApplicationException("没有找到股东!");
            //如果转给股东 只能转给自己的股东  如果不是股东 只能转给跟自己一条线的人
            if (to.isMutedUser()) throw new ApplicationException("非股东不能转给公司!");
            String tlv = agentHandler.getLevelName(to);//接收人的级别
            Agent tem1 = agentHandler.findBoss(from.getId());
            if (tem1 == null) throw new ApplicationException("没有找到您的团队股东,请联系管理员!");
            if (tlv.equals(AgentLevelName.联合股东.toString())) {
                if (!tem1.getId().equals(to.getId())) {
                    throw new ApplicationException("只能转给自己团队的股东!");
                }
            } else {
                Agent tem2 = agentHandler.findBoss(to.getId());
                if (tem2 == null) throw new ApplicationException("没有找到接收人的团队股东!请联系管理员!");
                if (!tem2.getId().equals(tem1.getId())) {
                    throw new ApplicationException("只能转给自己团队的人!");
                }
            }
        }
    }


    /**
     * 退货操作  冻结库存 先把库存转给公司  代金券先不返还  等待公司确认
     *
     * @param uid
     * @param goodsIds
     * @param amounts
     * @param remark
     */
    @Override
    @Transactional
    public void applyBackTransfer(Integer uid, Integer[] goodsIds, Integer[] amounts, String remark) {
        MutedUser mutedUser = muteUserHandler.getMuteUser();
        //退给公司的订单
        Transfer transfer = initTransfer(uid, mutedUser.getId(), goodsIds, amounts, remark, null);
        applyBackTransfer(transfer);
        merge(transfer);
    }

    /**
     * 申请提交
     *
     * @param transfer
     */
    private void applyTransfer(Transfer transfer) {
        transfer.setStatus(GoodsTransferStatus.NEW);//转货状态
        transfer.setApplyTime(new Date());//转货时间
        sumAmount(transfer);//统计价格
        sumQuantity(transfer);//统计货物总数
    }

    /**
     * 扣除费用
     *
     * @return
     */
    private List<AccountsRecord> deductMoney(Transfer transfer) {
        Agent agent = transfer.getToAgent();
        Agent fromAgent = transfer.getFromAgent();
        Double sumAmount = transfer.getAmount();
        //可用的进货券
        Double purchase = accountsHandler.getAvailablePurchase(agent, sumAmount);
        //可用的代金券 扣除了进货券  不足会报错
        Double voucher = accountsHandler.getAvailableVoucher(agent, PreciseComputeUtil.round(sumAmount - purchase));
        transfer.setVoucher(voucher);
        transfer.setPurchase(purchase);
        List<AccountsRecord> records = new ArrayList<>();
        //减少代金券 生成记录
        String more = "进货-支付给" + fromAgent.getRealName();
        AccountsRecord vocuherRecord = accountsHandler.deductAccountAndRecord(AccountsType.VOUCHER, agent, fromAgent, voucher, more);
        records.add(vocuherRecord);
        //减少进货券
        if (purchase > 0) {
            AccountsRecord purchaseRecord = accountsHandler.deductAccountAndRecord(AccountsType.PURCHASE, agent, fromAgent, purchase, more);
            records.add(purchaseRecord);
        }
        return records;
    }


    /**
     * //TODO
     * 确认订单
     *
     * @param transfer
     */
    @Override
    @Transactional
    public void confirm(Transfer transfer) {
        confirmTransfer(transfer);
        transferDao.merge(transfer);
    }


    /**
     * //TODO
     * //确认退货订单
     *
     * @param transfer
     */
    @Override
    @Transactional
    public void backTransferConfirm(Transfer transfer) {
        List<AccountsRecord> records = confirmBackTransfer(transfer);
        //保存通知
        accountsRecordDao.saveList(records);
        //保存订单
        transferDao.merge(transfer);
        //通知
        noticeHandler.noticeAccountRecords(records);
    }

    /**
     * 拒绝退货订单
     *
     * @param transfer
     */
    @Override
    @Transactional
    public void backTransferRefuse(Transfer transfer) {
        Transfer transfer1 = refuseBack(transfer);
        merge(transfer);
        //通知
        noticeHandler.noticeTransfer(transfer1);
    }

    //拒绝退货
    private Transfer refuseBack(Transfer transfer) {
        //生成新的转货订单
        Transfer transfer1 = getTransferFromRefuseTrasfer(transfer);
        transferGoods(transfer1);//转货
        //保存
        transfer.setRemittance("退货订单拒绝");
        transfer.setStatus(GoodsTransferStatus.DISAGREE);
        return transfer1;

    }

    /**
     * 从拒绝退货订单获取补货订单
     *
     * @param transfer
     */
    private Transfer getTransferFromRefuseTrasfer(Transfer transfer) {
        Transfer transfer1 = new Transfer();
        transfer1.setFromAgent(transfer.getToAgent());
        transfer1.setToAgent(transfer.getFromAgent());
        transfer1.setItems(transfer.getItems());
        sumAmount(transfer1);
        sumQuantity(transfer1);
        transfer1.setApplyTime(new Date());
        transfer1.setRemittance("拒绝退货退回");
        transfer1.setStatus(GoodsTransferStatus.CONFIRM);
        return transfer1;
    }


    /**
     * 从拒绝退货发货订单获取补货订单
     *
     * @param transfer
     */
    @Override
    public Transfer transferFromDeleteNote(DeliveryNote note) {
        Transfer transfer = new Transfer();
        transfer.setFromAgent(muteUserHandler.getMuteUser());
        transfer.setToAgent(note.getApplyAgent());//申请人
        transfer.setRemittance("取消发货退回");
        Set<TransferItem> items = new HashSet<>();
        note.getDeliveryItems().stream().forEach(
                p -> {
                    TransferItem item = new TransferItem(transfer, p.getQuantity(), p.getPrice(), p.getGoods(), p.getPriceLevelName());
                    items.add(item);
                }
        );
        transfer.setItems(items);
        transfer.setApplyTime(new Date());
        sumAmount(transfer);
        sumQuantity(transfer);
        transfer.setStatus(GoodsTransferStatus.CONFIRM);
        transferGoods(transfer);//转货
        return transfer;
    }


    /**
     * 通过代金券+进货券购买
     *
     * @param fromUid
     * @param toUid
     * @param goodsIds
     * @param amounts
     * @param remark
     */
    @Override
    @Transactional
    public void transferAutoConfirm(Integer fromUid, Integer toUid, Integer[] goodsIds, Integer[] amounts, String remark, String country) {
        Transfer transfer = initTransfer(fromUid, toUid, goodsIds, amounts, remark, country);
        applyTransfer(transfer);//提交 初始化相关参数
        //扣除费用
        List<AccountsRecord> records = deductMoney(transfer);
        //确认
        confirmTransfer(transfer);
        //返利
        List<AccountsRecord> rewardRecords = rewardVoucher(transfer);
        records.addAll(rewardRecords);
        //保存订单
        transferDao.merge(transfer);
        //保存记录
        accountsRecordDao.saveList(records);
        //奖金通知
        noticeHandler.noticeAccountRecords(records);
        //产品库存通知
        noticeHandler.noticeTransfer(transfer);
    }


    /**
     * 自动转货与发货
     *
     * @param address
     * @param fromUid
     * @param toUid
     * @param goodsIds
     * @param amounts
     * @param remark
     */
    @Override
    @Transactional
    public void transferAutoConfirmAndDelivery(AddressMy address, Integer fromUid, Integer toUid, Integer[] goodsIds, Integer[] amounts, String remark) {
        String country = address.getCountry();
        if (address.getId() != null) {
            Address add = addressMyHandler.get(address.getId());
            country = add.getCountry();
        }
        transferAutoConfirm(fromUid, toUid, goodsIds, amounts, remark, country);//公司转出去
        if (address.getConsigneeCode() != null && !address.getConsigneeCode().equals("")) {//
            Agent toAgent = agentHandler.get("agentCode", address.getConsigneeCode().trim());
            if (toAgent == null) {
                throw new ApplicationException("收货人编号对应的代理不存在！请检查");
            }
            fromUid = toAgent.getId();
        } else {
            fromUid = toUid;
        }
        deliveryNoteHandler.deliveryGoods(address, toUid, fromUid, goodsIds, amounts, remark);
    }

    /**
     * 提交申请
     *
     * @param fromUid
     * @param toUid
     * @param goodsIds
     * @param amounts
     * @param remark
     * @return
     */
    @Override
    @Transactional
    public Integer applyTransfer(Integer fromUid, Integer toUid, Integer[] goodsIds, Integer[] amounts, String remark) {
        Transfer transfer = initTransfer(fromUid, toUid, goodsIds, amounts, remark, null);
        //申请
        applyTransfer(transfer);
        //保存
        transfer = transferDao.merge(transfer);
        return transfer.getId();
    }

    /**
     * 分页查询记录
     *
     * @param p
     * @param currentUser
     * @return
     */
    @Override
    public List<Transfer> findRecords(SearchParameter<Transfer> p, User currentUser) {
        return transferDao.findRecords(p, currentUser);
    }

    @Override
    public List<Transfer> findRecords(String startTime, String endTime, String agentCode) {

        if (startTime == null) startTime = "1994-09-01";
        if (endTime == null) startTime = "2994-09-01";
        Date st = DateUtil.formatStartTime(startTime);
        Date et = DateUtil.formatEndTime(endTime);
        Agent agent = null;

        if (agentCode != null && !agentCode.equals("")) {
            agent = agentHandler.get("agentCode", agentCode);
            if (agent == null) return new ArrayList<>();
        }
        final Agent tem = agent;
        List<Transfer> transfers = transferDao.findRecords(st, et);
        transfers = transfers.stream().filter(p -> {
            Agent toA = p.getToAgent();
            Agent fromA = p.getFromAgent();
            Boolean result = true;
            if (p.getFromAgent().isMutedUser()) {
                //接收人必须是股东
//                String ln = agentHandler.getLevelName(toA);
                if (tem != null) {//过滤团队
                    if (!agentHandler.isUp(toA.getId(), tem.getId())) {
                        result = false;
                    }
                }
//                if (ln.contains(AgentLevelName.联合股东.toString())) {
//                    result = true;
//                }
            } else if (toA.isMutedUser()) {
                //转出人必须是股东
//                String ln = agentHandler.getLevelName(fromA);
                if (tem != null) {//过滤团队
                    if (!agentHandler.isUp(fromA.getId(), tem.getId())) {
                        result=false;
                    }
                }
//                if (ln.contains(AgentLevelName.联合股东.toString())) {
//                    result = true;
//                }
            }
            return result;
        }).collect(Collectors.toList());
        return transfers;
    }


    @Autowired
    private GoodsAccountHandler goodsAccountHandler;

    @Autowired
    private AccountsHandler accountsHandler;

    @Autowired
    private AgentHandler agentHandler;

    @Autowired
    private TransferDao transferDao;

    public TransferDao getTransferDao() {
        return transferDao;
    }

    public void setTransferDao(TransferDao transferDao) {
        this.transferDao = transferDao;
        super.setBaseDao(transferDao);
    }

    @Autowired
    private AccountsRecordDao accountsRecordDao;

    @Autowired
    private NoticeHandler noticeHandler;

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private PriceHandler priceHandler;

    @Autowired
    private AgentDao agentDao;

    @Autowired
    private DeliveryNoteHandler deliveryNoteHandler;

    @Autowired
    private MuteUserHandler muteUserHandler;

    @Autowired
    private GoodsHandler goodsHandler;

    @Autowired
    private CountryHandler countryHandler;

    @Autowired
    private CountryPriceHandler countryPriceHandler;

    @Autowired
    private AddressMyHandler addressMyHandler;
}
