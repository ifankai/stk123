package com.stk123.repository;

import com.stk123.entity.StkEntity;
import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockBasicProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StkRepository extends JpaRepository<StkEntity, String> {

    @Query(value = "select code as code,name as name,market as market,cate as cate,place as place from StkEntity where market=:market and cate=:cate order by code")
    List<StockBasicProjection> findAllByMarketAndCateOrderByCode(@Param("market") Integer market, @Param("cate") Integer cate);

    default List<StockBasicProjection> findAllByMarketAndCateOrderByCode(Stock.EnumMarket market, Stock.EnumCate cate) {
        return this.findAllByMarketAndCateOrderByCode(market.getMarket(), cate.getCate());
    }

    @Query(value = "select code,name,market,cate,place from stk_cn where code not in (select code from stk_kline where kline_date=to_char(sysdate,'yyyymmdd')", nativeQuery = true)
    List<StockBasicProjection> findStockNotExsitingTodayKline();

}
