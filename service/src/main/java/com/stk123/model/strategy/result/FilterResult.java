package com.stk123.model.strategy.result;

import lombok.Setter;

public abstract class FilterResult<R> {

    @Setter
    private FilterWrapper filterWrapper; //过滤器定义
    protected Boolean pass;
    protected R result;

    public static <R> FilterResult TRUE(R result){
        return new FilterResultTrue().addResult(result);
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


    public FilterResult addResult(R result){
        this.result = result;
        return this;
    }

    public String getFilterName(){
        return this.filterWrapper == null ? "" : this.filterWrapper.getName();
    }

    public abstract boolean isPass();

}
