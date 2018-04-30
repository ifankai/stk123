package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_LABEL_TEXT")
public class StkLabelText implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="LABEL_ID")
    private Integer labelId;

    @Column(name="TEXT_ID")
    private Integer textId;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    @Column(name="UPDATE_TIME")
    private Timestamp updateTime;

    private StkLabel stkLabel;


    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public Integer getLabelId(){
        return this.labelId;
    }
    public void setLabelId(Integer labelId){
        this.labelId = labelId;
    }

    public Integer getTextId(){
        return this.textId;
    }
    public void setTextId(Integer textId){
        this.textId = textId;
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

    public StkLabel getStkLabel(){
        return this.stkLabel;
    }
    public void setStkLabel(StkLabel stkLabel){
        this.stkLabel = stkLabel;
    }


    public String toString(){
        return "id="+id+",labelId="+labelId+",textId="+textId+",insertTime="+insertTime+",updateTime="+updateTime+",stkLabel="+stkLabel;
    }

}
