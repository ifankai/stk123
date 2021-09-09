package com.stk123.repository;

import com.stk123.entity.StkKeywordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StkKeywordRepository extends JpaRepository<StkKeywordEntity, Long> {

    StkKeywordEntity findByName(String name);
}
