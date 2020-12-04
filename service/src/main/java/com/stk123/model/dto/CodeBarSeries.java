package com.stk123.model.dto;

import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;

import java.util.LinkedHashMap;

public class CodeBarSeries {

    private LinkedHashMap<String, BarSeries> map = new LinkedHashMap<>();

    public CodeBarSeries(String code, Double open, Double close){
        Bar bar = new Bar();

    }
}
