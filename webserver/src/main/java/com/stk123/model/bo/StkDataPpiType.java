package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import java.util.List;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_DATA_PPI_TYPE")
public class StkDataPpiType implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="NAME")
    private String name;

    @Column(name="PARENT_ID")
    private Integer parentId;

    @Column(name="URL")
    private String url;

    private List<StkDataPpi> stkDataPpi;


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

    public Integer getParentId(){
        return this.parentId;
    }
    public void setParentId(Integer parentId){
        this.parentId = parentId;
    }

    public String getUrl(){
        return this.url;
    }
    public void setUrl(String url){
        this.url = url;
    }

    public List<StkDataPpi> getStkDataPpi(){
        return this.stkDataPpi;
    }
    public void setStkDataPpi(List<StkDataPpi> stkDataPpi){
        this.stkDataPpi = stkDataPpi;
    }


    public String toString(){
        return "id="+id+",name="+name+",parentId="+parentId+",url="+url+",stkDataPpi="+stkDataPpi;
    }

}
