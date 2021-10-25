package com.stk123.service.core;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.stk123.common.util.BeanUtils;
import com.stk123.entity.StkFnDataEntity;
import com.stk123.entity.StkFnTypeEntity;
import com.stk123.model.core.Fn;
import com.stk123.model.core.Stock;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.repository.BaseRepository;
import com.stk123.repository.StkFnDataRepository;
import com.stk123.repository.StkFnTypeRepository;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FnService extends BaseRepository {

    private static List<StkFnTypeEntity> TYPES_LIST = null;
    private static Map<Integer, StkFnTypeEntity> TYPES_MAP = null;

    @Autowired
    private StkFnTypeRepository stkFnTypeRepository;
    @Autowired
    private StkFnDataRepository stkFnDataRepository;

    public List<StkFnTypeEntity> getTypes(EnumMarket market, Integer status){
        if(TYPES_LIST != null) return TYPES_LIST;
        TYPES_LIST = stkFnTypeRepository.findAllByMarketAndStatusOrderByDispOrder(market.getMarket(), status);
        TYPES_LIST.forEach(stkFnTypeEntity -> {
            stkFnTypeEntity.setDispName(stkFnTypeEntity.getDispName()==null?stkFnTypeEntity.getName():stkFnTypeEntity.getDispName());
            if(stkFnTypeEntity.getIsPercent()){
                stkFnTypeEntity.setDispName(stkFnTypeEntity.getDispName()+"(%)");
            }
        });
        TYPES_MAP = TYPES_LIST.stream().collect(Collectors.toMap(StkFnTypeEntity::getType, Function.identity(), (a, b) -> b, LinkedHashMap::new));
        return TYPES_LIST;
    }

    public Map<Integer, StkFnTypeEntity> getTypesAsMap(EnumMarket market, Integer status){
        if(TYPES_MAP != null) return TYPES_MAP;
        getTypes(market, status);
        return TYPES_MAP;
    }

    public List<StkFnDataEntity> findAllByCodeAndFnDateAfterOrderByFnDateDescTypeAsc(EnumMarket market, String code, String fnDate){
        switch (market){
            case CN:
                return stkFnDataRepository.findAllByCodeAndFnDateAfterOrderByFnDateDescTypeAsc(code, fnDate);
            case HK:
            case US:
                return Collections.EMPTY_LIST;
        }
        throw new RuntimeException("Market is not existing: "+market);
    }

    public Map<String, List<StkFnDataEntity>> findAllByCodeInAndFnDateAfterOrderByCodeAscFnDateDescTypeAsc(List<String> codes, String fnDate){
//        switch (market){
//            case CN:
                return stkFnDataRepository.getAllByCodeInAndFnDateAfterOrderByCodeAscFnDateDescTypeAsc(codes, fnDate);
//        }
//        throw new RuntimeException("Market is not existing: "+market);
    }

    public Fn getFn(Stock stock, List<StkFnTypeEntity> types, String fnDate){
        List<StkFnDataEntity> list = findAllByCodeAndFnDateAfterOrderByFnDateDescTypeAsc(stock.getMarket(), stock.getCode(), fnDate);
        Table<String, Integer, Fn.FnData> table = HashBasedTable.create();
        Map<Integer, StkFnTypeEntity> typesMap = types.stream().collect(Collectors.toMap(StkFnTypeEntity::getType, Function.identity()));
        for(StkFnDataEntity entity : list){
            StkFnTypeEntity type = typesMap.get(entity.getType());
            if(type == null)continue;
            table.put(entity.getFnDate(), entity.getType(), convertFnData(entity, type));
        }
        return new Fn(stock, types, table);
    }

    public Fn getFn(Stock stock, List<StkFnTypeEntity> types, List<StkFnDataEntity> list){
        Table<String, Integer, Fn.FnData> table = HashBasedTable.create();
        Map<Integer, StkFnTypeEntity> typesMap = types.stream().collect(Collectors.toMap(StkFnTypeEntity::getType, Function.identity()));
        for(StkFnDataEntity entity : list){
            StkFnTypeEntity type = typesMap.get(entity.getType());
            if(type == null)continue;
            table.put(entity.getFnDate(), entity.getType(), convertFnData(entity, type));
        }
        return new Fn(stock, types, table);
    }

    private Fn.FnData convertFnData(StkFnDataEntity entity, StkFnTypeEntity typeEntity){
        Fn.FnData fnData = new Fn.FnData();
        fnData.setDate(entity.getFnDate());
        fnData.setValue(entity.getFnValue());
        fnData.setType(typeEntity);
        return fnData;
    }
}
