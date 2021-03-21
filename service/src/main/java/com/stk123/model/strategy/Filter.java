package com.stk123.model.strategy;

import com.stk123.model.strategy.result.FilterResult;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 和Function一样，换个好理解的名字
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface Filter<B> {

    FilterResult filter(Strategy strategy, B t);

    static <B> Filter<B> or(Filter<B>... filters){
        return (strategy, bs) -> {
            List<Object> results = new ArrayList<>();
            for (Filter<B> filter : filters) {
                FilterResult filterResult = filter.filter(strategy, bs);
                results.add(filterResult.result());
                if(filterResult.pass()) return filterResult;
            }
            return FilterResult.FALSE("OR[" + StringUtils.join(results, ", ") + "]");
        };
    }
}
