package com.stk123.model.strategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
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
public class StrategyResult {

    //@JsonView(View.Default.class)
    private Strategy strategy;

    @JsonView(View.All.class)
    @JsonProperty("filters")
    private List<FilterResult> filterResults = new ArrayList<>();

    private List<FilterResult> expectFilterResults; //未来期望的过滤结果，比如：期望未来10天内涨幅达到20%

    private String date;
    //private String code;
    private Stock stock;

    @JsonView(View.All.class)
    private double percentile;

    /*public String getCode(){
        return stock.getCode();
    }*/

    public void addFilterResult(FilterResult filterResult){
        this.filterResults.add(filterResult);
    }

    public void addExpectFilterResult(FilterResult filterResult){
        if(this.expectFilterResults == null) this.expectFilterResults = new ArrayList<>();
        this.expectFilterResults.add(filterResult);
    }

    public boolean isFilterAllPassed(){
        return filterResults.stream().filter(FilterResult::pass).count() == strategy.getFilterCount();
    }

    public boolean isExpectFilterPassed(){
        return expectFilterResults.stream().filter(FilterResult::pass).count() >= 1;
    }

    public List<FilterResult> getSortableFilterResults(){
        return filterResults.stream().filter(fr -> fr instanceof Sortable).collect(Collectors.toList());
    }
    public double getSortableValue(){
        FilterResult filterResult = filterResults.stream().filter(fr -> fr instanceof Sortable).findFirst().orElse(null);
        if(filterResult != null){
            return ((Sortable) filterResult).getValue();
        }
        return 0d;
    }

    public void calcPercentile(){
        List<Sortable> sortables = getSortableFilterResults().stream().map(s -> (Sortable)s).collect(Collectors.toList());
        if(sortables.size() == 1){
            this.setPercentile(sortables.get(0).getPercentile());
        }else{
            this.setPercentile(sortables.stream().mapToDouble(s -> s.getPercentile() * ((FilterResult)s).getFilterExecutor().getWeight()).sum());
        }
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

    /*public void setSortablePassed(boolean pass){
        filterResults.stream().filter(fr -> fr instanceof Sortable).findFirst().ifPresent(filterResult -> filterResult.setPass(pass));
    }*/

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
                ", stock=" + stock.getCode() +
                ", date=" + date +
                ", percentile=" + percentile +
                ", filterResults=" + filterResults +
                ", expectFilterResults=" + expectFilterResults +
                '}';
    }
}
