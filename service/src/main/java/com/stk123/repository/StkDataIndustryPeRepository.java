package com.stk123.repository;

import com.stk123.entity.StkDataIndustryPeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StkDataIndustryPeRepository extends JpaRepository<StkDataIndustryPeEntity, StkDataIndustryPeEntity.CompositeKey> {

    StkDataIndustryPeEntity findByIndustryIdAndPeDate(Integer industryId, String peDate);

}
