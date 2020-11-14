package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import java.util.List;
import com.stk123.common.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_LABEL")
public class StkLabel implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="NAME")
    private String name;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    @Column(name="UPDATE_TIME")
    private Timestamp updateTime;

    @Column(name="USER_ID")
    private Integer userId;

    private List<StkLabelText> stkLabelText;


    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public Timestamp getUpdateTime(){
        return this.updateTime;
    }
    public void setUpdateTime(Timestamp updateTime){
        this.updateTime = updateTime;
    }

    public Integer getUserId(){
        return this.userId;
    }
    public void setUserId(Integer userId){
        this.userId = userId;
    }

    public List<StkLabelText> getStkLabelText(){
        return this.stkLabelText;
    }
    public void setStkLabelText(List<StkLabelText> stkLabelText){
        this.stkLabelText = stkLabelText;
    }


    public String toString(){
        return "id="+id+",name="+name+",insertTime="+insertTime+",updateTime="+updateTime+",userId="+userId+",stkLabelText="+stkLabelText;
    }

}
