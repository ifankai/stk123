package com.stk123.repository;

import com.stk123.entity.StkHkMoneyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface StkHkMoneyRepository extends JpaRepository<StkHkMoneyEntity, Integer> {

    StkHkMoneyEntity findByCodeAndMoneyDate(String code, String moneyDate);

    List<StkHkMoneyEntity> findTop60ByCodeOrderByMoneyDateDesc(String code);

    List<StkHkMoneyEntity> findAllByCodeInAndMoneyDateGreaterThanEqualOrderByMoneyDateDesc(List<String> code, String afterFlowDate);

    default Map<String, List<StkHkMoneyEntity>> getAllByCodeInAndMoneyDateGreaterThanEqualOrderByMoneyDateDesc(List<String> codes, String afterFlowDate){
        List<StkHkMoneyEntity> flows = findAllByCodeInAndMoneyDateGreaterThanEqualOrderByMoneyDateDesc(codes, afterFlowDate);
        Map<String, List<StkHkMoneyEntity>> result = new LinkedHashMap<>(codes.size());
        for(StkHkMoneyEntity n : flows){
            List<StkHkMoneyEntity> list = result.get(n.getCode());
            if(list == null){
                list = new ArrayList<>();
                result.put(n.getCode(), list);
            }
            list.add(n);
        }
        return result;
    }
}
