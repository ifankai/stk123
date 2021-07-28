package com.stk123.task.schedule;

import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.model.strategy.sample.Strategies;
import com.stk123.repository.StkRepository;
import com.stk123.service.core.BacktestingService;
import com.stk123.service.core.StockService;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CommonsLog
@Service
@Setter
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BacktestingTask extends AbstractTask {

    private String strategy;
    private String code;
    private String startDate = null;
    private String endDate = null;
    private String realtime = null;
    private String history = null;

    @Autowired
    private BacktestingService backtestingService;
    @Autowired
    private StockService stockService;
    @Autowired
    private StkRepository stkRepository;

    /*public BacktestingTask(){
        super(false);
    }*/

    public void register() {
        this.run(() -> execute());
    }

    public void execute() {
        try {
            if(StringUtils.isEmpty(strategy)){
                strategy = Strategies.STRATEGIES_MY_STOCKS;
            }
            List<String> codes = null;
            if(StringUtils.isEmpty(code)){
                codes = stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.CN, EnumCate.STOCK).stream().map(StockBasicProjection::getCode).collect(Collectors.toList());
            }else{
                codes = Arrays.asList(StringUtils.split(code, ","));
            }
            StrategyBacktesting strategyBacktesting = null;
            if(StringUtils.isNotEmpty(history)){
                strategyBacktesting = backtestingService.backtestingAllHistory(codes,
                        Arrays.asList(StringUtils.split(strategy, ",")), realtime != null);
            }else {
                strategyBacktesting = backtestingService.backtesting(codes,
                        Arrays.asList(StringUtils.split(strategy, ",")), startDate, endDate, realtime != null);
            }
            List<StrategyResult> results = strategyBacktesting.getPassedStrategyResult();
            log.info("results =========== "+results.size());
        } catch (Exception e) {
            log.error("BacktestingTask", e);
        }

    }

}
