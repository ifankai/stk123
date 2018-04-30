package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_DEPT_TYPE")
public class StkDeptType implements Serializable {

    @Column(name="DEPT_ID", pk=true)
    private Integer deptId;

    @Column(name="DEPT_NAME")
    private String deptName;


    public Integer getDeptId(){
        return this.deptId;
    }
    public void setDeptId(Integer deptId){
        this.deptId = deptId;
    }

    public String getDeptName(){
        return this.deptName;
    }
    public void setDeptName(String deptName){
        this.deptName = deptName;
    }


    public String toString(){
        return "deptId="+deptId+",deptName="+deptName;
    }

}
