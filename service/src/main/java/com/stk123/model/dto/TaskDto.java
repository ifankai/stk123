package com.stk123.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDto {

    private String name;
    private int countOfRunning; //
    private long startTime;   //最近一个task的startTime
    private long endTimeSucc;   //最近一个成功的结束时间
    private long endTimeFail;   //最近一个失败的结束时间
    private String succMsg;
    private String failMsg;

}
