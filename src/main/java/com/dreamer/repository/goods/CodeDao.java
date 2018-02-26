package com.dreamer.repository.goods;

import com.dreamer.domain.mall.securityCode.SecurityCode;
import com.dreamer.domain.user.enums.AgentStatus;
import com.dreamer.repository.mobile.BaseDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CodeDao extends BaseDaoImpl<SecurityCode> {

    public List<SecurityCode> searchByCode(String code){
        String hql = "select sc from SecurityCode as sc left join Agent  as user on user.agentCode = sc.agentCode  where (sc.code = :code or sc.box = :code) and user.agentStatus = :status";
        org.hibernate.query.Query query = currentSession().createQuery(hql);
        query.setParameter("code",code);
        query.setParameter("status", AgentStatus.ACTIVE);
        return  query.list();
    }

}
