package com.stk123.task.schedule;

import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.service.BacktestingService;
import com.stk123.service.task.Task;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BacktestingTask extends Task {

    @Autowired
    private BacktestingService backtestingService;

    private StrategyBacktesting strategyBacktesting;

    @Override
    public void execute(String... args) throws Exception {
        String strategies = args[0];
        String codes = args[1];

        strategyBacktesting = backtestingService.backtesting(Arrays.asList(StringUtils.split(codes, ",")) , StringUtils.split(strategies, ","));
    }

    public String success(){
        return "";
    }
}
