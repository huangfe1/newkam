package com.dreamer.view.goods;

import com.dreamer.domain.mall.securityCode.CodePrefix;
import com.dreamer.domain.mall.securityCode.CodeSegment;
import com.dreamer.domain.mall.securityCode.SecurityCode;
import com.dreamer.domain.user.User;
import com.dreamer.repository.goods.SecurityCodeDAO;
import com.dreamer.service.goods.SecurityCodeHandler;
import com.dreamer.service.mobile.CodeHandler;
import com.dreamer.service.mobile.CodePrefixHandler;
import com.dreamer.service.mobile.CodeSegmentHandler;
import com.dreamer.util.ExcelFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ps.mx.otter.exception.ApplicationException;
import ps.mx.otter.exception.NotAuthorizationException;
import ps.mx.otter.utils.WebUtil;
import ps.mx.otter.utils.message.Message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@RestController
@RequestMapping("/securityCode")
public class SecurityCodeController {


    @RequestMapping(value = "/edit.json", method = RequestMethod.POST)
    public Message edit(
            @ModelAttribute("parameter") SecurityCode parameter,
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam("codeSegment") String codeSegment,
            HttpServletRequest request, Model model) {
        try {
            User user = (User) WebUtil.getCurrentUser(request);
            if (null != date) {
                long H = (long) (Math.random() * 10 + 8);
                long M = (long) (Math.random() * 60);
                long S = (long) (Math.random() * 60);
                date.setTime(date.getTime() + H * 1000 * 60 * 60 + M * 1000 * 60 + S * 1000);
                parameter.setUpdateTime(date);
            }
            if (parameter.getVersion() == null) {
                securityCodeHandler.addCodeSegment(user, parameter, codeSegment);
            } else {
                securityCodeHandler.updateSegment(user, parameter, codeSegment);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            LOG.error("防伪码保存失败,", exp);
            return Message.createFailedMessage(exp.getMessage());
        }
        return Message.createSuccessMessage();
    }

    //码前缀编辑
    @RequestMapping("/prefix/edit.json")
    public Message prefix_edit(CodePrefix codePrefix) {
        try {
            codePrefixHandler.merge(codePrefix);
            return Message.createSuccessMessage();
        } catch (Exception e) {
            return Message.createFailedMessage(e.getMessage());
        }
    }

    //码前缀删除
    @RequestMapping("/prefix/remove.json")
    public Message prefix_remove(CodePrefix codePrefix) {
        try {
            codePrefixHandler.delete(codePrefix);
            return Message.createSuccessMessage();
        } catch (Exception e) {
            return Message.createFailedMessage(e.getMessage());
        }
    }

    //生成码段，提供下载
    @RequestMapping("/code/create.json")
    public void code_create(CodeSegment codeSegment, HttpServletResponse response, @RequestParam Integer pid) throws IOException {
        CodePrefix codePrefix = codePrefixHandler.get(pid);
        codeSegment.setPrefix(codePrefix.getUrl());
        List<String> ss = new ArrayList<>();
        ss.add("小码");
        ss.add("大码");
        List<List> hs = new ArrayList<>();
        List<List<Map>> ds = new ArrayList<>();
        Integer tem = 10;
        if (codeSegment.getCodeNumber() != null && codeSegment.getCodeNumber() > 0) {//需要生成小码
            Integer last = codeSegmentHandler.findLastestCode() == null ? 0 : codeSegmentHandler.findLastestCode();
            Integer startCode = last + tem;
            Integer endCode = startCode + codeSegment.getCodeNumber();
            codeSegment.setStartCode(startCode);
            codeSegment.setEndCode(endCode);
            List<String> headers = new ArrayList<>();
            headers.add("小码");
            headers.add("链接");
            List<Map> datas = new ArrayList<>();
            Map m = null;
            for (int i = 0; i < codeSegment.getCodeNumber(); i++) {
                m = new HashMap();
                m.put(0, startCode + i);
                m.put(1, codePrefix.getUrl() + "?" + codePrefix.getCp() + "=" + (startCode + i));
                datas.add(m);
            }
            hs.add(headers);
            ds.add(datas);
        }

        if (codeSegment.getBoxNumber() != null && codeSegment.getBoxNumber() > 0) {//需要生成大码
            Integer last = codeSegmentHandler.findLastestBox() == null ? 0 : codeSegmentHandler.findLastestBox();
            Integer startBox = last + tem;
            Integer endBox = startBox + codeSegment.getBoxNumber();
            codeSegment.setStartBox(startBox);
            codeSegment.setEndBox(endBox);
            List<String> headers = new ArrayList<>();
            headers.add("大码");
            headers.add("链接");
            List<Map> datas = new ArrayList<>();
            Map m = null;
            for (int i = 0; i < codeSegment.getBoxNumber(); i++) {
                m = new HashMap();
                m.put(0, startBox + i);
                m.put(1, codePrefix.getUrl() + "?" + codePrefix.getBp() + "=" + (startBox + i));
                datas.add(m);
            }
            hs.add(headers);
            ds.add(datas);
        }
        codeSegment.setDate(new Date());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        try {
            if (!ds.isEmpty()) {
                codeSegmentHandler.merge(codeSegment);
                ExcelFile.ExpExs("", ss, hs, ds, response);//创建表格并写入
            } else {
                response.getWriter().write("错误。输入有误！码段不能都为0");
            }
        } catch (Exception e) {
            response.getWriter().write("错误。请联系管理员！");
            e.printStackTrace();
        }
    }


    //大小码绑定
    @RequestMapping("/dmz/code/bind.json")
    public Message code_bind(Integer perBox, @RequestParam(required = false) List<Integer> boxs, @RequestParam(required = false) List<Integer> codes, @RequestParam(required = false) MultipartFile bfile, @RequestParam(required = false) MultipartFile cfile) throws IOException {
        try {
            //离线提交，从文件中获取大小码
            if (bfile != null && cfile != null) {
                boxs = new ArrayList<>();
                codes = new ArrayList<>();
                //获取大码
                BufferedReader reader = new BufferedReader(new InputStreamReader(bfile.getInputStream()));
                String data;
                while ((data = reader.readLine()) != null) {
                    if (!data.equals("大码号")) {
                        boxs.add(Integer.valueOf(data));
                    }
                }
                //获取小吗
                reader = new BufferedReader(new InputStreamReader(cfile.getInputStream()));
                while ((data = reader.readLine()) != null) {
                    if (!data.equals("小码号")) {
                        codes.add(Integer.valueOf(data));
                    }
                }
            }
            if (boxs.size() * perBox != codes.size()) return Message.createFailedMessage("大小码数量不匹配！");
            repateCode(codes);//小码是否有重复
            ///绑定
            codeHandler.bind(boxs, codes, perBox);
        } catch (Exception e) {
            e.printStackTrace();
            return Message.createFailedMessage("绑定失败+" + e.getMessage());
        }
        return Message.createSuccessMessage();
    }

    @RequestMapping("/code/scan.json")
    //公司或者代理扫码录入
    public Message code_scan(HttpServletRequest request, @RequestParam(required = false) List<String> boxs, @RequestParam(required = false) List<String> codes, String agentCode, @RequestParam(required = false) String goodsName) {
        try {
            User user = (User) WebUtil.getCurrentUser(request);
            codeHandler.scan(boxs, codes, agentCode, goodsName, user);
        } catch (Exception e) {
            e.printStackTrace();
            return Message.createFailedMessage(e.getMessage());
        }
        return Message.createSuccessMessage();
    }

    //大码是否有重复
    private void repateBox(List<Integer> boxs) {
        List<Object> objects = new ArrayList<>();
        boxs.stream().forEach(b -> {
            objects.add(b);
        });
        List<SecurityCode> rs = codeHandler.getListIn("box", objects);
        if (rs.size() > 0) {
            throw new ApplicationException("大码" + rs.get(0) + "已经被绑定过了!");
        }
    }

    //小码是否有重复
    private void repateCode(List<Integer> codes) {
        List<Object> objects = new ArrayList<>();
        //判定小码
        codes.stream().forEach(c -> {
            objects.add(String.valueOf(c));
        });
        List<SecurityCode> rs = codeHandler.getListIn("code", objects);
        String msg ="";
        for(SecurityCode sc : rs){
            msg += sc.getCode()+",";
        }
        if (rs.size() > 0) {
            throw new ApplicationException("小码" + msg + "已经被绑定过了!");
        }
    }


    @RequestMapping("/code/remove.json")
    public Message code_remove(Integer id) {
        CodeSegment codeSegment = codeSegmentHandler.get(id);
        try {
            codeSegmentHandler.delete(codeSegment);
            return Message.createSuccessMessage();
        } catch (Exception e) {
            return Message.createFailedMessage(e.getMessage());
        }
    }


    /**
     * 录入防伪码  扫描传过来的
     *
     * @param parameter
     * @param codeSegment
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/scanEdit.json", method = RequestMethod.POST)
    public Message scanEdit(@ModelAttribute("parameter") SecurityCode parameter, String[] codes,
                            HttpServletRequest request, Model model) {
        try {
            if (Objects.isNull(codes)) {
                throw new ApplicationException();
            }
            User user = (User) WebUtil.getCurrentUser(request);
            List<String> list = new ArrayList<>();
            for (String str : codes) {
                list.add(str);
            }
            securityCodeHandler.addCodeSegmentWithNum(user, parameter, list);

        } catch (Exception exp) {
            exp.printStackTrace();
            LOG.error("防伪码录入失败,", exp);
            return Message.createFailedMessage(exp.getMessage());
        }
        return Message.createSuccessMessage();
    }

    @RequestMapping(value = "/editWithNum.json", method = RequestMethod.POST)
    public Message editWithNum(
            @ModelAttribute("parameter") SecurityCode parameter,
            @RequestParam("codeSegment") String codeSegment,
            @RequestParam("quantity") Integer quantity,
            HttpServletRequest request, Model model) {
        try {
            if (Objects.isNull(quantity) || quantity < 1) {
                throw new ApplicationException("数量输入错误");
            }
            User user = (User) WebUtil.getCurrentUser(request);
            if (parameter.getVersion() == null) {
                securityCodeHandler.addCodeSegmentWithNum(user, parameter, codeSegment, quantity);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            LOG.error("防伪码保存失败,", exp);
            return Message.createFailedMessage(exp.getMessage());
        }
        return Message.createSuccessMessage();
    }

    @RequestMapping(value = "/remove.json", method = RequestMethod.DELETE)
    public Message remove(
            @ModelAttribute("parameter") SecurityCode parameter,
            HttpServletRequest request, Model model) {
        try {
            User user = (User) WebUtil.getCurrentUser(request);
            if (user.isAdmin()) {
                securityCodeHandler.remove(parameter);
            } else {
                throw new NotAuthorizationException("无删除防伪码权限");
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            LOG.error("防伪码删除失败,", exp);
            return Message.createFailedMessage(exp.getMessage());
        }
        return Message.createSuccessMessage();
    }

    @RequestMapping(value = "/rang-rm.json", method = RequestMethod.POST)
    public Message removeRange(
            @ModelAttribute("parameter") SecurityCode parameter,
            @RequestParam(value = "agent") String agentCode,
            @RequestParam(value = "code") String code,
            HttpServletRequest request, Model model) {
        try {
            User user = (User) WebUtil.getCurrentUser(request);
            if (user.isAdmin()) {

                parameter.setAgentCode(agentCode);
                securityCodeHandler.removeRange(parameter, code);
            } else {
                throw new NotAuthorizationException("无删除防伪码权限");
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            LOG.error("防伪码删除失败,", exp);
            return Message.createFailedMessage(exp.getMessage());
        }
        return Message.createSuccessMessage();
    }

    @RequestMapping(value = "/rang-rm-num.json", method = RequestMethod.POST)
    public Message removeRangeWithNum(
            @ModelAttribute("parameter") SecurityCode parameter,
            @RequestParam(value = "agent") String agentCode,
            @RequestParam(value = "code") String code,
            @RequestParam("quantity") Integer quantity,
            HttpServletRequest request) {
        try {
            if (Objects.isNull(quantity) || quantity < 1) {
                throw new ApplicationException("数量输入错误");
            }
            User user = (User) WebUtil.getCurrentUser(request);
            if (user.isAdmin()) {
                parameter.setAgentCode(agentCode);
                securityCodeHandler.removeRangeWithNum(parameter, code, quantity);
            } else {
                throw new NotAuthorizationException("无删除防伪码权限");
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            LOG.error("防伪码删除失败,", exp);
            return Message.createFailedMessage(exp.getMessage());
        }
        return Message.createSuccessMessage();
    }


    @ModelAttribute("parameter")
    public SecurityCode preprocess(
            @RequestParam("id") Optional<Integer> id) {
        SecurityCode code = null;
        if (id.isPresent()) {
            code = securityCodeDAO.findById(id.get());
        } else {
            code = new SecurityCode();
        }
        return code;
    }

    @Autowired
    private SecurityCodeDAO securityCodeDAO;

    @Autowired
    private SecurityCodeHandler securityCodeHandler;

    @Autowired
    private CodePrefixHandler codePrefixHandler;

    @Autowired
    private CodeSegmentHandler codeSegmentHandler;

    @Autowired
    private CodeHandler codeHandler;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

}
