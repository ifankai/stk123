package com.stk123.repository;

import com.stk123.entity.StkNewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface StkNewsRepository extends JpaRepository<StkNewsEntity, Integer> {

    List<StkNewsEntity> findAllByCodeAndInfoCreateTimeAfterOrderByInsertTime(String code, Date infoCreateTime);

    List<StkNewsEntity> findAllByCodeInAndInfoCreateTimeAfterOrderByInsertTimeDesc(List<String> codes, Date infoCreateTime);

    default Map<String, List<StkNewsEntity>> getAllByCodeInAndInfoCreateTimeAfterOrderByInsertTime(List<String> codes, Date infoCreateTime){
        List<StkNewsEntity> news = findAllByCodeInAndInfoCreateTimeAfterOrderByInsertTimeDesc(codes, infoCreateTime);
        Map<String, List<StkNewsEntity>> result = new LinkedHashMap<>(codes.size());
        for(String code : codes) {
            result.put(code, new ArrayList<>());
        }
        for(StkNewsEntity n : news){
            List<StkNewsEntity> list = result.get(n.getCode());
            list.add(n);
        }
        return result;
    }
}
