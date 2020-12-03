package com.stk123.model.core.similar;

/**
 * 和Function一样，换个好理解的名字
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface Filter<B> {

    SimilarResult apply(B t);

}
