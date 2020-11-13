package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import java.util.List;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_ORGANIZATION")
public class StkOrganization implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="NAME")
    private String name;

    private List<StkOwnership> stkOwnership;


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

    public List<StkOwnership> getStkOwnership(){
        return this.stkOwnership;
    }
    public void setStkOwnership(List<StkOwnership> stkOwnership){
        this.stkOwnership = stkOwnership;
    }


    public String toString(){
        return "id="+id+",name="+name+",stkOwnership="+stkOwnership;
    }

}
