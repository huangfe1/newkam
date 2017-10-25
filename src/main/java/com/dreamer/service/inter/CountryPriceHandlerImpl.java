package com.dreamer.service.inter;

import com.dreamer.domain.inter.Country;
import com.dreamer.domain.inter.CountryPrice;
import com.dreamer.domain.mall.goods.Goods;
import com.dreamer.repository.inter.CountryPriceDao;
import com.dreamer.service.mobile.impl.BaseHandlerImpl;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CountryPriceHandlerImpl extends BaseHandlerImpl<CountryPrice> implements CountryPriceHandler{


    @Override
    public CountryPrice getPrice(Goods goods, Country country) {
        Map map = new HashedMap();
        map.put("goods.id",goods.getId());
        map.put("country.id",country.getId());
        return countryPriceDao.get(map);
    }

    private CountryPriceDao countryPriceDao;

    public CountryPriceDao getCountryPriceDao() {
        return countryPriceDao;
    }

    @Autowired
    public void setCountryPriceDao(CountryPriceDao countryPriceDao) {
        super.setBaseDao(countryPriceDao);
        this.countryPriceDao = countryPriceDao;
    }
}
