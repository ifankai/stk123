package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_INTERNET_SEARCH")
public class StkInternetSearch implements Serializable {

    @Column(name="SEARCH_SOURCE")
    private Integer searchSource;

    @Column(name="SEARCH_URL")
    private String searchUrl;

    @Column(name="LAST_SEARCH_TEXT")
    private String lastSearchText;

    @Column(name="UPDATE_TIME")
    private Timestamp updateTime;

    @Column(name="STATUS")
    private Integer status;

    @Column(name="DESC_1")
    private String desc1;


    public Integer getSearchSource(){
        return this.searchSource;
    }
    public void setSearchSource(Integer searchSource){
        this.searchSource = searchSource;
    }

    public String getSearchUrl(){
        return this.searchUrl;
    }
    public void setSearchUrl(String searchUrl){
        this.searchUrl = searchUrl;
    }

    public String getLastSearchText(){
        return this.lastSearchText;
    }
    public void setLastSearchText(String lastSearchText){
        this.lastSearchText = lastSearchText;
    }

    public Timestamp getUpdateTime(){
        return this.updateTime;
    }
    public void setUpdateTime(Timestamp updateTime){
        this.updateTime = updateTime;
    }

    public Integer getStatus(){
        return this.status;
    }
    public void setStatus(Integer status){
        this.status = status;
    }

    public String getDesc1(){
        return this.desc1;
    }
    public void setDesc1(String desc1){
        this.desc1 = desc1;
    }


    public String toString(){
        return "searchSource="+searchSource+",searchUrl="+searchUrl+",lastSearchText="+lastSearchText+",updateTime="+updateTime+",status="+status+",desc1="+desc1;
    }

}
