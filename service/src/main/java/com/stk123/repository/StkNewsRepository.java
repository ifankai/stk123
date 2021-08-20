package com.stk123.repository;

import com.stk123.entity.StkNewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface StkNewsRepository extends JpaRepository<StkNewsEntity, Integer> {

    List<StkNewsEntity> findAllByCodeAndInfoCreateTimeAfterOrderByInsertTimeDesc(String code, Date infoCreateTime);

    List<StkNewsEntity> findAllByCodeInAndInfoCreateTimeAfterOrderByInsertTimeDesc(List<String> codes, Date infoCreateTime);

    List<StkNewsEntity> findAllByCodeAndTypeAndInfoCreateTimeBetweenOrderByInsertTimeDesc(String code, Integer type, Date infoCreateTimeStart, Date infoCreateTimeEnd);

    List<StkNewsEntity> findAllByCodeAndInfoCreateTimeBetweenOrderByInsertTimeDesc(String code, Date infoCreateTimeStart, Date infoCreateTimeEnd);

    default Map<String, List<StkNewsEntity>> getAllByCodeInAndInfoCreateTimeAfterOrderByInsertTime(List<String> codes, Date infoCreateTime){
        List<StkNewsEntity> news = findAllByCodeInAndInfoCreateTimeAfterOrderByInsertTimeDesc(codes, infoCreateTime);
        Map<String, List<StkNewsEntity>> result = new LinkedHashMap<>(codes.size());
        for(StkNewsEntity n : news){
            List<StkNewsEntity> list = result.get(n.getCode());
            if(list == null){
                list = new ArrayList<>();
                result.put(n.getCode(), list);
            }
            list.add(n);
        }
        return result;
    }
}
