package com.stk123.model.strategy.result;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import com.stk123.model.strategy.FilterExecutor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.Collections;

public abstract class FilterResult<R> {

    @Setter
    @JsonView(View.Default.class)
    private FilterExecutor filterExecutor; //过滤器定义
    @JsonView(View.Default.class)
    protected Boolean pass;
    protected R result;

    @JsonView(View.Default.class)
    private TableTd log = null;

    public static <R> FilterResult TRUE(R result){
        return new FilterResultTrue().addResult(result);
    }

    public static <R> FilterResult TRUE(R result, String logDate, String logTitle, String logMsg){
        return new FilterResultTrue().addResult(result, logDate, logTitle, logMsg);
    }

    public static <R> FilterResult FALSE(R result){
        return new FilterResultFalse().addResult(result);
    }


    public boolean pass() {
        if(this.pass == null) {
            pass = isPass();
        }
        return pass;
    }

    public R result() {
        return this.result;
    }
    public TableTd log() {
        return this.log;
    }

    public FilterResult addResult(R result){
        this.result = result;
        return this;
    }

    public FilterResult addResult(R result, String logDate, String logTitle, String logMsg){
        this.result = result;
        this.log = new TableTd(logDate, logTitle, Collections.singletonList(logMsg));
        return this;
    }

    public String getFilterName(){
        return this.filterExecutor == null ? "" : this.filterExecutor.getName();
    }

    public abstract boolean isPass();

}
