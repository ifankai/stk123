package com.stk123.repository;

import com.stk123.entity.StkFnDataHkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StkFnDataHkRepository extends StkFnData<StkFnDataHkEntity>, JpaRepository<StkFnDataHkEntity, StkFnDataHkEntity.CompositeKey> {
}
