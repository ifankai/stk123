package com.stk123.model.core.similar;

import org.apache.commons.lang.math.NumberUtils;

import java.util.function.BiPredicate;

public class SimilarEquals<V extends Number & Comparable> implements SimilarResult, Sortable {

    private V value;
    private V target;
    private V tolerance; //上下容忍数值
    private BiPredicate biPredicate;

    public SimilarEquals(V value, V target) {
        this(value, target, null, null);
    }

    public SimilarEquals(V value, V target, V tolerance){
        this(value, target, tolerance, null);
    }

    public SimilarEquals(V value,V target, V tolerance, BiPredicate<V, V> biPredicate){
        this.value = value;
        this.target = target;
        this.tolerance = tolerance;
        this.biPredicate = biPredicate;
    }

    @Override
    public boolean similar() {
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
        return Math.abs(value.doubleValue() - target.doubleValue());
    }

    @Override
    public String toString() {
        return "SimilarEquals{" +
                "value=" + String.format("%.2f", value) +
                ", target=" + target +
                ", tolerance=" + tolerance +
                '}';
    }
}
