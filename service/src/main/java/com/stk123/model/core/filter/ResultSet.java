package com.stk123.model.core.filter;

import com.stk123.model.core.filter.result.FilterResult;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ResultSet {
    private String exampleName;
    private boolean pass;
    private List<FilterResult> filterResults = new ArrayList<>();
    private FilterResult expectFilterResult; //用于存放 pass为true时，对未来期望的过滤结果，比如：期望未来10天内涨幅达到20%

    public ResultSet(String name){
        this.exampleName = name;
    }

    public void addFilterResult(FilterResult filterResult){
        this.filterResults.add(filterResult);
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
        return "ResultSet{" +
                "exampleName='" + exampleName + '\'' +
                ", pass=" + pass +
                ", filterResults=" + filterResults +
                ", expectFilterResult=" + expectFilterResult +
                '}';
    }
}
