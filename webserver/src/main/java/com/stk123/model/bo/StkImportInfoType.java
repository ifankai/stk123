package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_IMPORT_INFO_TYPE")
public class StkImportInfoType implements Serializable {

    @Column(name="TYPE", pk=true)
    private Integer type;

    @Column(name="NAME")
    private String name;

    @Column(name="MATCH_PATTERN")
    private String matchPattern;

    @Column(name="NOT_MATCH_PATTERN")
    private String notMatchPattern;


    public Integer getType(){
        return this.type;
    }
    public void setType(Integer type){
        this.type = type;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getMatchPattern(){
        return this.matchPattern;
    }
    public void setMatchPattern(String matchPattern){
        this.matchPattern = matchPattern;
    }

    public String getNotMatchPattern(){
        return this.notMatchPattern;
    }
    public void setNotMatchPattern(String notMatchPattern){
        this.notMatchPattern = notMatchPattern;
    }


    public String toString(){
        return "type="+type+",name="+name+",matchPattern="+matchPattern+",notMatchPattern="+notMatchPattern;
    }

}
