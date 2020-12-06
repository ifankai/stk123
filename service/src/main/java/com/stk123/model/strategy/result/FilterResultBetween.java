package com.stk123.model.strategy.result;

public class FilterResultBetween<V extends Number & Comparable, U extends Number & Comparable, T>
        extends FilterResult {

    private V value;
    private U min;
    private U max;

    public FilterResultBetween(V value, U min, U max) {
        this.value = value;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isPass() {
        return value.compareTo(min.doubleValue()) > 0 && value.compareTo(max.doubleValue()) < 0;
    }


    @Override
    public String toString() {
        return "FilterResultBetween{" +
                "filterName=" + this.getFilterName() +
                ", pass=" + pass +
                ", value=" + String.format("%.2f", value) +
                ", min=" + min +
                ", max=" + max +
                ", result=" + result +
                '}';
    }
}
