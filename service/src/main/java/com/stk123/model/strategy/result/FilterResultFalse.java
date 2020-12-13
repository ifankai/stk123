package com.stk123.model.strategy.result;

public class FilterResultFalse extends FilterResult {

    @Override
    public boolean isPass() {
        return false;
    }

    @Override
    public String toString() {
        return "FilterResultFalse{" +
                "filterName=" + super.getFilterName() +
                ", pass=" + pass +
                ", result='" + result + '\'' +
                '}';
    }

}
