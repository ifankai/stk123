package com.stk123.model.core.similar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SimilarExample<B, S extends Similar> {

    private List<Function<B, S>> list = new ArrayList<>();

    public void addSimilar(Function<B, S> similar){
        this.list.add(similar);
    }

    public boolean isSimilar(B bar) {
        for(Function<B, S> similar : list){
            S result = similar.apply(bar);
            if(!result.similar())
                return false;
        }
        return true;
    }

}
