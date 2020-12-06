package com.stk123.model.strategy.result;

public class FilterResultFalse extends FilterResult {

    @Override
    public boolean isPass() {
        return false;
    }

    @Override
    public String toString() {
        return "FilterResultFalse{" +
                "pass=" + pass +
                ", result='" + result + '\'' +
                '}';
    }

}
