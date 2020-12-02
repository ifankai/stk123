package com.stk123.model.core.similar;

import java.util.function.BiPredicate;

public class SimilarEquals<V extends Comparable> implements Similar {

    private V v;
    private V u;
    private BiPredicate biPredicate;

    public SimilarEquals(V v, V u){
        this(v, u, null);
    }

    public SimilarEquals(V v, V u, BiPredicate<V, V> biPredicate){
        this.v = v;
        this.u = u;
        this.biPredicate = biPredicate;
    }

    @Override
    public boolean similar() {
        if(biPredicate == null){
            return v.equals(u);
        }
        return biPredicate.test(v, u);
    }


}
