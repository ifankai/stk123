package com.stk123.repository;

import com.stk123.entity.StkReportHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StkReportHeaderRepository extends JpaRepository<StkReportHeaderEntity, Integer> {
}
