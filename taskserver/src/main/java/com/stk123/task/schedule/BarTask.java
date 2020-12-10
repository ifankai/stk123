package com.stk123.task.schedule;

import com.stk123.common.util.EmailUtils;
import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
import com.stk123.service.BarService;
import com.stk123.task.Task;
import com.stk123.task.tool.TaskUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BarTask extends Task {

    @Autowired
    private StkKlineRepository stkKlineRepository;
    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private BarService barService;

    private final Date now = new Date();
    private int dayOfWeek;
    private boolean isWorkingDay;
    private Stock.EnumMarket market = Stock.EnumMarket.CN; //default A stock
    private boolean init = true;
    private boolean analyse = true;

    @Override
    public void execute(String... args) throws Exception {
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if ("US".equalsIgnoreCase(arg)) {
                    market = Stock.EnumMarket.US;
                }
                if ("HK".equalsIgnoreCase(arg)) {
                    market = Stock.EnumMarket.HK;
                }
                if ("init".equalsIgnoreCase(arg)) {
                    init = true;
                    analyse = false;
                }
                if ("analyse".equalsIgnoreCase(arg)) {
                    init = false;
                    analyse = true;
                }
            }
        }

        dayOfWeek = TaskUtils.getDayOfWeek(now);
        isWorkingDay = (dayOfWeek == 1 || dayOfWeek == 2 || dayOfWeek == 3 || dayOfWeek == 4 || dayOfWeek == 5)?true:false;

        if(init){
            log.info("----------开始初始化----------");
            init();
            log.info("----------结束初始化----------");
        }
        if (!isWorkingDay) {
            EmailUtils.send("周六数据同步完成！！！", "...");
            return;
        }

        if(analyse){
            log.info("----------开始分析----------");
            analyse();
            log.info("----------结束分析----------");
        }

    }

    public void init(){
        if (market == Stock.EnumMarket.CN) {
            initCN();
        }
    }

    public void analyse(){

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
                Stock stock = new Stock(codeName);
                if(isWorkingDay){
                    barService.initKLines(stock,5);
                }else{
                    barService.initKLines(stock,30);
                }
            }
        }catch(Exception e){
            log.error(e);
            EmailUtils.send("[BarTask出错]大盘指数K线下载出错 stk="+scn.getCode(), e);
        }

        try{
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.CN, Stock.EnumCate.INDEX_10jqka);
            for (StockBasicProjection codeName : list) {
                log.info(codeName.getCode());
                scn = codeName;
                Stock stock = new Stock(codeName);
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
            log.error(e);
            EmailUtils.send("[BarTask出错]同花顺概念指数K线下载出错 code="+scn.getCode(), e);
        }

        try {
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.CN, Stock.EnumCate.STOCK);
            log.info("initKLines..........start");
            initKLines(list, 4);
            log.info("initKLines..........end");

            list = stkRepository.findStockNotExsitingTodayKline();
            for (StockBasicProjection stockBasicProjection : list) {
                try {
                    Stock stk = new Stock(stockBasicProjection);
                    barService.initKLine(stk);
                } catch (Exception e) {
                    log.error(e);
                    EmailUtils.send("[BarTask出错]修补K线数据出错 code=" + stockBasicProjection.getCode(), e);
                }
            }
        }catch(Exception e){
            log.error(e);
            EmailUtils.send("[BarTask出错]个股K线下载出错 code="+scn.getCode(), e);
        }
    }

    //多线程 workers
    public void initKLines(List<StockBasicProjection> stks,int numberOfWorker) throws InterruptedException {

        final CountDownLatch countDownLatch = new CountDownLatch(stks.size());
        ExecutorService exec = Executors.newFixedThreadPool(numberOfWorker);
        for(final StockBasicProjection stk : stks){
            Runnable run = () -> {
                Stock stock = null;
                try{
                    stock = new Stock(stk);
                    log.info("initKLines=="+stock.getCode());
                    if(isWorkingDay){
                        barService.initKLine(stock);
                    }else{
                        barService.initKLines(stock, 30);
                    }
                }catch(Exception e){
                    log.error(e);
                    //ExceptionUtils.insertLog(conn, index.getCode(), e);
                    e.printStackTrace();
                }
            };
            exec.execute(run);
        }
        exec.shutdown();
        countDownLatch.await();

    }
}
