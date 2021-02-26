package com.stk123.task.schedule;

import com.stk123.common.CommonUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.entity.StkIndustryEntity;
import com.stk123.entity.StkKlineUsEntity;
import com.stk123.entity.StkPeEntity;
import com.stk123.model.core.Bar;
import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.model.xueqiu.Portfolio;
import com.stk123.repository.*;
import com.stk123.service.XueqiuService;
import com.stk123.service.core.BacktestingService;
import com.stk123.service.core.BarService;
import com.stk123.task.tool.TaskUtils;
import com.stk123.util.ExceptionUtils;
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

    public static void main(String[] args) throws Exception {
        AbstractTask task = new BarTask();
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
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.CN, Stock.EnumCate.INDEX_10jqka);
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
        List<StkIndustryEntity> inds = stkIndustryRepository.findAllByIndustry(1783L);
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
                    if(isWorkingDay){
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
            Set<String> allList = new HashSet<>();
            Set<String> myList = null;
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
            }
            log.info(allList);

            //策略回测开始    01,02 策略在com.stk123.model.strategy.sample.Sample 里定义
            StrategyBacktesting strategyBacktesting = backtestingService.backtesting(
                    new ArrayList<>(allList),
                    Arrays.asList(StringUtils.split("01,02a,02b,03", ",")),
                    startDate, endDate, realtime!=null);

            List<StrategyResult> results = strategyBacktesting.getPassedStrategyResult();
            if(results.size() > 0){
                StringBuffer sb = new StringBuffer();

                List<List<String>> datas = new ArrayList<>();
                results = results.stream().sorted(Comparator.comparing(strategyResult -> strategyResult.getStrategy().getName())).collect(Collectors.toList());
                for(StrategyResult strategyResult : results){

                    List<String> sources = new ArrayList<>();
                    if(myList != null && myList.contains(strategyResult.getCode())){
                        sources.add("自选股");
                    }
                    if (portfolios != null) {
                        for(Portfolio portfolio : portfolios){
                            List<com.stk123.model.xueqiu.Stock> stocks = portfolio.getStocks();
                            if(stocks != null && stocks.size() > 0) {
                                com.stk123.model.xueqiu.Stock stk = stocks.stream().filter(stock -> stock.getCode() != null && stock.getCode().contains(strategyResult.getCode())).findFirst().orElse(null);
                                if (stk != null) {
                                    sources.add(CommonUtils.wrapLink(portfolio.getName(), "https://xueqiu.com/P/" + portfolio.getSymbol()) + " ["+stk.getWeight()+"]");
                                }
                            }
                        }
                    }

                    StrategyBacktesting backtesting = backtestingService.backtesting(strategyResult.getCode(), strategyResult.getStrategy().getCode(), false);

                    List<String> data = ListUtils.createList(
                            Stock.build(strategyResult.getCode(), null).getNameAndCodeWithLink(),
                            strategyResult.getDate(),
                            strategyResult.getStrategy().getName(),
                            StringUtils.join(sources, "<br/>"),
                            backtesting.getStrategies().get(0).getPassRateString().replaceAll("]", "]<br/>") +
                                    (StringUtils.isNotEmpty(backtesting.getStrategies().get(0).getPassedFilterResultLog()) ? "<br/>"+backtesting.getStrategies().get(0).getPassedFilterResultLog() : "")
                            );
                    datas.add(data);
                }

                datas = datas.stream().sorted(Comparator.comparing(e -> e.get(3).contains("自选股"), Comparator.reverseOrder())).collect(Collectors.toList());;

                List<String> titles = ListUtils.createList("标的", "日期", "策略", "来源", "历史策略回测通过率");
                String table = CommonUtils.createHtmlTable(titles, datas);
                sb.append(table);


                sb.append("<br/><br/>--------------------------------------------------------<br/>");
                strategyBacktesting.getStrategies().forEach(strategy -> {
                    sb.append(strategy.toString().replaceAll("\n","<br/>")).append("<br/>");
                });

                EmailUtils.send("策略发现"+results.size()+"个标的", sb.toString());
            }else{
                EmailUtils.send("策略发现0个标的", "");
            }
        } catch (Exception e) {
            EmailUtils.send("策略发现标的报错", ExceptionUtils.getExceptionAsString(e));
            log.error("analyseKline", e);
        }
    }
}
