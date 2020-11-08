package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_CARE")
public class StkCare implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="TYPE")
    private String type;

    @Column(name="INFO")
    private String info;

    @Column(name="URL")
    private String url;

    @Column(name="MEMO")
    private String memo;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    @Column(name="INFO_CREATE_TIME")
    private Timestamp infoCreateTime;

    @Column(name="PARAM1")
    private String param1;

    @Column(name="PARAM2")
    private String param2;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }

    public String getInfo(){
        return this.info;
    }
    public void setInfo(String info){
        this.info = info;
    }

    public String getUrl(){
        return this.url;
    }
    public void setUrl(String url){
        this.url = url;
    }

    public String getMemo(){
        return this.memo;
    }
    public void setMemo(String memo){
        this.memo = memo;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public Timestamp getInfoCreateTime(){
        return this.infoCreateTime;
    }
    public void setInfoCreateTime(Timestamp infoCreateTime){
        this.infoCreateTime = infoCreateTime;
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


    public String toString(){
        return "code="+code+",type="+type+",info="+info+",url="+url+",memo="+memo+",insertTime="+insertTime+",infoCreateTime="+infoCreateTime+",param1="+param1+",param2="+param2;
    }

}
