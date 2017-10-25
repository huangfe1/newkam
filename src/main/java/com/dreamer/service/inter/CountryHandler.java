package com.dreamer.service.inter;

import com.dreamer.domain.inter.Country;
import com.dreamer.service.mobile.BaseHandler;
import ps.mx.otter.utils.SearchParameter;

import java.util.List;

public interface CountryHandler extends BaseHandler<Country> {


    List<Country> findByPage(SearchParameter<Country> parameter);



    Country merge(String agentCode,Country country);

}
