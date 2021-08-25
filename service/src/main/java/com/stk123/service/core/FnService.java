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

    @Autowired
    private StkFnTypeRepository stkFnTypeRepository;

    public Map<Integer, StkFnTypeEntity> getTypesAsMap(EnumMarket market, Integer status){
        return stkFnTypeRepository.findAllByMarketAndStatusOrderByDispOrder(market.getMarket(), status)
                .stream().collect(Collectors.toMap(StkFnTypeEntity::getType, Function.identity()));
    }

    public List<StkFnDataEntity> findAllByCodeAndFnDateAfterOrderByFnDateDescTypeAsc(EnumMarket market, String code, String fnDate){
        return null;
    }

    public Fn getFn(Stock stock, List<StkFnTypeEntity> types, String fnDate){
        List<StkFnDataEntity> list = findAllByCodeAndFnDateAfterOrderByFnDateDescTypeAsc(stock.getMarket(), stock.getCode(), fnDate);
        Table<String, Integer, Fn.FnData> table = HashBasedTable.create();
        Map<Integer, StkFnTypeEntity> typesMap = types.stream().collect(Collectors.toMap(StkFnTypeEntity::getType, Function.identity()));
        for(StkFnDataEntity entity : list){
            table.put(entity.getFnDate(), entity.getType(), convertFnData(entity, typesMap.get(entity.getType())));
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
