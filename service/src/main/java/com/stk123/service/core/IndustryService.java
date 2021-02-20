package com.stk123.service.core;

import com.stk123.model.projection.IndustryProjection;
import com.stk123.repository.StkIndustryRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class IndustryService {

    public final static String DEFAULT_SOURCES = "csindex_zz,csindex_zjh,10jqka_gn,10jqka_thshy";

    @Autowired
    private StkIndustryRepository stkIndustryRepository;

    @Transactional
    public Map<String, List<IndustryProjection>> findAllToMap(){
        Map<String, List<IndustryProjection>> results = new HashMap<>();
        List<IndustryProjection> all = stkIndustryRepository.findAllBySource(Arrays.asList(StringUtils.split(DEFAULT_SOURCES, ",")));
        for(IndustryProjection projection : all){
            List<IndustryProjection> list = results.get(projection.getCode());
            if(list == null){
                list = new ArrayList<>();
                list.add(projection);
                results.put(projection.getCode(), list);
            }else {
                list.add(projection);
            }
        }
        return results;
    }
}
