package com.stk123.service.core;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import com.stk123.common.CommonUtils;
import com.stk123.entity.StkReportDetailEntity;
import com.stk123.entity.StkReportHeaderEntity;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.strategy.sample.Strategies;
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
            map.put("reportDate", rptDate);

            List<StkReportDetailEntity> hotBkDetailList = new ArrayList<>();
            List<StkReportHeaderEntity> headersByBkAndReportDate =
                    bks.stream().filter(stkReportHeaderEntity -> rptDate.equals(stkReportHeaderEntity.getReportDate())).collect(Collectors.toList());
            for(String strategy08 : strategy08s){
                List<StkReportDetailEntity> detailByStrategyAndReportDate =
                        headersByBkAndReportDate.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream())
                                .filter(stkReportDetailEntity -> StringUtils.startsWith(stkReportDetailEntity.getStrategyCode(), strategy08)).collect(Collectors.toList());
                List<Stock> bks08 = stockService.getStocks(detailByStrategyAndReportDate.stream().map(StkReportDetailEntity::getCode).distinct().collect(Collectors.toList()));
                bksMap.putAll(bks08.stream().collect(Collectors.toMap(Stock::getCode, Function.identity())));
                hotBkDetailList.addAll(detailByStrategyAndReportDate);
            }

            Map<String,Set<String>> hotBkStrategyMap = hotBkDetailList.stream().collect(Collectors.groupingBy(StkReportDetailEntity::getCode, Collectors.mapping(StkReportDetailEntity::getStrategyCode, Collectors.toSet())));
            Map<String, Set<String>> hotBkStrategyMapSort = hotBkStrategyMap.entrySet().stream().sorted(Comparator.comparing(stringSetEntry -> calculateStrategy(stringSetEntry.getValue()), Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> oldVal,  LinkedHashMap::new));

            List<Map> hotBkList = hotBkStrategyMapSort.keySet().stream().map(s -> {
                return new HashMap(){{
                    put("stock", bksMap.get(s));
                    put("strategies", hotBkStrategyMap.get(s));
                }};
            }).collect(Collectors.toList());

            map.put("data", hotBkList);
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

            map.put("details", e.getValue().stream().distinct().map(detail -> new HashMap<String, Object>() {{
                    put("strategyCode", detail.getStrategyCode());
                    put("stocks", stockService.getStocksAsMap(Arrays.asList(StringUtils.split(detail.getRpsStockCode(), ",")), "code", "nameWithLink"));
                    String a = "<a title='查看板块精选个股' target='_blank' href='/S/"+detail.getRpsStockCode()+"'><i class='fas fa-th'></i></a>";
                    put("action", a);
                }}
            ));
            map.put("bk", bk);
            return map;
        }).collect(Collectors.toList());
        result.put("currentHotBks", groupbyCodeList);

        result.put("currentAllStocks", getStocksByType(headers, "allstocks", finalRptDate, EnumMarket.CN));
        result.put("currentMyStocksA", getStocksByType(headers, "mystocks", finalRptDate, EnumMarket.CN));
        result.put("currentMyStocksH", getStocksByType(headers, "mystocks", finalRptDate, EnumMarket.HK));
        result.put("currentMyStocksU", getStocksByType(headers, "mystocks", finalRptDate, EnumMarket.US));

        return result;
    }

    private List<Map> getStocksByType(List<StkReportHeaderEntity> headers, String type, String finalRptDate, EnumMarket market){
        List<StkReportHeaderEntity> allstocksHeaderByToday = headers.stream().filter(stkReportHeaderEntity -> type.equals(stkReportHeaderEntity.getType()) && finalRptDate.equals(stkReportHeaderEntity.getReportDate())).collect(Collectors.toList());
        List<StkReportDetailEntity> allstocksDetailByToday = allstocksHeaderByToday.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).collect(Collectors.toList());
        List<Map> allstocksList = new ArrayList<>();
        for(StkReportDetailEntity detail : allstocksDetailByToday){
            Stock stock = stockService.getStock(detail.getCode());
            if(!stock.isMarket(market)) continue;
            Map map = new HashMap();
            map.put("strategyDate", detail.getStrategyDate());
            map.put("strategyCode", detail.getStrategyCode());
            map.put("strategyName", Strategies.getStrategy(detail.getStrategyCode()).getName());
            map.put("strategyOutput", detail.getStrategyOutput());
            //map.put("strategy", detail.getStrategyDate() + "<br/>" + Strategies.getStrategy(detail.getStrategyCode()).getName() + "<br/>-----<br/>" + detail.getStrategyOutput());
            //map.put("text", detail.getText());
            map.put("code", stock.getCode());
            map.put("nameAndCodeWithLink", stock.getNameAndCodeWithLink());
            map.put("dayBarImage", stock.getDayBarImage());
            map.put("weekBarImage", stock.getWeekBarImage());
            if(detail.getRpsBkCode() != null) {
                List<Map> bkList = new ArrayList<>();
                String[] bkCodeArray = StringUtils.split(detail.getRpsBkCode(), ";");
                String[] bkRpsCodeArray = StringUtils.split(detail.getRpsCode(), ";");
                String[] bkRpsPercentileArray = StringUtils.split(detail.getRpsPercentile(), ";");
                String[] bkRpsStockCodeArray = StringUtils.split(detail.getRpsStockCode(), ";");
                for (int i = 0; i < bkCodeArray.length; i++) {
                    String bkCode = bkCodeArray[i];
                    Map bkMap = new HashMap();
                    bkMap.put("bk", stockService.getBk(bkCode));
                    bkMap.put("bkRpsCode", bkRpsCodeArray[i]);
                    bkMap.put("bkRpsName", Rps.getRpsStrategy(bkRpsCodeArray[i]).getName());
                    bkMap.put("bkRpsPercentile", bkRpsPercentileArray[i]);
                    bkMap.put("bkRpsStockCode", bkRpsStockCodeArray[i]);
                    bkList.add(bkMap);
                }
                map.put("bks", bkList);
            }
            allstocksList.add(map);
        }
        return allstocksList;
    }

    private int calculateStrategy(Set<String> strategies){
        return strategies.stream().mapToInt(s -> getStrategyScore(s)).sum();
    }
    private int getStrategyScore(String strategy){
        if("strategy_08c".equals(strategy)){
            return 5;
        }else if("strategy_08b".equals(strategy)){
            return 3;
        }else if("strategy_08a".equals(strategy)){
            return 1;
        }
        return 0;
    }

}
