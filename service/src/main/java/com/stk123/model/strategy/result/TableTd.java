package com.stk123.model.strategy.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class TableTd {
    private String xTitle;
    private String yTitle;
    private List<String> td;

    public TableTd(String xTitle, String yTitle, String td){
        this.xTitle = xTitle;
        this.yTitle = yTitle;
        if(td == null){
            this.td = new ArrayList<>();
        }
        this.td.add(td);
    }

    public void add(List<String> tds){
        if(td == null){
            this.td = new ArrayList<>();
        }
        this.td.addAll(tds);
    }
}
