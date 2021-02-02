package com.stk123.model.strategy;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.model.json.View;
import com.stk123.model.strategy.result.FilterResult;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class StrategyResult<X> {
    //@JsonView(View.Default.class)
    private Strategy<X> strategy;
    //private boolean pass;
    @JsonView(View.Default.class)
    private List<FilterResult> filterResults = new ArrayList<>();
    private List<FilterResult> expectFilterResults = new ArrayList<>(); //未来期望的过滤结果，比如：期望未来10天内涨幅达到20%

    @JsonView(View.Default.class)
    private String date;
    private String code;

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

    public boolean isFilterAllPassed(){
        return filterResults.stream().filter(e -> e.pass()).collect(Collectors.toList()).size() == strategy.getFilterCount();
    }

    @SneakyThrows
    public String toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        String json = objectMapper.writerWithView(View.Default.class).writeValueAsString(this);
        return json;
    }


    @Override
    public String toString() {
        return "StrategyResult{" +
                "name=" + strategy.getName() +
                ", code=" + code +
                ", date=" + date +
                ", filterResults=" + filterResults +
                ", expectFilterResults=" + expectFilterResults +
                '}';
    }
}
