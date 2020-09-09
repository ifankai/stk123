package com.stk123.spring.service;

import com.stk123.spring.dto.StkDto;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.junit.Assert;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexService extends BaseService {

    public List<StkDto> findStkByCode(List<String> codes) {
        Assert.assertNull(codes);
        String sql = "select code, name from Stk where code in ('"+ StringUtils.join(codes,"','")+"')";
        //Query query = em.createNativeQuery(sql, StkDto.class);
        //return query.getResultList();
        return list(sql, StkDto.class);
    }
}
