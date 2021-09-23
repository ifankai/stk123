package com.stk123.repository;

import com.stk123.entity.StkDictionaryHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkDictionaryHeaderRepository extends JpaRepository<StkDictionaryHeaderEntity, Integer> {

    List<StkDictionaryHeaderEntity> findAllByOrderByType();

}
