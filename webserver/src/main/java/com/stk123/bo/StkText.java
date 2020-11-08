package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_TEXT")
public class StkText implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="TYPE")
    private Integer type;

    @Column(name="CODE")
    private String code;

    @Column(name="CODE_TYPE")
    private Integer codeType;

    @Column(name="TITLE")
    private String title;

    @Column(name="TEXT")
    private String text;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    @Column(name="UPDATE_TIME")
    private Timestamp updateTime;

    @Column(name="DISP_ORDER")
    private Integer dispOrder;

    @Column(name="USER_ID")
    private Integer userId;

    @Column(name="SUB_TYPE")
    private Integer subType;


    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public Integer getType(){
        return this.type;
    }
    public void setType(Integer type){
        this.type = type;
    }

    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public Integer getCodeType(){
        return this.codeType;
    }
    public void setCodeType(Integer codeType){
        this.codeType = codeType;
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
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

    public Timestamp getUpdateTime(){
        return this.updateTime;
    }
    public void setUpdateTime(Timestamp updateTime){
        this.updateTime = updateTime;
    }

    public Integer getDispOrder(){
        return this.dispOrder;
    }
    public void setDispOrder(Integer dispOrder){
        this.dispOrder = dispOrder;
    }

    public Integer getUserId(){
        return this.userId;
    }
    public void setUserId(Integer userId){
        this.userId = userId;
    }

    public Integer getSubType(){
        return this.subType;
    }
    public void setSubType(Integer subType){
        this.subType = subType;
    }


    public String toString(){
        return "id="+id+",type="+type+",code="+code+",codeType="+codeType+",title="+title+",text="+text+",insertTime="+insertTime+",updateTime="+updateTime+",dispOrder="+dispOrder+",userId="+userId+",subType="+subType;
    }

}
