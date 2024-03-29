package com.stk123.service.core;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.stk123.common.CommonUtils;
import com.stk123.entity.StkReportDetailEntity;
import com.stk123.entity.StkReportHeaderEntity;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import com.stk123.model.core.Cache;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.strategy.sample.Strategies;
import com.stk123.repository.BaseRepository;
import com.stk123.repository.StkReportHeaderRepository;
import com.stk123.service.StkConstant;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    public StkReportDetailEntity createReportDetailEntity(String stockCode, String strategyCode, String strategyDate,
                                                          String strategyOutput, String rpsCode, String rpsPercentile,
                                                          String rpsBkCode, String rpsStockCode, String text,
                                                          String output1, String output2){
        return createReportDetailEntity(stockCode, strategyCode, strategyDate, strategyOutput, rpsCode, rpsPercentile, rpsBkCode, rpsStockCode, text, output1, output2, null, null, null);
    }

    public StkReportDetailEntity createReportDetailEntity(String stockCode, String strategyCode, String strategyDate,
                                                          String strategyOutput, String rpsCode, String rpsPercentile,
                                                          String rpsBkCode, String rpsStockCode, String text,
                                                          String output1, String output2, String outputVolumeHighest, String outputDownLongtime, String output3){
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
        stkReportDetailEntity.setOutput1(output1);
        stkReportDetailEntity.setOutput2(output2);
        stkReportDetailEntity.setOutputVolumeHighest(outputVolumeHighest);
        stkReportDetailEntity.setOutputDownLongtime(outputDownLongtime);
        stkReportDetailEntity.setOutput3(output3);
        return stkReportDetailEntity;
    }

    public StkReportHeaderEntity save(StkReportHeaderEntity stkReportHeaderEntity){
        return stkReportHeaderRepository.save(stkReportHeaderEntity);
    }

    private static String sql_findTopReportDate = "select report_date from (select report_date, rownum rn from  " +
            "(select report_date from stk_report_header where report_date <= :1 " +
            "group by report_date order by report_date desc)) where rn <= :2";
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

    public Map findReportCheckedAsMap(String finalReportDate, List<StkReportDetailEntity> detailsA, List<StkReportDetailEntity> detailsH, List<StkReportDetailEntity> detailsU){
        List<StkReportHeaderEntity> headers = stkReportHeaderRepository.findAllByReportDateOrderByReportDateDescInsertTimeDesc(finalReportDate);
        Map result = new HashMap();
        result.put("currentAllStocksChecked", getReportHeaderChecked(headers, StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS, finalReportDate));
        result.put("currentAllStocksRpsStrategyChecked", getReportHeaderChecked(headers, StkConstant.REPORT_HEADER_TYPE_RPSSTOCKS_STRATEGIES, finalReportDate));
        result.put("currentAllStocksRps15AChecked", getReportHeaderChecked(headers, StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_15A, finalReportDate));
        result.put("currentMyStocksAChecked", getReportHeaderChecked(headers, StkConstant.REPORT_HEADER_TYPE_MYSTOCKS, finalReportDate));
        List<StkReportDetailEntity> detailsCN = getReportDetailChecked(headers, StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS, finalReportDate);
        setCheckedTime(detailsA, detailsCN);
        List<StkReportDetailEntity> detailsHK = getReportDetailChecked(headers, StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_HK, finalReportDate);
        setCheckedTime(detailsH, detailsHK);
        List<StkReportDetailEntity> detailsUS = getReportDetailChecked(headers, StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_US, finalReportDate);
        setCheckedTime(detailsU, detailsUS);
        return result;
    }

    private void setCheckedTime(List<StkReportDetailEntity> detailsTarget, List<StkReportDetailEntity> details){
        detailsTarget.forEach(d -> {
            StkReportDetailEntity a = details.stream().filter(stkReportDetailEntity -> stkReportDetailEntity.getStrategyCode().equals(d.getStrategyCode())).findFirst().orElse(null);
            if(a == null) d.setCheckedTime(null);
            else d.setCheckedTime(a.getCheckedTime());
        });
    }

    //@Cached(name = "report", key="#reportDate", expire = 3600, cacheType = CacheType.LOCAL) //1小时
    public Map findReportAsMap(String reportDate){
        log.info("reportDate="+reportDate);
        Map result = new HashMap();
        List<Map> reportDatesMap = findTopReportDate(reportDate, 12);
        List<String> reportDates = reportDatesMap.stream().map(map -> (String)map.get("REPORT_DATE")).sorted(Comparator.naturalOrder()).collect(Collectors.toList());

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
                hotBkDetailList.addAll(detailByStrategyAndReportDate);
            }

            Map<String,Set<String>> hotBkStrategyMap = hotBkDetailList.stream().collect(Collectors.groupingBy(StkReportDetailEntity::getCode, Collectors.mapping(StkReportDetailEntity::getStrategyCode, Collectors.toSet())));
            Map<String, Set<String>> hotBkStrategyMapSort = hotBkStrategyMap.entrySet().stream().sorted(Comparator.comparing(stringSetEntry -> calculateStrategy(stringSetEntry.getValue()), Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> oldVal,  LinkedHashMap::new));

            List<Map> hotBkList = hotBkStrategyMapSort.keySet().stream().map(s -> {
                return new HashMap(){{
                    put("stock", Cache.getBk(s));
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

        Stream<String> bkCodeByTodayA = bksByToday.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).filter(item -> StringUtils.startsWith(item.getStrategyCode(), "strategy_08a")).map(StkReportDetailEntity::getCode);
        List<String> bkCodeByNotTodayA = bksByNotToday.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).filter(s -> StringUtils.startsWith(s.getStrategyCode(), "strategy_08a")).map(StkReportDetailEntity::getCode).collect(Collectors.toList());
        List<String> bkCodeNewA = bkCodeByTodayA.filter(s -> !bkCodeByNotTodayA.contains(s)).collect(Collectors.toList());
        Stream<String> bkCodeByTodayB = bksByToday.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).filter(item -> StringUtils.startsWith(item.getStrategyCode(), "strategy_08b")).map(StkReportDetailEntity::getCode);
        List<String> bkCodeByNotTodayB = bksByNotToday.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).filter(s -> StringUtils.startsWith(s.getStrategyCode(), "strategy_08b")).map(StkReportDetailEntity::getCode).collect(Collectors.toList());
        List<String> bkCodeNewB = bkCodeByTodayB.filter(s -> !bkCodeByNotTodayB.contains(s)).collect(Collectors.toList());
        Stream<String> bkCodeByTodayC = bksByToday.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).filter(item -> StringUtils.startsWith(item.getStrategyCode(), "strategy_08c")).map(StkReportDetailEntity::getCode);
        List<String> bkCodeByNotTodayC = bksByNotToday.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).filter(s -> StringUtils.startsWith(s.getStrategyCode(), "strategy_08c")).map(StkReportDetailEntity::getCode).collect(Collectors.toList());
        List<String> bkCodeNewC = bkCodeByTodayC.filter(s -> !bkCodeByNotTodayC.contains(s)).collect(Collectors.toList());
        result.put("hotBksNew", ListUtils.union(ListUtils.union(bkCodeNewA, bkCodeNewB), bkCodeNewC)); //新出现的板块

        Map<String, List<StkReportDetailEntity>> groupbyCodeByStrategy08 = bksByToday.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).
                filter(s -> StringUtils.startsWith(s.getStrategyCode(), "strategy_08")).collect(Collectors.groupingBy(StkReportDetailEntity::getCode));
        List<Map> bksGroupbyCodeByStrategy08 = getBksAsMap(groupbyCodeByStrategy08, finalRptDate);
        result.put("currentHotBks", bksGroupbyCodeByStrategy08);

        Map<String, List<StkReportDetailEntity>> groupbyCodeByNotStrategy08 = bksByToday.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).
                filter(s -> !StringUtils.startsWith(s.getStrategyCode(), "strategy_08")).collect(Collectors.groupingBy(StkReportDetailEntity::getCode));
        List<Map> bksGroupbyCodeByNotStrategy08 = getBksAsMap(groupbyCodeByNotStrategy08, finalRptDate);
        result.put("currentBksStrategy", bksGroupbyCodeByNotStrategy08);

        result.put("currentAllStocks", getStocksByType(headers, StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS, finalRptDate, EnumMarket.CN));
        result.put("currentAllStocksRpsStrategy", getStocksByType(headers, StkConstant.REPORT_HEADER_TYPE_RPSSTOCKS_STRATEGIES, finalRptDate, EnumMarket.CN));
        result.put("currentMyStocksA", getStocksByType(headers, StkConstant.REPORT_HEADER_TYPE_MYSTOCKS, finalRptDate, EnumMarket.CN));
        result.put("currentMyStocksH", getStocksByType(headers, StkConstant.REPORT_HEADER_TYPE_MYSTOCKS, finalRptDate, EnumMarket.HK));
        result.put("currentMyStocksU", getStocksByType(headers, StkConstant.REPORT_HEADER_TYPE_MYSTOCKS, finalRptDate, EnumMarket.US));

        result.put("currentAllStocksRpsA", getAllStocksRpsByType(headers, finalRptDate, StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS));
        result.put("currentAllStocksRpsH", getAllStocksRpsByType(headers, finalRptDate, StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_HK));
        result.put("currentAllStocksRpsU", getAllStocksRpsByType(headers, finalRptDate, StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_US));

        //k线云梯
        result.put("currentAllStocksRps15A", getStocksByType(headers, StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_15A, finalRptDate, EnumMarket.CN));

        return result;
    }

    private List<StkReportDetailEntity> getReportDetailChecked(List<StkReportHeaderEntity> headers, String type, String finalRptDate){
        List<StkReportHeaderEntity> headerByDate = headers.stream().filter(stkReportHeaderEntity -> type.equals(stkReportHeaderEntity.getType()) && finalRptDate.equals(stkReportHeaderEntity.getReportDate())).collect(Collectors.toList());
        if(!headerByDate.isEmpty()) {
            StkReportHeaderEntity stkReportHeaderEntity = headerByDate.get(0);
            return stkReportHeaderEntity.getStkReportDetailEntities().stream().filter(stkReportDetailEntity -> stkReportDetailEntity.getCheckedTime() != null).collect(Collectors.toList());
        }
        return null;
    }

    private Map getReportHeaderChecked(List<StkReportHeaderEntity> headers, String type, String finalRptDate){
        List<StkReportHeaderEntity> headerByDate = headers.stream().filter(stkReportHeaderEntity -> type.equals(stkReportHeaderEntity.getType()) && finalRptDate.equals(stkReportHeaderEntity.getReportDate())).collect(Collectors.toList());
        Map result = new HashMap();
        if(!headerByDate.isEmpty()){
            StkReportHeaderEntity stkReportHeaderEntity = headerByDate.get(0);
            result.put("reportDate", stkReportHeaderEntity.getReportDate());
            result.put("type", stkReportHeaderEntity.getType());
            result.put("checked", stkReportHeaderEntity.getCheckedTime() == null ? null : stkReportHeaderEntity.getCheckedTime().getTime());
        }
        return result;
    }

    private List<StkReportDetailEntity> getAllStocksRpsByType(List<StkReportHeaderEntity> headers, String finalRptDate, String type){
        List<StkReportDetailEntity> allStocksRpsA = getDetailsByTypeAndDate(headers, type, finalRptDate);
        allStocksRpsA.forEach(rps -> {
            rps.setStrategyName(Strategies.getStrategy(rps.getStrategyCode()).getNameWithCode());
        });
        return allStocksRpsA;
    }

    private List<Map> getBksAsMap(Map<String, List<StkReportDetailEntity>> groupbyCodeMap, String finalRptDate){
        return groupbyCodeMap.entrySet().stream().sorted(Comparator.comparing(e -> e.getValue().size(), Comparator.reverseOrder())).map(e -> {
            Map map = new HashMap();
            Stock bk = Cache.getBk(e.getKey());
            map.put("code", e.getKey());
            map.put("strategyDate", finalRptDate);

            map.put("details", e.getValue().stream().distinct().map(detail -> new HashMap<String, Object>() {{
                        put("strategyCode", detail.getStrategyCode());
                        put("strategyName", Strategies.getStrategy(detail.getStrategyCode()).getName());
                        put("stocks", stockService.getStocksAsMap(Arrays.asList(StringUtils.split(detail.getRpsStockCode(), ",")), "code", "nameWithLink"));
                        String a = "<a title='查看板块精选个股' target='_blank' href='/s/"+detail.getRpsStockCode()+"'><i class='fas fa-th'></i></a>";
                        put("action", a);
                    }}
            ).collect(Collectors.toList()));
            map.put("bk", bk);
            return map;
        }).collect(Collectors.toList());
    }

    private List<StkReportDetailEntity> getDetailsByTypeAndDate(List<StkReportHeaderEntity> headers, String type, String date){
        List<StkReportHeaderEntity> headerByDate = headers.stream().filter(stkReportHeaderEntity -> type.equals(stkReportHeaderEntity.getType()) && date.equals(stkReportHeaderEntity.getReportDate())).collect(Collectors.toList());
        return headerByDate.stream().flatMap(stkReportHeaderEntity -> stkReportHeaderEntity.getStkReportDetailEntities().stream()).sorted(Comparator.comparing(StkReportDetailEntity::getId)).collect(Collectors.toList());
    }

    private List<Map> getStocksByType(List<StkReportHeaderEntity> headers, String type, String finalRptDate, EnumMarket market){
        List<StkReportDetailEntity> allstocksDetailByToday = getDetailsByTypeAndDate(headers, type, finalRptDate);

        Map<String, List<StkReportDetailEntity>> allstocksDetailGroupByCode = allstocksDetailByToday.stream().collect(Collectors.groupingBy(StkReportDetailEntity::getCode));
        List<String> allstocksCodesDistinct = allstocksDetailByToday.stream().map(StkReportDetailEntity::getCode).distinct().collect(Collectors.toList());

        List<Map> allstocksList = new ArrayList<>();
        for(String code : allstocksCodesDistinct){
            Stock stock = stockService.getStock(code);
            if(stock == null){
                log.error("code is not in Stocks cache:"+ code);
                continue;
            }
            if(!stock.isMarket(market)) continue;
            List<StkReportDetailEntity> details = allstocksDetailGroupByCode.get(code);
            StkReportDetailEntity detail = details.get(0);
            Map map = new HashMap();
            map.put("strategyDate", detail.getStrategyDate());
            map.put("strategyCode", details.stream().map(StkReportDetailEntity::getStrategyCode).collect(Collectors.toList()));
            map.put("strategyName", details.stream().map(d -> Strategies.getStrategy(d.getStrategyCode()).getName()).distinct().collect(Collectors.toList()));
            map.put("strategyOutput", details.stream().filter(d -> d.getStrategyOutput()!=null).flatMap(d -> Arrays.asList(d.getStrategyOutput().split("<br/>")).stream()).distinct().collect(Collectors.joining("<br/>")));
            //map.put("strategy", detail.getStrategyDate() + "<br/>" + Strategies.getStrategy(detail.getStrategyCode()).getName() + "<br/>-----<br/>" + detail.getStrategyOutput());
            //map.put("text", detail.getText());
            map.put("code", stock.getCode());
            map.put("market", stock.getMarket());
            map.put("nameAndCodeWithLink", stock.getNameAndCodeWithLink());
            map.put("dayBarImage", stock.getDayBarImage());
            map.put("dayFlowImage", stock.getDayFlowImage());
            map.put("weekBarImage", stock.getWeekBarImage());
            map.put("monthBarImage", stock.getMonthBarImage());
            map.put("tags", stock.getTags());
            map.put("statuses", stock.getStatuses());
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
                    bkMap.put("bkRpsStockCode", bkRpsStockCodeArray==null?"":bkRpsStockCodeArray[i]);
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

    public List<String> getHotBks(){
        StkReportHeaderEntity reportHeader = stkReportHeaderRepository.findTopByTypeOrderByReportDateDesc(StkConstant.REPORT_HEADER_TYPE_BKS);
        return reportHeader.getStkReportDetailEntities().stream().filter(stkReportDetailEntity -> StringUtils.startsWith(stkReportDetailEntity.getStrategyCode(), "strategy_08")).map(StkReportDetailEntity::getCode).distinct().collect(Collectors.toList());
    }

}
