package com.dreamer.view.goods;

import com.dreamer.domain.mall.shopcart.CartItem;
import com.dreamer.domain.mall.shopcart.ShopCart;
import com.dreamer.domain.mall.transfer.Transfer;
import com.dreamer.domain.user.AddressMy;
import com.dreamer.domain.user.Agent;
import com.dreamer.domain.user.User;
import com.dreamer.service.mobile.AgentHandler;
import com.dreamer.service.mobile.MuteUserHandler;
import com.dreamer.service.mobile.TransferHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ps.mx.otter.exception.ApplicationException;
import ps.mx.otter.utils.WebUtil;
import ps.mx.otter.utils.message.Message;

import javax.servlet.http.HttpServletRequest;
import java.util.Map.Entry;
import java.util.Optional;

@RestController
@RequestMapping("/transfer")
public class TransferController {


    /**
     * 主动转货
     */
    @RequestMapping(value = "/to.json", method = RequestMethod.POST)
    @ResponseBody
    public Message transferTo(String fromName, String fromCode, String toName, String toCode, Integer[] goodsIds, Integer[] quantitys, String remark, HttpServletRequest request) {
        try {
            User user = (User) WebUtil.getCurrentUser(request);
            Agent fromAgent;
            if (user.isAdmin()) {
                fromAgent = muteUserHandler.getMuteUser();
                if (!fromAgent.getAgentCode().equals(fromCode)) {//不是管理员，修改了账号
                    fromAgent = agentHandler.findByAgentCodeOrId(fromCode);
                    if (!fromAgent.getRealName().equals(fromName)) {
                        throw new ApplicationException("输入的转货人账号与名字不匹配");
                    }
                }
            } else {//不是管理员 转出人就是自己
                fromAgent = agentHandler.get(user.getId());
            }
            Agent toAgent = agentHandler.findByAgentCodeOrId(toCode);
            if (!toAgent.getRealName().equals(toName)) {
                throw new ApplicationException("输入的收货人编号与名字不匹配");
            }
            //转货
            transferHandler.transfer(fromAgent.getId(), toAgent.getId(), goodsIds, quantitys, remark);
            return Message.createSuccessMessage();
        } catch (Exception exp) {
            exp.printStackTrace();
            LOG.error("转出货物失败", exp);
            return Message.createFailedMessage(exp.getMessage());
        }
    }


    /**
     * 代金券+进货券自动购买
     *
     * @return
     */
    @RequestMapping("/autoConfirm.json")
    @ResponseBody
    public Message transferAutoConfirm(HttpServletRequest request, String remark) {
        try {
            ShopCart cart = (ShopCart) WebUtil.getSessionAttribute(request, TCART);
            //初始化
            if (cart.getItems().isEmpty()) {
                return Message.createFailedMessage("购物车为空！");
            }
            Integer[] goodsIds = new Integer[cart.getItems().size()];
            Integer[] quantitys = new Integer[cart.getItems().size()];
            //封装赋值
            int i = 0;
            for (Entry<Integer, CartItem> entry : cart.getItems().entrySet()) {
                goodsIds[i] = entry.getKey();
                quantitys[i] = entry.getValue().getQuantity();
                i++;
            }
            if (goodsIds == null || goodsIds.length == 0) {
                throw new ApplicationException("未指定申请转货的货物");
            }
            User user = (User) WebUtil.getCurrentUser(request);
            Agent fromAgent = muteUserHandler.getMuteUser();

            //扣钱转货操作
            transferHandler.transferAutoConfirm(fromAgent.getId(), user.getId(), goodsIds, quantitys, remark,null);
            WebUtil.removeSessionAttribute(request, TCART);
            return Message.createSuccessMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return Message.createFailedMessage(e.getMessage());
        }
    }


    /**
     * 代金券+进货券自动购买
     *
     * @return
     */
    @RequestMapping("/delivery.json")
    @ResponseBody
    public Message delivery(AddressMy address, HttpServletRequest request, String remark) {
        try {
            ShopCart cart = (ShopCart) WebUtil.getSessionAttribute(request, TCART);
            //初始化
            if (cart.getItems().isEmpty()) {
                return Message.createFailedMessage("购物车为空！");
            }
            Integer[] goodsIds = new Integer[cart.getItems().size()];
            Integer[] quantitys = new Integer[cart.getItems().size()];
            //封装赋值
            int i = 0;
            for (Entry<Integer, CartItem> entry : cart.getItems().entrySet()) {
                goodsIds[i] = entry.getKey();
                quantitys[i] = entry.getValue().getQuantity();
                i++;
            }
            if (goodsIds == null || goodsIds.length == 0) {
                throw new ApplicationException("未指定申请转货的货物");
            }
            User user = (User) WebUtil.getCurrentUser(request);
            Agent fromAgent = muteUserHandler.getMuteUser();

            //扣钱转货操作 发货
            transferHandler.transferAutoConfirmAndDelivery(address, fromAgent.getId(), user.getId(), goodsIds, quantitys, remark);
            WebUtil.removeSessionAttribute(request, TCART);
            return Message.createSuccessMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return Message.createFailedMessage(e.getMessage());
        }
    }



    @ModelAttribute("parameter")
    public Transfer preprocess(@RequestParam("id") Optional<Integer> id) {
        Transfer parameter;
        if (id.isPresent()) {
            parameter = transferHandler.get(id.get());
        } else {
            parameter = new Transfer();
        }
        return parameter;
    }


    /**
     */
    private static final String TCART = "tshopcart";


    @Autowired
    private AgentHandler agentHandler;

    @Autowired
    private MuteUserHandler muteUserHandler;

    @Autowired
    private TransferHandler transferHandler;


    private final Logger LOG = LoggerFactory.getLogger(getClass());


}
