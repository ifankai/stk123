package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import java.util.List;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_KEYWORD")
public class StkKeyword implements Serializable {

    @Column(name="ID", pk=true)
    private Long id;

    @Column(name="NAME")
    private String name;

    @Column(name="BOOST")
    private Integer boost;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    @Column(name="STATUS")
    private Integer status;

    private List<StkKeywordLink> stkKeywordLink;


    public Long getId(){
        return this.id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public Integer getBoost(){
        return this.boost;
    }
    public void setBoost(Integer boost){
        this.boost = boost;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public Integer getStatus(){
        return this.status;
    }
    public void setStatus(Integer status){
        this.status = status;
    }

    public List<StkKeywordLink> getStkKeywordLink(){
        return this.stkKeywordLink;
    }
    public void setStkKeywordLink(List<StkKeywordLink> stkKeywordLink){
        this.stkKeywordLink = stkKeywordLink;
    }


    public String toString(){
        return "id="+id+",name="+name+",boost="+boost+",insertTime="+insertTime+",status="+status+",stkKeywordLink="+stkKeywordLink;
    }

}
