package com.stk123.spring.jpa.repository;

import com.stk123.spring.jpa.entity.Admapp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AdmappRepository extends JpaRepository<Admapp, String> {

    Admapp findByAdapplicationEquals(String adapplication);

    @Modifying
    @Transactional
    @Query("update Admapp set adaupd = :#{#admapp.adaupd} where rowid = :#{#admapp.rowid}")
    int updateTime(@Param("admapp") Admapp admapp);

}
