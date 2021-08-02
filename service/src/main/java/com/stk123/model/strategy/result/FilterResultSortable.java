package com.stk123.model.strategy.result;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import com.stk123.model.strategy.Sortable;
import lombok.Getter;
import lombok.Setter;

public class FilterResultSortable extends FilterResult implements Sortable {
    @JsonView(View.Default.class)
    @Setter
    private double value;

    @Getter
    private int order;

    @JsonView(View.Default.class)
    private double percentile;

    public FilterResultSortable(double value){
        this.value = value;
    }

    @Override
    public boolean isPass() {
        return true;
    }

    @Override
    public String toString() {
        return "FilterResultSortable{" +
                "code=" + this.getCode() +
                ", name=" + this.getFilterName() +
                ", pass=" + pass +
                ", value=" + String.format("%.2f", value) +
                ", order=" + order +
                ", percentile=" + percentile +
                ", result=" + result +
                '}';
    }


    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public void setPercentile(double percentile) {
        this.percentile = percentile;
    }

    @Override
    public double getPercentile() {
        return this.percentile;
    }
}
