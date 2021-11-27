package com.stk123.repository;

import com.stk123.entity.StkEntity;
import com.stk123.model.core.Stock;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.projection.StockCodeNameProjection;
import com.stk123.model.projection.StockProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface StkRepository extends JpaRepository<StkEntity, String> {

    StkEntity findByCode(String code);

    @Query(value = "select code as code,name as name,market as market,cate as cate,place as place,totalCapital as totalCapital,hot as hot from StkEntity where market in (:markets) and cate=:cate and hot>=:hot")
    List<StockProjection> findAllByMarketInAndCateAndHotGreaterThan(@Param("markets") List<Integer> markets, @Param("cate")Integer cate, @Param("hot")Integer hot);

    @Query(value = "select t from StkEntity t where code=:code")
    StockProjection getByCode(@Param("code")String code);

    @Query(value = "select t from StkEntity t where market=1 or ((market=2 or market=3) and hot>500)")
    List<StockProjection> findAllStk();

    //@Query(value = "select code as code,name as name,market as market,cate as cate,place as place,totalCapital as totalCapital from StkEntity where market=:market and cate=:cate order by code")
    @Query(value = "select code as code,name as name,market as market,cate as cate,place as place,totalCapital as totalCapital,hot as hot " +
            "from StkEntity where market=:market and cate=:cate order by code")
    List<StockBasicProjection> findAllByMarketAndCateOrderByCode(@Param("market") Integer market, @Param("cate") Integer cate);

    default List<StockBasicProjection> findAllByMarketAndCateOrderByCode(EnumMarket market, EnumCate cate) {
        return this.findAllByMarketAndCateOrderByCode(market.getMarket(), cate.getCate());
    }

    @Query(value = "select code as code,name as name,market as market,cate as cate,place as place,totalCapital as totalCapital,hot as hot " +
            "from StkEntity where market=:market and cate=:cate and (market=1 or ((market=2 or market=3) and hot>=:hot)) order by code")
    List<StockBasicProjection> findAllByMarketAndCateAndHotGreaterThanOrderByCode(@Param("market") Integer market, @Param("cate") Integer cate, @Param("hot") Integer hot);

    default List<StockBasicProjection> findAllByMarketAndCateAndHotGreaterThanOrderByCode(EnumMarket market, EnumCate cate, Integer hot) {
        return this.findAllByMarketAndCateAndHotGreaterThanOrderByCode(market.getMarket(), cate.getCate(), hot);
    }

    @Query(value = "select code,name,market,cate,place,total_capital as totalCapital,hot from stk_cn where code not in (select code from stk_kline where kline_date=to_char(sysdate,'yyyymmdd'))", nativeQuery = true)
    List<StockBasicProjection> findStockNotExsitingTodayKline();

    @Query(value = "select code as code,name as name,market as market,cate as cate,place as place,totalCapital as totalCapital,hot " +
            "from StkEntity where code in (:codes)")
    List<StockBasicProjection> findAllByCodes(@Param("codes") List<String> codes);

    @Query(value = "select code as code,name as name from stk_cn union all select code as code,name as name from stk_hk where hot > 1000 union all select code as code,name as name from stk_us where hot > 1000", nativeQuery = true)
    List<StockCodeNameProjection> findAllByOrderByCode();

    @Query(value = "select code as code,name as name,market as market,cate as cate,place as place,totalCapital as totalCapital,hot " +
            "from StkEntity where code=:code and market=:market and place=:place")
    StockBasicProjection findByCodeAndMarketAndPlace(@Param("code") String code, @Param("market") Integer market, @Param("place") Integer place);

    @Modifying
    @Transactional
    @Query(value = "update stk set address = :address where code = :code", nativeQuery = true)
    void updateAddressByCode(@Param("code") String code, @Param("address") String address);
}
