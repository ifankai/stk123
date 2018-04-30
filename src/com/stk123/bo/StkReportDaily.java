package com.stk123.bo;

import java.io.Serializable;
import java.sql.Timestamp;

import com.stk123.tool.util.JdbcUtils.Column;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_REPORT_DAILY")
public class StkReportDaily implements Serializable {

    @Column(name="TYPE")
    private Integer type;

    @Column(name="REPORT_DATE")
    private String reportDate;

    @Column(name="CODE")
    private String code;

    @Column(name="REMARK")
    private String remark;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    @Column(name="REMARK_2")
    private String remark2;


    public Integer getType(){
        return this.type;
    }
    public void setType(Integer type){
        this.type = type;
    }

    public String getReportDate(){
        return this.reportDate;
    }
    public void setReportDate(String reportDate){
        this.reportDate = reportDate;
    }

    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public String getRemark(){
        return this.remark;
    }
    public void setRemark(String remark){
        this.remark = remark;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public String getRemark2(){
        return this.remark2;
    }
    public void setRemark2(String remark2){
        this.remark2 = remark2;
    }


    public String toString(){
        return "type="+type+",reportDate="+reportDate+",code="+code+",remark="+remark+",insertTime="+insertTime+",remark2="+remark2;
    }

}
