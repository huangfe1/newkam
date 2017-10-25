package com.dreamer.service.inter;

import com.dreamer.domain.inter.Country;
import com.dreamer.domain.inter.CountryPrice;
import com.dreamer.domain.mall.goods.Goods;
import com.dreamer.service.mobile.BaseHandler;

public interface CountryPriceHandler extends BaseHandler<CountryPrice>  {

    CountryPrice getPrice(Goods goods, Country country);


}
