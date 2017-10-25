package com.dreamer.view.inter;

import com.dreamer.domain.inter.Country;
import com.dreamer.domain.user.AgentLevel;
import com.dreamer.service.inter.CountryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ps.mx.otter.utils.SearchParameter;
import ps.mx.otter.utils.WebUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/inter/country")
public class CountryQueryController {

    @RequestMapping(value = { "/index.html", "/search.html" }, method = RequestMethod.GET)
    public String index(
            @ModelAttribute("parameter") SearchParameter<Country> parameter,
            Model model, HttpServletRequest request) {
        try {
            List<Country> countries = countryHandler.findByPage(parameter);
            WebUtil.turnPage(parameter, request);

            model.addAttribute("countries", countries);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return "/inter/country_index";
    }

    @RequestMapping(value = "/edit.html", method = RequestMethod.GET)
    public String edit_enter(
            @ModelAttribute("parameter") SearchParameter<AgentLevel> parameter) {
//        model.addAttribute("types", GoodsType.values());//等级类型
        return "/inter/country_edit";
    }



    @ModelAttribute("parameter")
    public SearchParameter<Country> preprocess(@RequestParam("id") Optional<Integer> id) {
        Country country ;
        if (id.isPresent()) {
            country = countryHandler.get(id.get());
        } else {
            country = new Country();
        }
        return new SearchParameter<>(country);
    }

    @Autowired
    private CountryHandler countryHandler;

}
