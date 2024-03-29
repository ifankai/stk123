package com.stk123.task.schedule;

import cn.hutool.extra.ssh.JschUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Session;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.common.util.HtmlUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.entity.*;
import com.stk123.model.core.Bar;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import com.stk123.model.core.Cache;
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
import com.stk123.service.StkConstant;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.htmlparser.tags.TableTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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
    private String ampm;

    private String today = TaskUtils.getToday();//"20160923";
    private final Date now = new Date();
    private int dayOfWeek = TaskUtils.getDayOfWeek(now);
    private boolean isWorkingDay = dayOfWeek == 1 || dayOfWeek == 2 || dayOfWeek == 3 || dayOfWeek == 4 || dayOfWeek == 5;


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
    @Autowired
    private StkReportHeaderRepository stkReportHeaderRepository;
    @Autowired
    private StkReportDetailRepository stkReportDetailRepository;

    public static void main(String[] args) throws Exception {
        BarTask task = new BarTask();
        task.execute("analyse", "common", "today=20200101");
        System.out.println(task.today);
    }

    public void register(){
        this.runByName("initCN_Index", () -> initCN_Index());
        this.runByName("initCN_Index_eastmoney_gn", this::initCN_Index_eastmoney_gn);
        this.runByName("initCN_Stock", this::initCN_Stock);
        this.runByName("initHK", this::initHK);
        //this.runByName("updateHKCapitalFlow", this::updateHKCapitalFlow);
        this.runByName("initUS", this::initUS);
        this.runByName("analyseCN", this::analyseCN);
        this.runByName("analyseHK", this::analyseHK);
        this.runByName("analyseUS", this::analyseUS);
        this.runByName("analyseMyStocks", this::analyseMyStocks);
        this.runByName("analyseMass", this::analyseMass);
        this.runByName("analyseAllStocks", this::analyseAllStocks);
        this.runByName("analyseAllCNRps", this::analyseAllCNRps);
        this.runByName("analyseAllHKRps", this::analyseAllHKRps);
        this.runByName("analyseAllUSRps", this::analyseAllUSRps);
        this.runByName("analyseCNRpsStocksByStrategies", this::analyseCNRpsStocksByStrategies);
        this.runByName("analyseRpsStocksByStrategy15a", this::analyseRpsStocksByStrategy15a);
        this.runByName("analyseBks", this::analyseBks);
        this.runByName("statAllStocks", this::statAllStocks);
        this.runByName("statHistory", this::statHistory);
        this.runByName("clearAll", this::clearAll);
    }

    public void clearAll(){
        Cache.clear();
        System.gc();
    }

    public void initCN_Index() {
        stkKlineRepository.deleteAllByKlineDateAfterToday();

        log.info("初始化CN的大盘指数");
        StockBasicProjection scn = null;
        try {
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.CN, EnumCate.INDEX);
            for (StockBasicProjection codeName : list) {
                log.info(codeName.getCode());
                scn = codeName;
                Stock stock = Stock.build(codeName);
                if (isWorkingDay) {
                    barService.initKLines(stock, 5);
                } else {
                    barService.initKLines(stock, 30);
                }
            }
        } catch (Exception e) {
            log.error("error", e);
            EmailUtils.send("[BarTask出错]大盘指数K线下载出错 stk=" + (scn != null ? scn.getCode() : null), e);
        }
    }

    public void initCN_Index_eastmoney_gn() {
        StockBasicProjection scn = null;
        try {
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

        } catch (Exception e) {
            log.error("error", e);
            EmailUtils.send("[BarTask出错]同花顺概念指数K线下载出错 code=" + (scn != null ? scn.getCode() : null), e);
        }
    }

    public void initCN_Stock() {
        StockBasicProjection scn = null;
        try {
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.CN, EnumCate.STOCK);
            log.info("CN initKLines..........start");
            initKLines(list, 6);
            log.info("CN initKLines..........end");

            list = stkRepository.findStockNotExsitingTodayKline();
            for (StockBasicProjection stockBasicProjection : list) {
                try {
                    scn = stockBasicProjection;
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

    public void updateHKCapitalFlow() {
        try{
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.HK, EnumCate.STOCK);
            Session session = null;
            try {
                session = JschUtil.getSession(SyncTask.host, SyncTask.port, SyncTask.username, "Kevin181302");

                for (final StockBasicProjection stk : list) {
                    try{
                        //Stock stock = Stock.build(stk);
                        if (!session.isConnected()) {
                            session = JschUtil.getSession(SyncTask.host, SyncTask.port, SyncTask.username, "Kevin181302");
                        }
                        log.info("updateCapitalFlow:" + stk.getCode());

                        String cmd = "source /etc/profile;source ~/.bash_profile;source ~/.bashrc; " + " curl -A 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36 QIHU 360EE' http://www.aastocks.com/sc/stocks/analysis/moneyflow.aspx?symbol=" + stk.getCode() + "\\&type=h";
                        String output = JschUtil.exec(session, cmd, Charset.forName("UTF-8"));
                        //log.info(output);

                        TableTag tab = (TableTag) HtmlUtils.getNodeByAttribute(output, null, "class", "ns2 mar15T");
                        List<List<String>> datas = HtmlUtils.getListFromTable(tab);
                        //System.out.println(datas);
                        if (datas.size() > 2) {
                            for (int i = 2; i < datas.size(); i++) {
                                List<String> row = datas.get(i);
                                String date = org.apache.commons.lang3.StringUtils.replace(row.get(0), "/", "");
                                double mainAmount = CommonUtils.parseAmount(row.get(5));

                                StkCapitalFlowEntity stkCapitalFlowEntity = stkCapitalFlowRepository.findByCodeAndFlowDate(stk.getCode(), date);
                                if (stkCapitalFlowEntity == null) {
                                    stkCapitalFlowEntity = new StkCapitalFlowEntity();
                                    stkCapitalFlowEntity.setCode(stk.getCode());
                                    stkCapitalFlowEntity.setFlowDate(date);
                                    stkCapitalFlowEntity.setMainAmount(mainAmount);
                                    stkCapitalFlowEntity.setInsertTime(new Date());
                                    stkCapitalFlowRepository.save(stkCapitalFlowEntity);
                                }
                            }
                        }
                    }catch (Exception e){
                        log.error("updateHKCapitalFlow error:"+stk.getCode(), e);
                    }
                }
            }finally {
                JschUtil.close(session);
            }

        }catch(Exception e){
            log.error("updateHKCapitalFlow error", e);
            EmailUtils.send("Initial HK Stock CapitalFlow Error", e);
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
        if(!isWorkingDay) return;
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
        EmailUtils.send("平均PE:"+ stotalPE +",中位PE:"+smidPE+",整体PB:"+stotalPB+",中位PB:"+smidPB+
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
        log.info("analyseUS start");
        /*StkKlineUsEntity stkKlineUsEntity = stkKlineUsRepository.findTop1ByCodeOrderByKlineDateDesc(".DJI");
        today = stkKlineUsEntity.getKlineDate();
        Map<String, BigDecimal> peMap = barService.calcAvgMidPeTtm(today, EnumMarket.US);
        StkPeEntity stkPeEntity = stkPeRepository.findOrCreateById(today);
        stkPeEntity.setReportDate(today);
        Double avgPe = NumberUtils.toDouble(peMap.get("avg_pe_ttm"));
        stkPeEntity.setResult9(avgPe); 和 statAllStocks gt120Ma 冲突
        Double midPE = NumberUtils.toDouble(peMap.get("mid_pe_ttm"));
        stkPeEntity.setResult10(midPE);
        stkPeRepository.save(stkPeEntity);*/
        log.info("analyseUS end");
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
                    if(isWorkingDay/* || stock.isMarketUS()*/){
                        barService.initKLine(stock);
                    }else{
                        barService.initKLines(stock, 20);
                    }

                    if(stock.isMarketCN() || stock.isMarketHK()) {
                        stock.updateCapitalFlow();
                    }
                    if(stock.isMarketCN()){
                        stock.updateHkMoney();
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
        log.info("start analyseMyStocks");
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
                /*List<Portfolio> portfolios = XueqiuService.getPortfolios("6237744859");
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
                }*/

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

                //allList = allList.stream().filter(stockWrapper -> !excludeList.contains(stockWrapper.getCode())).collect(Collectors.toSet());

                if(!allList.isEmpty()){
                    StkPeEntity stkPeEntity = stkPeRepository.findFirstByReportDate(report);
                    if(stkPeEntity != null){
                        stkPeEntity.setReportText(allList.stream().map(StockWrapper::getCode).collect(Collectors.joining(",")));
                        stkPeRepository.save(stkPeEntity);
                    }
                }
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

            stocks = StockService.filterByBarDate(stocks, CommonUtils.addDay(new Date(), -30));

            String strategies = Strategies.STRATEGIES_MY_STOCKS;
            if(StringUtils.isNotEmpty(strategy)){
                strategies = strategy;
            }
            //策略回测开始    01,02 策略在com.stk123.model.strategy.sample.Sample 里定义
            StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
            backtestingService.backtestingOnStock(strategyBacktesting,
                    stocks,
                    Arrays.asList(StringUtils.split(strategies, ",")),
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
                    String type = StkConstant.REPORT_HEADER_TYPE_MYSTOCKS; //"mystocks";
                    String name = "自选股策略";
                    if(realtime!=null){
                        type = type+"_"+ampm;
                        name = name+"[盘中"+ampm.toUpperCase()+"]";
                    }
                    stkReportHeaderEntity = reportService.createReportHeaderEntity(report, type, realtime!=null?1:0, name);
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
                                StringUtils.join(sources, "<br/>")+"<br/>"+StringUtils.join(strategyResult.getResults(),"<br/>"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> bkInfo.getBkSr().getStrategy().getCode()).collect(Collectors.toList()), ";"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> CommonUtils.numberFormat2Digits(bkInfo.getBkSr().getPercentile())).collect(Collectors.toList()), ";"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> bkInfo.getBkSr().getStock().getCode()).collect(Collectors.toList()), ";"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> bkInfo.getStockInfoList().getCodes()).collect(Collectors.toList()), ";"),
                                backtestingAllHistory.getStrategies().get(0).getPassRateString(), null, null
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

                EmailUtils.send((realtime!=null?"[**实时**]":"")+"自选股策略发现 A股" + (datasA.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个, " +
                                "H股" + (datasH.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个, " +
                                "美股"+ (datasU.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个"
                        , sb.toString());
            }else{
                EmailUtils.send((realtime!=null?"[**实时**]":"")+"自选股策略发现0个标的", "");
            }
        } catch (Exception e) {
            EmailUtils.send("报错[analyseMyStocks]", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseMyStocks", e);
        }
        log.info("end analyseMyStocks");
    }


    public void analyseAllStocks(){
        log.info("start analyseAllStocks");
        try {
            StkReportHeaderEntity stkReportHeaderEntity = null;
            List<Stock> stocks = Cache.getStocksWithBks();
            //stockService.buildHolder(StocksAllCN);
            stocks = StockService.filterByMarketCap(stocks, 30);
            stocks = StockService.filterByFn(stocks);
            stocks = StockService.filterByStatusExclude(stocks);

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
                    String type = StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS; //"allstocks";
                    String name = "全市场策略";
                    if(realtime!=null){
                        type = type+"_"+ampm;
                        name = name+"[盘中"+ampm.toUpperCase()+"]";
                    }
                    stkReportHeaderEntity = reportService.createReportHeaderEntity(report, type, realtime!=null?1:0, name);
                }

                results = results.stream().sorted(Comparator.comparing(sr -> sr.getStock().getCode())).collect(Collectors.toList());

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
                                backtestingAllHistory.getStrategies().get(0).getPassRateString(), null, null
                        );
                        stkReportHeaderEntity.addDetail(stkReportDetailEntity);
                    }
                }

                // report save
                if(stkReportHeaderEntity != null){
                    reportService.save(stkReportHeaderEntity);
                }

                StringBuffer sb = new StringBuffer();
                sb.append("A: ").append(CommonUtils.k("查看", "全市场A", codeA)).append("<br/><br/>");

                sb.append("A股");
                List<String> titles = ListUtils.createList("标的", "日期/策略", "日K线", "周K线", "历史策略回测通过率");
                sb.append(CommonUtils.createHtmlTable(titles, datasA));sb.append("<br/>");

                EmailUtils.send((realtime!=null?"[**实时**]":"")+"全市场策略发现 A股" + (datasA.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个", sb.toString());
            }else{
                EmailUtils.send((realtime!=null?"[**实时**]":"")+"全市场策略发现0个标的", "");
            }
        } catch (Exception e) {
            EmailUtils.send("报错[analyseAllStocks]", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseAllStocks", e);
        }
        log.info("end analyseAllStocks");
    }

    public void analyseAllCNRps(){
        log.info("start analyseAllCNRps");
        try {
            String reportDateStart7 = CommonUtils.formatDate(CommonUtils.addDayOfWorking(new Date(), -3), CommonUtils.sf_ymd2);
            String reportDateStart30 = CommonUtils.addDay2String(new Date(), -30);
            String reportDateEnd = CommonUtils.addDay2String(new Date(), -1);

            List<StkReportHeaderEntity> headers7 = stkReportHeaderRepository.findAllByTypeAndReportDateBetweenOrderByInsertTimeDesc(StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS, reportDateStart7, reportDateEnd);
            List<StkReportDetailEntity> detailsIn7 = headers7.stream().flatMap(header -> header.getStkReportDetailEntities().stream()).collect(Collectors.toList());
            List<StkReportHeaderEntity> headers30 = stkReportHeaderRepository.findAllByTypeAndReportDateBetweenOrderByInsertTimeDesc(StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS, reportDateStart30, reportDateEnd);
            List<StkReportDetailEntity> detailsIn30 = headers30.stream().flatMap(header -> header.getStkReportDetailEntities().stream()).collect(Collectors.toList());


            StkReportHeaderEntity stkReportHeaderEntity = null;
            List<Stock> stocks = Cache.getStocksWithBks();

            stocks = StockService.filterByMarketCap(stocks, 30);
            stocks = StockService.filterByFn(stocks);
            stocks = StockService.filterByStatusExclude(stocks);
            // 15天涨幅大于 60% 的过滤掉
            stocks = StockService.filterByBarChange(stocks,15, 40); //排除3周大于50
            stocks = StockService.filterByBarChange(stocks,80, 150); //排除3个月大于150
            stocks = StockService.filterByBarChange(stocks,360, 300); //排除一年半大于3倍

            //stocks = StockService.filterByHoldingAmount(stocks, 10_0000); //过滤掉人均持股小于10万的股票

            if(StringUtils.isNotEmpty(report)){
                String type = StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS;
                String name = "全市场RPS";
                stkReportHeaderEntity = reportService.createReportHeaderEntity(report, type, 0, name);
            }

            // rps start
            StringBuffer rps = new StringBuffer();
            List<Strategy> rpsList = Rps.getAllRpsStrategyOnStock();
            Set<String> rpsVolume = Arrays.stream(Strategies.RPS_VOLUME.split(",")).collect(Collectors.toSet());

            for(Strategy rpsStrategy : rpsList){
                //List<StrategyResult> srs = stockService.calcRps(stocks, rpsStrategy.getCode());
                if(rpsStrategy.isEmptyStrategy())continue;
                List<StrategyResult> rpsSrs = stockService.calcRps(stocks, rpsStrategy.getCode());
                //List<Stock> rpsStocks = rpsSrs.stream().map(StrategyResult::getStock).collect(Collectors.toList());
                //rpsStocks = rpsStocks.subList(0, Math.min(150, rpsStocks.size()));

                List<Stock> results = new ArrayList<>();
                List<Stock> resultsGreaterThan3 = new ArrayList<>();
                int cap20 = 15;
                int cap30 = 80;
                int cap100 = 55;
                int cap200 = 35;
                int cap500 = 25;
                int total = (cap20+cap30+cap100+cap200+cap500);

                List<String> exclude = null;
                String rpsExclude = Strategies.RPS_EXCLUDE.get(rpsStrategy.getCode());
                if(rpsExclude != null && StringUtils.isNotEmpty(report)){
                    List<StkReportDetailEntity> detailEntities = stkReportDetailRepository.findAllByStrategyDateAndStrategyCodeIn(report, Arrays.stream(StringUtils.split(rpsExclude, ",")).collect(Collectors.toList()));
                    exclude = detailEntities.stream().flatMap(detail -> Arrays.stream(StringUtils.split(detail.getRpsStockCode(), ","))).collect(Collectors.toList());
                }

                for(StrategyResult sr : rpsSrs){
                    Stock stock = sr.getStock();
                    if(CollectionUtils.isNotEmpty(exclude) && exclude.contains(stock.getCode())){
                        continue;
                    }
                    if(rpsVolume.contains(rpsStrategy.getCode()) && sr.getSortableValue() >= 3 && resultsGreaterThan3.size() < total){
                        resultsGreaterThan3.add(stock);
                    }
                    if(stock.getMarketCap() >= 15 && stock.getMarketCap() < 30){
                        if(cap20-- > 0){
                            results.add(stock);
                        }
                    }else if(stock.getMarketCap() >= 30 && stock.getMarketCap() < 100){
                        if(cap30-- > 0){
                            results.add(stock);
                        }
                    }else if(stock.getMarketCap() >= 100 && stock.getMarketCap() < 200){
                        if(cap100-- > 0){
                            results.add(stock);
                        }
                    }else if(stock.getMarketCap() >= 200 && stock.getMarketCap() < 500){
                        if(cap200-- > 0){
                            results.add(stock);
                        }
                    }else if(stock.getMarketCap() >= 500){
                        if(cap500-- > 0){
                            results.add(stock);
                        }
                    }
                }

                rps.append(rpsStrategy.getNameWithCode() + ": " + CommonUtils.k("查看", rpsStrategy.getNameWithCode(), results.stream().map(Stock::getCode).collect(Collectors.toList())) + " ====>" + results.stream().map(Stock::getCode).collect(Collectors.joining(",")));
                rps.append("<br/>");

                if(stkReportHeaderEntity != null){
                    String rpsStockCode = results.stream().map(Stock::getCode).collect(Collectors.joining(","));
                    List<String> codesIn7 = detailsIn7.stream().filter(detail -> detail.getStrategyCode().equals(rpsStrategy.getCode())).flatMap(detail -> Arrays.stream(StringUtils.split(detail.getRpsStockCode()==null?"":detail.getRpsStockCode(), ","))).distinct().collect(Collectors.toList());
                    String output1 = Arrays.stream(StringUtils.split(rpsStockCode, ",")).filter(code -> !codesIn7.contains(code)).distinct().collect(Collectors.joining(","));

                    List<String> codesIn30 = detailsIn30.stream().filter(detail -> detail.getStrategyCode().equals(rpsStrategy.getCode())).flatMap(detail -> Arrays.stream(StringUtils.split(detail.getRpsStockCode()==null?"":detail.getRpsStockCode(), ","))).distinct().collect(Collectors.toList());
                    String output2 = Arrays.stream(StringUtils.split(rpsStockCode, ",")).filter(code -> !codesIn30.contains(code)).distinct().collect(Collectors.joining(","));

                    //量能创历史新高
                    String outputVolumeHighest = null;
                    if(results.size() > 0 && Strategies.STRATEGIES_ON_RPS_14.get(rpsStrategy.getCode()) != null){
                        StrategyBacktesting strategyBacktesting = backtestingService.backtestingOnStock(results, Collections.singletonList(Strategies.STRATEGIES_ON_RPS_14.get(rpsStrategy.getCode())));
                        List<StrategyResult> srResults = strategyBacktesting.getPassedStrategyResult();
                        List<Stock> finalStocks = srResults.stream().map(StrategyResult::getStock).distinct().collect(Collectors.toList());
                        outputVolumeHighest = finalStocks.stream().map(Stock::getCode).collect(Collectors.joining(","));
                    }

                    //从高点调整很久（大于24周/120天）
                    String outputDownLongtime = null;
                    if(results.size() > 0){
                        StrategyBacktesting strategyBacktesting = backtestingService.backtestingOnStock(results, Collections.singletonList(Strategies.STRATEGIES_ON_RPS_16));
                        List<StrategyResult> srResults = strategyBacktesting.getPassedStrategyResult();
                        List<Stock> finalStocks = srResults.stream().map(StrategyResult::getStock).distinct().collect(Collectors.toList());
                        outputDownLongtime = finalStocks.stream().map(Stock::getCode).collect(Collectors.joining(","));
                    }

                    String output3 = resultsGreaterThan3.stream().map(Stock::getCode).collect(Collectors.joining(","));

                    StkReportDetailEntity stkReportDetailEntity = reportService.createReportDetailEntity(null, rpsStrategy.getCode(), report,
                        null, null, null, null, rpsStockCode, null,
                            output1, output2, outputVolumeHighest, outputDownLongtime, output3
                    );
                    stkReportHeaderEntity.addDetail(stkReportDetailEntity);
                }
            }

            // report save
            if(stkReportHeaderEntity != null){
                reportService.save(stkReportHeaderEntity);
            }

            EmailUtils.send("全市场RPS A股", rps.toString());

            // rps end
        } catch (Exception e) {
            EmailUtils.send("报错[analyseAllCNRps]", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseAllCNRps", e);
        }
        log.info("end analyseAllCNRps");
    }

    public void analyseAllHKRps(){
        log.info("start analyseAllHKRps");
        try {
            String reportDateStart7 = CommonUtils.formatDate(CommonUtils.addDayOfWorking(new Date(), -4), CommonUtils.sf_ymd2);
            String reportDateStart30 = CommonUtils.addDay2String(new Date(), -30);
            String reportDateEnd = CommonUtils.addDay2String(new Date(), -1);

            List<StkReportHeaderEntity> headers7 = stkReportHeaderRepository.findAllByTypeAndReportDateBetweenOrderByInsertTimeDesc(StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_HK, reportDateStart7, reportDateEnd);
            List<StkReportDetailEntity> detailsIn7 = headers7.stream().flatMap(header -> header.getStkReportDetailEntities().stream()).collect(Collectors.toList());
            List<StkReportHeaderEntity> headers30 = stkReportHeaderRepository.findAllByTypeAndReportDateBetweenOrderByInsertTimeDesc(StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_HK, reportDateStart30, reportDateEnd);
            List<StkReportDetailEntity> detailsIn30 = headers30.stream().flatMap(header -> header.getStkReportDetailEntities().stream()).collect(Collectors.toList());


            StkReportHeaderEntity stkReportHeaderEntity = null;
            List<Stock> stocks = Cache.getHKStocks();

            stocks = StockService.filterByBarDate(stocks, CommonUtils.addDay(new Date(), -30));

//            stocks = stockService.filterByMarketCap(stocks, 50);
//            stocks = stockService.filterByFn(stocks);
            stocks = StockService.filterByStatusExclude(stocks);
            stocks = StockService.filterByBarChange(stocks,15, 60);
            stocks = StockService.filterByBarChange(stocks,80, 200);

            stocks = StockService.filterByBarAmount(stocks, 500_0000);

            if(StringUtils.isNotEmpty(report)){
                String type = StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_HK;
                String name = "全市场RPS(HK)";
                stkReportHeaderEntity = reportService.createReportHeaderEntity(report, type, 0, name);
            }

            // rps start
            StringBuffer rps = new StringBuffer();
            List<Strategy> rpsList = Rps.getAllRpsStrategyOnStock();
            Set<String> rpsVolume = Arrays.stream(Strategies.RPS_VOLUME.split(",")).collect(Collectors.toSet());

            for(Strategy rpsStrategy : rpsList){
                if(rpsStrategy.isEmptyStrategy())continue;
                List<StrategyResult> rpsSrs = stockService.calcRps(stocks, rpsStrategy.getCode());
                //List<Stock> rpsStocks = stockService.calcRps(stocks, rpsStrategy.getCode()).stream().map(StrategyResult::getStock).collect(Collectors.toList());
                //List<Stock> results = rpsStocks.subList(0, Math.min(150, rpsStocks.size()));

                List<Stock> results = new ArrayList<>();
                List<Stock> resultsGreaterThan5 = new ArrayList<>();
                int cap20 = 25;
                int cap30 = 20;
                int cap100 = 20;
                int cap200 = 25;
                int cap500 = 15;
                int total = (cap20+cap30+cap100+cap200+cap500);

                List<String> exclude = null;
                String rpsExclude = Strategies.RPS_EXCLUDE.get(rpsStrategy.getCode());
                if(rpsExclude != null && StringUtils.isNotEmpty(report)){
                    List<StkReportDetailEntity> detailEntities = stkReportDetailRepository.findAllByStrategyDateAndStrategyCodeIn(report, Arrays.stream(StringUtils.split(rpsExclude, ",")).collect(Collectors.toList()));
                    exclude = detailEntities.stream().flatMap(detail -> Arrays.stream(StringUtils.split(detail.getRpsStockCode(), ","))).collect(Collectors.toList());
                }

                for(StrategyResult sr : rpsSrs){
                    Stock stock = sr.getStock();
                    if(CollectionUtils.isNotEmpty(exclude) && exclude.contains(stock.getCode())){
                        continue;
                    }
                    if(rpsVolume.contains(rpsStrategy.getCode()) && sr.getSortableValue() >= 3 && resultsGreaterThan5.size() < total){
                        resultsGreaterThan5.add(stock);
                    }
                    if(stock.getMarketCap() >= 5 && stock.getMarketCap() < 30){
                        if(cap20-- > 0){
                            results.add(stock);
                        }
                    }else if(stock.getMarketCap() >= 30 && stock.getMarketCap() < 100){
                        if(cap30-- > 0){
                            results.add(stock);
                        }
                    }else if(stock.getMarketCap() >= 100 && stock.getMarketCap() < 200){
                        if(cap100-- > 0){
                            results.add(stock);
                        }
                    }else if(stock.getMarketCap() >= 200 && stock.getMarketCap() < 500){
                        if(cap200-- > 0){
                            results.add(stock);
                        }
                    }else if(stock.getMarketCap() >= 500){
                        if(cap500-- > 0){
                            results.add(stock);
                        }
                    }
                }

                rps.append(rpsStrategy.getNameWithCode() + ": " + CommonUtils.k("查看", rpsStrategy.getNameWithCode(), results.stream().map(Stock::getCode).collect(Collectors.toList())) + " ====>" + results.stream().map(Stock::getCode).collect(Collectors.joining(",")));
                rps.append("<br/>");

                if(stkReportHeaderEntity != null){
                    String rpsStockCode = results.stream().map(Stock::getCode).collect(Collectors.joining(","));
                    List<String> codesIn7 = detailsIn7.stream().filter(detail -> detail.getStrategyCode().equals(rpsStrategy.getCode())).flatMap(detail -> Arrays.stream(StringUtils.split(detail.getRpsStockCode()==null?"":detail.getRpsStockCode(), ","))).distinct().collect(Collectors.toList());
                    String output1 = Arrays.stream(StringUtils.split(rpsStockCode, ",")).filter(code -> !codesIn7.contains(code)).distinct().collect(Collectors.joining(","));

                    List<String> codesIn30 = detailsIn30.stream().filter(detail -> detail.getStrategyCode().equals(rpsStrategy.getCode())).flatMap(detail -> Arrays.stream(StringUtils.split(detail.getRpsStockCode()==null?"":detail.getRpsStockCode(), ","))).distinct().collect(Collectors.toList());
                    String output2 = Arrays.stream(StringUtils.split(rpsStockCode, ",")).filter(code -> !codesIn30.contains(code)).distinct().collect(Collectors.joining(","));

                    //量能创历史新高
                    String outputVolumeHighest = null;
                    if(results.size() > 0 && Strategies.STRATEGIES_ON_RPS_14.get(rpsStrategy.getCode()) != null){
                        StrategyBacktesting strategyBacktesting = backtestingService.backtestingOnStock(results, Collections.singletonList(Strategies.STRATEGIES_ON_RPS_14.get(rpsStrategy.getCode())));
                        List<StrategyResult> srResults = strategyBacktesting.getPassedStrategyResult();
                        List<Stock> finalStocks = srResults.stream().map(StrategyResult::getStock).distinct().collect(Collectors.toList());
                        outputVolumeHighest = finalStocks.stream().map(Stock::getCode).collect(Collectors.joining(","));
                    }

                    String output3 = resultsGreaterThan5.stream().map(Stock::getCode).collect(Collectors.joining(","));

                    StkReportDetailEntity stkReportDetailEntity = reportService.createReportDetailEntity(null, rpsStrategy.getCode(), report,
                            null, null, null, null,
                            results.stream().map(Stock::getCode).collect(Collectors.joining(",")), null, output1, output2, outputVolumeHighest, null, output3
                    );
                    stkReportHeaderEntity.addDetail(stkReportDetailEntity);
                }
            }

            // report save
            if(stkReportHeaderEntity != null){
                reportService.save(stkReportHeaderEntity);
            }

            EmailUtils.send("全市场RPS H股", rps.toString());

            // rps end
        } catch (Exception e) {
            EmailUtils.send("报错[analyseAllHKRps]", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseAllHKRps", e);
        }
        log.info("end analyseAllHKRps");
    }

    public void analyseAllUSRps(){
        log.info("start analyseAllUSRps");
        try {
            String reportDateStart7 = CommonUtils.addDay2String(new Date(), -7);
            String reportDateStart30 = CommonUtils.addDay2String(new Date(), -30);
            String reportDateEnd = CommonUtils.addDay2String(new Date(), -1);

            List<StkReportHeaderEntity> headers7 = stkReportHeaderRepository.findAllByTypeAndReportDateBetweenOrderByInsertTimeDesc(StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_US, reportDateStart7, reportDateEnd);
            List<StkReportDetailEntity> detailsIn7 = headers7.stream().flatMap(header -> header.getStkReportDetailEntities().stream()).collect(Collectors.toList());
            List<StkReportHeaderEntity> headers30 = stkReportHeaderRepository.findAllByTypeAndReportDateBetweenOrderByInsertTimeDesc(StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_US, reportDateStart30, reportDateEnd);
            List<StkReportDetailEntity> detailsIn30 = headers30.stream().flatMap(header -> header.getStkReportDetailEntities().stream()).collect(Collectors.toList());


            StkReportHeaderEntity stkReportHeaderEntity = null;
            Cache.clearUS();
            List<Stock> stocks = Cache.getUSStocks();

            stocks = StockService.filterByBarDate(stocks, CommonUtils.addDay(new Date(), -30));
            stocks = StockService.filterByBarTodayChange(stocks, -20);

//            stocks = stockService.filterByMarketCap(stocks, 50);
//            stocks = stockService.filterByFn(stocks);
            stocks = StockService.filterByStatusExclude(stocks);
            stocks = StockService.filterByBarAmount(stocks, 500_0000);

            if(StringUtils.isNotEmpty(report)){
                String type = StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_US;
                String name = "全市场RPS(US)";
                stkReportHeaderEntity = reportService.createReportHeaderEntity(report, type, 0, name);
            }

            // rps start
            StringBuffer rps = new StringBuffer();
            List<Strategy> rpsList = Rps.getAllRpsStrategyOnStock();
            Set<String> rpsVolume = Arrays.stream(Strategies.RPS_VOLUME.split(",")).collect(Collectors.toSet());

            for(Strategy rpsStrategy : rpsList){
                if(rpsStrategy.isEmptyStrategy())continue;
                List<StrategyResult> rpsSrs = stockService.calcRps(stocks, rpsStrategy.getCode());
                //List<Stock> rpsStocks = stockService.calcRps(stocks, rpsStrategy.getCode()).stream().map(StrategyResult::getStock).collect(Collectors.toList());
                //List<Stock> results = rpsStocks.subList(0, Math.min(150, rpsStocks.size()));

                List<Stock> results = new ArrayList<>();
                List<Stock> resultsGreaterThan3 = new ArrayList<>();
                int cap20 = 20;
                int cap30 = 50;
                int cap100 = 50;
                int cap200 = 30;
                int cap500 = 20;
                int total = (cap20+cap30+cap100+cap200+cap500);

                for(StrategyResult sr : rpsSrs){
                    Stock stock = sr.getStock();
                    if(rpsVolume.contains(rpsStrategy.getCode()) && sr.getSortableValue() >= 3 && resultsGreaterThan3.size() < total){
                        resultsGreaterThan3.add(stock);
                    }
                    if(stock.getMarketCap() >= 10 && stock.getMarketCap() < 30){
                        if(cap20-- > 0){
                            results.add(stock);
                        }
                    }else if(stock.getMarketCap() >= 30 && stock.getMarketCap() < 100){
                        if(cap30-- > 0){
                            results.add(stock);
                        }
                    }else if(stock.getMarketCap() >= 100 && stock.getMarketCap() < 200){
                        if(cap100-- > 0){
                            results.add(stock);
                        }
                    }else if(stock.getMarketCap() >= 200 && stock.getMarketCap() < 500){
                        if(cap200-- > 0){
                            results.add(stock);
                        }
                    }else if(stock.getMarketCap() >= 500){
                        if(cap500-- > 0){
                            results.add(stock);
                        }
                    }
                }

                rps.append(rpsStrategy.getNameWithCode() + ": " + CommonUtils.k("查看", rpsStrategy.getNameWithCode(), results.stream().map(Stock::getCode).collect(Collectors.toList())) + " ====>" + results.stream().map(Stock::getCode).collect(Collectors.joining(",")));
                rps.append("<br/>");

                if(stkReportHeaderEntity != null){
                    String rpsStockCode = results.stream().map(Stock::getCode).collect(Collectors.joining(","));
                    List<String> codesIn7 = detailsIn7.stream().filter(detail -> detail.getStrategyCode().equals(rpsStrategy.getCode())).flatMap(detail -> Arrays.stream(StringUtils.split(detail.getRpsStockCode()==null?"":detail.getRpsStockCode(), ","))).distinct().collect(Collectors.toList());
                    String output1 = Arrays.stream(StringUtils.split(rpsStockCode, ",")).filter(code -> !codesIn7.contains(code)).distinct().collect(Collectors.joining(","));

                    List<String> codesIn30 = detailsIn30.stream().filter(detail -> detail.getStrategyCode().equals(rpsStrategy.getCode())).flatMap(detail -> Arrays.stream(StringUtils.split(detail.getRpsStockCode()==null?"":detail.getRpsStockCode(), ","))).distinct().collect(Collectors.toList());
                    String output2 = Arrays.stream(StringUtils.split(rpsStockCode, ",")).filter(code -> !codesIn30.contains(code)).distinct().collect(Collectors.joining(","));

                    String output3 = resultsGreaterThan3.stream().map(Stock::getCode).collect(Collectors.joining(","));

                    StkReportDetailEntity stkReportDetailEntity = reportService.createReportDetailEntity(null, rpsStrategy.getCode(), report,
                            null, null, null, null,
                            results.stream().map(Stock::getCode).collect(Collectors.joining(",")), null, output1, output2, null, null, output3
                    );
                    stkReportHeaderEntity.addDetail(stkReportDetailEntity);
                }
            }

            // report save
            if(stkReportHeaderEntity != null){
                reportService.save(stkReportHeaderEntity);
            }

            EmailUtils.send("全市场RPS US股", rps.toString());

            // rps end
        } catch (Exception e) {
            EmailUtils.send("报错[analyseAllUSRps]", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseAllUSRps", e);
        }
        log.info("end analyseAllHKRps");
    }

    public void analyseCNRpsStocksByStrategies(){
        log.info("start analyseCNRpsStocksByStrategies");
        try{
            StkReportHeaderEntity stkReportHeaderEntity = null;
            String reportDateStart = CommonUtils.addDay2String(new Date(), -70);
            String reportDateEnd = CommonUtils.addDay2String(new Date(), -10);

            List<StkReportHeaderEntity> headers = stkReportHeaderRepository.findAllByTypeAndReportDateBetweenOrderByInsertTimeDesc(StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS, reportDateStart, reportDateEnd);
            //排除 rps_09,rps_10,rps_11
            List<StkReportDetailEntity> details = headers.stream().flatMap(header -> header.getStkReportDetailEntities().stream().filter(detail -> !StringUtils.contains("rps_09,rps_10,rps_11", detail.getStrategyCode()) )).collect(Collectors.toList());
            List<String> codes = details.stream().flatMap(detail -> Arrays.stream(detail.getRpsStockCode().split(","))).distinct().collect(Collectors.toList());
            List<Stock> stocks = stockService.getStocks(codes);

            String strategies = Strategies.STRATEGIES_ON_RPS;
            if(StringUtils.isNotEmpty(strategy)){
                strategies = strategy;
            }

            StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
            backtestingService.backtestingOnStock(strategyBacktesting, stocks, Arrays.asList(StringUtils.split(strategies, ",")),
                    startDate, endDate, realtime!=null);

            List<StrategyResult> results = strategyBacktesting.getPassedStrategyResult();
            if(results.size() > 0) {
                List<List<String>> datasA = new ArrayList<>();
                Set<String> codeA = new LinkedHashSet<>();
                
                if(StringUtils.isNotEmpty(report)){
                    String type = StkConstant.REPORT_HEADER_TYPE_RPSSTOCKS_STRATEGIES; //"rpsstocks_strategies";
                    String name = "RPS股票策略";
                    stkReportHeaderEntity = reportService.createReportHeaderEntity(report, type, realtime!=null?1:0, name);
                }

                String rowCode = null;
                for (StrategyResult strategyResult : results) {
                    Stock stock = strategyResult.getStock();

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
                            ""
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
                                "", null, null
                        );
                        stkReportHeaderEntity.addDetail(stkReportDetailEntity);
                    }

                }

                // report save
                if(stkReportHeaderEntity != null){
                    reportService.save(stkReportHeaderEntity);
                }

                StringBuffer sb = new StringBuffer();
                sb.append("A: ").append(CommonUtils.k("查看", "RPS股票策略", codeA)).append("<br/><br/>");

                sb.append("A股");
                List<String> titles = ListUtils.createList("标的", "日期/策略", "日K线", "周K线", "");
                sb.append(CommonUtils.createHtmlTable(titles, datasA));sb.append("<br/>");

                EmailUtils.send((realtime!=null?"[**实时**]":"")+"RPS股票策略发现 A股" + (datasA.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个", sb.toString());
            }else{
                EmailUtils.send((realtime!=null?"[**实时**]":"")+"RPS股票策略发现0个标的", "");
            }
            
        } catch (Exception e) {
            EmailUtils.send("报错[analyseCNRpsStocksByStrategies]", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseCNRpsStocksByStrategies", e);
        }
        log.info("end analyseCNRpsStocksByStrategies");
    }

    //k线云梯
    public void analyseRpsStocksByStrategy15a(){
        log.info("start analyseRpsStocksByStrategy15a");
        String title = "RPS股票策略-K线云梯(15a)";
        try{
            StkReportHeaderEntity stkReportHeaderEntity = null;
            String reportDateStart = CommonUtils.addDay2String(new Date(), -12);
            String reportDateEnd = CommonUtils.addDay2String(new Date(), -5);

            List<StkReportHeaderEntity> headers = stkReportHeaderRepository.findAllByTypeAndReportDateBetweenOrderByInsertTimeDesc(StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS, reportDateStart, reportDateEnd);
            List<StkReportDetailEntity> details = headers.stream().flatMap(header -> header.getStkReportDetailEntities().stream().filter(detail -> StringUtils.contains(Strategies.STRATEGIES_ON_RPS_15A, detail.getStrategyCode()) )).collect(Collectors.toList());
            List<String> codes = details.stream().flatMap(detail -> Arrays.stream(detail.getRpsStockCode().split(","))).distinct().collect(Collectors.toList());
            List<Stock> stocks = stockService.getStocks(codes);

            String strategies = "15a";
            if(StringUtils.isNotEmpty(strategy)){
                strategies = strategy;
            }

            StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
            backtestingService.backtestingOnStock(strategyBacktesting, stocks, Arrays.asList(StringUtils.split(strategies, ",")),
                    startDate, endDate, realtime!=null);

            List<StrategyResult> results = strategyBacktesting.getPassedStrategyResult();
            if(results.size() > 0) {
                List<List<String>> datasA = new ArrayList<>();
                Set<String> codeA = new LinkedHashSet<>();

                if(StringUtils.isNotEmpty(report)){
                    String type = StkConstant.REPORT_HEADER_TYPE_ALLSTOCKS_RPS_15A;
                    stkReportHeaderEntity = reportService.createReportHeaderEntity(report, type, realtime!=null?1:0, title);
                }

                String rowCode = null;
                for (StrategyResult strategyResult : results) {
                    Stock stock = strategyResult.getStock();

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
                            ""
                    );

                    datasA.add(data);
                    codeA.add(stock.getCode());

                    if(stkReportHeaderEntity != null){
                        StkReportDetailEntity stkReportDetailEntity = reportService.createReportDetailEntity(stock.getCode(),strategyResult.getStrategy().getCode(), strategyResult.getDate(),
                                StringUtils.join(strategyResult.getResults(),"<br/>"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> bkInfo.getBkSr().getStrategy().getCode()).collect(Collectors.toList()), ";"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> CommonUtils.numberFormat2Digits(bkInfo.getBkSr().getPercentile())).collect(Collectors.toList()), ";"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> bkInfo.getBkSr().getStock().getCode()).collect(Collectors.toList()), ";"),
                                StringUtils.join(bkInfoList.getBkInfos().stream().map(bkInfo -> bkInfo.getStockInfoList().getCodes()).collect(Collectors.toList()), ";"),
                                "", null, null
                        );
                        stkReportHeaderEntity.addDetail(stkReportDetailEntity);
                    }

                }

                // report save
                if(stkReportHeaderEntity != null){
                    reportService.save(stkReportHeaderEntity);
                }

                StringBuffer sb = new StringBuffer();
                sb.append(CommonUtils.k("查看", title, codeA)).append("<br/><br/>");

                sb.append("A股");
                List<String> titles = ListUtils.createList("标的", "日期/策略", "日K线", "周K线", "");
                sb.append(CommonUtils.createHtmlTable(titles, datasA));sb.append("<br/>");

                EmailUtils.send((realtime!=null?"[**实时**]":"")+title+" " + (datasA.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count()) + "个", sb.toString());
            }else{
                EmailUtils.send((realtime!=null?"[**实时**]":"")+title+" 0个标的", "");
            }

        } catch (Exception e) {
            EmailUtils.send("报错[analyseRpsStocksByStrategy15a]", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseRpsStocksByStrategy15a", e);
        }
        log.info("end analyseRpsStocksByStrategy15a");
    }

    public void analyseBks(){
        log.info("start analyseBks");
        try{
            StkReportHeaderEntity stkReportHeaderEntity = null;
            List<Stock> bks = Cache.getBksWithStocks();

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
                    stkReportHeaderEntity = reportService.createReportHeaderEntity(report, StkConstant.REPORT_HEADER_TYPE_BKS, realtime!=null?1:0, "板块策略");
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

                    Stock.StockInfoList stockInfoList = bk.getStocksInfos(15, Rps.CODE_STOCK_SCORE);
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
                        String output1 = null;
                        String output2 = null;
                        if (strategyResult.getStrategy().getCode().startsWith("strategy_08")) {
                            List<StrategyResult> list = (List<StrategyResult>) strategyResult.getStrategy().getStrategyResultsAll();
                            output1 = list.stream().map(StrategyResult::getStockCode).collect(Collectors.joining(","));

                            StkReportDetailEntity stkReportDetailEntity = stkReportDetailRepository.findTopByStrategyCodeOrderByIdDesc(strategyResult.getStrategy().getCode());
                            String output1yesterday = stkReportDetailEntity.getOutput1();
                            if(StringUtils.isNotEmpty(output1yesterday)){
                                int i = 0;
                                Map<String, Integer> output1Map = new LinkedHashMap<>();
                                for(StrategyResult sr : list){
                                    output1Map.put(sr.getStockCode(), i++);
                                }
                                i = 0;
                                List<String> listYesterday = Arrays.asList(StringUtils.split(output1yesterday, ","));
                                for(String str : listYesterday){
                                    int upPosition = output1Map.merge(str, i++, (oldValue, newValue) -> newValue - oldValue);
                                    output1Map.put(str, -upPosition);
                                }
                                // 根据上升位次从高到底排序，然后取前20
                                output2 = output1Map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder())).map(entry -> entry.getKey()+'|'+entry.getValue()).limit(20).collect(Collectors.joining(","));
                            }
                        }
                        StkReportDetailEntity stkReportDetailEntity = reportService.createReportDetailEntity(bk.getCode(), strategyResult.getStrategy().getCode(),
                                strategyResult.getDate(),
                                StringUtils.join(strategyResult.getResults(),"<br/>"),
                                Rps.CODE_STOCK_SCORE, String.valueOf(strategyResult.getPercentile()),
                                stockInfoList.getBk().getCode(),
                                stockInfoList.getCodes(),null , output1, output2);
                        stkReportHeaderEntity.addDetail(stkReportDetailEntity);
                    }
                }

                if(stkReportHeaderEntity != null){
                    reportService.save(stkReportHeaderEntity);
                }

                List<String> titles = ListUtils.createList("标的", "日期/策略/来源", "K线", "Rps["+Rps.CODE_STOCK_SCORE +"]");
                StringBuffer sb = new StringBuffer();
                sb.append("板块");       sb.append(CommonUtils.createHtmlTable(titles, datasBk1));sb.append("<br/>");
                sb.append("板块阶段强势");sb.append(CommonUtils.createHtmlTable(titles, datasBk2));
                EmailUtils.send("板块策略发现 "+ (datasBk1.stream().filter(data -> StringUtils.isNotEmpty(data.get(0))).count())+ "个" , sb.toString());
            }


        }catch (Exception e){
            EmailUtils.send("报错[analyseBks]", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseBks", e);
        }
        log.info("end analyseBks");
    }


    //相似算法
    public void analyseMass(){
        if(Cache.StocksMass == null) {
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.CN, EnumCate.STOCK);
            //List<StockBasicProjection> list = stkRepository.findAllByCodes(ListUtils.createList("000630","000650","002038","002740","000651","002070","603876","600373","000002","000920","002801","000726","603588","002791","300474"));
            list = list.stream().filter(stockBasicProjection -> !StringUtils.contains(stockBasicProjection.getName(), "退")).collect(Collectors.toList());

            Cache.StocksMass = stockService.buildStocksWithProjection(list);
            Cache.StocksMass = stockService.buildBarSeries(Cache.StocksMass, 500, realtime != null);
            stockService.buildHolder(Cache.StocksMass);

            Cache.StocksMass = Cache.StocksMass.stream().filter(stock -> {
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
            Cache.StocksMass = filterByHolder(Cache.StocksMass);

            //排除总市值小于50亿的
            Cache.StocksMass = StockService.filterByMarketCap(Cache.StocksMass, 50);

        }

        /*if(StocksH == null) {
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.HK, Stock.EnumCate.STOCK);
            list = list.stream().filter(stockBasicProjection -> !StringUtils.contains(stockBasicProjection.getName(), "退市")).collect(Collectors.toList());

            StocksH = stockService.buildStocksWithProjection(list);
            StocksH = stockService.buildBarSeries(StocksH, 250, realtime != null);
        }*/


        MassResult massResultA = Mass.execute(Cache.StocksMass);

        int count = massResultA.getCount();
        List<String> titles = ListUtils.createList("标的", "日期", "日K线", "周K线");
        String table = CommonUtils.createHtmlTable(titles, massResultA.getDatas()) + "<br/><br/>";
        
        EmailUtils.send("相似策略发现"+count+"个标的", table);

    }

    public void statAllStocks(){
        log.info("start statAllStocks");
        try{
            String reportDate = this.report;
            if(reportDate == null){
                reportDate = CommonUtils.formatDate(new Date(), CommonUtils.sf_ymd2);
            }
            StkPeEntity entity = stkPeRepository.findFirstByReportDate(reportDate);
            if(entity == null){
                return;
            }
            List<Stock> stocks = Cache.getStocksWithBks();
            int priceLimitUp = 0;
            List<Stock> priceLimitUp2 = new ArrayList<>();
            List<Stock> priceLimitUp3 = new ArrayList<>();
            List<Stock> priceLimitUp4 = new ArrayList<>();
            int priceLimitDown = 0;
            int upCount = 0;
            int downCount = 0;
            int gt20Ma = 0; //20日均线上方
            int gt120Ma = 0; //120日均线上方

            for(Stock stock : stocks){
                Bar bar = stock.getBar();
                if(bar == null) continue;
                if(bar.getChange() > 0){
                    upCount ++;
                }else if(bar.getChange() < 0){
                    downCount ++;
                }
                if(stock.isPriceLimitUp()){
                    int cnt = stock.getPriceLimitUpCount();
                    if(cnt +2 > stock.getBarSize()){ //排除新股涨停板
                        continue;
                    }
                    priceLimitUp ++;
                    if(cnt == 2){
                        priceLimitUp2.add(stock);
                    }else if(cnt == 3){
                        priceLimitUp3.add(stock);
                    }else if(cnt > 3){
                        priceLimitUp4.add(stock);
                    }
                }
                if(stock.isPriceLimitDown()){
                    priceLimitDown ++;
                }

                if(bar.getClose() > bar.getMA(20, Bar.EnumValue.C)){
                    gt20Ma ++;
                }
                if(bar.getClose() > bar.getMA(120, Bar.EnumValue.C)){
                    gt120Ma ++;
                }
            }

            entity.setStockCount(stocks.size());
            entity.setResult1((double)priceLimitUp);
            entity.setResult2((double)priceLimitDown);
            entity.setResult3((double)upCount);
            entity.setResult4((double)downCount);

            entity.setResult5((double)priceLimitUp2.size());
            entity.setResult6((double)priceLimitUp3.size());
            entity.setResult7((double)priceLimitUp4.size());
            entity.setString3(priceLimitUp2.stream().map(Stock::getCode).collect(Collectors.joining(",")));
            entity.setString1(priceLimitUp3.stream().map(Stock::getCode).collect(Collectors.joining(",")));
            entity.setString2(priceLimitUp4.stream().sorted(Comparator.comparing(Stock::getPriceLimitUpCount, Comparator.reverseOrder())).map(stock -> stock.getCode()+'|'+stock.getPriceLimitUpCount()).collect(Collectors.joining(",")));

            entity.setResult8((double)gt20Ma);
            entity.setResult9((double)gt120Ma);

            stkPeRepository.save(entity);
        } catch (Exception e) {
            EmailUtils.send("报错[statAllStocks]", ExceptionUtils.getExceptionAsString(e));
            log.error("statAllStocks", e);
        }
        log.info("end statAllStocks");
    }

    public void statHistory(){
        List<StkKlineEntity> ks = stkKlineRepository.findAllByCodeAndKlineDateAfterOrderByKlineDateDesc("999999", "20210101");
        List<Stock> stocks = Cache.getStocksWithBks();
        for(StkKlineEntity k : ks){
            String date = k.getKlineDate();

            int total = 0;
            int priceLimitUp = 0;
            List<Stock> priceLimitUp2 = new ArrayList<>();
            List<Stock> priceLimitUp3 = new ArrayList<>();
            List<Stock> priceLimitUp4 = new ArrayList<>();
            int priceLimitDown = 0;
            int upCount = 0;
            int downCount = 0;
            int gt20Ma = 0; //20日均线上方
            int gt120Ma = 0; //120日均线上方

            for(Stock stock : stocks){
                Bar bar = stock.getBar(date);
                if(bar == null) continue;
                total ++;
                
                if(bar.getChange() > 0){
                    upCount ++;
                }else if(bar.getChange() < 0){
                    downCount ++;
                }
                if(stock.isPriceLimitUp(date)){
                    int cnt = stock.getPriceLimitUpCount(date);
                    if(cnt +2 > stock.getBarSize(date)){ //排除新股涨停板
                        continue;
                    }
                    priceLimitUp ++;
                    if(cnt == 2){
                        priceLimitUp2.add(stock);
                    }else if(cnt == 3){
                        priceLimitUp3.add(stock);
                    }else if(cnt > 3){
                        priceLimitUp4.add(stock);
                    }
                }
                if(stock.isPriceLimitDown(date)){
                    priceLimitDown ++;
                }

                if(bar.getClose() > bar.getMA(20, Bar.EnumValue.C)){
                    gt20Ma ++;
                }
                if(bar.getClose() > bar.getMA(120, Bar.EnumValue.C)){
                    gt120Ma ++;
                }
            }

            StkPeEntity entity = stkPeRepository.findFirstByReportDate(date);
            if(entity == null){
                entity = new StkPeEntity();
                entity.setReportDate(date);
            }

            entity.setStockCount(total);
            entity.setResult1((double)priceLimitUp);
            entity.setResult2((double)priceLimitDown);
            entity.setResult3((double)upCount);
            entity.setResult4((double)downCount);

            entity.setResult5((double)priceLimitUp2.size());
            entity.setResult6((double)priceLimitUp3.size());
            entity.setResult7((double)priceLimitUp4.size());
            entity.setString1(priceLimitUp3.stream().map(Stock::getCode).collect(Collectors.joining(",")));
            entity.setString2(priceLimitUp4.stream().sorted(Comparator.comparing(Stock::getPriceLimitUpCount, Comparator.reverseOrder())).map(stock -> stock.getCode()+'|'+stock.getPriceLimitUpCount()).collect(Collectors.joining(",")));

            entity.setResult8((double)gt20Ma);
            entity.setResult9((double)gt120Ma);

            stkPeRepository.save(entity);
        }
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


}
