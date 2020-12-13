package com.stk123.model.strategy;

import com.stk123.model.strategy.result.FilterResult;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class StrategyResult<X> {
    private Strategy<X> strategy;
    //private boolean pass;
    private List<FilterResult> filterResults = new ArrayList<>();
    private List<FilterResult> expectFilterResults = new ArrayList<>(); //未来期望的过滤结果，比如：期望未来10天内涨幅达到20%
    private String date;

  /*  public ResultSet(String strategyName){
        this.strategyName = strategyName;
    }*/

    public void addFilterResult(FilterResult filterResult){
        this.filterResults.add(filterResult);
    }
    public void addExpectFilterResult(FilterResult filterResult){
        this.expectFilterResults.add(filterResult);
    }

    /*public double getSortableSumValue(){
        double ret = 0;
        for(FilterResult result : filterResults){
            if(result.isPass() && result.getFilterResult() instanceof Sortable){
                Sortable sortable = (Sortable)result.getFilterResult();
                ret += sortable.getValue();
            }
        }
        return ret;
    }*/



    @Override
    public String toString() {
        return "StrategyResult{" +
                "date=" + date +
                ", filterResults=" + filterResults +
                ", expectFilterResults=" + expectFilterResults +
                '}';
    }
}
