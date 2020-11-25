package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Collection;
import java.util.Objects;

//@Entity
@Table(name = "STK_SYNC_TASK")
public class StkSyncTaskEntity {
    private long id;
    private String name;
    private Time taskStartTime;
    private Time taskEndTime;
    private Time syncStartTime;
    private Collection<StkSyncEntity> stkSyncsById;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NAME", nullable = true, length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "TASK_START_TIME", nullable = true)
    public Time getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(Time taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    @Basic
    @Column(name = "TASK_END_TIME", nullable = true)
    public Time getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(Time taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    @Basic
    @Column(name = "SYNC_START_TIME", nullable = true)
    public Time getSyncStartTime() {
        return syncStartTime;
    }

    public void setSyncStartTime(Time syncStartTime) {
        this.syncStartTime = syncStartTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkSyncTaskEntity that = (StkSyncTaskEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(taskStartTime, that.taskStartTime) &&
                Objects.equals(taskEndTime, that.taskEndTime) &&
                Objects.equals(syncStartTime, that.syncStartTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, taskStartTime, taskEndTime, syncStartTime);
    }

    @OneToMany(mappedBy = "stkSyncTaskByTaskId")
    public Collection<StkSyncEntity> getStkSyncsById() {
        return stkSyncsById;
    }

    public void setStkSyncsById(Collection<StkSyncEntity> stkSyncsById) {
        this.stkSyncsById = stkSyncsById;
    }
}
