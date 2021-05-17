package com.stk123.repository;

import com.stk123.entity.StkEntity;
import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.projection.StockCodeNameProjection;
import com.stk123.model.projection.StockProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StkRepository extends JpaRepository<StkEntity, String> {

    @Query(value = "select code as code,name as name,market as market,cate as cate,place as place,hot as hot from StkEntity where market in (:markets) and cate=:cate and hot>=:hot")
    List<StockProjection> findAllByMarketAndCateAndHotGreaterThan(@Param("markets") List<Integer> markets, @Param("cate")Integer cate, @Param("hot")Integer hot);

    @Query(value = "select t from StkEntity t where code=:code")
    StockProjection findByCode(@Param("code")String code);

    @Query(value = "select t from StkEntity t where market=1 or ((market=2 or market=3) and hot>500)")
    List<StockProjection> findAllStk();

    @Query(value = "select code as code,name as name,market as market,cate as cate,place as place,totalCapital as totalCapital from StkEntity where market=:market and cate=:cate order by code")
    List<StockBasicProjection> findAllByMarketAndCateOrderByCode(@Param("market") Integer market, @Param("cate") Integer cate);

    default List<StockBasicProjection> findAllByMarketAndCateOrderByCode(Stock.EnumMarket market, Stock.EnumCate cate) {
        return this.findAllByMarketAndCateOrderByCode(market.getMarket(), cate.getCate());
    }

    @Query(value = "select code,name,market,cate,place from stk_cn where code not in (select code from stk_kline where kline_date=to_char(sysdate,'yyyymmdd'))", nativeQuery = true)
    List<StockBasicProjection> findStockNotExsitingTodayKline();

    @Query(value = "select code as code,name as name,market as market,cate as cate,place as place from StkEntity where code in (:codes)")
    List<StockBasicProjection> findAllByCodes(@Param("codes") List<String> codes);

    @Query(value = "select code as code,name as name from stk_cn union all select code as code,name as name from stk_hk where hot > 500 union all select code as code,name as name from stk_us where hot > 500", nativeQuery = true)
    List<StockCodeNameProjection> findAllByOrderByCode();

    @Query(value = "select code as code,name as name,market as market,cate as cate,place as place from StkEntity where code=:code and market=:market and place=:place")
    StockBasicProjection findByCodeAndMarketAndPlace(@Param("code") String code, @Param("market") Integer market, @Param("place") Integer place);
}
