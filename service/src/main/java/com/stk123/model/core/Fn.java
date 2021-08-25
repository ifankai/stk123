package com.stk123.model.core;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Table;
import com.stk123.entity.StkFnDataEntity;
import com.stk123.entity.StkFnTypeEntity;
import com.stk123.model.json.View;
import com.stk123.util.ServiceUtils;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Fn {

    @Data
    public static class FnData{
        @JsonView(View.All.class)
        private String date;
        @JsonView(View.All.class)
        private int quarterNumber;
        @JsonView(View.All.class)
        private Double value;
        //@JsonView(View.All.class)
        private StkFnTypeEntity type;
        private Map<String, FnData> calcValues;
    }

    private Stock stock;
    private List<StkFnTypeEntity> types;
    private Table<String, Integer, FnData> table;


    public Fn(Stock stock, List<StkFnTypeEntity> types, Table<String, Integer, FnData> table){
        this.stock = stock;
        this.types = types;
        this.table = table;

        Collection<FnData> values = table.values();
        for(FnData fnData : values){
            int mm = Integer.parseInt(StringUtils.substring(fnData.getDate(), 4, 6));
            fnData.setQuarterNumber(ServiceUtils.getQuarterNumber(12, mm));
        }

    }
}
