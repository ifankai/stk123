package com.stk123.repository;

import com.stk123.entity.StkFnDataUsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StkFnDataUsRepository extends StkFnData<StkFnDataUsEntity>, JpaRepository<StkFnDataUsEntity, StkFnDataUsEntity.CompositeKey> {
}
