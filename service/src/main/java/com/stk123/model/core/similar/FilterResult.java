package com.stk123.model.core.similar;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class FilterResult {
    private boolean pass;
    private List<FilterSimilarResult> filterSimilarResults = new ArrayList<>();

    public FilterResult(){}

    public void addFilterResult(FilterSimilarResult filterResult){
        this.filterSimilarResults.add(filterResult);
    }

    public double getSortableSumValue(){
        double ret = 0;
        for(FilterSimilarResult result : filterSimilarResults){
            if(result.isPass() && result.getSimilarResult() instanceof Sortable){
                Sortable sortable = (Sortable)result.getSimilarResult();
                ret += sortable.getValue();
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return "FilterResult{" +
                "pass=" + pass +
                ", filterSimilarResults=" + filterSimilarResults +
                '}';
    }
}
