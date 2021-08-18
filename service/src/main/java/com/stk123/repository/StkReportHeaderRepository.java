package com.stk123.repository;

import com.stk123.entity.StkReportHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StkReportHeaderRepository extends JpaRepository<StkReportHeaderEntity, Integer> {

    List<StkReportHeaderEntity> findAllByReportDateInOrderByReportDateDescInsertTimeDesc(List<String> reportDates);

    List<StkReportHeaderEntity> findAllByTypeAndReportDateBetweenOrderByInsertTimeDesc(String type, String reportDateStart, String reportDateEnd);
}
