package com.stk123.model.core.similar;

public class SimilarBetween<V extends Number & Comparable, U extends Number & Comparable> implements SimilarResult {

    private V value;
    private U min;
    private U max;

    public SimilarBetween(V value, U min, U max) {
        this.value = value;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean similar() {
        return value.compareTo(min.doubleValue()) > 0 && value.compareTo(max.doubleValue()) < 0;
    }

}
