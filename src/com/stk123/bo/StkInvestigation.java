package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_INVESTIGATION")
public class StkInvestigation implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="CODE")
    private String code;

    @Column(name="TITLE")
    private String title;

    @Column(name="INVESTIGATOR")
    private String investigator;

    @Column(name="INVESTIGATOR_COUNT")
    private Integer investigatorCount;

    @Column(name="TEXT")
    private String text;

    @Column(name="TEXT_COUNT")
    private Integer textCount;

    @Column(name="INVEST_DATE")
    private Timestamp investDate;

    @Column(name="INSERT_DATE")
    private Timestamp insertDate;

    @Column(name="SOURCE_URL")
    private String sourceUrl;

    @Column(name="SOURCE_TYPE")
    private String sourceType;


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

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getInvestigator(){
        return this.investigator;
    }
    public void setInvestigator(String investigator){
        this.investigator = investigator;
    }

    public Integer getInvestigatorCount(){
        return this.investigatorCount;
    }
    public void setInvestigatorCount(Integer investigatorCount){
        this.investigatorCount = investigatorCount;
    }

    public String getText(){
        return this.text;
    }
    public void setText(String text){
        this.text = text;
    }

    public Integer getTextCount(){
        return this.textCount;
    }
    public void setTextCount(Integer textCount){
        this.textCount = textCount;
    }

    public Timestamp getInvestDate(){
        return this.investDate;
    }
    public void setInvestDate(Timestamp investDate){
        this.investDate = investDate;
    }

    public Timestamp getInsertDate(){
        return this.insertDate;
    }
    public void setInsertDate(Timestamp insertDate){
        this.insertDate = insertDate;
    }

    public String getSourceUrl(){
        return this.sourceUrl;
    }
    public void setSourceUrl(String sourceUrl){
        this.sourceUrl = sourceUrl;
    }

    public String getSourceType(){
        return this.sourceType;
    }
    public void setSourceType(String sourceType){
        this.sourceType = sourceType;
    }


    public String toString(){
        return "id="+id+",code="+code+",title="+title+",investigator="+investigator+",investigatorCount="+investigatorCount+",text="+text+",textCount="+textCount+",investDate="+investDate+",insertDate="+insertDate+",sourceUrl="+sourceUrl+",sourceType="+sourceType;
    }

}
