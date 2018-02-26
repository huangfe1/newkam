package com.dreamer.service.mobile.impl;

import com.dreamer.domain.mall.delivery.DeliveryItem;
import com.dreamer.domain.mall.delivery.DeliveryNote;
import com.dreamer.domain.mall.transfer.Transfer;
import com.dreamer.domain.pmall.order.Order;
import com.dreamer.domain.pmall.order.OrderItem;
import com.dreamer.domain.user.AccountsRecord;
import com.dreamer.domain.user.AddressClone;
import com.dreamer.domain.user.Agent;
import com.dreamer.service.mobile.NoticeHandler;
import com.dreamer.service.mobile.factory.WxConfigFactory;
import com.dreamer.util.TokenInfo;
import com.wxjssdk.JSAPI;
import com.wxjssdk.dto.SdkResult;
import com.wxjssdk.util.DateUtil;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by huangfei on 05/07/2017.
 */
@Service
public class NoticeHandlerImpl implements NoticeHandler {

    @Autowired
    private WxConfigFactory wxConfigFactory;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 调用微信通知 通知客户 资金通知
     *
     * @param records
     */
    public void noticeAccountRecords(List<AccountsRecord> records) {
        String template_id = "E3rO4uIgZS1StrffcWJHxLwK7wfkY8AJFNunvvyMgQs";
        Map<String, Object> data;
        for (AccountsRecord record : records) {
            String url = "http://ht.kam365.com/kam/mobile/accounts/records.html?stateType=" + record.getAccountsType().getState();
            data = new HashMap<>();
            data.put("first", createItemMap(record.getInfo()));//变动原因
            data.put("keyword2", createItemMap(record.getAmount()));
            data.put("keyword1", createItemMap(DateUtil.formatDate(record.getUpdateTime())));
            data.put("keyword3", createItemMap(record.getNowAmount()));
            data.put("remark", createItemMap("咖盟，让这个世界心生爱目！"));
            sendTemplateMessage(record.getAgent().getWxOpenid(), template_id, url, data);

        }
    }

    /**
     * 调用微信通知 通知客户 转货通知
     *
     * @param records
     */
    public void noticeTransfer(Transfer transfer) {
        String url = "http://ht.kam365.com/kam/mobile/transfer/records.html";
        Agent fromAgent = transfer.getFromAgent();
        Agent toAgent = transfer.getToAgent();
        //入库通知
        String template_id = "gIopagcLf7H_87HSWWtnuvGiCkC6lUVnd0PWGUxSHCk";
        Map<String, Object> toData = new HashedMap();
        Map<String, Object> fromData = new HashedMap();
        toData.put("first", createItemMap("您好，您有一批产品入库，请查看!"));
        StringBuffer stringBuffer = new StringBuffer();
        transfer.getItems().stream().forEach(p -> {
            stringBuffer.append(p.getGoods().getName());
            stringBuffer.append("X");
            stringBuffer.append(p.getQuantity());
            stringBuffer.append("\r\n");
        });
        toData.put("keyword1", createItemMap(stringBuffer.toString()));
        toData.put("keyword2", createItemMap("请前往系统查看!"));
        toData.put("remark", createItemMap("咖盟，让这个世界心生爱目！"));
        sendTemplateMessage(toAgent.getWxOpenid(), template_id, url, toData);
        //出库通知

        template_id = "QuSDx2Fthk9BHGuPwTLq_IUVh5g_wFP9SmVGJbgfwOA";
        fromData.put("first", createItemMap("您好，您有一批产品出库，请查看!"));
        fromData.put("keyword1", createItemMap(transfer.getId()));
        fromData.put("keyword2", createItemMap(stringBuffer.toString()));
        fromData.put("remark", createItemMap("咖盟，让这个世界心生爱目！"));
        sendTemplateMessage(fromAgent.getWxOpenid(), template_id, url, fromData);
    }

