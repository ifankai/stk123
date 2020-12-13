package com.stk123.model.strategy.result;

public class FilterResultTrue extends FilterResult {

    @Override
    public boolean isPass() {
        return true;
    }

    @Override
    public String toString() {
        return "FilterResultTrue{" +
                "filterName=" + super.getFilterName() +
                ", pass=" + pass +
                ", result='" + result + '\'' +
                '}';
    }

}
