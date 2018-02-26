package com.dreamer.service.mobile;

import com.dreamer.domain.mall.securityCode.SecurityCode;
import com.dreamer.domain.user.User;

import java.util.List;

public interface CodeHandler extends BaseHandler<SecurityCode> {

    void bind(List<Integer> boxs,List<Integer> codes,Integer perBox);

    void scan(List<String> boxs, List<String> codes, String agentCode,String goodsName, User user);

    List<SecurityCode> searchByCode(String code);//通过大小码查询

}