    /**
     * 调用微信通知 通知客户 待发货通知
     *
     * @param note
     */
    public void noticeDeliveryNote(DeliveryNote note) {
        Agent fromAgent = note.getFromAgent();
        Agent toAgent = note.getToAgent();
        String template_id = "QfaXo9siEWxXbvA3jyuHZmZOpgjn1hbv8ZzpHGOunqY";
        String url = "http://ht.kam365.com/kam/mobile/delivery/records.html";
        Map<String, Object> fromAgentData = new HashedMap();
        Map<String, Object> toAgentData = new HashedMap();
        //出货人
        fromAgentData.put("first", createItemMap("您好，您的发货订单提交成功，即将发给" + toAgent.getRealName()));
        //产品数量
        String stringBuffer = items2String(note.getDeliveryItems());

        fromAgentData.put("keyword1", createItemMap(stringBuffer));
        fromAgentData.put("keyword2", createItemMap(DateUtil.formatDate(note.getUpdateTime())));
        fromAgentData.put("remark", createItemMap("咖盟，让这个世界心生爱目"));
        sendTemplateMessage(fromAgent.getWxOpenid(), template_id, url, fromAgentData);
        if (fromAgent.getId().equals(toAgent.getId())) return;//如果是自己给自己发货就不用提醒了
        //收货人
        toAgentData.put("first", createItemMap("您好," + note.getApplyAgent().getRealName() + "给您的发货订单已经提交，即将发货！"));
        toAgentData.put("keyword1", createItemMap(stringBuffer));
        toAgentData.put("keyword2", createItemMap(DateUtil.formatDate(note.getUpdateTime())));
        toAgentData.put("remark", createItemMap("咖盟，让这个世界心生爱目!"));
        sendTemplateMessage(toAgent.getWxOpenid(), template_id, url, toAgentData);
    }


//    /**
//     * 优惠商城购买通知
//     *
//     * @param order
//     */
//    public void noticeOrderToParent(Order order) {
//
//    }
//
//    private void noticeOrder(){
//
//    }


    /**
     * 调用微信通知 通知客户 取消发货通知
     *
     * @param note
     */
    public void noticeDeliveryNoteDeleted(DeliveryNote note) {
        Agent fromAgent = note.getFromAgent();
        Agent toAgent = note.getToAgent();
        String template_id = "QfaXo9siEWxXbvA3jyuHZmZOpgjn1hbv8ZzpHGOunqY";
        String url = "http://ht.kam365.com/kam/mobile/delivery/records.html";
        Map<String, Object> fromAgentData = new HashedMap();
        Map<String, Object> toAgentData = new HashedMap();
        //出货人
        fromAgentData.put("first", createItemMap("您好，您发给" + toAgent.getRealName() + "的订单取消成功，产品已退回到您的库存!"));
        //产品数量
        String stringBuffer = items2String(note.getDeliveryItems());

        fromAgentData.put("keyword1", createItemMap(stringBuffer));
        fromAgentData.put("keyword2", createItemMap(DateUtil.formatDate(note.getUpdateTime())));
        fromAgentData.put("remark", createItemMap("咖盟，让这个世界心生爱目!"));
        sendTemplateMessage(fromAgent.getWxOpenid(), template_id, url, fromAgentData);
        //收货人
        toAgentData.put("first", createItemMap("您好," + fromAgent.getRealName() + "给您的发货订单已经取消，不会发货！"));
        toAgentData.put("keyword1", createItemMap(stringBuffer));
        toAgentData.put("keyword2", createItemMap(DateUtil.formatDate(note.getUpdateTime())));
        toAgentData.put("remark", createItemMap("咖盟，让这个世界心生爱目!"));
        sendTemplateMessage(toAgent.getWxOpenid(), template_id, url, toAgentData);
    }


    /**
     * 调用微信通知 通知客户 已经发货通知
     *
     * @param note
     */
    public void noticeDelived(DeliveryNote note) {
        Agent fromAgent = note.getFromAgent();
        Agent toAgent = note.getToAgent();
        String template_id = "mBng3sOfkaRDy9e1mHhIsXxQx2iuSDcVX76EBDAABx8";
        String url = "http://ht.kam365.com/kam/mobile/delivery/records.html";
        Map<String, Object> fromAgentData = new HashedMap();
        Map<String, Object> toAgentData = new HashedMap();
        //出货人
        fromAgentData.put("first", createItemMap("您好，发给" + toAgent.getRealName() + "订单已经发货！"));
        //产品数量
        String stringBuffer = items2String(note.getDeliveryItems());
        fromAgentData.put("keyword1", createItemMap(stringBuffer));
        fromAgentData.put("keyword2", createItemMap(note.getLogistics()));
        fromAgentData.put("keyword3", createItemMap(note.getLogisticsCode()));
        fromAgentData.put("keyword4", createItemMap(note.getAddress().getAddress()));
        fromAgentData.put("remark", createItemMap("咖盟，让这个世界心生爱目!"));
        sendTemplateMessage(fromAgent.getWxOpenid(), template_id, url, fromAgentData);
        if (fromAgent.getId().equals(toAgent.getId())) return;
        //收货人
        toAgentData.put("first", createItemMap("您好," + fromAgent.getRealName() + "发给您的发货订单已经发货，请耐心等待产品到来！"));
        toAgentData.put("keyword1", createItemMap(note.getId()));
        toAgentData.put("keyword2", createItemMap(DateUtil.formatDate(note.getUpdateTime())));
        fromAgentData.put("keyword3", createItemMap(note.getLogisticsCode()));
        fromAgentData.put("keyword4", createItemMap(note.getAddress().getProvince() + note.getAddress().getCity() + note.getAddress().getCounty() + note.getAddress().getAddress()));
        toAgentData.put("remark", createItemMap("咖盟，让这个世界心生爱目!"));
        sendTemplateMessage(toAgent.getWxOpenid(), template_id, url, toAgentData);
    }


