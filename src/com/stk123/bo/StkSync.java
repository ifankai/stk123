package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_SYNC")
public class StkSync implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="TASK_ID")
    private Integer taskId;

    @Column(name="TABLE_NAME")
    private String tableName;

    @Column(name="SQL_TYPE")
    private Integer sqlType;

    @Column(name="SQL_TEXT")
    private String sqlText;

    @Column(name="SQL_PARAMS")
    private String sqlParams;

    @Column(name="STATUS")
    private Integer status;

    @Column(name="ERROR_MSG")
    private String errorMsg;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    @Column(name="SYNC_TIME")
    private Timestamp syncTime;

    private StkSyncTask stkSyncTask;


    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public Integer getTaskId(){
        return this.taskId;
    }
    public void setTaskId(Integer taskId){
        this.taskId = taskId;
    }

    public String getTableName(){
        return this.tableName;
    }
    public void setTableName(String tableName){
        this.tableName = tableName;
    }

    public Integer getSqlType(){
        return this.sqlType;
    }
    public void setSqlType(Integer sqlType){
        this.sqlType = sqlType;
    }

    public String getSqlText(){
        return this.sqlText;
    }
    public void setSqlText(String sqlText){
        this.sqlText = sqlText;
    }

    public String getSqlParams(){
        return this.sqlParams;
    }
    public void setSqlParams(String sqlParams){
        this.sqlParams = sqlParams;
    }

    public Integer getStatus(){
        return this.status;
    }
    public void setStatus(Integer status){
        this.status = status;
    }

    public String getErrorMsg(){
        return this.errorMsg;
    }
    public void setErrorMsg(String errorMsg){
        this.errorMsg = errorMsg;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public Timestamp getSyncTime(){
        return this.syncTime;
    }
    public void setSyncTime(Timestamp syncTime){
        this.syncTime = syncTime;
    }

    public StkSyncTask getStkSyncTask(){
        return this.stkSyncTask;
    }
    public void setStkSyncTask(StkSyncTask stkSyncTask){
        this.stkSyncTask = stkSyncTask;
    }


    public String toString(){
        return "id="+id+",taskId="+taskId+",tableName="+tableName+",sqlType="+sqlType+",sqlText="+sqlText+",sqlParams="+sqlParams+",status="+status+",errorMsg="+errorMsg+",insertTime="+insertTime+",syncTime="+syncTime+",stkSyncTask="+stkSyncTask;
    }

}
