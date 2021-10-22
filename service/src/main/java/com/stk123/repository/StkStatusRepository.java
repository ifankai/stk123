package com.stk123.repository;

import com.stk123.entity.StkImportInfoEntity;
import com.stk123.entity.StkStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.*;

@Repository
public interface StkStatusRepository extends JpaRepository<StkStatusEntity, Integer> {

    @Query(value = "select t from StkStatusEntity t where t.code=:code and t.valid=1 and ((:date >= t.startTime and t.endTime is null) or (:date between t.startTime and t.endTime))")
    List<StkStatusEntity> findAllByCodeAndDateIsBetweenStartTimeAndEndTime(@Param("code")String code, @Param("date") Date date);

    @Query(value = "select t from StkStatusEntity t where t.type=:type and t.valid=1 and ((:date >= t.startTime and t.endTime is null) or (:date between t.startTime and t.endTime))")
    List<StkStatusEntity> findAllByTypeAndDateIsBetweenStartTimeAndEndTime(@Param("type")Integer type, @Param("date") Date date);

    @Query(value = "select t from StkStatusEntity t where t.valid=1 and ((:date >= t.startTime and t.endTime is null) or (:date between t.startTime and t.endTime))")
    List<StkStatusEntity> findAllByDateIsBetweenStartTimeAndEndTime(@Param("date") Date date);

    default Map<String, List<StkStatusEntity>> getAllByCodeInAndDateIsBetweenStartTimeAndEndTime(Date date){
        List<StkStatusEntity> news = findAllByDateIsBetweenStartTimeAndEndTime(date);
        Map<String, List<StkStatusEntity>> result = new LinkedHashMap();
        for(StkStatusEntity n : news){
            List<StkStatusEntity> list = result.computeIfAbsent(n.getCode(), k -> new ArrayList<>());
            list.add(n);
        }
        return result;
    }

    @Transactional
    void deleteAllByCodeAndType(String code, Integer type);

    @Transactional
    void deleteAllByCodeAndTypeAndSubType(String code, Integer type, String subType);

    @Query(value = "select t from StkStatusEntity t where t.type=:type and t.valid=1 and t.valid=1 order by coalesce(t.updateTime, t.insertTime) desc")
    List<StkStatusEntity> findAllByTypeOrderByInsertTime(@Param("type")Integer type);
}
