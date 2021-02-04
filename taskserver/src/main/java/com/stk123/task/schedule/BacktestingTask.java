package com.stk123.task.schedule;

import com.stk123.common.util.EmailUtils;
import com.stk123.entity.StkTaskLogEntity;
import com.stk123.model.strategy.PassedResult;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.service.core.BacktestingService;
import com.stk123.service.core.TaskService;
import com.stk123.service.task.Task;
import com.stk123.util.ServiceUtils;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
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

    @Autowired
    private BacktestingService backtestingService;

    /*public BacktestingTask(){
        super(false);
    }*/

    public void register() {
        this.runAnyway(() -> execute());
    }

    public void execute() {
        try {
            StrategyBacktesting strategyBacktesting = backtestingService.backtesting(Arrays.asList(StringUtils.split(code, ",")),
                    Arrays.asList(StringUtils.split(strategy, ",")), startDate, endDate, realtime != null);

        } catch (Exception e) {
            log.error("BacktestingTask", e);
        }

    }

}
