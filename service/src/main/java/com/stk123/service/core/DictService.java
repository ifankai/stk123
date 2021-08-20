package com.stk123.service.core;

import com.stk123.entity.StkDictionaryEntity;
import com.stk123.repository.StkDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.comparator.Comparators;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DictService {

    private static Map<Integer, Map<String, StkDictionaryEntity>> dict = new HashMap<Integer, Map<String, StkDictionaryEntity>>();

    @Autowired
    private StkDictionaryRepository stkDictionaryRepository;

    @PostConstruct
    public void init(){
        dict.clear();
        List<StkDictionaryEntity> dicts = stkDictionaryRepository.findAll();
        for(StkDictionaryEntity sd : dicts){
            if(dict.get(sd.getType()) == null){
                Map<String, StkDictionaryEntity> map = new LinkedHashMap<>();
                map.put(sd.getKey(), sd);
                dict.put(sd.getType(), map);
            }else{
                dict.get(sd.getType()).put(sd.getKey(), sd);
            }
        }
    }

    public Collection<StkDictionaryEntity> getDictionary(Integer type){
        return dict.get(type).values();
    }

    public Map<String, StkDictionaryEntity> getDictionaryAsMap(Integer type){
        return dict.get(type).values().stream().collect(Collectors.toMap(StkDictionaryEntity::getKey, Function.identity()));
    }

    public Collection<StkDictionaryEntity> getDictionaryByTypes(Integer... types){
        List<StkDictionaryEntity> dicts = new ArrayList<>();
        for(Integer type :types){
            dicts.addAll(dict.get(type).values());
        }
        return dicts;
    }

    public Collection<StkDictionaryEntity> getDictionaryOrderByParam(Integer type){
        Collection<StkDictionaryEntity> list = getDictionary(type);
        return list.stream().sorted(Comparator.comparing(StkDictionaryEntity::getParam, Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
    }

    public StkDictionaryEntity getDictionary(Integer type, String key){
        return dict.get(type).get(key);
    }

    public StkDictionaryEntity getDictionary(Integer type, int key){
        return getDictionary(type, String.valueOf(key));
    }


}
