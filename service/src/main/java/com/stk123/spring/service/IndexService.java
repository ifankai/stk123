package com.stk123.spring.service;

import com.stk123.spring.dto.StkDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndexService extends BaseService {

    @Transactional
    public List<StkDto> findStkByCode(List<String> codes) {
        //String sql = "select code, name from Stk where code in ('"+ StringUtils.join(codes,"','")+"')";

        List<String> sqlList = new ArrayList<String>();
        for(String code : codes){
            sqlList.add("select code, name from stk where code='"+code+"'");
        }
        String sql = StringUtils.join(sqlList, " union all ");
        //Query query = em.createNativeQuery(sql, StkDto.class);
        //return query.getResultList();
        return list(sql, StkDto.class);
    }
}
