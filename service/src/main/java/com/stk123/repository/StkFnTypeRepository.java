package com.stk123.repository;

import com.stk123.entity.StkFnDataEntity;
import com.stk123.entity.StkFnTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkFnTypeRepository extends JpaRepository<StkFnTypeEntity, Integer> {

    List<StkFnTypeEntity> findAllByMarketAndCodeIsNotNull(Integer market);

    List<StkFnTypeEntity> findAllByMarketAndStatusOrderByDispOrder(Integer market, Integer status);

    List<StkFnTypeEntity> findAllByMarketAndStatusAndTypeInOrderByDispOrder(Integer market, Integer status, List<Integer> types);
}
