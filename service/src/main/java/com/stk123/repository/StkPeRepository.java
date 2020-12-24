package com.stk123.repository;

import com.stk123.entity.StkPeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StkPeRepository extends JpaRepository<StkPeEntity, String> {

}
