package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "SYNC_LOG", schema = "STK", catalog = "")
public class SyncLogEntity {
    private long id;
    private Time insertTime;
    private String logText;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    @Column(name = "LOG_TEXT", nullable = true)
    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyncLogEntity that = (SyncLogEntity) o;
        return id == that.id &&
                Objects.equals(insertTime, that.insertTime) &&
                Objects.equals(logText, that.logText);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, insertTime, logText);
    }
}
