package com.stk123.model.mass;

import com.stk123.model.core.Bar;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;

@Data
@AllArgsConstructor
public class MassFunction {
    private int weight;
    private Function<Bar, Double> function;
}
