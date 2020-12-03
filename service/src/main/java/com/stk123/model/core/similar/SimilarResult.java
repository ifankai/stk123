package com.stk123.model.core.similar;

public interface SimilarResult {

    boolean similar();

    SimilarResult FALSE = () -> false;

    SimilarResult TRUE = () -> true;

}
