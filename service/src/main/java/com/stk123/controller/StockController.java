package com.stk123.controller;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.entity.*;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Rating;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import com.stk123.model.core.Cache;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.json.View;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.repository.*;
import com.stk123.service.StkConstant;
import com.stk123.service.XueqiuService;
import com.stk123.service.core.BarService;
import com.stk123.service.core.DictService;
import com.stk123.service.core.StockService;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/stock")
@CommonsLog
public class StockController {

    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private BarService barService;
    @Autowired
    private StkTextRepository stkTextRepository;
    @Autowired
    private StkNewsRepository stkNewsRepository;
    @Autowired
    private DictService dictService;
    @Autowired
    private StkKlineRepository stkKlineRepository;
    @Autowired
    private StkStatusRepository stkStatusRepository;

    @RequestMapping(value = {"/init"})
    @ResponseBody
    public RequestResult initStockAndBk(){
        Cache.initAll();
        return RequestResult.success(true);
    }
    @RequestMapping(value = {"/inited"})
    @ResponseBody
    public RequestResult inited(){
        return RequestResult.success(Cache.inited);
    }

    @RequestMapping(value = {"/list/{market:1|2|3|cn|us|hk}/{cate}"})
    @ResponseBody
    public RequestResult list(@PathVariable(value = "market", required = false)String market,
                               @PathVariable(value = "cate", required = false)Integer cate){
        EnumMarket em = EnumMarket.getMarket(market);
        if(em == null){
            return RequestResult.failure("Should not be here.");
        }
        List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(em.getMarket(), cate);
        return RequestResult.success(list);
    }

    @RequestMapping(value = {"/info/{code}"})
    @ResponseBody
    public RequestResult info(@PathVariable(value = "code")String code){
        return RequestResult.success(stockService.findInfo(code));
    }

    @RequestMapping(value = {"/updatekline/{code}"})
    @ResponseBody
    public RequestResult updateKline(@PathVariable(value = "code")String code){
        try {
            Stock stock = stockService.buildStocks(code).stream().findFirst().get();
            log.info("update k line start:"+code);
            barService.updateKline(stock, Integer.MAX_VALUE);
            log.info("update k line end:"+code);
        } catch (Exception e) {
            log.error("",e);
            return RequestResult.success(e.getMessage());
        }
        return RequestResult.success();
    }

    @RequestMapping(value = {"/score/{code}"})
    @ResponseBody
    public RequestResult score(@PathVariable(value = "code")String code){
        Stock stock = Stock.build(code);
        Rating rating = stock.getRating();
        return RequestResult.success(rating);
    }

    @RequestMapping(value = {"/score"})
    @ResponseBody
    public RequestResult score1(@RequestParam(value = "from", required = false, defaultValue = "0")Double percentileFrom,
                                @RequestParam(value = "to", required = false, defaultValue = "100")Double percentileTo
    ){
        List<Stock> stocks = Cache.getStocksWithBks();
        stocks = stocks.stream().sorted(Comparator.comparing(Stock::getScore, Comparator.reverseOrder())).collect(Collectors.toList());
        List<Map> list = new ArrayList<>();
        int size = stocks.size();
        stocks = stocks.subList((int)(size*(100-percentileTo)/100), (int)(size*(100-percentileFrom)/100));
        for(Stock stock : stocks){
            Map map = new HashMap();
            map.put("code", stock.getNameAndCode());
            map.put("rating", stock.getRating().toMap());
            list.add(map);
        }
        Map result = new HashMap();
        result.put("codes", StringUtils.join(stocks.stream().map(Stock::getCode).collect(Collectors.toList()), ","));
        result.put("stocks", list);
        return RequestResult.success(result);
    }

    @RequestMapping(value = {"/clear"})
    @ResponseBody
    public RequestResult clear(){
        Cache.clear();
        return RequestResult.success();
    }

    @RequestMapping(value = {"/{code}", "/rps/{rpsCode}/{code}"})
    @ResponseBody
    @JsonView(View.All.class)
    public RequestResult stocks(@PathVariable(value = "code")String code,
                                @PathVariable(value = "rpsCode", required = false)String rpsCode){
        String[] stks = StringUtils.split(code, ",");
        //List<Stock> stocks = stockService.buildStocks(stks);
        //stocks = stockService.getStocksWithBks(stocks, Stocks.getBks(), 60, false);
        List<Stock> stocks = stockService.getStocks(Arrays.asList(stks));

        Map result = null;
        if(StringUtils.isNotEmpty(rpsCode)) {
            List<StrategyResult> srs = stockService.calcRps(stocks, Rps.CODE_STOCK_SCORE);
            result = stockService.getStrategyResultAsMap(srs);
        }else{
            result = stockService.getStocksAsMap(stocks);
            if(stocks.size() == 1){
                String stkCode = stocks.get(0).getCode();
                StkKlineEntity stkKlineEntity = stkKlineRepository.findTop1ByCodeOrderByKlineDateDesc(stkCode);
                result.put("k", stkKlineEntity);
                StkEntity stkEntity = stkRepository.findByCode(stkCode);
                result.put("stk", stkEntity);
            }
        }
        return RequestResult.success(result);
    }

