package com.stk123.repository;

import com.stk123.entity.StkStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface StkStatusRepository extends JpaRepository<StkStatusEntity, Integer> {

    @Query(value = "select t from StkStatusEntity t where t.code=:code and t.valid=1 and ((:date >= t.startTime and t.endTime is null) or (:date between t.startTime and t.endTime))")
    List<StkStatusEntity> findAllByCodeAndDateIsBetweenStartTimeAndEndTime(@Param("code")String code, @Param("date") Date date);

    @Transactional
    void deleteAllByCodeAndType(String code, Integer type);

    @Transactional
    void deleteAllByCodeAndTypeAndSubType(String code, Integer type, String subType);

    @Query(value = "select t from StkStatusEntity t where t.type=:type and t.valid=1 and t.valid=1 order by coalesce(t.updateTime, t.insertTime) desc")
    List<StkStatusEntity> findAllByTypeOrderByInsertTime(@Param("type")Integer type);
}
