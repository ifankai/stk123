package com.stk123.model.core.similar;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FilterWraper<B, S> {
    private Filter<B, S> filter;
    private int counter;

    public FilterWraper(Filter<B, S> filter){
        this.filter = filter;
        this.counter = 0;
    }

    public void increase(){
        this.counter ++;
    }
}
