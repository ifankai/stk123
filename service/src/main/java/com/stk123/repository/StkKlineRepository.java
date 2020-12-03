package com.stk123.repository;

import com.stk123.entity.StkKlineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkKlineRepository extends JpaRepository<StkKlineEntity, StkKlineEntity.CompositeKey> {

    @Query(value = "select * from (select * from stk_kline t where code = :code order by kline_date desc) where rownum <= :rn", nativeQuery = true)
    List<StkKlineEntity> queryTopNByCodeOrderByKlineDateDesc(@Param("rn")Integer rn, @Param("code")String code);
}
