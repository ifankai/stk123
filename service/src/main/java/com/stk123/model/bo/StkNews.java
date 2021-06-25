package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import com.stk123.common.util.JdbcUtils.Table;

import java.io.Serializable;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_NEWS")
public class StkNews implements Serializable {

    @Column(name="ID", pk=true)
    private Long id;

    @Column(name="CODE")
    private String code;

    @Column(name="TYPE")
    private Integer type;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    @Column(name="CARE_FLAG")
    private Integer careFlag;

    @Column(name="INFO")
    private String info;

    @Column(name="TITLE")
    private String title;

    @Column(name="URL_SOURCE")
    private String urlSource;

    @Column(name="URL_TARGET")
    private String urlTarget;

    @Column(name="INFO_CREATE_TIME")
    private Timestamp infoCreateTime;

    private Stk stk;

    private StkImportInfoType stkImportInfoType;


    public Long getId(){
        return this.id;
    }
    public void setId(Long id){
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

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public Integer getCareFlag(){
        return this.careFlag;
    }
    public void setCareFlag(Integer careFlag){
        this.careFlag = careFlag;
    }

    public String getInfo(){
        return this.info;
    }
    public void setInfo(String info){
        this.info = info;
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getUrlSource(){
        return this.urlSource;
    }
    public void setUrlSource(String urlSource){
        this.urlSource = urlSource;
    }

    public String getUrlTarget(){
        return this.urlTarget;
    }
    public void setUrlTarget(String urlTarget){
        this.urlTarget = urlTarget;
    }

    public Timestamp getInfoCreateTime(){
        return this.infoCreateTime;
    }
    public void setInfoCreateTime(Timestamp infoCreateTime){
        this.infoCreateTime = infoCreateTime;
    }

    public Stk getStk(){
        return this.stk;
    }
    public void setStk(Stk stk){
        this.stk = stk;
    }

    public StkImportInfoType getStkImportInfoType(){
        return this.stkImportInfoType;
    }
    public void setStkImportInfoType(StkImportInfoType stkImportInfoType){
        this.stkImportInfoType = stkImportInfoType;
    }


    public String toString(){
        return "id="+id+",code="+code+",type="+type+",insertTime="+insertTime+",careFlag="+careFlag+",info="+info+",title="+title+",urlSource="+urlSource+",urlTarget="+urlTarget+",infoCreateTime="+infoCreateTime+",stk="+stk+",stkImportInfoType="+stkImportInfoType;
    }

}
