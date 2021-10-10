package com.stk123.repository;

import com.stk123.entity.StkCapitalFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface StkCapitalFlowRepository extends JpaRepository<StkCapitalFlowEntity, StkCapitalFlowEntity.CompositeKey> {

    StkCapitalFlowEntity findByCodeAndFlowDate(String code, String flowDate);

    List<StkCapitalFlowEntity> findAllByCodeAndFlowDateGreaterThanEqualOrderByFlowDateDesc(String code, String afterFlowDate);

    List<StkCapitalFlowEntity> findTop60ByCodeOrderByFlowDateDesc(String code);

    List<StkCapitalFlowEntity> findAllByCodeInAndFlowDateGreaterThanEqualOrderByFlowDateDesc(List<String> code, String afterFlowDate);

    default Map<String, List<StkCapitalFlowEntity>> getAllByCodeInAndFlowDateGreaterThanEqualOrderByFlowDateDesc(List<String> codes, String afterFlowDate){
        List<StkCapitalFlowEntity> flows = findAllByCodeInAndFlowDateGreaterThanEqualOrderByFlowDateDesc(codes, afterFlowDate);
        Map<String, List<StkCapitalFlowEntity>> result = new LinkedHashMap<>(codes.size());
        for(StkCapitalFlowEntity n : flows){
            List<StkCapitalFlowEntity> list = result.get(n.getCode());
            if(list == null){
                list = new ArrayList<>();
                result.put(n.getCode(), list);
            }
            list.add(n);
        }
        return result;
    }

}
