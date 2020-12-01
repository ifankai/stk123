package com.stk123.repository;

import com.stk123.entity.StkEntity;
import com.stk123.model.projection.StockCodeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StkRepository extends JpaRepository<StkEntity, String> {

    @Query(value = "select code as code,name as name from StkEntity where market=:market and cate=:cate order by code")
    List<StockCodeName> findAllByMarket(@Param("market") Integer market, @Param("cate") Integer cate);

}
