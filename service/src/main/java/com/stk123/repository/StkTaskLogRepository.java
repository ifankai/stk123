package com.stk123.repository;

import com.stk123.entity.StkTaskLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkTaskLogRepository  extends JpaRepository<StkTaskLogEntity, Long> {

    List<StkTaskLogEntity> findAllByTaskCodeAndTaskDateAndStrategyCodeAndCodeOrderByIdDesc(String taskCode, String taskDate, String strategyCode, String code);

}
