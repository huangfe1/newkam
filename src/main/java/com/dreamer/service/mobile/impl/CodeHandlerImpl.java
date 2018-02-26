package com.dreamer.service.mobile.impl;

import com.dreamer.domain.mall.securityCode.SecurityCode;
import com.dreamer.domain.user.Agent;
import com.dreamer.domain.user.User;
import com.dreamer.repository.goods.CodeDao;
import com.dreamer.service.mobile.AgentHandler;
import com.dreamer.service.mobile.CodeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.mx.otter.exception.ApplicationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CodeHandlerImpl extends BaseHandlerImpl<SecurityCode> implements CodeHandler {

    //大小码绑定
    @Override
    @Transactional
    public void bind(List<Integer> boxs, List<Integer> codes, Integer perBox) {
        for (int i = 0; i < codes.size(); i++) {
            String code = String.valueOf(codes.get(i));
            String box = String.valueOf(boxs.get(i / perBox));
            SecurityCode securityCode = new SecurityCode();
            securityCode.setAgentCode("01");//设置为公司
            securityCode.setCode(code);
            securityCode.setBox(box);
            securityCode.setUpdateTime(new Date());
            securityCode.setOwner("公司");
            securityCode.setRecorder("绑定人员");
            securityCode.setGoodsName("暂未绑定");
            codeDao.merge(securityCode);
        }
    }

    @Override
    public List<SecurityCode> searchByCode(String code) {
        return codeDao.searchByCode(code);
    }

    //扫码录入
    @Override
    @Transactional
    public void scan(List<String> boxs, List<String> codes, String agentCode, String goodsName, User user) {
        Agent toAgent = agentHandler.get("agentCode", agentCode);
        List<Object> objects = new ArrayList<>();
        List<SecurityCode> securityCodes = new ArrayList<>();
        //如果是大码
        if (boxs != null && boxs.size() != 0) {
            boxs.forEach(b -> objects.add(b));
            securityCodes = codeDao.getListIn("box", objects);
            if(securityCodes.isEmpty())throw new ApplicationException("大码没有任何小码关联，请重新绑定，或者直接录入小码");
//            System.out.println("_++++");
        }
        //如果是小码
        if (codes != null && codes.size() != 0) {
            codes.forEach(c -> objects.add(c));
            securityCodes = codeDao.getListIn("code", objects);
        }
//        System.out.println("--dd");
        if (user.isAdmin() || user.isMutedUser()) {//公司录入
            //修改码属性
            securityCodes.forEach(c -> {
                c.setOwner(toAgent.getRealName());
                c.setRecorder(user.getRealName());
                c.setUpdateTime(new Date());
                c.setAgentCode(toAgent.getAgentCode());
                c.setGoodsName(goodsName);//修改产品名字
                codeDao.merge(c);//修改
            });
        } else {//代理录入
            Agent fromAgent = agentHandler.get(user.getId());
            if (toAgent == null) {
                throw new ApplicationException("要绑定的代理不存在");
            }
            //只能录入给自己的代理
            if (!toAgent.getParent().getId().equals(user.getId())) {
                throw new ApplicationException("要绑定的代理不是自己的直接代理，不能录入！");
            }
            //大小码要分开录入
            if (boxs != null && boxs.size() != 0 && codes != null && codes.size() != 0) {
                throw new ApplicationException("大码小码请分开录入!");
            }
            //修改码属性
            securityCodes.forEach(c -> {
                //只修改属于自己的码
                if (c.getAgentCode().equals(fromAgent.getAgentCode())) {
                    c.setOwner(toAgent.getRealName());
                    String tem ="";
                    if(c.getRecorder()!=null)tem=c.getRecorder();
                    c.setRecorder(tem+"_"+user.getRealName());//录入者加入
                    c.setUpdateTime(new Date());
                    c.setAgentCode(toAgent.getAgentCode());
                    codeDao.merge(c);//修改
                }
            });
        }

    }

    @Autowired
    private AgentHandler agentHandler;

    private CodeDao codeDao;

    public CodeDao getCodeDao() {
        return codeDao;
    }

    @Autowired
    public void setCodeDao(CodeDao codeDao) {
        super.setBaseDao(codeDao);
        this.codeDao = codeDao;
    }
}
