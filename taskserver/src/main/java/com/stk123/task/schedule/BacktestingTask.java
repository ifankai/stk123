package com.stk123.task.schedule;

import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.service.core.BacktestingService;
import com.stk123.service.task.Task;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

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

        String startDate = null;
        String endDate = null;
        for(String arg : args){
            if(arg.startsWith("strategy=")){
                strategies = StringUtils.split(arg, "=")[1];
            }
            if(arg.startsWith("code=")){
                codes = StringUtils.split(arg, "=")[1];
            }
            if(arg.startsWith("startDate=")){
                startDate = StringUtils.split(arg, "=")[1];
            }
            if(arg.startsWith("endDate=")){
                endDate = StringUtils.split(arg, "=")[1];
            }
        }
        strategyBacktesting = backtestingService.backtesting(Arrays.asList(StringUtils.split(codes, ",")),
                Arrays.asList(StringUtils.split(strategies, ",")), startDate, endDate);
        strategyBacktesting.print();
    }

    public String success(){
        return StringUtils.join(this.strategyBacktesting.getStrategies().stream().map(strategy -> strategy.toString()).collect(Collectors.toList()), "\n");
    }
}
