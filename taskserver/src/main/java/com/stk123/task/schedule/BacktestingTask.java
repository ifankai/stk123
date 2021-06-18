package com.stk123.task.schedule;

import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.sample.Strategies;
import com.stk123.service.core.BacktestingService;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Arrays;

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
            if(StringUtils.isNotEmpty(history)){
                StrategyBacktesting strategyBacktesting = backtestingService.backtestingAllHistory(Arrays.asList(StringUtils.split(code, ",")),
                        Arrays.asList(StringUtils.split(strategy, ",")), realtime != null);
            }else {
                StrategyBacktesting strategyBacktesting = backtestingService.backtesting(Arrays.asList(StringUtils.split(code, ",")),
                        Arrays.asList(StringUtils.split(strategy, ",")), startDate, endDate, realtime != null);
            }
        } catch (Exception e) {
            log.error("BacktestingTask", e);
        }

    }

}
