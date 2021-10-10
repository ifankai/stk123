package com.stk123.repository;

import com.stk123.entity.StkReportHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface StkReportHeaderRepository extends JpaRepository<StkReportHeaderEntity, Integer> {

    List<StkReportHeaderEntity> findAllByReportDateOrderByReportDateDescInsertTimeDesc(String reportDate);

    List<StkReportHeaderEntity> findAllByReportDateInOrderByReportDateDescInsertTimeDesc(List<String> reportDates);

    List<StkReportHeaderEntity> findAllByTypeAndReportDateBetweenOrderByInsertTimeDesc(String type, String reportDateStart, String reportDateEnd);

    @Modifying
    @Transactional
    @Query(value = "update stk_report_header set checked_time = :date where type = :type and report_date=:reportDate", nativeQuery = true)
    void updateCheckedByTypeAndReportDate(@Param("type") String type, @Param("reportDate") String reportDate, @Param("date") Date date);

    List<StkReportHeaderEntity> findAllByTypeAndReportDate(String type, String reportDate);
}
