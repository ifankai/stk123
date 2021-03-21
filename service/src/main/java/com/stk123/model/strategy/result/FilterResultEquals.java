package com.stk123.model.strategy.result;

import com.stk123.model.strategy.Sortable;

import java.util.function.BiPredicate;

public class FilterResultEquals<V extends Number & Comparable, R> extends FilterResult implements Sortable {

    private V value;
    private V target;
    private V tolerance; //上下容忍数值
    private BiPredicate biPredicate;

    public FilterResultEquals(V value){
        this(value, value);
    }

    public FilterResultEquals(V value, V target) {
        this(value, target, null);
    }

    public FilterResultEquals(V value, V target, V tolerance){
        this(value, target, tolerance, null);
    }

    public FilterResultEquals(V value, V target, V tolerance, BiPredicate<V, V> biPredicate){
        this.value = value;
        this.target = target;
        this.tolerance = tolerance;
        this.biPredicate = biPredicate;
    }

    @Override
    public boolean isPass() {
        if(tolerance == null){
            return value.doubleValue() == target.doubleValue();
        }
        if(biPredicate == null){
            return value.doubleValue() >= target.doubleValue() - tolerance.doubleValue() &&
                   value.doubleValue() <= target.doubleValue() + tolerance.doubleValue();
        }
        return biPredicate.test(value, tolerance);
    }


    @Override
    public double getValue() {
        return value.doubleValue();
    }

    @Override
    public String toString() {
        return "FilterResultEquals{" +
                "filterName=" + this.getFilterName() +
                ", pass=" + pass +
                ", value=" + String.format("%.2f", value) +
                ", target=" + target +
                ", tolerance=" + tolerance +
                //", biPredicate=" + biPredicate +
                ", result=" + result +
                '}';
    }
}
