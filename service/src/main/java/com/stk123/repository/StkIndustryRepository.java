package com.stk123.repository;

import com.stk123.entity.StkIndustryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkIndustryRepository extends JpaRepository<StkIndustryEntity, StkIndustryEntity.CompositeKey> {

    List<StkIndustryEntity> findAllByIndustry(Long industry);

}
