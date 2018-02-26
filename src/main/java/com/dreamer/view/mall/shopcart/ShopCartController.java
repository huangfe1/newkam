package com.dreamer.view.mall.shopcart;

import com.dreamer.domain.inter.Country;
import com.dreamer.domain.mall.goods.Goods;
import com.dreamer.domain.mall.goods.GoodsType;
import com.dreamer.domain.mall.shopcart.ShopCart;
import com.dreamer.domain.user.Agent;
import com.dreamer.repository.goods.GoodsDAO;
import com.dreamer.repository.user.AgentDAO;
import com.dreamer.service.inter.CountryHandler;
import com.dreamer.service.inter.CountryPriceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ps.mx.otter.exception.DataNotFoundException;
import ps.mx.otter.utils.WebUtil;
import ps.mx.otter.utils.message.Message;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequestMapping({"/vmall/shopcart", "/tmall/shopcart"})
@SessionAttributes({"indexUrl", "ref"})
public class ShopCartController {

    private static final String VCART = "shopcart";

    private static final String TCART = "tshopcart";


    /**
     * 获取不同的Cart
     *
     * @param goodsType
     * @return
     */
    private String getCartFromType(GoodsType goodsType) {
        if (goodsType == GoodsType.TEQ) {
            return TCART;
        }
        return VCART;
    }

    @RequestMapping(value = {"/add.json", "/put.json"}, method = RequestMethod.POST)
    public Message addGoodsToShopcart(
            @RequestParam("goodsId") Integer goodsId,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            HttpServletRequest request) {
        try {
            Agent agentUser = (Agent) WebUtil.getCurrentUser(request);
            Agent agent = agentDAO.findById(agentUser.getId());
            Goods goods = goodsDAO.findById(goodsId);
            String CART = getCartFromType(goods.getGoodsType());
            if (Objects.isNull(goods)) {
                throw new DataNotFoundException("商品不存在");
            }
            Object ob = WebUtil.getSessionAttribute(request, CART);
            Integer addQuantity = Objects.nonNull(quantity) ? quantity : 1, result;
            ShopCart cart;
            if (Objects.nonNull(ob)) {
                cart = (ShopCart) ob;
                if (quantity != null) {//put 覆盖
                    cart.removeItems(goods);
                }
            } else {
                cart = new ShopCart();
            }
            Double price = agent.getMainLevelPrice(goods).getPrice();
            //首先查看查看是否传入cid
            Integer cid = (Integer) WebUtil.getSessionAttribute(request, "cid");
            if (cid != null && cid > 0) {//如果存在 需要加入国际差价
                Country country = countryHandler.get(cid);
                //获取某个产品国家的差价
                Double tem = countryPriceHandler.getPrice(goods, country).getPrice();
                price += tem;
                goods.setRetailPrice(goods.getRetailPrice()+tem);//原价也要加
            }
            cart.addGoods(goods, addQuantity, price);
            result = cart.getQuantity();
            WebUtil.addSessionAttribute(request, CART, cart);
            return Message.createSuccessMessage("OK", String.valueOf(result));
        } catch (Exception exp) {
            exp.printStackTrace();
            return Message.createFailedMessage(exp.getMessage());
        }
    }


    @RequestMapping(value = "/mins.json", method = RequestMethod.POST)
    public Message minsGoodsToShopcart(
            @RequestParam("goodsId") Integer goodsId, HttpServletRequest request) {
        try {
            Goods goods = goodsDAO.findById(goodsId);
            String CART = getCartFromType(goods.getGoodsType());
            if (Objects.isNull(goods)) {
                throw new DataNotFoundException("商品不存在");
            }
            Object ob = WebUtil.getSessionAttribute(request, CART);
            ShopCart cart;
            if (Objects.nonNull(ob)) {
                cart = (ShopCart) ob;
                cart.removeGoods(goods, 1);
                WebUtil.addSessionAttribute(request, CART, cart);
            }

            return Message.createSuccessMessage();
        } catch (Exception exp) {
            exp.printStackTrace();
            return Message.createFailedMessage(exp.getMessage());
        }
    }

    @RequestMapping(value = "/remove.json", method = RequestMethod.POST)
    public Message removeGoodsToShopcart(
            @RequestParam("goodsId") Integer goodsId, HttpServletRequest request) {
        try {
            Goods goods = goodsDAO.findById(goodsId);
            String CART = getCartFromType(goods.getGoodsType());
            if (Objects.isNull(goods)) {
                throw new DataNotFoundException("商品不存在");
            }
            Object ob = WebUtil.getSessionAttribute(request, CART);
            if (Objects.nonNull(ob)) {
                ShopCart cart = (ShopCart) ob;
                cart.removeItems(goods);
                WebUtil.addSessionAttribute(request, CART, cart);
            }
            return Message.createSuccessMessage();
        } catch (Exception exp) {
            exp.printStackTrace();
            return Message.createFailedMessage(exp.getMessage());
        }
    }


    @Autowired
    private GoodsDAO goodsDAO;

    @Autowired
    private AgentDAO agentDAO;

    @Autowired
    private CountryHandler countryHandler;

    @Autowired
    private CountryPriceHandler countryPriceHandler;


}
