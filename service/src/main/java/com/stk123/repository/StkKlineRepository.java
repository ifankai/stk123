package com.stk123.repository;

import cn.hutool.core.bean.BeanUtil;
import com.stk123.common.util.BeanUtils;
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
import java.util.*;

@Repository
public interface StkKlineRepository extends JpaRepository<StkKlineEntity, StkKlineEntity.CompositeKey> {

    StkKlineEntity findTop1ByCodeOrderByKlineDateDesc(String code);

    List<StkKlineEntity> findAllByCodeAndKlineDateAfterOrderByKlineDateDesc(String code, String date);

    @Query(value = "select t from StkKlineEntity t where t.code=:code and t.klineDate=:klineDate")
    StkKlineEntity findById(@Param("code")String code, @Param("klineDate")String klineDate);

    @Modifying
    @Query(value = "delete from stk_kline k where k.kline_date > :date", nativeQuery = true)
    void deleteAllByKlineDateAfter(@Param("date")String date);

    @Transactional
    default void deleteAllByKlineDateAfterToday() {
        this.deleteAllByKlineDateAfter(DateFormatUtils.format(new Date(), "yyyyMMdd"));
    }

    @Transactional
    default StkKlineEntity saveIfNotExisting(StkKlineEntity stkKlineEntity){
        Optional<StkKlineEntity> entity = this.findById(new StkKlineEntity.CompositeKey(stkKlineEntity.getCode(),stkKlineEntity.getKlineDate()));
        if(!entity.isPresent()){
            return saveAndFlush(stkKlineEntity);
        }
        return entity.get();
    }

    @Transactional
    default StkKlineEntity saveOrUpdate(StkKlineEntity stkKlineEntity){
        Optional<StkKlineEntity> entity = this.findById(new StkKlineEntity.CompositeKey(stkKlineEntity.getCode(),stkKlineEntity.getKlineDate()));
        if(entity.isPresent()){
            BeanUtils.mapIgnoreNull(stkKlineEntity, entity.get());
            return saveAndFlush(entity.get());
        }else {
            return saveAndFlush(stkKlineEntity);
        }
    }


    @Query(value = "select avg(pe_ttm) as avg_pe_ttm,median(pe_ttm) as mid_pe_ttm from stk_kline where kline_date=:kdate and pe_ttm is not null and pe_ttm>3 and pe_ttm<200", nativeQuery = true)
    Map<String, BigDecimal> calcAvgMidPeTtm(@Param("kdate") String kdate);

    @Query(value = "select avg(pb_ttm) as avg_pb_ttm,median(pb_ttm) as mid_pb_ttm from stk_kline where kline_date=:kdate and pb_ttm is not null and pb_ttm>0 and pb_ttm<30", nativeQuery = true)
    Map<String, BigDecimal> calcAvgMidPbTtm(@Param("kdate") String kdate);


}
