package com.stk123.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {

    private String name;
    private int countOfRunning; //
    private long startTime;   //最近一个task的startTime
    private long endTimeSucc;   //最近一个成功的结束时间
    private long endTimeFail;   //最近一个失败的结束时间
    private String succMsg;
    private String failMsg;

}
