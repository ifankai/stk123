package com.stk123.repository;

import com.stk123.entity.StkImportInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface StkImportInfoRepository extends JpaRepository<StkImportInfoEntity, Integer> {

    List<StkImportInfoEntity> findAllByCodeAndInsertTimeAfterOrderByInsertTimeDesc(String code, Date insertTime);

    List<StkImportInfoEntity> findAllByCodeInAndInsertTimeAfterOrderByInsertTimeDesc(List<String> codes, Date insertTime);

    default Map<String, List<StkImportInfoEntity>> getAllByCodeInAndInsertTimeAfterOrderByInsertTime(List<String> codes, Date insertTime){
        List<StkImportInfoEntity> news = findAllByCodeInAndInsertTimeAfterOrderByInsertTimeDesc(codes, insertTime);
        Map<String, List<StkImportInfoEntity>> result = new LinkedHashMap<>(codes.size());
        for(StkImportInfoEntity n : news){
            List<StkImportInfoEntity> list = result.get(n.getCode());
            if(list == null){
                list = new ArrayList<>();
                result.put(n.getCode(), list);
            }
            list.add(n);
        }
        return result;
    }
}
