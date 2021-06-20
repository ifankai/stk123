package com.stk123.repository;

import com.stk123.entity.StkIndustryTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StkIndustryTypeRepository extends JpaRepository<StkIndustryTypeEntity, Integer> {

    StkIndustryTypeEntity findByCode(String code);
}
