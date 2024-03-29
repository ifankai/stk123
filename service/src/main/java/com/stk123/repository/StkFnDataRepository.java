package com.stk123.repository;

import com.stk123.entity.StkFnDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkFnDataRepository extends StkFnData<StkFnDataEntity>, JpaRepository<StkFnDataEntity, StkFnDataEntity.CompositeKey> {

    //List<StkFnDataEntity> findAllByCodeAndFnDateAfterOrderByFnDateDescTypeAsc(String code, String fnDate);
}
