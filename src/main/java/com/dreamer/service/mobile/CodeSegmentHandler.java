package com.dreamer.service.mobile;

import com.dreamer.domain.mall.securityCode.CodeSegment;
import ps.mx.otter.utils.SearchParameter;

import java.util.List;


public interface CodeSegmentHandler extends BaseHandler<CodeSegment>{

    List<CodeSegment> findByPage(SearchParameter<CodeSegment> parameter,Integer code);

    Integer findLastestBox();

    Integer findLastestCode();


}
