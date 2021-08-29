package com.stk123.model.core;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Table;
import com.stk123.common.CommonUtils;
import com.stk123.entity.StkFnDataEntity;
import com.stk123.entity.StkFnTypeEntity;
import com.stk123.model.json.View;
import com.stk123.util.ServiceUtils;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Fn {

    public final static int DISPLAY_YEAR_NUMBER = 3;

    @Data
    public static class FnData{
        @JsonView(View.All.class)
        private String date;
        @JsonView(View.All.class)
        private int quarterNumber;
        @JsonView(View.All.class)
        private Double value;
        @JsonView(View.All.class)
        private String dispValue;
        //@JsonView(View.All.class)
        private StkFnTypeEntity type;
        private Map<String, FnData> calcValues;
    }

    private Stock stock;
    @JsonView(View.All.class)
    private List<StkFnTypeEntity> types;
    @JsonView(View.All.class)
    private Table<String, Integer, FnData> table;

    /**
     * 所有计算都在构造函数里完成！
     */
    public Fn(Stock stock, List<StkFnTypeEntity> types, Table<String, Integer, FnData> table){
        this.stock = stock;
        this.types = types;
        this.table = table;

        Collection<FnData> values = table.values();
        for(FnData fnData : values){
            int mm = Integer.parseInt(StringUtils.substring(fnData.getDate(), 4, 6));
            fnData.setQuarterNumber(ServiceUtils.getQuarterNumber(12, mm));
            StkFnTypeEntity type = fnData.getType();
            if(type == null)continue;
            if(type.getCurrencyUnitAdjust() != 1 && fnData.getValue() != null) {
                fnData.setValue(fnData.getValue() * type.getCurrencyUnitAdjust());
                fnData.setDispValue(CommonUtils.numberFormat2Digits(fnData.getValue()));
            }
            /*if(type.getIsPercent()){
                fnData.setDispValue(CommonUtils.numberFormat2Digits(fnData.getValue())+"%");
            }*/
            if(fnData.getDispValue() == null){
                fnData.setDispValue(fnData.getValue() == null ? "-" : CommonUtils.numberFormat2Digits(fnData.getValue()));
            }
        }
    }

    public Double getValueByType(Integer type){
        FnData fnData = this.table.column(type).values().stream().findFirst().orElse(null);
        if(fnData == null || fnData.getValue() == null) return null;
        return fnData.getValue();
    }

    public Map getAsMap(){
        Map result = new HashMap();
        List typeList = this.types;
        result.put("types", new ArrayList(){{
            StkFnTypeEntity dateType = new StkFnTypeEntity();
            dateType.setType(0);
            add(dateType);
            addAll(typeList);
        }});
        Set<String> dates = this.table.rowKeySet();
        FnData empty = new FnData();
        empty.setDispValue("-");

        int year = 0;
        List table = new ArrayList();
        for(String date : dates){
            Map<Integer, FnData> row = this.table.row(date);
            Map map = new HashMap();
            FnData firstColumn = this.getFirstColumn(row);
            if(firstColumn.getQuarterNumber() == 4) {
                year ++;
                firstColumn.setDispValue(StringUtils.substring(firstColumn.getDate(), 0, 4));
            }else{
                if(year > DISPLAY_YEAR_NUMBER) continue;
                firstColumn.setDispValue("Q"+firstColumn.getQuarterNumber());
            }
            map.put("0", firstColumn);
            this.types.forEach(typeEntity -> {
                FnData fnData = row.get(typeEntity.getType());
                map.put(typeEntity.getType().toString(), fnData==null?empty:fnData);
            });
            table.add(map);
        }
        result.put("table", table);
        return result;
    }

    private FnData getFirstColumn(Map<Integer, FnData> row){
        FnData tmp = row.values().stream().findFirst().get();
        FnData first = new FnData();
        first.setDate(tmp.getDate());
        first.setQuarterNumber(tmp.getQuarterNumber());
        return first;
    }
}