    @RequestMapping(value = "/news/{code}")
    @ResponseBody
    public RequestResult news(@PathVariable(value = "code")String code,
                              @RequestParam(value = "start", required = false)String start,
                              @RequestParam(value = "end", required = false)String end){
        Date dateStart = start == null ? CommonUtils.addDay(new Date(), -365) : CommonUtils.parseDate(start);
        Date dateEnd = end == null ? new Date() : CommonUtils.parseDate(end);
        List<StkNewsEntity> news = stkNewsRepository.findAllByCodeAndInfoCreateTimeBetweenOrderByInsertTimeDesc(code, dateStart, dateEnd);
        return RequestResult.success(getNewsAsMap(news));
    }

    private List<Map> getNewsAsMap(List<StkNewsEntity> news){
        List<Map> list = new ArrayList<>();
        Map<String, StkDictionaryEntity> dicts = dictService.getDictionaryAsMap(StkConstant.DICT_NEWS);
        for(StkNewsEntity entity : news){
            Map<String, String> map = new HashMap<>();
            map.put("type", "["+dicts.get(entity.getType().toString()).getText()+"]");
            map.put("title", entity.getTitle());
            map.put("createdAt", CommonUtils.formatDate(entity.getInfoCreateTime()));
            map.put("urlTarget", entity.getUrlTarget());
            list.add(map);
        }
        return list;
    }

    @RequestMapping(value = "/xqnotice/{code}")
    @ResponseBody
    @Cached(name = "xueqiu-notice", expire = 600, cacheType = CacheType.LOCAL)
    public RequestResult xueqiuNotice(@PathVariable(value = "code")String code){
        Stock stock = stockService.getStock(code);
        List<Map> notice = getNoticeFromXueqiu(stock);
        return RequestResult.success(notice);
    }

    @SneakyThrows
    private List<Map> getNoticeFromXueqiu(Stock stock) {
        String scode = stock.getCodeWithPlace();
        Map<String, String> headerRequests = XueqiuService.getCookies();
        List<Map> notices = new ArrayList<Map>();
        Date now = new Date();
        int pageNum = 1;
        boolean clearCookie = false;
        do{
            String page = HttpUtils.get("https://xueqiu.com/statuses/stock_timeline.json?symbol_id="+scode+"&count=50&source=%E5%85%AC%E5%91%8A&page="+pageNum,null,headerRequests, "GBK");
            if("400".equals(page) || "404".equals(page)){
                if(!clearCookie){
                    XueqiuService.clearCookie();
                    clearCookie = true;
                    continue;
                }
                break;
            }
            Map m = JsonUtils.testJson(page);
            List<Map> list = (List)m.get("list");
            boolean flag = false;
            for(Map n : list){
                int retweet = Integer.parseInt(String.valueOf(n.get("retweet_count")));
                int reply = Integer.parseInt(String.valueOf(n.get("reply_count")));
                if(retweet > 0 || reply > 0){
                    String createdAt = String.valueOf(n.get("created_at"));
                    Date date = new Date(Long.parseLong(createdAt));
                    //System.out.println(StkUtils.formatDate(date));
                    if(date.before(ServiceUtils.addDay(now, -500))){
                        flag = true;
                        break;
                    }
                    Map map = new HashMap();
                    map.put("reply", CommonUtils.wrapLink(String.valueOf(retweet+reply), "https://xueqiu.com"+n.get("target")));
                    map.put("createdAt", ServiceUtils.formatDate(date));
                    map.put("title", n.get("description"));
                    notices.add(map);
                }
            }
            if(flag){
                break;
            }
            if(pageNum++ >= 100)break;
        }while(true);
        return notices;
    }

    @PostMapping(value = "/status/exclude")
    @ResponseBody
    public RequestResult statusExclude(@RequestBody StkStatusEntity status){
        status.setType(StkConstant.STATUS_TYPE_1);
        status.setValid(1);
        status.setInsertTime(new Date());
        stockService.saveOrUpdateStatus(status);
        return RequestResult.success();
    }
}
