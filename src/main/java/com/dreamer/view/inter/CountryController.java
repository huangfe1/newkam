package com.dreamer.view.inter;

import com.dreamer.domain.inter.Country;
import com.dreamer.service.inter.CountryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ps.mx.otter.utils.message.Message;

import java.util.Optional;

@RestController
@RequestMapping("/inter/country")
public class CountryController {


    @RequestMapping(value = "/edit.json", method = RequestMethod.POST)
    public Message edit(@ModelAttribute("parameter") Country parameter,String agentCode) {
        try {
            countryHandler.merge(agentCode,parameter);
            return Message.createSuccessMessage();
        } catch (Exception exp) {
            return Message.createFailedMessage(exp.getMessage());
        }
    }

    @RequestMapping(value = "/remove.json",method=RequestMethod.DELETE)
    public Message remove(@ModelAttribute("parameter") Country parameter) {
        try {
            countryHandler.delete(parameter);
            return Message.createSuccessMessage();
        } catch (Exception exp) {
            exp.printStackTrace();
            return Message.createFailedMessage(exp.getMessage());
        }
    }

    @ModelAttribute("parameter")
    public Country preprocess(@RequestParam("id") Optional<Integer> id) {
        Country country = null;
        if (id.isPresent()) {
            country = countryHandler.get(id.get());
        } else {
            country = new Country();
        }
        return country;
    }


    @Autowired
    private CountryHandler countryHandler;

}
