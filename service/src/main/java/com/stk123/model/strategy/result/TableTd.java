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
    private List<String> contents;

    public TableTd(String xTitle, String yTitle, String content){
        this.xTitle = xTitle;
        this.yTitle = yTitle;
        if(this.contents == null){
            this.contents = new ArrayList<>();
        }
        this.contents.add(content);
    }

    public void add(List<String> contents){
        if(contents == null){
            this.contents = new ArrayList<>();
        }
        this.contents.addAll(contents);
    }
}
