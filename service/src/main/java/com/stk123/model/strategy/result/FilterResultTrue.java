package com.stk123.model.strategy.result;

public class FilterResultTrue extends FilterResult {

    @Override
    public boolean isPass() {
        return true;
    }

    @Override
    public String toString() {
        return "FilterResultTrue{" +
                "pass=" + pass +
                ", result='" + result + '\'' +
                '}';
    }

}
