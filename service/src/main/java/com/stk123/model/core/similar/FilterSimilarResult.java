package com.stk123.model.core.similar;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FilterSimilarResult {

    private boolean pass;
    private SimilarResult similarResult;

    public FilterSimilarResult(boolean pass){
        this(pass, null);
    }

    public FilterSimilarResult(boolean pass, SimilarResult similarResult){
        this.pass = pass;
        this.similarResult = similarResult;
    }

    @Override
    public String toString() {
        return "FilterSimilarResult{" +
                "pass=" + pass +
                ", similarResult=" + similarResult +
                '}';
    }
}
