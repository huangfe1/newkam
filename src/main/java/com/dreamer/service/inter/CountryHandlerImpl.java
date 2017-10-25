package com.dreamer.service.inter;

import com.dreamer.domain.inter.Country;
import com.dreamer.domain.user.Agent;
import com.dreamer.repository.inter.CountryDao;
import com.dreamer.service.mobile.AgentHandler;
import com.dreamer.service.mobile.impl.BaseHandlerImpl;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.mx.otter.exception.ApplicationException;
import ps.mx.otter.utils.SearchParameter;

import java.util.List;

@Service
public class CountryHandlerImpl extends BaseHandlerImpl<Country> implements CountryHandler{

    @Override
    public List<Country> findByPage(SearchParameter<Country> parameter) {
        DetachedCriteria criteria = DetachedCriteria.forClass(parameter.getEntity().getClass());
        return countryDao.searchByPage(parameter,criteria);
    }

    @Override
    @Transactional
    public Country merge(String agentCode, Country country) {
        Agent agent = agentHandler.get("agentCode",agentCode);
        if(agent==null){
           throw new ApplicationException("代理对应的编号不存在");
        }
        country.setAgent(agent);
        return countryDao.merge(country);
    }

    private CountryDao countryDao;

    @Autowired
    private AgentHandler agentHandler;

    public CountryDao getCountryDao() {
        return countryDao;
    }

    @Autowired
    public void setCountryDao(CountryDao countryDao) {
        super.setBaseDao(countryDao);
        this.countryDao = countryDao;
    }
}
