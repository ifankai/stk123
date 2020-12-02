package com.stk123.model.core.similar;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FilterExample<B , S extends Similar> {

    @Getter
    private String name;
    private List<FilterWraper<B, S>> list = new ArrayList<>();

    public FilterExample(String name) {
        this.name = name;
    }

    public void addFilter(Filter<B, S> filter){
        this.list.add(new FilterWraper<>(filter));
    }

    public boolean test(B b) {
        //把通过数量少的放前面，以便提供性能
        list.sort(Comparator.comparingInt(FilterWraper::getCounter));

        for(FilterWraper<B, S> filterWraper : list){
            Filter<B, S> filter = filterWraper.getFilter();
            S result = filter.apply(b);
            if(!result.similar())
                return false;
            filterWraper.increase();
        }
        return true;
    }

}
