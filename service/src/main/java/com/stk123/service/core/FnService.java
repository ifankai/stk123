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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FnService extends BaseRepository {

    private static Map<Integer, StkFnTypeEntity> TYPES = null;

    @Autowired
    private StkFnTypeRepository stkFnTypeRepository;
    @Autowired
    private StkFnDataRepository stkFnDataRepository;

    public Map<Integer, StkFnTypeEntity> getTypesAsMap(EnumMarket market, Integer status){
        if(TYPES != null) return TYPES;
        TYPES = stkFnTypeRepository.findAllByMarketAndStatusOrderByDispOrder(market.getMarket(), status)
                .stream().collect(Collectors.toMap(StkFnTypeEntity::getType, Function.identity()));
        return TYPES;
    }

    public List<StkFnDataEntity> findAllByCodeAndFnDateAfterOrderByFnDateDescTypeAsc(EnumMarket market, String code, String fnDate){
        switch (market){
            case CN:
                return stkFnDataRepository.findAllByCodeAndFnDateAfterOrderByFnDateDescTypeAsc(code, fnDate);
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
