package com.stk123.task.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.entity.*;
import com.stk123.model.core.Bar;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import com.stk123.model.core.Stocks;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.mass.*;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.model.strategy.sample.Strategies;
import com.stk123.model.xueqiu.Portfolio;
import com.stk123.repository.*;
import com.stk123.service.XueqiuService;
import com.stk123.service.core.BacktestingService;
import com.stk123.service.core.BarService;
import com.stk123.service.core.ReportService;
import com.stk123.service.core.StockService;
import com.stk123.task.tool.TaskUtils;
import com.stk123.util.ExceptionUtils;
import com.stk123.util.HttpUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
    private String report;

    private String today = TaskUtils.getToday();//"20160923";
    private final Date now = new Date();
    private int dayOfWeek = TaskUtils.getDayOfWeek(now);
    private boolean isWorkingDay = dayOfWeek == 1 || dayOfWeek == 2 || dayOfWeek == 3 || dayOfWeek == 4 || dayOfWeek == 5;

    /**排除一些垃圾板块**/
    // AB股[BK0498] AH股[BK0499] 上证380[BK0705] 转债标的[BK0528] 新三板[BK0600] 深股通[BK0804] 三板精选[BK0925] 昨日涨停[SZBK0815]
    // B股[BK0636] QFII重仓[BK0535] 沪企改革[BK0672] 富时罗素[BK0867] 标准普尔[BK0879] 债转股[BK0980] 股权激励[BK0567] 融资融券[BK0596]
    // 债转股[BK0980] 养老金[BK0823] 预亏预减[BK0570] 独角兽[BK0835] 基金重仓[BK0536] 创业板综[BK0742] 证金持股[BK0718] 创业成份[BK0638]
    // 沪股通[BK0707] 深成500[BK0568] 预盈预增[BK0571] 送转预期[BK0633] 中证500[BK0701] MSCI中国[BK0821] 机构重仓[BK0552] 次新股[BK0501]
    // 昨日触板[BK0817] HS300_[BK0500] 上证180_[BK0612] 深证100R[BK0743]
    public String BK_REMOVE = "BK0498,BK0499,BK0705,BK0528,BK0600,BK0804,BK0925,BK0816,BK0815," +
            "BK0636,BK0535,BK0672,BK0867,BK0879,BK0980,BK0567,BK0596"+
            "BK0980,BK0823,BK0570,BK0835,BK0536,BK0742,BK0718,BK0638"+
            "BK0707,BK0568,BK0571,BK0633,BK0701,BK0821,BK0552,BK0501"+
            "BK0817,BK0500,BK0612,BK0743";

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
    @Autowired
    private StkCapitalFlowRepository stkCapitalFlowRepository;
    @Autowired
    private ReportService reportService;

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
        this.runByName("analyseMyStocks", this::analyseMyStocks);
        this.runByName("analyseMass", this::analyseMass);
        this.runByName("analyseAllStocks", this::analyseAllStocks);
        this.runByName("analyseBks", this::analyseBks);
        this.runByName("clearAll", this::clearAll);
        this.runByName("test", this::updateCNCapitalFlow);
    }

    public void clearAll(){
        Stocks.clear();
        System.gc();
    }

    public void initCN() {
        stkKlineRepository.deleteAllByKlineDateAfterToday();

        log.info("初始化CN的大盘指数");
        StockBasicProjection scn = null;
        try{
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.CN, EnumCate.INDEX);
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
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.CN, EnumCate.INDEX_eastmoney_gn);
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
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.CN, EnumCate.STOCK);
            log.info("CN initKLines..........start");
            initKLines(list, 8);
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
            log.error("initCN error", e);
            EmailUtils.send("[BarTask出错]个股K线下载出错 code="+ (scn != null ? scn.getCode() : null), e);
        }
    }

    public void initHK() {
        try{
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.HK, EnumCate.STOCK);
            log.info("HK initKLines..........start");
            initKLines(list, 4);
            log.info("HK initKLines..........end");
        }catch(Exception e){
            log.error("initHK error", e);
            EmailUtils.send("Initial HK Stock K Line Error", e);
        }
    }

    public void initUS(){
        log.info("初始化US的大盘指数");
        try{
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.US, EnumCate.INDEX);
            log.info("US index K ..........start");
            initKLines(list, 1);
            log.info("US index K ..........end");
        }catch(Exception e){
            log.error("error", e);
            EmailUtils.send("Initial US Index K Line Error", e);
        }

        log.info("初始化US的个股");
        try{
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.US, EnumCate.STOCK);
            //List<StockBasicProjection> list = stkRepository.findAllByCodes(Arrays.asList("BIDU"));
            log.info("US initKLines..........start");
            initKLines(list, 4);
            log.info("US initKLines..........end");
        }catch(Exception e){
            log.error("initUS error", e);
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
        List<Bar> list = barService.findAllByKlineDateAndCodeIn(today, codes, EnumMarket.CN);

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
        Map<String, BigDecimal> peMap = barService.calcAvgMidPeTtm(today, EnumMarket.HK);
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
        Map<String, BigDecimal> peMap = barService.calcAvgMidPeTtm(today, EnumMarket.US);
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

                    if(stock.isMarketCN()) {
                        updateCNCapitalFlow(stock.getCode());
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

    @ToString
    @Getter
    @Setter
    static class StockWrapper{
        String code;
        //Stock stock;
        Set<String> sources = new LinkedHashSet<>();

        public static StockWrapper of(String code, String source){
            StockWrapper stockWrapper = new StockWrapper();
            stockWrapper.setCode(code);
            if(source != null)
                stockWrapper.sources.add(source);
            return stockWrapper;
        }
    }

    public static void addStock(Set<StockWrapper> set, String code, String source){
        String scode = Stock.getCodeWithoutPlace(code);
        StockWrapper stockWrapper = set.stream().filter(stockWrapper1 -> stockWrapper1.code.equals(scode)).findFirst().orElse(null);
        if(stockWrapper == null){
            set.add(StockWrapper.of(scode, source));
        }else{
            stockWrapper.sources.add(source);
        }
    }
    public static void addStocks(Set<StockWrapper> set, Collection<String> myList, String source) {
        myList.forEach(code -> {
            addStock(set, code, source);
        });
    }



    public void analyseMyStocks(){
        try {
            StkReportHeaderEntity stkReportHeaderEntity = null;
            Set<StockWrapper> allList = new LinkedHashSet<>();

            if(code != null){
                List<String> codes = Arrays.asList(StringUtils.split(code, ","));
                addStocks(allList, codes, null);
            }else {
                XueqiuService.clearFollowStks();
                Set<String> myList = XueqiuService.getFollowStks("全部");
                Set<String> iList = XueqiuService.getFollowStks("我的");
                if (myList == null || myList.isEmpty() || iList == null || iList.isEmpty()) {
                    EmailUtils.send("雪球抓取自选股失败", "雪球抓取自选股失败");
                    return;
                }
                log.info(myList);
                List<String> excludeList = new ArrayList<>(iList).subList(0,20);
                addStocks(allList, myList, "自选股");

                //雪球组合
                List<Portfolio> portfolios = XueqiuService.getPortfolios("6237744859");
                int k = 1;
                for (Portfolio portfolio : portfolios) {
                    log.info(portfolio);
                    try {
                        List<com.stk123.model.xueqiu.Stock> stocks = XueqiuService.getPortfolioStocks(portfolio.getSymbol());
                        portfolio.setStocks(stocks);
                        for (com.stk123.model.xueqiu.Stock stock : stocks) {
                            addStock(allList, stock.getCode(),
                                    CommonUtils.wrapLink(portfolio.getName(), "https://xueqiu.com/P/" + portfolio.getSymbol()) + " [" + stock.getWeight() + "]");
                        }
                        if (k++ % 10 == 0) {
                            Thread.sleep(15 * 1000);
                        }
                    } catch (Exception e) {
                        log.error("雪球抓取自选组合失败:"+portfolio.getSymbol(), e);
                        //return;
                    }
                }

                //雪球牛人自选股
                List<String> uids = ListUtils.createList("7580740929,吉普赛007", "3616204477,最后遇到你",
                        "6930223813,玉山落雨", "6515995346,上善若水1");
                for(String uid : uids){
                    String[] suid = StringUtils.split(uid, ",");
                    List<String> xqOtherStks = XueqiuService.getFollowStksByUid(suid[0]);
                    addStocks(allList, xqOtherStks, CommonUtils.wrapLink(suid[1], "https://xueqiu.com/u/"+suid[0]));
                }

                //成长股
                /*List<StkIndustryEntity> inds = stkIndustryRepository.findAllByIndustry(1783);
                Set<String> growthList = inds.stream().map(StkIndustryEntity::getCode).collect(Collectors.toSet());
                addStocks(allList, growthList, "成长股");*/

                //反转股
                /*List<StkIndustryEntity> inds = stkIndustryRepository.findAllByIndustry(1782);
                Set<String> reverseList = inds.stream().map(StkIndustryEntity::getCode).collect(Collectors.toSet());
                List<Stock> tmpStocks = stockService.buildStocks(new ArrayList<>(reverseList));
                stockService.buildHolder(tmpStocks);
                tmpStocks = filterByHolder(tmpStocks);
                log.info("反转股个数："+tmpStocks.size());
                addStocks(allList, tmpStocks.stream().map(Stock::getCode).collect(Collectors.toSet()), "反转股");*/

                allList = allList.stream().filter(stockWrapper -> !excludeList.contains(stockWrapper.getCode())).collect(Collectors.toSet());
            }

            log.info("allList.size="+allList.size());

            List<Stock> stocks = stockService.buildStocks(allList.stream().map(StockWrapper::getCode).collect(Collectors.toList()));
            if(market != null){
                stocks = stocks.stream().filter(stock -> StringUtils.containsIgnoreCase(market, stock.getMarket().name())).collect(Collectors.toList());
            }
            log.info(stocks.stream().map(Stock::getCode).collect(Collectors.toList()));

            /*stocks = stockService.buildBarSeries(stocks, 500, realtime != null);
            stocks = stockService.buildIndustries(stocks);
            //建立板块关系，计算rps
            stockService.buildBkAndCalcRps(stocks, Stock.EnumMarket.CN, Stock.EnumCate.INDEX_eastmoney_gn);
            stockService.buildHolder(stocks);*/

            stocks = stockService.getStocksWithBks(stocks, EnumMarket.CN, EnumCate.INDEX_eastmoney_gn, realtime!=null);

            String stratgies = Strategies.STRATEGIES_MY_STOCKS;
            if(StringUtils.isNotEmpty(strategy)){
                stratgies = strategy;
            }
            //策略回测开始    01,02 策略在com.stk123.model.strategy.sample.Sample 里定义
            StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
            backtestingService.backtestingOnStock(strategyBacktesting,
                    stocks,
                    Arrays.asList(StringUtils.split(stratgies, ",")),
                    startDate, endDate, realtime!=null);

            List<StrategyResult> results = strategyBacktesting.getPassedStrategyResult();
            if(results.size() > 0){
                List<List<String>> datasA = new ArrayList<>();
                List<List<String>> datasH = new ArrayList<>();
                List<List<String>> datasU = new ArrayList<>();

                Set<String> codeA = new LinkedHashSet<>();
                Set<String> codeH = new LinkedHashSet<>();
                Set<String> codeU = new LinkedHashSet<>();

                if(StringUtils.isNotEmpty(report)){
                    StrategyResult strategyResult = results.get(0);
                    stkReportHeaderEntity = reportService.createReportHeaderEntity(strategyResult.getDate(), "mystocks", realtime!=null?1:0, "自选股策略");
                }

                String rowCode = null;
                for(StrategyResult strategyResult : results){
                    Stock stock = strategyResult.getStock(); //stocks.stream().filter(stk -> stk.getCode().equals(strategyResult.getCode())).findFirst().orElse(null);

                    List<String> sources = new ArrayList<>();
                    for (StockWrapper stockWrapper : allList) {
                        if (stockWrapper.getCode().equals(stock.getCode())) {
                            Set<String> stockWrapperSources = stockWrapper.getSources();
                            sources.addAll(stockWrapperSources);
                        }
                    }

                    StrategyBacktesting backtestingAllHistory = backtestingService.backtestingAllHistory(stock.getCode(), strategyResult.getStrategy().getCode(), false);

                    boolean displayCode = true;
                    if(stock.getCode().equals(rowCode)){
                        displayCode = false;
                    }else{
                        rowCode = stock.getCode();
                    }

                    Stock.BkInfoList bkInfoList = stock.getBkInfos(15, Rps.CODE_BK_60, Rps.CODE_BK_STOCKS_SCORE_30);
                    List<String> data = ListUtils.createList(
                            displayCode ? stock.getNameAndCodeWithLinkAndBold() + bkInfoList.toHtml() : "",
                            strategyResult.getDate()+
                                    "<br/>"+strategyResult.getStrategy().getName().replaceAll("，", "<br/>")+
                                    "<br/>"+StringUtils.join(sources, "<br/>"),
                            displayCode?stock.getDayBarImage():"",displayCode?stock.getWeekBarImage():"",
                            //backtesting.getStrategies().get(0).getPassRateString().replaceAll("]", "]<br/>") +
                            //        (StringUtils.isNotEmpty(backtesting.getStrategies().get(0).getPassedFilterResultLog()) ? "<br/>"+backtesting.getStrategies().get(0).getPassedFilterResultLog() : "")
                            backtestingAllHistory.getStrategies().get(0).getPassRateString().replaceAll("]", "]<br/>")
                    );

                    if(stkReportHeaderEntity != null){
                        StkReportDetailEntity stkReportDetailEntity = reportService.createReportDetailEntity(stock.getCode(),strategyResult.getStrategy().getCode(), strategyResult.getDate(),
                                StringUtils.join(sources, "<br/>")+";"+StringUtils.join(strategyResult.getResults(),"<br/>"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> bkInfo.getBkSr().getStrategy().getCode()).collect(Collectors.toList()), ";"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> CommonUtils.numberFormat2Digits(bkInfo.getBkSr().getPercentile())).collect(Collectors.toList()), ";"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> bkInfo.getBkSr().getStock().getCode()).collect(Collectors.toList()), ";"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> bkInfo.getStockInfoList().getCodes()).collect(Collectors.toList()), ";"),
                                backtestingAllHistory.getStrategies().get(0).getPassRateString()
                                );
                        stkReportHeaderEntity.addDetail(stkReportDetailEntity);
                    }

                    if(stock.isMarketCN()) {
                        datasA.add(data);
                        codeA.add(stock.getCode());
                    }else if(stock.isMarketHK()){
                        datasH.add(data);
                        codeH.add(stock.getCode());
                    }else if(stock.isMarketUS()){
                        datasU.add(data);
                        codeU.add(stock.getCode());
                    }
                }

                if(stkReportHeaderEntity != null){
                    reportService.save(stkReportHeaderEntity);
                }

                List<String> titles = ListUtils.createList("标的", "日期/策略/来源", "日K线", "周K线", "历史策略回测通过率");
                StringBuffer sb = new StringBuffer();
                sb.append("A: ").append(CommonUtils.k("查看", "自选股A", codeA)).append("<br/>");
                sb.append("H: ").append(CommonUtils.k("查看", "自选股H", codeH)).append("<br/>");
                sb.append("U: ").append(CommonUtils.k("查看", "自选股U", codeU)).append("<br/><br/>");

                sb.append("A股");        sb.append(CommonUtils.createHtmlTable(titles, datasA));sb.append("<br/>");
                sb.append("H股");        sb.append(CommonUtils.createHtmlTable(titles, datasH));sb.append("<br/>");
                sb.append("美股");       sb.append(CommonUtils.createHtmlTable(titles, datasU));sb.append("<br/>");

                EmailUtils.send((realtime!=null?"[实时]":"")+"自选股策略发现 A股" + (datasA.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个, " +
                                "H股" + (datasH.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个, " +
                                "美股"+ (datasU.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个"
                        , sb.toString());
            }else{
                EmailUtils.send((realtime!=null?"[实时]":"")+"自选股策略发现0个标的", "");
            }
        } catch (Exception e) {
            EmailUtils.send("报错[analyseMyStocks]", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseMyStocks", e);
        }
    }


    public void analyseAllStocks(){
        try {
            StkReportHeaderEntity stkReportHeaderEntity = null;
            List<Stock> stocks = Stocks.getStocksWithBks();
            //stockService.buildHolder(StocksAllCN);

            String strategies = Strategies.STRATEGIES_ALL_STOCKS;
            if(StringUtils.isNotEmpty(strategy)){
                strategies = strategy;
            }

            StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
            backtestingService.backtestingOnStock(strategyBacktesting,
                    stocks,
                    Arrays.asList(StringUtils.split(strategies, ",")),
                    startDate, endDate, realtime!=null);

            List<StrategyResult> results = strategyBacktesting.getPassedStrategyResult();
            if(results.size() > 0) {
                List<List<String>> datasA = new ArrayList<>();
                Set<String> codeA = new LinkedHashSet<>();

                if(StringUtils.isNotEmpty(report)){
                    StrategyResult strategyResult1 = results.get(0);
                    stkReportHeaderEntity = reportService.createReportHeaderEntity(strategyResult1.getDate(), "allstocks", realtime!=null?1:0, "全市场策略");
                }

                String rowCode = null;
                for (StrategyResult strategyResult : results) {
                    Stock stock = strategyResult.getStock(); //stocks.stream().filter(stk -> stk.getCode().equals(strategyResult.getCode())).findFirst().orElse(null);

                    StrategyBacktesting backtestingAllHistory = backtestingService.backtestingAllHistory(stock.getCode(), strategyResult.getStrategy().getCode(), false);

                    boolean displayCode = true;
                    if(stock.getCode().equals(rowCode)){
                        displayCode = false;
                    }else{
                        rowCode = stock.getCode();
                    }

                    Stock.BkInfoList bkInfoList = stock.getBkInfos(15, Rps.CODE_BK_60, Rps.CODE_BK_STOCKS_SCORE_30);
                    List<String> data = ListUtils.createList(
                            displayCode ? stock.getNameAndCodeWithLinkAndBold() + bkInfoList.toHtml() : "",
                            strategyResult.getDate()+"<br/>"
                                    + strategyResult.getStrategy().getName().replaceAll("，", "<br/>")
                                    + "<br/>-----------<br/>" + StringUtils.join(strategyResult.getResults(),"<br/>"),
                            displayCode?stock.getDayBarImage():"",displayCode?stock.getWeekBarImage():"",
                            //backtesting.getStrategies().get(0).getPassRateString().replaceAll("]", "]<br/>") +
                            //        (StringUtils.isNotEmpty(backtesting.getStrategies().get(0).getPassedFilterResultLog()) ? "<br/>"+backtesting.getStrategies().get(0).getPassedFilterResultLog() : "")
                            backtestingAllHistory.getStrategies().get(0).getPassRateString().replaceAll("]", "]<br/>")
                    );

                    if(stock.isMarketCN()) {
                        datasA.add(data);
                        codeA.add(stock.getCode());
                    }

                    if(stkReportHeaderEntity != null){
                        StkReportDetailEntity stkReportDetailEntity = reportService.createReportDetailEntity(stock.getCode(),strategyResult.getStrategy().getCode(), strategyResult.getDate(),
                                StringUtils.join(strategyResult.getResults(),"<br/>"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> bkInfo.getBkSr().getStrategy().getCode()).collect(Collectors.toList()), ";"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> CommonUtils.numberFormat2Digits(bkInfo.getBkSr().getPercentile())).collect(Collectors.toList()), ";"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> bkInfo.getBkSr().getStock().getCode()).collect(Collectors.toList()), ";"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> bkInfo.getStockInfoList().getCodes()).collect(Collectors.toList()), ";"),
                                backtestingAllHistory.getStrategies().get(0).getPassRateString()
                        );
                        stkReportHeaderEntity.addDetail(stkReportDetailEntity);
                    }
                }

                // report save
                if(stkReportHeaderEntity != null){
                    reportService.save(stkReportHeaderEntity);
                }

                // rps start
                StringBuffer rps = new StringBuffer();
                if(realtime == null) {
                    List<Strategy> rpsList = Rps.getAllRpsStrategyOnStock();

                    for(Strategy rpsStrategy : rpsList){
                        //List<StrategyResult> srs = stockService.calcRps(stocks, rpsStrategy.getCode());
                        List<Stock> rpsStocks = stockService.calcRps(stocks, rpsStrategy.getCode()).stream().map(StrategyResult::getStock).collect(Collectors.toList());
                        rpsStocks = rpsStocks.subList(0, Math.min(150, rpsStocks.size()));
                        rps.append(rpsStrategy.getNameWithCode() + ": " + CommonUtils.k("查看", rpsStrategy.getNameWithCode(), rpsStocks.stream().map(Stock::getCode).collect(Collectors.toList())));
                        rps.append("<br/>");
                    }
                }
                // rps end

                StringBuffer sb = new StringBuffer();
                sb.append(rps).append("<br/>");
                sb.append("A: ").append(CommonUtils.k("查看", "全市场A", codeA)).append("<br/><br/>");

                sb.append("A股");
                List<String> titles = ListUtils.createList("标的", "日期/策略", "日K线", "周K线", "历史策略回测通过率");
                sb.append(CommonUtils.createHtmlTable(titles, datasA));sb.append("<br/>");

                EmailUtils.send((realtime!=null?"[实时]":"")+"全市场策略发现 A股" + (datasA.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个", sb.toString());
            }else{
                EmailUtils.send((realtime!=null?"[实时]":"")+"全市场策略发现0个标的", "");
            }
        } catch (Exception e) {
            EmailUtils.send("报错[analyseAllStocks]", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseAllStocks", e);
        }
    }


    public void analyseBks(){
        try{
            StkReportHeaderEntity stkReportHeaderEntity = null;
            List<Stock> bks = Stocks.getBksWithStocks();

            String strategies = Strategies.STRATEGIES_BK;
            if(StringUtils.isNotEmpty(strategy)){
                this.strategy = strategies;
            }

            StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
            backtestingService.backtestingOnStock(strategyBacktesting,
                    bks,
                    Arrays.asList(StringUtils.split(strategies, ",")),
                    startDate, endDate, realtime!=null);

            List<StrategyResult> results = strategyBacktesting.getPassedStrategyResult();
            if(results.size() > 0){
                List<List<String>> datasBk1 = new ArrayList<>();
                List<List<String>> datasBk2 = new ArrayList<>();

                if(StringUtils.isNotEmpty(report)){
                    StrategyResult strategyResult = results.get(0);
                    stkReportHeaderEntity = reportService.createReportHeaderEntity(strategyResult.getDate(), "bks", realtime!=null?1:0, "板块策略");
                }

                String rowCode = null;
                for(StrategyResult strategyResult : results){
                    Stock bk = strategyResult.getStock();//bks.stream().filter(stk -> stk.getCode().equals(strategyResult.getCode())).findFirst().orElse(null);

                    Stock.TURNING_POINTS.clear();
                    boolean displayCode = true;
                    if(bk.getCode().equals(rowCode)){
                        displayCode = false;
                    }else{
                        rowCode = bk.getCode();
                    }

                    Stock.StockInfoList stockInfoList = bk.getStocksInfos(15, Rps.CODE_STOCK_SCORE_20);
                    List<String> data = ListUtils.createList(
                            displayCode ? bk.getNameAndCodeWithLinkAndBold() : "",
                            strategyResult.getDate()+
                                    "<br/>"+strategyResult.getStrategy().getName().replaceAll("，", "<br/>"),
                            displayCode ? bk.getDayBarImage() : "",
                            displayCode ? stockInfoList.toHtml(true) : ""
                    );
                    if(bk.isCateIndexEastmoneyGn()) {
                        if (strategyResult.getStrategy().getCode().startsWith("strategy_08")) { //板块阶段强势策略
                            datasBk2.add(data);
                        } else {
                            datasBk1.add(data);
                        }
                    }

                    if(stkReportHeaderEntity != null){
                        StkReportDetailEntity stkReportDetailEntity = reportService.createReportDetailEntity(bk.getCode(), strategyResult.getStrategy().getCode(),
                                strategyResult.getDate(),
                                StringUtils.join(strategyResult.getResults(),"<br/>"),
                                Rps.CODE_STOCK_SCORE_20, null,
                                stockInfoList.getBk().getCode(),
                                stockInfoList.getCodes(),null );
                        stkReportHeaderEntity.addDetail(stkReportDetailEntity);
                    }
                }

                if(stkReportHeaderEntity != null){
                    reportService.save(stkReportHeaderEntity);
                }

                List<String> titles = ListUtils.createList("标的", "日期/策略/来源", "K线", "Rps["+Rps.CODE_STOCK_SCORE_20+"]");
                StringBuffer sb = new StringBuffer();
                sb.append("板块");       sb.append(CommonUtils.createHtmlTable(titles, datasBk1));sb.append("<br/>");
                sb.append("板块阶段强势");sb.append(CommonUtils.createHtmlTable(titles, datasBk2));
                EmailUtils.send("板块策略发现 "+ (datasBk1.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count())+ "个" , sb.toString());
            }


        }catch (Exception e){
            EmailUtils.send("报错[analyseBks]", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseBks", e);
        }
    }


    //相似算法
    public void analyseMass(){
        if(Stocks.StocksMass == null) {
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.CN, EnumCate.STOCK);
            //List<StockBasicProjection> list = stkRepository.findAllByCodes(ListUtils.createList("000630","000650","002038","002740","000651","002070","603876","600373","000002","000920","002801","000726","603588","002791","300474"));
            list = list.stream().filter(stockBasicProjection -> !StringUtils.contains(stockBasicProjection.getName(), "退")).collect(Collectors.toList());

            Stocks.StocksMass = stockService.buildStocksWithProjection(list);
            Stocks.StocksMass = stockService.buildBarSeries(Stocks.StocksMass, 500, realtime != null);
            stockService.buildHolder(Stocks.StocksMass);

            Stocks.StocksMass = Stocks.StocksMass.stream().filter(stock -> {
                try {
                    //250日最低点发生在最近50日内
                    Bar bar250 = stock.getBar().getLowestBar(250, Bar.EnumValue.C);
                    Bar bar50 = stock.getBar().getLowestBar(60, Bar.EnumValue.C);
                    if (bar50.getDate().equals(bar250.getDate())) {
                        return true;
                    }

                    //排除250日新低
                    Bar today = stock.getBar();
                    if(today.getDate().equals(bar250.getDate())){
                        return false;
                    }

                    //250均线 或 120均线 多头排列
                    Bar yesterday = today.yesterday();
                    if (today.getMA(120, Bar.EnumValue.C) >= yesterday.getMA(120, Bar.EnumValue.C)
                            || today.getMA(250, Bar.EnumValue.C) >= yesterday.getMA(250, Bar.EnumValue.C)) {
                        return true;
                    }

                    return false;
                }catch (Exception e){
                    e.getStackTrace();
                    return false;
                }
            }).collect(Collectors.toList());

            //排除 人均持股 20万以下的
            Stocks.StocksMass = filterByHolder(Stocks.StocksMass);

            //排除总市值小于50亿的
            Stocks.StocksMass = StockService.filterByMarketCap(Stocks.StocksMass, 50);

        }

        /*if(StocksH == null) {
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.HK, Stock.EnumCate.STOCK);
            list = list.stream().filter(stockBasicProjection -> !StringUtils.contains(stockBasicProjection.getName(), "退市")).collect(Collectors.toList());

            StocksH = stockService.buildStocksWithProjection(list);
            StocksH = stockService.buildBarSeries(StocksH, 250, realtime != null);
        }*/


        MassResult massResultA = Mass.execute(Stocks.StocksMass);

        int count = massResultA.getCount();
        List<String> titles = ListUtils.createList("标的", "日期", "日K线", "周K线");
        String table = CommonUtils.createHtmlTable(titles, massResultA.getDatas()) + "<br/><br/>";
        
        EmailUtils.send("相似策略发现"+count+"个标的", table);

    }

    public List<Stock> filterByHolder(List<Stock> stocks) {
        return stocks.stream().filter(stock -> {
            StkHolderEntity holder = stock.getHolder();
            if(holder == null){
                return false;
            }
            double holderCount = holder.getHolder()==null?0:holder.getHolder();
            double holderChange = holder.getHolderChange()==null?0:holder.getHolderChange();
            double holdingAmount = holder.getHoldingAmount()==null?0:holder.getHoldingAmount();
            if(holderCount > 0 && holderCount <= 12000){
                return true;
            }
            if(holderChange < -10){
                return true;
            }
            if(holderChange > 15){
                return false;
            }
            if(holdingAmount > 200000 && holderChange < 15){
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    public void updateCNCapitalFlow(){
        updateCNCapitalFlow("002346");
    }

    public void updateCNCapitalFlow(String code){
        log.info("updateCNCapitalFlow:"+code);
        try {
            //http://push2his.eastmoney.com/api/qt/stock/fflow/daykline/get?cb=jQuery1123004606016255487422_1626750887144&lmt=0&klt=101&fields1=f1%2Cf2%2Cf3%2Cf7&fields2=f51%2Cf52%2Cf53%2Cf54%2Cf55%2Cf56%2Cf57%2Cf58%2Cf59%2Cf60%2Cf61%2Cf62%2Cf63%2Cf64%2Cf65&ut=b2884a393a59ad64002292a3e90d46a5&secid=0.002346&_=1626750887145
            long time = new Date().getTime();
            String scode = (Stock.build(code).isPlaceSH()?"1.":"0.") + code;
            String url = "http://push2his.eastmoney.com/api/qt/stock/fflow/daykline/get?cb=jQuery" + time + "&lmt=0&klt=101&fields1=f1%2Cf2%2Cf3%2Cf7&fields2=f51%2Cf52%2Cf53%2Cf54%2Cf55%2Cf56%2Cf57%2Cf58%2Cf59%2Cf60%2Cf61%2Cf62%2Cf63%2Cf64%2Cf65&ut=&secid="+scode+"&_=" + time;
            String page = HttpUtils.get(url, null);
            String json = StringUtils.substringBetween(page, "jQuery"+time+"(", ");");

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.readValue(json, HashMap.class);
            Map data = (Map) map.get("data");
            if(data == null) return;
            List<String> list = (List<String>) data.get("klines");
            for(String s : list){
                String[] ss = s.split(",");
                String date = StringUtils.replace(ss[0], "-", "");
                StkCapitalFlowEntity stkCapitalFlowEntity = stkCapitalFlowRepository.findByCodeAndFlowDate(code, date);
                if(stkCapitalFlowEntity == null) {

                    String mainAmount = ss[1];
                    String mainPercent = ss[6];
                    String superLargeAmount = ss[5];
                    String superLargePercent = ss[10];
                    String largeAmount = ss[4];
                    String largePercent = ss[9];
                    String middleAmount = ss[3];
                    String middlePercent = ss[8];
                    String smallAmount = ss[2];
                    String samllPercent = ss[7];

                    stkCapitalFlowEntity = new StkCapitalFlowEntity();
                    stkCapitalFlowEntity.setCode(code);
                    stkCapitalFlowEntity.setFlowDate(date);
                    stkCapitalFlowEntity.setMainAmount(Double.parseDouble(mainAmount));
                    stkCapitalFlowEntity.setMainPercent(Double.parseDouble(mainPercent));
                    stkCapitalFlowEntity.setSuperLargeAmount(Double.parseDouble(superLargeAmount));
                    stkCapitalFlowEntity.setSuperLargePercent(Double.parseDouble(superLargePercent));
                    stkCapitalFlowEntity.setLargeAmount(Double.parseDouble(largeAmount));
                    stkCapitalFlowEntity.setLargePercent(Double.parseDouble(largePercent));
                    stkCapitalFlowEntity.setMiddleAmount(Double.parseDouble(middleAmount));
                    stkCapitalFlowEntity.setMiddlePercent(Double.parseDouble(middlePercent));
                    stkCapitalFlowEntity.setSmallAmount(Double.parseDouble(smallAmount));
                    stkCapitalFlowEntity.setSmallPercent(Double.parseDouble(samllPercent));
                    stkCapitalFlowEntity.setInsertTime(new Date());

                    stkCapitalFlowRepository.save(stkCapitalFlowEntity);
                }
            }

        }catch (Exception e){
            log.error("updateCapitalFlow error:"+code, e);
        }
    }
}
