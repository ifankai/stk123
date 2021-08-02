package com.stk123.repository;

import com.stk123.entity.StkIndustryEntity;
import com.stk123.model.projection.IndustryProjection;
import jnr.ffi.annotations.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkIndustryRepository extends JpaRepository<StkIndustryEntity, StkIndustryEntity.CompositeKey> {

    List<StkIndustryEntity> findAllByIndustry(Integer industry);

    List<StkIndustryEntity> findAllByIndustryIn(List<Integer> industry);

    List<StkIndustryEntity> findAllByCodeInAndIndustryIn(List<String> codes, List<Integer> industry);

    //@Query(value = "select i.industry as id,i.code as code,t.name as name,t.source as source,t.code as bkCode from stk_industry i, stk_industry_type t where i.industry=t.id and i.code=:code", nativeQuery = true)
    List<StkIndustryEntity> findAllByCode(String code);

    //@Query(value = "select i.industry as id,i.code as code,t.name as name,t.source as source,t.code as bkCode from stk_industry i, stk_industry_type t where i.industry=t.id and t.source in (:sources)", nativeQuery = true)
    //List<StkIndustryEntity> findAllBy(List<String> sources);

    @Query(value = "select i.industry as id,i.code as code,t.name as name,t.source as source,t.code as bkCode from stk_industry i, stk_industry_type t where i.industry=t.id and i.code in (:codes) and t.source in (:sources)", nativeQuery = true)
    List<IndustryProjection> findAllByCodeAndSource(@Param("codes") List<String> codes, @Param("sources") List<String> sources);

    void deleteAllByIndustry(Integer industry);
}
