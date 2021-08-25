package com.stk123.repository;

import com.stk123.entity.StkFnDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface StkFnData<E> {

    List<E> findAllByCodeAndFnDateAfterOrderByFnDateDescTypeAsc(String code, String fnDate);

}
