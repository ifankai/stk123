package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_MONITOR")
public class StkMonitor implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="CODE")
    private String code;

    @Column(name="TYPE")
    private Integer type;

    @Column(name="STATUS")
    private Integer status;

    @Column(name="TRIGGER_DATE")
    private Timestamp triggerDate;

    @Column(name="INSERT_DATE")
    private Timestamp insertDate;

    @Column(name="PARAM_1")
    private String param1;

    @Column(name="PARAM_2")
    private String param2;

    @Column(name="PARAM_3")
    private String param3;

    @Column(name="PARAM_4")
    private String param4;

    @Column(name="PARAM_5")
    private String param5;

    @Column(name="RESULT_1")
    private String result1;


    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public Integer getType(){
        return this.type;
    }
    public void setType(Integer type){
        this.type = type;
    }

    public Integer getStatus(){
        return this.status;
    }
    public void setStatus(Integer status){
        this.status = status;
    }

    public Timestamp getTriggerDate(){
        return this.triggerDate;
    }
    public void setTriggerDate(Timestamp triggerDate){
        this.triggerDate = triggerDate;
    }

    public Timestamp getInsertDate(){
        return this.insertDate;
    }
    public void setInsertDate(Timestamp insertDate){
        this.insertDate = insertDate;
    }

    public String getParam1(){
        return this.param1;
    }
    public void setParam1(String param1){
        this.param1 = param1;
    }

    public String getParam2(){
        return this.param2;
    }
    public void setParam2(String param2){
        this.param2 = param2;
    }

    public String getParam3(){
        return this.param3;
    }
    public void setParam3(String param3){
        this.param3 = param3;
    }

    public String getParam4(){
        return this.param4;
    }
    public void setParam4(String param4){
        this.param4 = param4;
    }

    public String getParam5(){
        return this.param5;
    }
    public void setParam5(String param5){
        this.param5 = param5;
    }

    public String getResult1(){
        return this.result1;
    }
    public void setResult1(String result1){
        this.result1 = result1;
    }


    public String toString(){
        return "id="+id+",code="+code+",type="+type+",status="+status+",triggerDate="+triggerDate+",insertDate="+insertDate+",param1="+param1+",param2="+param2+",param3="+param3+",param4="+param4+",param5="+param5+",result1="+result1;
    }

}
