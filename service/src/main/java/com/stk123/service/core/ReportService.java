package com.stk123.service.core;

import com.stk123.entity.StkReportDetailEntity;
import com.stk123.entity.StkReportHeaderEntity;
import com.stk123.model.core.Stock;
import com.stk123.repository.BaseRepository;
import com.stk123.repository.StkReportHeaderRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@CommonsLog
public class ReportService {

    @Autowired
    private StkReportHeaderRepository stkReportHeaderRepository;
    @Autowired
    private StockService stockService;

    public StkReportHeaderEntity createReportHeaderEntity(String reportDate, String type, int realtime, String name){
        StkReportHeaderEntity stkReportHeaderEntity = new StkReportHeaderEntity();
        stkReportHeaderEntity.setReportDate(reportDate);
        stkReportHeaderEntity.setType(type);
        stkReportHeaderEntity.setRealtime(realtime);
        stkReportHeaderEntity.setName(name);
        stkReportHeaderEntity.setStatus(1);
        stkReportHeaderEntity.setInsertTime(new Date());
        return stkReportHeaderEntity;
    }

    public StkReportDetailEntity createReportDetailEntity(String stockCode, String strategyCode, String strategyDate, String strategyOutput, String rpsCode, String rpsPercentile, String rpsBkCode, String rpsStockCode, String text){
        StkReportDetailEntity stkReportDetailEntity = new StkReportDetailEntity();
        stkReportDetailEntity.setCode(stockCode);
        stkReportDetailEntity.setStrategyCode(strategyCode);
        stkReportDetailEntity.setStrategyDate(strategyDate);
        stkReportDetailEntity.setStrategyOutput(strategyOutput);
        stkReportDetailEntity.setRpsCode(rpsCode);
        stkReportDetailEntity.setRpsPercentile(rpsPercentile);
        stkReportDetailEntity.setRpsBkCode(rpsBkCode);
        stkReportDetailEntity.setRpsStockCode(rpsStockCode);
        stkReportDetailEntity.setText(text);
        return stkReportDetailEntity;
    }

    public StkReportHeaderEntity save(StkReportHeaderEntity stkReportHeaderEntity){
        return stkReportHeaderRepository.save(stkReportHeaderEntity);
    }

    private static String sql_findTopReportDate = "select report_date from (select report_date, rownum rn from  " +
            "(select report_date from stk_report_header where report_date <= :2" +
            "group by report_date order by report_date desc)) where rn <= :1";
    @Transactional
    public List<Map> findTopReportDate(int topN, String reportDate) {
        return BaseRepository.getInstance().list(sql_findTopReportDate, Map.class, topN, reportDate);
    }

    private static String findAllHotBksByReportDate = "select d.* from stk_report_header h, stk_report_detail d " +
            "where h.id=d.header_id and h.report_date in (:1) and h.type='bks' and d.strategy_code like 'strategy_08%'";
    @Transactional
    public List<StkReportDetailEntity> findAllHotBksByReportDate(List<String> reportDates) {
        return BaseRepository.getInstance().list(sql_findTopReportDate, StkReportDetailEntity.class, reportDates);
    }

    public Map findReportAsMap(String reportDate){
        Map result = new HashMap();
        List<Map> reportDatesMap = findTopReportDate(7, reportDate);
        List<String> reportDates = reportDatesMap.stream().map(map -> (String)map.get("REPORT_DATE")).collect(Collectors.toList());
        log.info(reportDates);
        String reportDateShow = null;
        if(!reportDates.contains(reportDate)){
            reportDateShow = reportDates.get(0);
        }else {
            reportDateShow = reportDate;
        }
        result.put("reportDate", reportDateShow);

        List<StkReportHeaderEntity> headers = stkReportHeaderRepository.findAllByReportDateInOrderByReportDateDescInsertTimeDesc(reportDates);
        List<StkReportHeaderEntity> bks = headers.stream().filter(stkReportHeaderEntity -> "bks".equals(stkReportHeaderEntity.getType())).collect(Collectors.toList());

        List<String> strategy08s = bks.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream())
                .map(StkReportDetailEntity::getStrategyCode).filter(s -> StringUtils.startsWith(s, "strategy_08")).distinct().collect(Collectors.toList());

        List<Map> hotBks = new ArrayList<>();
        for(String rptDate : reportDates){
            Map map = new HashMap();
            map.put("title", rptDate);

            Map mapBy08 = new HashMap();
            List<StkReportHeaderEntity> headersByReportDate =
                    bks.stream().filter(stkReportHeaderEntity -> rptDate.equals(stkReportHeaderEntity.getReportDate())).collect(Collectors.toList());
            for(String strategy08 : strategy08s){
                List<StkReportDetailEntity> detailByStrategyAndReportDate =
                        headersByReportDate.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream())
                                .filter(stkReportDetailEntity -> StringUtils.startsWith(stkReportDetailEntity.getStrategyCode(), strategy08)).collect(Collectors.toList());
                List<Stock> stocks = stockService.getStocks(detailByStrategyAndReportDate.stream().map(StkReportDetailEntity::getCode).collect(Collectors.toList()));
                mapBy08.put(strategy08, stocks);
            }
            map.put("data", mapBy08);
            hotBks.add(map);
        }
        result.put("hotBks", hotBks);

        String finalRptDate = reportDateShow;
        List<StkReportHeaderEntity> bksByToday = bks.stream().filter(stkReportHeaderEntity -> finalRptDate.equals(stkReportHeaderEntity.getReportDate())).collect(Collectors.toList());

        List<StkReportHeaderEntity> mystocksByToday = headers.stream().filter(stkReportHeaderEntity -> "mystocks".equals(stkReportHeaderEntity.getType()) && finalRptDate.equals(stkReportHeaderEntity.getReportDate())).collect(Collectors.toList());

        return result;
    }

}
