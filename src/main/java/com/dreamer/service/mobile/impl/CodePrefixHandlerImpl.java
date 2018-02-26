package com.dreamer.service.mobile.impl;

import com.dreamer.domain.mall.securityCode.CodePrefix;
import com.dreamer.repository.goods.CodePrefixDao;
import com.dreamer.service.mobile.CodePrefixHandler;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ps.mx.otter.utils.SearchParameter;

import java.util.List;

@Service
public class CodePrefixHandlerImpl extends BaseHandlerImpl<CodePrefix> implements CodePrefixHandler {

    @Override
    public List<CodePrefix> findByPage(SearchParameter<CodePrefix> parameter) {
        DetachedCriteria criteria = DetachedCriteria.forClass(CodePrefix.class);
        Example example = Example.create(parameter.getEntity()).enableLike(MatchMode.ANYWHERE);
        criteria.add(example);
        return codePrefixDao.searchByPage(parameter,criteria);
    }

    private CodePrefixDao codePrefixDao;

    public CodePrefixDao getCodePrefixDao() {
        return codePrefixDao;
    }

    @Autowired
    public void setCodePrefixDao(CodePrefixDao codePrefixDao) {
        super.setBaseDao(codePrefixDao);
        this.codePrefixDao = codePrefixDao;
    }
}
