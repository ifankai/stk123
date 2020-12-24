package com.stk123.repository;

import com.stk123.entity.StkKlineEntity;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface StkKlineRepository extends JpaRepository<StkKlineEntity, StkKlineEntity.CompositeKey> {

    String sql_queryTopNByCodeOrderByKlineDateDesc =
            "select code,kline_date as \"date\",open,close,high,low,volumn as volume,amount,last_close,percentage as change,hsl " +
            "from (select * from stk_kline t where code = :1 order by kline_date desc) where rownum <= :2";
    default BarSeries queryTopNByCodeOrderByKlineDateDesc(String code, Integer rows) {
        List<Bar> list = BaseRepository.getInstance().list(sql_queryTopNByCodeOrderByKlineDateDesc, Bar.class, code, rows);
        BarSeries bs = new BarSeries();
        for(Bar bar : list){
            bs.add(bar);
        }
        return bs;
    }


    String sql_queryTopNByCodeListOrderByKlineDateDesc =
            "select code,kline_date as \"date\",open,close,high,low,volumn as volume,amount,last_close,percentage as change,hsl from (select t.*, rank() over(partition by t.code order by t.kline_date desc) as rn " +
            "from stk_kline t where t.code in (:1)) where rn <= :2";
    @Transactional
    default LinkedHashMap<String, BarSeries> queryTopNByCodeListOrderByKlineDateDesc(Integer rn, List<String> codes) {
        List<Bar> list = BaseRepository.getInstance().list(sql_queryTopNByCodeListOrderByKlineDateDesc, Bar.class, codes, rn);
        LinkedHashMap<String, BarSeries> result = new LinkedHashMap<>(codes.size());
        for(String code : codes) {
            result.put(code, new BarSeries());
        }
        for(Bar bar : list){
            BarSeries bs = result.get(bar.getCode());
            bs.add(bar);
        }
        return result;
    }

    List<StkKlineEntity> findAllByKlineDateAndCodeIn(String klineDate, List<String> codes);

    @Modifying
    @Query(value = "delete from stk_kline k where k.kline_date > :date", nativeQuery = true)
    void deleteAllByKlineDateAfter(@Param("date")String date);

    @Transactional
    default void deleteAllByKlineDateAfterToday() {
        this.deleteAllByKlineDateAfter(DateFormatUtils.format(new Date(), "yyyyMMdd"));
    }

    default StkKlineEntity saveIfNotExisting(StkKlineEntity stkKlineEntity){
        //TODO
        return this.save(stkKlineEntity);
    }


    @Query(value = "select avg(pe_ttm) as avg_pe_ttm,median(pe_ttm) as mid_pe_ttm from stk_kline where kline_date=:kdate and pe_ttm is not null and pe_ttm>3 and pe_ttm<200", nativeQuery = true)
    Map<String, BigDecimal> calcAvgMidPeTtm(@Param("kdate") String kdate);

    @Query(value = "select avg(pb_ttm) as avg_pb_ttm,median(pb_ttm) as mid_pb_ttm from stk_kline where kline_date=:kdate and pb_ttm is not null and pb_ttm>0 and pb_ttm<30", nativeQuery = true)
    Map<String, BigDecimal> calcAvgMidPbTtm(@Param("kdate") String kdate);


}
