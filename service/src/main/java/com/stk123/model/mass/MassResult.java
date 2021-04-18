package com.stk123.model.mass;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MassResult {
    private List<List<String>> datas = new ArrayList<>();
    private int count = 0;

    public void count(int count){
        this.count += count;
    }
}
