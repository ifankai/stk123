package com.stk123.model.strategy;

import com.stk123.model.strategy.result.FilterResult;

/**
 * 和Function一样，换个好理解的名字
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface Filter<B> {

    FilterResult filter(B t);

}
