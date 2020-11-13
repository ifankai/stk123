package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_SYNC_TABLE")
public class StkSyncTable implements Serializable {

    @Column(name="NAME")
    private String name;

    @Column(name="PK")
    private String pk;


    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getPk(){
        return this.pk;
    }
    public void setPk(String pk){
        this.pk = pk;
    }


    public String toString(){
        return "name="+name+",pk="+pk;
    }

}
