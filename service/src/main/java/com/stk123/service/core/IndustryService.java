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

    public final static String SOURCE_EASTMONEY_GN = "eastmoney_gn";

    public final static String DEFAULT_SOURCES = "csindex_zz,csindex_zjh,10jqka_gn,10jqka_thshy,eastmoney_gn";

    @Autowired
    private StkIndustryRepository stkIndustryRepository;

    @Transactional
    public Map<String, List<IndustryProjection>> findAllToMap(List<String> codes){
        List<IndustryProjection> all = stkIndustryRepository.findAllByCodeAndSource(codes, Arrays.asList(StringUtils.split(SOURCE_EASTMONEY_GN, ",")));
        return toMap(all);
    }

    @Transactional
    public Map<String, List<IndustryProjection>> findAllToMap(){
        List<IndustryProjection> all = stkIndustryRepository.findAllBySource(Arrays.asList(StringUtils.split(SOURCE_EASTMONEY_GN, ",")));
        return toMap(all);
    }

    private Map<String, List<IndustryProjection>> toMap(List<IndustryProjection> all){
        Map<String, List<IndustryProjection>> results = new HashMap<>();
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
