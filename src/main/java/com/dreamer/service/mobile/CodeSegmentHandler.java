package com.dreamer.service.mobile;

import com.dreamer.domain.mall.securityCode.CodePrefix;
import ps.mx.otter.utils.SearchParameter;

import java.util.List;


public interface CodePrefixHandler extends BaseHandler<CodePrefix>{

    List<CodePrefix> findByPage(SearchParameter<CodePrefix> parameter);

}