    @Override
    public void noticeOrder(Order order) {
        Agent toAgent = order.getUser();
        AddressClone addressClone = order.getAddressClone();
        String name = addressClone.getConsignee();
        String template_id = "mBng3sOfkaRDy9e1mHhIsXxQx2iuSDcVX76EBDAABx8";
        String url = "http://ht.kam365.com/kam/pm/order/myOrder.html";
        Map<String, Object> fromAgentData = new HashedMap();
        //出货人
        fromAgentData.put("first", createItemMap("您好，您发给" + name + "订单已经发货！"));
        //产品数量
        String stringBuffer = items2StringOrder(order.getItems());
        fromAgentData.put("keyword1", createItemMap(stringBuffer));
        fromAgentData.put("keyword2", createItemMap(order.getLogistics()));
        fromAgentData.put("keyword3", createItemMap(order.getLogisticsCode()));
        fromAgentData.put("keyword4", createItemMap(addressClone.getProvince() + addressClone.getCity() + addressClone.getCounty() + addressClone.getAddress()));
        fromAgentData.put("remark", createItemMap("咖盟，让这个世界心生爱目!"));
        sendTemplateMessage(toAgent.getWxOpenid(), template_id, url, fromAgentData);
    }

    @Override
    public void noticeNewUser(Agent agent) {
        String template_id = "FdYs3cLbTY7YZFoY179p8tfFQbrIMoEqpYmh7wXT-YA";
        String url = "http://ht.kam365.com/kam/mobile/contacts.html";
        Map<String, Object> data = new HashedMap();
        data.put("first", createItemMap("您好，您新增了一位新的客户，请跟进服务！"));
        data.put("keyword1", createItemMap(agent.getRealName()));
        data.put("keyword2", createItemMap(DateUtil.formatDate(new Date())));
        data.put("remark", createItemMap("咖盟，让这个世界心生爱目!"));
        sendTemplateMessage(agent.getParent().getWxOpenid(), template_id, url, data);
    }

    private String items2String(Set<DeliveryItem> items) {
        StringBuffer stringBuffer = new StringBuffer();
        items.stream().forEach(p -> {
                    stringBuffer.append(p.getGoods().getName());
                    stringBuffer.append("X");
                    stringBuffer.append(p.getQuantity());
                    stringBuffer.append("\r\n");
                }
        );
        return stringBuffer.toString();
    }

    private String items2StringOrder(Set<OrderItem> items) {
        StringBuffer stringBuffer = new StringBuffer();
        items.stream().forEach(p -> {
                    stringBuffer.append(p.getPmallGoods().getName());
                    stringBuffer.append("X");
                    stringBuffer.append(p.getQuantity());
                    stringBuffer.append("\r\n");
                }
        );
        return stringBuffer.toString();
    }


    private void sendTemplateMessage(String openid, String template_id, String url, Map<String, Object> data) {
        Runnable runnable = () -> {
            try {
                SdkResult sdkResult = JSAPI.sendTemplateMessage(TokenInfo.getAccessToken(wxConfigFactory.getBaseConfig().getAppid()), openid, template_id, url, data);
                if (!sdkResult.isSuccess()) {
                    logger.error("通知失败内容,{},{},{}", data.toString(), sdkResult.getError(), openid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private Map createItemMap(Object value) {
        Map map = new HashedMap();
        map.put("value", value + "\r\n");
        map.put("color", "#000000");
        return map;
    }


}
