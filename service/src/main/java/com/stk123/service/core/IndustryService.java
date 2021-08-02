package com.stk123.service.core;

import com.stk123.entity.StkIndustryEntity;
import com.stk123.entity.StkIndustryTypeEntity;
import com.stk123.model.projection.IndustryProjection;
import com.stk123.repository.StkIndustryRepository;
import com.stk123.repository.StkIndustryTypeRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IndustryService {

    public final static String SOURCE_EASTMONEY_GN = "eastmoney_gn";

    public final static String DEFAULT_SOURCES = "csindex_zz,csindex_zjh,10jqka_gn,10jqka_thshy,eastmoney_gn";

    @Autowired
    private StkIndustryRepository stkIndustryRepository;
    @Autowired
    private StkIndustryTypeRepository stkIndustryTypeRepository;

    @Transactional
    public Map<String, List<StkIndustryEntity>> findAllToMap(List<String> codes){
        List<StkIndustryTypeEntity> types = stkIndustryTypeRepository.findAllBySourceIn(Arrays.asList(StringUtils.split(SOURCE_EASTMONEY_GN, ",")));
        List<StkIndustryEntity> all = stkIndustryRepository.findAllByCodeInAndIndustryIn(codes, types.stream().map(StkIndustryTypeEntity::getId).collect(Collectors.toList()));
        return toMap(all);
    }

    @Transactional
    public Map<String, List<StkIndustryEntity>> findAllToMap(){
        List<StkIndustryTypeEntity> types = stkIndustryTypeRepository.findAllBySourceIn(Arrays.asList(StringUtils.split(SOURCE_EASTMONEY_GN, ",")));
        List<StkIndustryEntity> all = stkIndustryRepository.findAllByIndustryIn(types.stream().map(StkIndustryTypeEntity::getId).collect(Collectors.toList()));
        return toMap(all);
    }

    private Map<String, List<StkIndustryEntity>> toMap(List<StkIndustryEntity> all){
        Map<String, List<StkIndustryEntity>> results = new HashMap<>();
        for(StkIndustryEntity projection : all){
            List<StkIndustryEntity> list = results.get(projection.getCode());
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
