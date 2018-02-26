package com.dreamer.view.goods;

import com.dreamer.domain.mall.securityCode.SecurityCode;
import com.dreamer.service.mobile.AgentHandler;
import com.dreamer.service.mobile.CodeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/dmz/securityCode")
public class SecurityCodeDMZController {

    @RequestMapping(value = {"/index.html", "/search.html"}, method = RequestMethod.GET)
    public String index(@RequestParam(required = false) String code, @RequestParam(required = false) String boxCode, Model model) {
        try {
            List<SecurityCode> codes = new ArrayList<>();
            if (code != null) {
                code = code.replaceFirst("^0*", "");//去掉前缀0
                codes = codeHandler.searchByCode(code);
            } else if (boxCode != null) {
                boxCode = boxCode.replaceFirst("^0*", "");//去掉前缀0
                codes = codeHandler.searchByCode(boxCode);
            }
//            List<SecurityCode> tems = new ArrayList<>();
//            for (SecurityCode tem : codes) {
//                Agent agent_temp = agentHandler.get("agentCode", tem.getAgentCode());
//                //移除公司的
//                if (agent_temp.getAgentStatus() != AgentStatus.ACTIVE || tem.getAgentCode().equals("01")){
//
//                }else {
//                    tems.add(tem);
//                }
//            }
            model.addAttribute("codes", codes);//返回筛选后的
        } catch (Exception exp) {
            exp.printStackTrace();
            LOG.error("防伪码查询失败", exp);
        }
        return "goods/dmz/securityCode_search";
    }


    @ModelAttribute("parameter")
    public SecurityCode preprocess() {
        SecurityCode parameter = new SecurityCode();
        return parameter;
    }


    @Autowired
    private AgentHandler agentHandler;

    @Autowired
    private CodeHandler codeHandler;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

}
