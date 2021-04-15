package com.stk123.model.mass;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MassResult {
    public List<String> data;
    public int count;
}
