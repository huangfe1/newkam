package com.dreamer.view.goods;

import com.dreamer.domain.mall.securityCode.CodePrefix;
import com.dreamer.domain.mall.securityCode.CodeSegment;
import com.dreamer.domain.mall.securityCode.SecurityCode;
import com.dreamer.domain.user.Agent;
import com.dreamer.domain.user.User;
import com.dreamer.repository.goods.SecurityCodeDAO;
import com.dreamer.service.mobile.CodePrefixHandler;
import com.dreamer.service.mobile.CodeSegmentHandler;
import com.dreamer.service.mobile.factory.WxConfigFactory;
import com.dreamer.service.pay.JsApiParameterFactory;
import com.dreamer.util.TokenInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ps.mx.otter.utils.SearchParameter;
import ps.mx.otter.utils.WebUtil;
import ps.mx.otter.utils.json.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/securityCode")
public class SecurityCodeQueryController {


    @RequestMapping(value = {"/index.html", "/search.html"}, method = RequestMethod.GET)
    public String index(
            @ModelAttribute("parameter") SearchParameter<SecurityCode> parameter,
            HttpServletRequest request, Model model) {
        try {
            User user = (User) WebUtil.getCurrentUser(request);
            List<SecurityCode> codes = null;
            if (user.isAgent()) {
                Agent agent = (Agent) user;
                parameter.getEntity().setAgentCode(agent.getAgentCode());
            }
            codes = securityCodeDAO.searchEntityByPage(parameter, null,
                    (t) -> true);

            WebUtil.turnPage(parameter, request);
            model.addAttribute("codes", codes);
        } catch (Exception exp) {
            exp.printStackTrace();
            LOG.error("防伪码查询失败", exp);
        }
        return "/goods/securityCode_index";
    }

    @RequestMapping(value = "/edit.html", method = RequestMethod.GET)
    public String edit_enter(
            @ModelAttribute("parameter") SearchParameter<SecurityCode> parameter,
            HttpServletRequest request, Model model) {

        return "/goods/securityCode_edit";
    }

    @RequestMapping(value = "/edit_num.html", method = RequestMethod.GET)
    public String edit_num_enter(
            @ModelAttribute("parameter") SearchParameter<SecurityCode> parameter,
            HttpServletRequest request, Model model) {

        return "/goods/securityCode_edit_num";
    }


    //码前缀列表
    @RequestMapping("/prefix/list.html")
    public String prefix_list(CodePrefix prefix, Model model,HttpServletRequest request) throws Exception {
        SearchParameter parameter = new SearchParameter<>(prefix);
        List<CodePrefix> codePrefixes = codePrefixHandler.findByPage(parameter);
        model.addAttribute("prefixs", codePrefixes);
        model.addAttribute("prefix", prefix);
        WebUtil.turnPage(parameter,request);
        model.addAttribute("parameter",parameter);
        return "goods/prefix_list";
    }

    //编辑码前缀
    @RequestMapping("/prefix/edit.html")
    public String prefix_edit(@RequestParam(required = false) Integer id, Model model) {
        CodePrefix codePrefix = new CodePrefix();
        if (id != null) {
            codePrefix = codePrefixHandler.get(id);
        }
        model.addAttribute("codePrefix", codePrefix);
        return "goods/prefix_edit";
    }

    //生成码段入口
    @RequestMapping("/code/create.html")
    public String code_create(CodeSegment codeSegment,@RequestParam(required = false) Integer code,Model model,HttpServletRequest request) throws Exception {
        SearchParameter<CodeSegment> parameter = new SearchParameter<>(codeSegment);
        model.addAttribute("codeSegment",codeSegment);
        List<CodeSegment> codeSegments = codeSegmentHandler.findByPage(parameter,code);
        model.addAttribute("codeSegments",codeSegments);
        WebUtil.turnPage(parameter,request);
        model.addAttribute("parameter",parameter);
        List<CodePrefix> prefixes = codePrefixHandler.findAll();
        model.addAttribute("prefixes",prefixes);
        model.addAttribute("code",code);
        return "/goods/code_create";
    }


    //公司绑定大小码入口
    @RequestMapping("/dmz/code/bind.html")
    public String companyScan() {

        return "/goods/dmz/code_bind";
    }


    /**
     * 扫码录入防伪码
     *
     * @param parameter
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/scan_num.html", method = RequestMethod.GET)
    public String scan_num_enter(
            @ModelAttribute("parameter") SearchParameter<SecurityCode> parameter,
            HttpServletRequest request, Model model) {
        String url = request.getRequestURL().toString();
        if (!Objects.isNull(request.getQueryString()) && !request.getQueryString().equals("")) {
            url += "?" + request.getQueryString();
        }
        HashMap jspmap = jsApiParameterFactory.buildWxconfig(wxConfigFactory.getBaseConfig(), url, TokenInfo.getJsapiTicket(wxConfigFactory.getBaseConfig().getAppid()));
        String jsonParam = JsonUtil.mapToJsonStr(jspmap);
        model.addAttribute("jsapiParamJson", jsonParam);
        return "/goods/code_scan";
    }

    @ModelAttribute("parameter")
    public SearchParameter<SecurityCode> preprocess(
            @RequestParam("id") Optional<Integer> id) {
        SearchParameter<SecurityCode> parameter = new SearchParameter<>();
        SecurityCode code = null;
        if (id.isPresent()) {
            code = securityCodeDAO.findById(id.get());
        } else {
            code = new SecurityCode();
        }
        parameter.setEntity(code);
        return parameter;
    }

    @Autowired
    private SecurityCodeDAO securityCodeDAO;

    @Autowired
    private JsApiParameterFactory jsApiParameterFactory;


    @Autowired
    private WxConfigFactory wxConfigFactory;

    @Autowired
    private CodePrefixHandler codePrefixHandler;

    @Autowired
    private CodeSegmentHandler codeSegmentHandler;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

}
