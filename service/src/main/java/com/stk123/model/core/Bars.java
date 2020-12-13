package com.stk123.model.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class Bars {

    public static boolean isTrue(Predicate<Bar> predicate, Bar... bars) {
        for(Bar bar : bars) {
            boolean b = predicate.test(bar);
            if(!b) return false;
        }
        return true;
    }

    public static Bar getMin(Bar.EnumValue enumValue, Bar... bars){
        Optional<Bar> optional = Arrays.stream(bars).min(Comparator.comparingDouble(bar -> bar.getValue(enumValue)));
        return optional.orElse(null);
    }

    public static Bar getMax(Bar.EnumValue enumValue, Bar... bars){
        Optional<Bar> optional = Arrays.stream(bars).max(Comparator.comparingDouble(bar -> bar.getValue(enumValue)));
        return optional.orElse(null);
    }
}
