package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_INFO_LOG")
public class StkInfoLog implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="SOURCE")
    private String source;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="URL")
    private String url;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    private Stk stk;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public String getSource(){
        return this.source;
    }
    public void setSource(String source){
        this.source = source;
    }

    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public String getUrl(){
        return this.url;
    }
    public void setUrl(String url){
        this.url = url;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public Stk getStk(){
        return this.stk;
    }
    public void setStk(Stk stk){
        this.stk = stk;
    }


    public String toString(){
        return "code="+code+",source="+source+",description="+description+",url="+url+",insertTime="+insertTime+",stk="+stk;
    }

}
