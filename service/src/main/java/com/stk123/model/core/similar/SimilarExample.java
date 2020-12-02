package com.stk123.model.core.similar;

import com.stk123.model.core.BarSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class SimilarExample<B, S extends Similar> {

    private List<FunctionWraper<Function<B, S>>> list = new ArrayList<>();

    public void addSimilar(Function<B, S> similar){
        this.list.add(new FunctionWraper<>(similar));
    }

    public boolean isSimilar(B bar) {
        //把通过数量少的放前面，以便提供性能
        Collections.sort(list, Comparator.comparingInt(FunctionWraper::getCounter));

        for(FunctionWraper<Function<B, S>> functionWraper : list){
            Function<B, S> function = functionWraper.getFunction();
            S result = function.apply(bar);
            if(!result.similar())
                return false;
            functionWraper.increase();
        }
        return true;
    }

}
