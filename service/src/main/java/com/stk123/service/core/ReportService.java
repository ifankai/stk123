package com.stk123.service.core;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import com.stk123.common.CommonUtils;
import com.stk123.entity.StkReportDetailEntity;
import com.stk123.entity.StkReportHeaderEntity;
import com.stk123.model.core.Stock;
import com.stk123.repository.BaseRepository;
import com.stk123.repository.StkReportHeaderRepository;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            "(select report_date from stk_report_header where report_date <= :1 " +
            "group by report_date order by report_date asc)) where rn <= :2";
    @Transactional
    public List<Map> findTopReportDate(String reportDate, int topN) {
        return BaseRepository.getInstance().list2Map(sql_findTopReportDate, reportDate, topN);
    }

    private static String findAllHotBksByReportDate = "select d.* from stk_report_header h, stk_report_detail d " +
            "where h.id=d.header_id and h.report_date in (:1) and h.type='bks' and d.strategy_code like 'strategy_08%'";
    @Transactional
    public List<StkReportDetailEntity> findAllHotBksByReportDate(List<String> reportDates) {
        return BaseRepository.getInstance().list(findAllHotBksByReportDate, StkReportDetailEntity.class, reportDates);
    }

    public Map findReportAsMap(String reportDate){
        Map result = new HashMap();
        List<Map> reportDatesMap = findTopReportDate(CommonUtils.addDay2String(reportDate, 1), 5);
        List<String> reportDates = reportDatesMap.stream().map(map -> (String)map.get("REPORT_DATE")).collect(Collectors.toList());

        String reportDateShow = null;
        if(!reportDates.isEmpty() && !reportDates.contains(reportDate)){
            reportDateShow = reportDates.get(reportDates.size()-1);
        }else {
            reportDateShow = reportDate;
        }
        result.put("reportDate", reportDateShow);

        List<StkReportHeaderEntity> headers = stkReportHeaderRepository.findAllByReportDateInOrderByReportDateDescInsertTimeDesc(reportDates);
        List<StkReportHeaderEntity> bks = headers.stream().filter(stkReportHeaderEntity -> "bks".equals(stkReportHeaderEntity.getType())).collect(Collectors.toList());

        List<String> strategy08s = bks.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream())
                .map(StkReportDetailEntity::getStrategyCode).filter(s -> StringUtils.startsWith(s, "strategy_08")).distinct().collect(Collectors.toList());

        Map<String, Stock> bksMap = new HashMap<>();

        List<Map> hotBks = new ArrayList<>();
        for(String rptDate : reportDates){
            Map map = new HashMap();
            map.put("title", rptDate);

            Map mapBy08 = new HashMap();
            List<StkReportHeaderEntity> headersByBkAndReportDate =
                    bks.stream().filter(stkReportHeaderEntity -> rptDate.equals(stkReportHeaderEntity.getReportDate())).collect(Collectors.toList());
            for(String strategy08 : strategy08s){
                List<StkReportDetailEntity> detailByStrategyAndReportDate =
                        headersByBkAndReportDate.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream())
                                .filter(stkReportDetailEntity -> StringUtils.startsWith(stkReportDetailEntity.getStrategyCode(), strategy08)).collect(Collectors.toList());
                List<Stock> bks08 = stockService.getStocks(detailByStrategyAndReportDate.stream().map(StkReportDetailEntity::getCode).collect(Collectors.toList()));
                bksMap.putAll(bks08.stream().collect(Collectors.toMap(Stock::getCode, Function.identity())));
                mapBy08.put(strategy08, bks08);
            }
            map.put("data", mapBy08);
            hotBks.add(map);
        }
        result.put("hotBks", hotBks);

        String finalRptDate = reportDateShow;
        List<StkReportHeaderEntity> bksByToday = bks.stream().filter(stkReportHeaderEntity -> finalRptDate.equals(stkReportHeaderEntity.getReportDate())).collect(Collectors.toList());
        List<StkReportHeaderEntity> bksByNotToday = bks.stream().filter(stkReportHeaderEntity -> !finalRptDate.equals(stkReportHeaderEntity.getReportDate())).collect(Collectors.toList());

        Stream<String> bkCodeByToday = bksByToday.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).map(StkReportDetailEntity::getCode);
        List<String> bkCodeByNotToday = bksByNotToday.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).map(StkReportDetailEntity::getCode).collect(Collectors.toList());
        List<String> bkCodeNew = bkCodeByToday.filter(s -> !bkCodeByNotToday.contains(s)).collect(Collectors.toList());
        result.put("hotBksNew", bkCodeNew);

        Map<String, List<StkReportDetailEntity>> groupbyCodeMap = bksByToday.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).
                filter(s -> StringUtils.startsWith(s.getStrategyCode(), "strategy_08")).collect(Collectors.groupingBy(StkReportDetailEntity::getCode));
        List<Map> groupbyCodeList = groupbyCodeMap.entrySet().stream().sorted(Comparator.comparing(e -> e.getValue().size(), Comparator.reverseOrder())).map(e -> {
            Map map = new HashMap();
            Stock bk = bksMap.get(e.getKey());
            map.put("code", e.getKey());
            map.put("strategyDate", finalRptDate);

            map.put("details", e.getValue().stream().map(detail -> new HashMap<String, Object>() {{
                    put("strategyCode", detail.getStrategyCode());
                    put("stocks", stockService.getStocksAsMap(Arrays.asList(StringUtils.split(detail.getRpsStockCode(), ",")), "code", "nameWithLink"));
                    put("action", CommonUtils.a2stocks("查看", bk.getName(), Arrays.asList(StringUtils.split(detail.getRpsStockCode(), ",")) ));
                }}
            ));
            map.put("bk", bk);
            return map;
        }).collect(Collectors.toList());
        result.put("currentHotBks", groupbyCodeList);


        List<StkReportHeaderEntity> mystocksByToday = headers.stream().filter(stkReportHeaderEntity -> "mystocks".equals(stkReportHeaderEntity.getType()) && finalRptDate.equals(stkReportHeaderEntity.getReportDate())).collect(Collectors.toList());

        return result;
    }


}
