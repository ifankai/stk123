package com.stk123.repository;

import com.stk123.entity.StkErrorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkErrorLogRepository extends JpaRepository<StkErrorLogEntity, Integer> {

    List<StkErrorLogEntity> findAllByCodeOrderByInsertTimeDesc(String code);

}
