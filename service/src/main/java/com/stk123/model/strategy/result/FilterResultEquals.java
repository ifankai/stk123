package com.stk123.model.strategy.result;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import com.stk123.model.strategy.Sortable;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.function.BiPredicate;

public class FilterResultEquals extends FilterResult implements Sortable {

    @JsonView(View.Default.class)
    @Setter
    private double value;
    private double target;
    private Double tolerance; //上下容忍数值
    private BiPredicate biPredicate;
    @Getter
    private int order;
    @JsonView(View.Default.class)
    private double percentile;

    public FilterResultEquals(double value){
        this(value, value);
    }

    public FilterResultEquals(double value, double target) {
        this(value, target, null);
    }

    public FilterResultEquals(double value, double target, Double tolerance){
        this(value, target, tolerance, null);
    }

    public FilterResultEquals(double value, double target, Double tolerance, BiPredicate<Double, Double> biPredicate){
        this.value = value;
        this.target = target;
        this.tolerance = tolerance;
        this.biPredicate = biPredicate;
    }

    @Override
    public boolean isPass() {
        if(tolerance == null){
            return value == target;
        }
        if(biPredicate == null){
            return value >= target - tolerance.doubleValue() &&
                   value <= target + tolerance.doubleValue();
        }
        return biPredicate.test(value, tolerance);
    }

    @Override
    public String toString() {
        return "FilterResultEquals{" +
                "code=" + this.getCode() +
                ", name=" + this.getFilterName() +
                ", pass=" + pass +
                ", value=" + String.format("%.2f", value) +
                ", order=" + order +
                ", percentile=" + percentile +
                ", target=" + target +
                ", tolerance=" + tolerance +
                //", biPredicate=" + biPredicate +
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
