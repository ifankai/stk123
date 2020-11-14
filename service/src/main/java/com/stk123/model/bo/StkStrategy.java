package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_STRATEGY")
public class StkStrategy implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="STRATEGY_DATE")
    private String strategyDate;

    @Column(name="NAME")
    private String name;

    @Column(name="TEXT")
    private String text;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;


    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getStrategyDate(){
        return this.strategyDate;
    }
    public void setStrategyDate(String strategyDate){
        this.strategyDate = strategyDate;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getText(){
        return this.text;
    }
    public void setText(String text){
        this.text = text;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }


    public String toString(){
        return "id="+id+",strategyDate="+strategyDate+",name="+name+",text="+text+",insertTime="+insertTime;
    }

}
