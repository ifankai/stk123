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
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
public class StrategyResult<X> {

    //@JsonView(View.Default.class)
    private Strategy<X> strategy;

    @JsonView(View.Default.class)
    private List<FilterResult> filterResults = new ArrayList<>();

    private List<FilterResult> expectFilterResults = new ArrayList<>(); //未来期望的过滤结果，比如：期望未来10天内涨幅达到20%

    @JsonView(View.Default.class)
    private String date;
    private String code;

    public void addFilterResult(FilterResult filterResult){
        this.filterResults.add(filterResult);
    }

    public void addExpectFilterResult(FilterResult filterResult){
        this.expectFilterResults.add(filterResult);
    }

    public boolean isFilterAllPassed(){
        return filterResults.stream().filter(FilterResult::pass).count() == strategy.getFilterCount();
    }

    public boolean isExpectFilterPassed(){
        return expectFilterResults.stream().filter(FilterResult::pass).count() >= 1;
    }

    public double getSortableValue(){
        FilterResult filterResult = filterResults.stream().filter(fr -> fr instanceof Sortable).findFirst().orElse(null);
        if(filterResult != null){
            return ((Sortable) filterResult).getValue();
        }
        return 0d;
    }

    //TODO 多个sortable filter
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

    public void setSortablePassed(boolean pass){
        filterResults.stream().filter(fr -> fr instanceof Sortable).findFirst().ifPresent(filterResult -> filterResult.setPass(pass));
    }

    public <R> List<R> getResults(){
        return filterResults.stream().map(FilterResult<R>::result).filter(Objects::nonNull).collect(Collectors.toList());
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
