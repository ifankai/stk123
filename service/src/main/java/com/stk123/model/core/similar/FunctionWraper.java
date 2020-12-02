package com.stk123.model.core.similar;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FunctionWraper<T> {
    private T function;
    private int counter;

    public FunctionWraper(T function){
        this.function = function;
        this.counter = 0;
    }

    public void increase(){
        this.counter ++;
    }
}
