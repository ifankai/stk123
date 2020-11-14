package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_DATA_PPI")
public class StkDataPpi implements Serializable {

    @Column(name="TYPE_ID")
    private Integer typeId;

    @Column(name="PPI_DATE")
    private String ppiDate;

    @Column(name="VALUE")
    private Double value;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    private StkDataPpiType stkDataPpiType;


    public Integer getTypeId(){
        return this.typeId;
    }
    public void setTypeId(Integer typeId){
        this.typeId = typeId;
    }

    public String getPpiDate(){
        return this.ppiDate;
    }
    public void setPpiDate(String ppiDate){
        this.ppiDate = ppiDate;
    }

    public Double getValue(){
        return this.value;
    }
    public void setValue(Double value){
        this.value = value;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public StkDataPpiType getStkDataPpiType(){
        return this.stkDataPpiType;
    }
    public void setStkDataPpiType(StkDataPpiType stkDataPpiType){
        this.stkDataPpiType = stkDataPpiType;
    }


    public String toString(){
        return "typeId="+typeId+",ppiDate="+ppiDate+",value="+value+",insertTime="+insertTime+",stkDataPpiType="+stkDataPpiType;
    }

}
