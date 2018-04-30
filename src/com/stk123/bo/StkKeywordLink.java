package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_KEYWORD_LINK")
public class StkKeywordLink implements Serializable {

    @Column(name="ID", pk=true)
    private Long id;

    @Column(name="CODE")
    private String code;

    @Column(name="CODE_TYPE")
    private Integer codeType;

    @Column(name="KEYWORD_ID")
    private Long keywordId;

    @Column(name="BOOST")
    private Integer boost;

    @Column(name="LINK_TYPE")
    private Integer linkType;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    private StkKeyword stkKeyword;


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

    public Integer getCodeType(){
        return this.codeType;
    }
    public void setCodeType(Integer codeType){
        this.codeType = codeType;
    }

    public Long getKeywordId(){
        return this.keywordId;
    }
    public void setKeywordId(Long keywordId){
        this.keywordId = keywordId;
    }

    public Integer getBoost(){
        return this.boost;
    }
    public void setBoost(Integer boost){
        this.boost = boost;
    }

    public Integer getLinkType(){
        return this.linkType;
    }
    public void setLinkType(Integer linkType){
        this.linkType = linkType;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public StkKeyword getStkKeyword(){
        return this.stkKeyword;
    }
    public void setStkKeyword(StkKeyword stkKeyword){
        this.stkKeyword = stkKeyword;
    }


    public String toString(){
        return "id="+id+",code="+code+",codeType="+codeType+",keywordId="+keywordId+",boost="+boost+",linkType="+linkType+",insertTime="+insertTime+",stkKeyword="+stkKeyword;
    }

}
