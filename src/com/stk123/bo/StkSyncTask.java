package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import java.util.List;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_SYNC_TASK")
public class StkSyncTask implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="NAME")
    private String name;

    @Column(name="TASK_START_TIME")
    private Timestamp taskStartTime;

    @Column(name="TASK_END_TIME")
    private Timestamp taskEndTime;

    @Column(name="SYNC_START_TIME")
    private Timestamp syncStartTime;

    private List<StkSync> stkSync;


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

    public Timestamp getTaskStartTime(){
        return this.taskStartTime;
    }
    public void setTaskStartTime(Timestamp taskStartTime){
        this.taskStartTime = taskStartTime;
    }

    public Timestamp getTaskEndTime(){
        return this.taskEndTime;
    }
    public void setTaskEndTime(Timestamp taskEndTime){
        this.taskEndTime = taskEndTime;
    }

    public Timestamp getSyncStartTime(){
        return this.syncStartTime;
    }
    public void setSyncStartTime(Timestamp syncStartTime){
        this.syncStartTime = syncStartTime;
    }

    public List<StkSync> getStkSync(){
        return this.stkSync;
    }
    public void setStkSync(List<StkSync> stkSync){
        this.stkSync = stkSync;
    }


    public String toString(){
        return "id="+id+",name="+name+",taskStartTime="+taskStartTime+",taskEndTime="+taskEndTime+",syncStartTime="+syncStartTime+",stkSync="+stkSync;
    }

}
