package com.stk123.spring.jpa.repository;

import com.stk123.spring.jpa.entity.Admapp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmappRepository extends JpaRepository<Admapp, String> {

    Admapp findByAdapplicationEquals(String adapplication);

}
