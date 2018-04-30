package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_INDUSTRY_TYPE")
public class StkIndustryType implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="NAME")
    private String name;

    @Column(name="SOURCE")
    private String source;

    @Column(name="CARE_FLAG")
    private Integer careFlag;

    @Column(name="PARENT_ID")
    private Integer parentId;

    @Column(name="US_NAME")
    private String usName;


    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getSource(){
        return this.source;
    }
    public void setSource(String source){
        this.source = source;
    }

    public Integer getCareFlag(){
        return this.careFlag;
    }
    public void setCareFlag(Integer careFlag){
        this.careFlag = careFlag;
    }

    public Integer getParentId(){
        return this.parentId;
    }
    public void setParentId(Integer parentId){
        this.parentId = parentId;
    }

    public String getUsName(){
        return this.usName;
    }
    public void setUsName(String usName){
        this.usName = usName;
    }


    public String toString(){
        return "id="+id+",name="+name+",source="+source+",careFlag="+careFlag+",parentId="+parentId+",usName="+usName;
    }

}
