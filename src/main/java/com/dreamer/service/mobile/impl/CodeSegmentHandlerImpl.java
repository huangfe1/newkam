package com.dreamer.service.mobile.impl;

import com.dreamer.domain.mall.securityCode.CodeSegment;
import com.dreamer.repository.goods.CodeSegmentDao;
import com.dreamer.service.mobile.CodeSegmentHandler;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ps.mx.otter.utils.SearchParameter;

import java.util.List;

@Service
public class CodeSegmentHandlerImpl extends BaseHandlerImpl<CodeSegment> implements CodeSegmentHandler {

    @Override
    public List<CodeSegment> findByPage(SearchParameter<CodeSegment> parameter, Integer code) {
        DetachedCriteria criteria = DetachedCriteria.forClass(CodeSegment.class);
        Example example = Example.create(parameter.getEntity()).enableLike(MatchMode.ANYWHERE);
        criteria.add(example);
        if (code != null) {
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.and(Restrictions.ge("endCode", code), Restrictions.le("startCode", code)));
            disjunction.add(Restrictions.and(Restrictions.ge("endBox", code), Restrictions.le("startBox", code)));
            criteria.add(disjunction);
        }
        criteria.addOrder(Order.desc("id"));
        return codeSegmentDao.searchByPage(parameter, criteria);
    }

    @Override
    public Integer findLastestBox() {
        return codeSegmentDao.findLastestBox();
    }

    @Override
    public Integer findLastestCode() {
        return codeSegmentDao.findLastestCode();
    }

    private CodeSegmentDao codeSegmentDao;

    public CodeSegmentDao getCodeSegmentDao() {
        return codeSegmentDao;
    }

    @Autowired
    public void setCodeSegmentDao(CodeSegmentDao codeSegmentDao) {
        super.setBaseDao(codeSegmentDao);
        this.codeSegmentDao = codeSegmentDao;
    }
}
