package com.stk123.model.core.similar;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FilterSimilarResult {

    private boolean pass;
    private Similar similar;

    public FilterSimilarResult(boolean pass){
        this(pass, null);
    }

    public FilterSimilarResult(boolean pass, Similar similar){
        this.pass = pass;
        this.similar = similar;
    }

    @Override
    public String toString() {
        return "FilterSimilarResult{" +
                "pass=" + pass +
                ", similar=" + similar +
                '}';
    }
}
