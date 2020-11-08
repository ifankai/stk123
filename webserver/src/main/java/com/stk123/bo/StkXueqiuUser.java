package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_XUEQIU_USER")
public class StkXueqiuUser implements Serializable {

    @Column(name="ID", pk=true)
    private Long id;

    @Column(name="USER_ID")
    private String userId;

    @Column(name="NAME")
    private String name;


    public Long getId(){
        return this.id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public String getUserId(){
        return this.userId;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }


    public String toString(){
        return "id="+id+",userId="+userId+",name="+name;
    }

}
