package com.stk123.spring.jpa.repository;

import com.stk123.spring.jpa.entity.StkDataIndustryPeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StkDataIndustryPeRepository extends JpaRepository<StkDataIndustryPeEntity, StkDataIndustryPeEntity.CompositeKey> {

    StkDataIndustryPeEntity findByIndustryIdAndPeDate(Long industryId, String peDate);

}
