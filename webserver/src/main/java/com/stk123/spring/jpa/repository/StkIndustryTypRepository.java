package com.stk123.spring.jpa.repository;

import com.stk123.spring.jpa.entity.StkIndustryTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StkIndustryTypRepository extends JpaRepository<StkIndustryTypeEntity, Integer> {
}
