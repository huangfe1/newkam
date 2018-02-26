package com.dreamer.view.data;

import com.dreamer.domain.data.FileData;
import com.dreamer.service.mobile.FileDataHandler;
import com.dreamer.util.CheckMobile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ps.mx.otter.utils.SearchParameter;
import ps.mx.otter.utils.WebUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/fileData")
public class FileDataQueryController {

    @RequestMapping("/list.html")
    public String list(@ModelAttribute("parameter")SearchParameter<FileData> parameter, Model model, HttpServletRequest request) {
        try {
            List<FileData> datas = fileDataHandler.findBypage(parameter);
            WebUtil.turnPage(parameter, request);
            model.addAttribute("datas", datas);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String USER_AGENT = request.getHeader("USER-AGENT").toLowerCase();
        //如果是手机客户端
        if (CheckMobile.check(USER_AGENT)) {
            return "/data/mobile_list";
        }
        return "/data/list";
    }

    @RequestMapping("/edit.html")
    public String edit(@ModelAttribute("parameter") SearchParameter<FileData> parameter){
        return "/data/edit";
    }


    @ModelAttribute("parameter")
    public SearchParameter<FileData> preprocess(@RequestParam("id")Optional<Integer> id) {
        SearchParameter<FileData> parameter = new SearchParameter<>();
        if (id.isPresent()) {
            parameter.setEntity(fileDataHandler.get(id.get()));
        } else {
            parameter.setEntity(new FileData());
        }
        return parameter;
    }

    @Autowired
    private FileDataHandler fileDataHandler;

}
