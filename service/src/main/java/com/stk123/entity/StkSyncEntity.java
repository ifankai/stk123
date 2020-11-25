package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_SYNC", schema = "STK", catalog = "")
public class StkSyncEntity {
    private long id;
    private Long taskId;
    private String tableName;
    private Boolean sqlType;
    private String sqlText;
    private String sqlParams;
    private Boolean status;
    private String errorMsg;
    private Time insertTime;
    private Time syncTime;
    private StkSyncTaskEntity stkSyncTaskByTaskId;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "TASK_ID", nullable = true, precision = 0)
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    @Basic
    @Column(name = "TABLE_NAME", nullable = true, length = 100)
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Basic
    @Column(name = "SQL_TYPE", nullable = true, precision = 0)
    public Boolean getSqlType() {
        return sqlType;
    }

    public void setSqlType(Boolean sqlType) {
        this.sqlType = sqlType;
    }

    @Basic
    @Column(name = "SQL_TEXT", nullable = true, length = 2000)
    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    @Basic
    @Column(name = "SQL_PARAMS", nullable = true)
    public String getSqlParams() {
        return sqlParams;
    }

    public void setSqlParams(String sqlParams) {
        this.sqlParams = sqlParams;
    }

    @Basic
    @Column(name = "STATUS", nullable = true, precision = 0)
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Basic
    @Column(name = "ERROR_MSG", nullable = true)
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    public Time getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Time insertTime) {
        this.insertTime = insertTime;
    }

    @Basic
    @Column(name = "SYNC_TIME", nullable = true)
    public Time getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Time syncTime) {
        this.syncTime = syncTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkSyncEntity that = (StkSyncEntity) o;
        return id == that.id &&
                Objects.equals(taskId, that.taskId) &&
                Objects.equals(tableName, that.tableName) &&
                Objects.equals(sqlType, that.sqlType) &&
                Objects.equals(sqlText, that.sqlText) &&
                Objects.equals(sqlParams, that.sqlParams) &&
                Objects.equals(status, that.status) &&
                Objects.equals(errorMsg, that.errorMsg) &&
                Objects.equals(insertTime, that.insertTime) &&
                Objects.equals(syncTime, that.syncTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, taskId, tableName, sqlType, sqlText, sqlParams, status, errorMsg, insertTime, syncTime);
    }

    @ManyToOne
    @JoinColumn(name = "TASK_ID", referencedColumnName = "ID")
    public StkSyncTaskEntity getStkSyncTaskByTaskId() {
        return stkSyncTaskByTaskId;
    }

    public void setStkSyncTaskByTaskId(StkSyncTaskEntity stkSyncTaskByTaskId) {
        this.stkSyncTaskByTaskId = stkSyncTaskByTaskId;
    }
}
