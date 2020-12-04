package com.stk123.repository;

import com.stk123.entity.StkKlineEntity;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface StkKlineRepository extends JpaRepository<StkKlineEntity, StkKlineEntity.CompositeKey> {

    @Query(value = "select * from (select * from stk_kline t where code = :code order by kline_date desc) where rownum <= :rn", nativeQuery = true)
    List<StkKlineEntity> queryTopNByCodeOrderByKlineDateDesc(@Param("rn")Integer rn, @Param("code")String code);


    String sql_queryTopNByCodeListOrderByKlineDateDesc =
            "select code,kline_date as date,open,close,high,low,volumn as volume,amount,last_close,percentage as change,hsl from (select t.*, rank() over(partition by t.code order by t.kline_date desc) as rn " +
            "from stk_kline t where t.code in ('?')) where rm <= ?";
    default LinkedHashMap<String, BarSeries> queryTopNByCodeListOrderByKlineDateDesc(Integer rn, List<String> codes) {
        List<Bar> list = BaseRepository.getInstance().list(sql_queryTopNByCodeListOrderByKlineDateDesc, Bar.class, StringUtils.join(codes, "','"), rn);
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
}
