package com.stk123.repository;

import com.stk123.entity.StkReportDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Repository
public interface StkReportDetailRepository extends JpaRepository<StkReportDetailEntity, Integer> {

    @Modifying
    @Transactional
    @Query(value = "update stk_report_detail set checked_time = :date where id = :id", nativeQuery = true)
    void updateCheckedById(@Param("id") Integer id, @Param("date") Date date);

    StkReportDetailEntity findTopByStrategyCodeOrderByIdDesc(String strategyCode);
}
