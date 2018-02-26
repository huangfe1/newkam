package com.dreamer.repository.goods;

import com.dreamer.domain.mall.securityCode.CodeSegment;
import com.dreamer.repository.mobile.BaseDaoImpl;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class CodeSegmentDao extends BaseDaoImpl<CodeSegment> {

    public Integer findLastestBox() {
        String hql = "select cs.endBox from CodeSegment cs where cs.endBox!=null order by cs.id desc";
        Query query = currentSession().createQuery(hql);
        query.setMaxResults(1);
        Integer result = (Integer) query.uniqueResult();
        return result;
    }

    public Integer findLastestCode(){
        String hql = "select cs.endCode from CodeSegment cs where cs.endCode!=null order by cs.id desc";
        Query query = currentSession().createQuery(hql);
        query.setMaxResults(1);
        Integer result = (Integer) query.uniqueResult();
        return result;
    }
}