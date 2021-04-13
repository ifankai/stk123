package com.stk123.task.schedule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.stk123.common.CommonUtils;
import com.stk123.common.ml.KhivaUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.common.util.ImageUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.entity.StkIndustryEntity;
import com.stk123.entity.StkKlineUsEntity;
import com.stk123.entity.StkPeEntity;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.model.strategy.sample.Sample;
import com.stk123.model.xueqiu.Portfolio;
import com.stk123.repository.*;
import com.stk123.service.XueqiuService;
import com.stk123.service.core.BacktestingService;
import com.stk123.service.core.BarService;
import com.stk123.service.core.StockService;
import com.stk123.task.tool.TaskUtils;
import com.stk123.util.ExceptionUtils;
import com.stk123.util.ServiceUtils;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@CommonsLog
@Service
@Setter
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BarTask extends AbstractTask {

    private String code;
    private String startDate;
    private String endDate;
    private String realtime;
    private String market;
    private String strategy;

    @Autowired
    private StkKlineRepository stkKlineRepository;
    @Autowired
    private StkKlineUsRepository stkKlineUsRepository;
    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private BarService barService;
    @Autowired
    private StkPeRepository stkPeRepository;
    @Autowired
    private StkIndustryRepository stkIndustryRepository;
    @Autowired
    private BacktestingService backtestingService;
    @Autowired
    private StockService stockService;

    public static void main(String[] args) throws Exception {
        BarTask task = new BarTask();
        task.execute("analyse", "common", "today=20200101");
        System.out.println(task.today);
    }

    public void register(){
        this.runByName("initCN", () -> initCN());
        this.runByName("initHK", this::initHK);
        this.runByName("initUS", this::initUS);
        this.runByName("analyseCN", this::analyseCN);
        this.runByName("analyseHK", this::analyseHK);
        this.runByName("analyseUS", this::analyseUS);
        this.runByName("analyseKline", this::analyseKline);
        this.runByName("analyseAllStocks", this::analyseAllStocks);
    }

    public void initCN() {
        stkKlineRepository.deleteAllByKlineDateAfterToday();

        log.info("初始化CN的大盘指数");
        StockBasicProjection scn = null;
        try{
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.CN, Stock.EnumCate.INDEX);
            for (StockBasicProjection codeName : list) {
                log.info(codeName.getCode());
                scn = codeName;
                Stock stock = Stock.build(codeName);
                if(isWorkingDay){
                    barService.initKLines(stock,5);
                }else{
                    barService.initKLines(stock,30);
                }
            }
        }catch(Exception e){
            log.error("error", e);
            EmailUtils.send("[BarTask出错]大盘指数K线下载出错 stk="+ (scn != null ? scn.getCode() : null), e);
        }

        try{
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.CN, Stock.EnumCate.INDEX_eastmoney_gn);
            for (StockBasicProjection codeName : list) {
                log.info(codeName.getCode());
                scn = codeName;
                Stock stock = Stock.build(codeName);
                barService.initKLine(stock);
            }

            /*RetryUtils.retryIfException(new Retry(){
                @Override
                public void run() throws Exception {
                    Industry.updateCapitalFlow(conn, todayK, "gnzjl");
                    Industry.updateCapitalFlow(conn, todayK, "hyzjl");
                }
            });*/

        }catch(Exception e){
            log.error("error", e);
            EmailUtils.send("[BarTask出错]同花顺概念指数K线下载出错 code="+ (scn != null ? scn.getCode() : null), e);
        }

        try {
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.CN, Stock.EnumCate.STOCK);
            log.info("CN initKLines..........start");
            initKLines(list, 4);
            log.info("CN initKLines..........end");

            list = stkRepository.findStockNotExsitingTodayKline();
            for (StockBasicProjection stockBasicProjection : list) {
                try {
                    Stock stk = Stock.build(stockBasicProjection);
                    barService.initKLine(stk);
                } catch (Exception e) {
                    log.error(e);
                    EmailUtils.send("[BarTask出错]修补K线数据出错 code=" + stockBasicProjection.getCode(), e);
                }
            }
        }catch(Exception e){
            log.error("error", e);
            EmailUtils.send("[BarTask出错]个股K线下载出错 code="+ (scn != null ? scn.getCode() : null), e);
        }
    }

    public void initHK() {
        try{
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.HK, Stock.EnumCate.STOCK);
            log.info("HK initKLines..........start");
            initKLines(list, 4);
            log.info("HK initKLines..........end");
        }catch(Exception e){
            log.error("error", e);
            EmailUtils.send("Initial HK Stock K Line Error", e);
        }
    }

    public void initUS(){
        log.info("初始化US的大盘指数");
        try{
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.US, Stock.EnumCate.INDEX);
            log.info("US index K ..........start");
            initKLines(list, 1);
            log.info("US index K ..........end");
        }catch(Exception e){
            log.error("error", e);
            EmailUtils.send("Initial US Index K Line Error", e);
        }

        log.info("初始化US的个股");
        try{
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.US, Stock.EnumCate.STOCK);
            //List<StockBasicProjection> list = stkRepository.findAllByCodes(Arrays.asList("BIDU"));
            log.info("US initKLines..........start");
            initKLines(list, 4);
            log.info("US initKLines..........end");
        }catch(Exception e){
            log.error("error", e);
            EmailUtils.send("Initial US Stock K Line Error", e);
        }


    }

    public void analyseCN() {
        log.info("1. CN calculate pe/pb.");
        Map<String, BigDecimal> peMap = stkKlineRepository.calcAvgMidPeTtm(today);
        Map<String, BigDecimal> pbMap = stkKlineRepository.calcAvgMidPbTtm(today);
        StkPeEntity stkPeEntity = stkPeRepository.findOrCreateById(today);
        stkPeEntity.setReportDate(today);
        Double totalPE = NumberUtils.toDouble(peMap.get("AVG_PE_TTM"));
        stkPeEntity.setTotalPe(totalPE);
        Double midPE = NumberUtils.toDouble(peMap.get("MID_PE_TTM"));
        stkPeEntity.setMidPe(midPE);
        Double totalPB = NumberUtils.toDouble(pbMap.get("AVG_PB_TTM"));
        stkPeEntity.setTotalPb(totalPB);
        Double midPB = NumberUtils.toDouble(pbMap.get("AVG_PB_TTM"));
        stkPeEntity.setMidPb(midPB);
        stkPeRepository.save(stkPeEntity);

        log.info("2. check growth stk average pe "+new Date());
        List<StkIndustryEntity> inds = stkIndustryRepository.findAllByIndustry(1783);
        List<String> codes = inds.stream().map(StkIndustryEntity::getCode).collect(Collectors.toList());
        List<Bar> list = barService.findAllByKlineDateAndCodeIn(today, codes, Stock.EnumMarket.CN);

        double gtotalPE = 0.0;
        double gtotalPB = 0.0;
        int peCnt = 0;
        int pbCnt = 0;
        for(Bar bar : list){
            Double pe = bar.getPeTtm();
            if(pe != null && pe >= 5 && pe <= 200){
                peCnt++;
                gtotalPE += pe;
            }
            Double pb = bar.getPbTtm();
            if(pb != null && pb > 0){
                pbCnt++;
                gtotalPB += pb;
            }
        }
        Double averagePE = gtotalPE/peCnt;
        Double averagePB = gtotalPB/pbCnt;

        stkPeEntity.setAveragePe(averagePE);
        stkPeEntity.setAvgPb(averagePB);
        stkPeRepository.save(stkPeEntity);

        String stotalPE = CommonUtils.numberFormat2Digits(totalPE);
        String smidPE = CommonUtils.numberFormat2Digits(midPE);
        String stotalPB = CommonUtils.numberFormat2Digits(totalPB);
        String smidPB = CommonUtils.numberFormat2Digits(midPB);
        String saveragePE = CommonUtils.numberFormat2Digits(averagePE);
        String saveragePB = CommonUtils.numberFormat2Digits(averagePB);

        String peAndpeg = "市场整体中位PB低点大约是2PB：<br>" +
                "2008年最低点约1700点附近，中位数市净率约2倍。<br>" +
                "2012年前后最低点大约2000点，中位数市净率约2倍。<br>" +
                "2016-2017年前后，大约2366点对应中位数2倍市净率。<br><br>" +
                "成长股最佳PE投资区间是20PE-50PE。<br>" +
                "低于20PE而其业绩却超过30%增长的话，要小心业绩陷阱。<br>" +
                "高于50PE要注意泡沫风险，就不要盲目杀入了。<br>"
                + TaskUtils.createPEAndPEG()+"<br>";

        List<List<String>> pe = new ArrayList<List<String>>();
        List<String> pe1 = new ArrayList<String>();
        pe1.add("");pe1.add("平均PE");pe1.add("中位PE");pe1.add("平均PB");pe1.add("中位PB");pe1.add("统计数");
        pe.add(pe1);
        List<String> pe2 = new ArrayList<String>();
        pe2.add("成长股");pe2.add(saveragePE);pe2.add("");pe2.add(saveragePB);pe2.add("");pe2.add(String.valueOf(peCnt));
        pe.add(pe2);
        List<String> pe3 = new ArrayList<String>();
        pe3.add("全市场");pe3.add(stotalPE);pe3.add(smidPE);pe3.add(stotalPB);pe3.add(smidPB);pe3.add("");
        pe.add(pe3);
        EmailUtils.sendAndReport("平均PE:"+ stotalPE +",中位PE:"+smidPE+",整体PB:"+stotalPB+",中位PB:"+smidPB+
                                      ";成长股平均PE:"+saveragePE+",PB:"+saveragePB+",日期:"+today,
                                 TaskUtils.createHtmlTable(null, pe) + "<br>" + peAndpeg );
    }


    public void analyseHK(){
        Map<String, BigDecimal> peMap = barService.calcAvgMidPeTtm(today, Stock.EnumMarket.HK);
        StkPeEntity stkPeEntity = stkPeRepository.findOrCreateById(today);
        stkPeEntity.setReportDate(today);
        Double avgPe = NumberUtils.toDouble(peMap.get("avg_pe_ttm"));
        stkPeEntity.setResult7(avgPe);
        Double midPE = NumberUtils.toDouble(peMap.get("mid_pe_ttm"));
        stkPeEntity.setResult8(midPE);
        stkPeRepository.save(stkPeEntity);
    }

    public void analyseUS(){
        StkKlineUsEntity stkKlineUsEntity = stkKlineUsRepository.findTop1ByCodeOrderByKlineDateDesc(".DJI");
        today = stkKlineUsEntity.getKlineDate();
        Map<String, BigDecimal> peMap = barService.calcAvgMidPeTtm(today, Stock.EnumMarket.US);
        StkPeEntity stkPeEntity = stkPeRepository.findOrCreateById(today);
        stkPeEntity.setReportDate(today);
        Double avgPe = NumberUtils.toDouble(peMap.get("avg_pe_ttm"));
        stkPeEntity.setResult9(avgPe);
        Double midPE = NumberUtils.toDouble(peMap.get("mid_pe_ttm"));
        stkPeEntity.setResult10(midPE);
        stkPeRepository.save(stkPeEntity);
    }

    //多线程 workers
    public void initKLines(List<StockBasicProjection> stks,int numberOfWorker) throws InterruptedException {

        final CountDownLatch countDownLatch = new CountDownLatch(stks.size());
        ExecutorService exec = Executors.newFixedThreadPool(numberOfWorker);
        for(final StockBasicProjection stk : stks){
            Runnable run = () -> {
                try{
                    Stock stock = Stock.build(stk);
                    log.info("initKLines=="+stock.getCode());
                    if(isWorkingDay || stock.isMarketUS()){
                        barService.initKLine(stock);
                    }else{
                        barService.initKLines(stock, 20);
                    }
                }catch(Exception e){
                    log.error("initKLines", e);
                }finally{
                    countDownLatch.countDown();
                }
            };
            exec.execute(run);
        }
        exec.shutdown();
        countDownLatch.await();

    }

    public void analyseKline(){
        try {
            Set<String> allList = new LinkedHashSet<>();
            Set<String> myList = null;
            Set<String> bkList = null;
            Set<String> growthList = null;
            List<Portfolio> portfolios = null;

            if(code != null){
                allList.addAll(Arrays.asList(StringUtils.split(code, ",")));
            }else {
                myList = XueqiuService.getFollowStks("全部");
                if (myList.isEmpty()) {
                    EmailUtils.send("雪球抓取自选股失败", "雪球抓取自选股失败");
                    return;
                }
                //Set<String> myList = XueqiuService.getFollowStks("我的");
                log.info(myList);
                allList.addAll(myList);

                //雪球组合
                portfolios = XueqiuService.getPortfolios("6237744859");
                int k = 1;
                for (Portfolio portfolio : portfolios) {
                    log.info(portfolio);
                    try {
                        List<com.stk123.model.xueqiu.Stock> stocks = XueqiuService.getPortfolioStocks(portfolio.getSymbol());
                        portfolio.setStocks(stocks);
                        allList.addAll(stocks.stream().map(com.stk123.model.xueqiu.Stock::getCode).collect(Collectors.toList()));
                        if (k++ % 10 == 0) {
                            Thread.sleep(15 * 1000);
                        }
                    } catch (Exception e) {
                        log.error("雪球抓取自选组合失败", e);
                        //return;
                    }
                }

                //成长股
                List<StkIndustryEntity> inds = stkIndustryRepository.findAllByIndustry(1783);
                growthList = inds.stream().map(StkIndustryEntity::getCode).collect(Collectors.toSet());
                allList.addAll(growthList);

                //板块
                if(realtime == null) {//实时行情只关注股票，排除板块
                    List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.CN, Stock.EnumCate.INDEX_eastmoney_gn);
                    bkList = list.stream().map(StockBasicProjection::getCode).collect(Collectors.toSet());
                    //排除一些垃圾板块 AB股[BK0498] AH股[BK0499] 上证380[BK0705] 转债标的[BK0528] 新三板[BK0600] 深股通[BK0804] 三板精选[BK0925]
                    //B股[BK0636] QFII重仓[BK0535] 沪企改革[BK0672] 富时罗素[BK0867] 标准普尔[BK0879] 债转股[BK0980] 股权激励[BK0567] 融资融券[BK0596]
                    bkList = CollectionUtil.removeAny(bkList, "BK0498", "BK0499", "BK0705", "BK0528", "BK0600", "BK0804",
                            "BK0925", "BK0816", "BK0815", "BK0636", "BK0535", "BK0672", "BK0867", "BK0879", "BK0980", "BK0567", "BK0596");
                    allList.addAll(bkList);
                }
            }
            log.info(allList);
            allList = allList.stream().map(Stock::getCodeWithoutPlace).collect(Collectors.toSet());
            log.info(allList);

            List<Stock> stocks = stockService.buildStocks(new ArrayList<>(allList));
            if(market != null){
                stocks = stocks.stream().filter(stock -> StringUtils.containsIgnoreCase(market, stock.getMarket().name())).collect(Collectors.toList());
            }
            log.info(stocks.stream().map(Stock::getCode).collect(Collectors.toList()));

            if(StringUtils.isEmpty(strategy)){
                this.strategy = Sample.STRATEGIES;
            }
            //策略回测开始    01,02 策略在com.stk123.model.strategy.sample.Sample 里定义
            StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
            backtestingService.backtestingOnStock(strategyBacktesting,
                    stocks,
                    Arrays.asList(StringUtils.split(this.strategy, ",")),
                    startDate, endDate, realtime!=null);

            List<StrategyResult> results = strategyBacktesting.getPassedStrategyResult();
            if(results.size() > 0){
                StringBuffer sb = new StringBuffer();

                List<List<String>> datasA = new ArrayList<>();
                List<List<String>> datasH = new ArrayList<>();
                List<List<String>> datasU = new ArrayList<>();
                List<List<String>> datasBk1 = new ArrayList<>();
                List<List<String>> datasBk2 = new ArrayList<>();

                String rowCode = null;
                for(StrategyResult strategyResult : results){
                    Stock stock = stocks.stream().filter(stk -> stk.getCode().equals(strategyResult.getCode())).findFirst().orElse(null);

                    List<String> sources = new ArrayList<>();
                    if(myList != null && myList.contains(strategyResult.getCode())){
                        sources.add("自选股");
                    }
                    if (portfolios != null) {
                        for(Portfolio portfolio : portfolios){
                            List<com.stk123.model.xueqiu.Stock> xqStock = portfolio.getStocks();
                            if(xqStock != null && xqStock.size() > 0) {
                                com.stk123.model.xueqiu.Stock xqStk = xqStock.stream().filter(stk -> stk.getCode() != null && stk.getCode().contains(strategyResult.getCode())).findFirst().orElse(null);
                                if (xqStk != null) {
                                    sources.add(CommonUtils.wrapLink(portfolio.getName(), "https://xueqiu.com/P/" + portfolio.getSymbol()) + " ["+xqStk.getWeight()+"]");
                                }
                            }
                        }
                    }
                    if(growthList != null && growthList.contains(strategyResult.getCode())){
                        sources.add("成长股");
                    }
                    boolean isBk = false;
                    if(bkList.contains(strategyResult.getCode())){
                        sources.add("板块");
                        isBk = true;
                    }

                    Stock.TURNING_POINTS.clear();
                    StrategyBacktesting backtesting = backtestingService.backtestingAllHistory(strategyResult.getCode(), strategyResult.getStrategy().getCode(), false);

                    boolean displayCode = true;
                    if(strategyResult.getCode().equals(rowCode)){
                        displayCode = false;
                    }else{
                        rowCode = strategyResult.getCode();
                    }
                    List<String> data = ListUtils.createList(
                            displayCode ? stock.getNameAndCodeWithLink() : "",
                            strategyResult.getDate(),
                            strategyResult.getStrategy().getName().replaceAll("，", "<br/>"),
                            StringUtils.join(sources, "<br/>"),
                            backtesting.getStrategies().get(0).getPassRateString().replaceAll("]", "]<br/>") +
                                    (StringUtils.isNotEmpty(backtesting.getStrategies().get(0).getPassedFilterResultLog()) ? "<br/>"+backtesting.getStrategies().get(0).getPassedFilterResultLog() : "")
                            );
                    if(isBk){
                        if(strategyResult.getStrategy().getCode().startsWith("strategy_08")){ //板块阶段强势策略
                            datasBk2.add(data);
                        }else {
                            datasBk1.add(data);
                        }
                    }else {

                        if(stock.isMarketCN()) {
                            datasA.add(data);
                        }else if(stock.isMarketHK()){
                            datasH.add(data);
                        }else if(stock.isMarketUS()){
                            datasU.add(data);
                        }
                    }
                }

                List<String> titles = ListUtils.createList("标的", "日期", "策略", "来源", "历史策略回测通过率");
                sb.append("A股");        sb.append(CommonUtils.createHtmlTable(titles, datasA));sb.append("<br/>");
                sb.append("H股");        sb.append(CommonUtils.createHtmlTable(titles, datasH));sb.append("<br/>");
                sb.append("美股");       sb.append(CommonUtils.createHtmlTable(titles, datasU));sb.append("<br/>");
                sb.append("板块");       sb.append(CommonUtils.createHtmlTable(titles, datasBk1));sb.append("<br/>");
                sb.append("板块阶段强势");sb.append(CommonUtils.createHtmlTable(titles, datasBk2));

                sb.append("<br/><br/>--------------------------------------------------------<br/>");
                strategyBacktesting.getStrategies().forEach(strategy -> {
                    sb.append(strategy.toString().replaceAll("\n","<br/>")).append("<br/>");
                });

                sb.append("<br/><br/>--------------------------------------------------------<br/>");
                List<Stock> finalStocks = stocks;
                List missingList = allList.stream().filter(code -> finalStocks.stream().noneMatch(stock -> code.contains(stock.getCode()))).collect(Collectors.toList());
                sb.append("被忽略的标的：<br/>");
                sb.append(StringUtils.join(missingList, ","));

                EmailUtils.send("策略发现 A股" + (datasA.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个, " +
                                              "H股" + (datasH.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个, " +
                                              "美股"+ (datasU.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个, " +
                                              "板块"+ (datasBk1.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count())+ "个"
                        , sb.toString());
            }else{
                EmailUtils.send("策略发现0个标的", "");
            }
        } catch (Exception e) {
            EmailUtils.send("策略发现标的报错", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseKline", e);
        }
    }

    public void analyseAllStocks(){
        //List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.CN, Stock.EnumCate.STOCK);
        List<StockBasicProjection> list = stkRepository.findAllByCodes(ListUtils.createList(""));
        List<Stock> allList = stockService.buildStocksWithProjection(list);
        allList = stockService.buildBarSeries(allList, 250);

        List<Stock> stocks = new ArrayList<>();
        List<double[]> array = new ArrayList<>();
        for(Stock stock : allList){
            Bar bar = stock.getBar();
            List<Double> close = bar.map(50, bar1 -> bar1.getMA(5, Bar.EnumValue.C));
            double[] doubles = close.stream().mapToDouble(Double::doubleValue).toArray();
            if(doubles.length == 50){
                stocks.add(stock);
                array.add(doubles);
            }
        }

        Stock stock_002572 = Stock.build("000408");
        Bar a = stock_002572.getBarSeries().getBar("20210326");
        List<Double> close = a.map(50, bar1 -> bar1.getMA(5, Bar.EnumValue.C));
        double[] query = close.stream().mapToDouble(Double::doubleValue).toArray();

        double[] distances = KhivaUtils.mass(array, query);
        int[] indexes = KhivaUtils.getIndexesOfMin(distances, 5);
        Arrays.stream(indexes).forEach(idx -> {
            System.out.println("index:"+idx+", stock:"+stocks.get(idx).getNameAndCode());
        });

        List<List<String>> datas = new ArrayList<>();
        List<String> titles = ListUtils.createList("标的", "日期", "相似标的1","相似标的2","相似标的3");

        String imageStr = ImageUtils.getImageStr(ServiceUtils.getResourceFileAsBytes("similar_stock_image/ST藏格[SZ000408]-20210326.png"));
        String imageHtml = CommonUtils.getImgBase64(imageStr, 450, 300);
        List<String> data = ListUtils.createList(stock_002572.getNameAndCodeWithLink() + imageHtml, "",
                stocks.get(0).getNameAndCodeWithLink()+", distance:"+distances[indexes[0]]+stocks.get(0).getDayBarImage(),
                stocks.get(1).getNameAndCodeWithLink()+", distance:"+distances[indexes[1]]+stocks.get(1).getDayBarImage(),
                stocks.get(2).getNameAndCodeWithLink()+", distance:"+distances[indexes[2]]+stocks.get(2).getDayBarImage());
        datas.add(data);

        StringBuilder sb = new StringBuilder();
        sb.append(CommonUtils.createHtmlTable(titles, datas));

        EmailUtils.send("相似标的策略", sb.toString());
    }
}
