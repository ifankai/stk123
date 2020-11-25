package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "STK_LABEL", schema = "STK", catalog = "")
public class StkLabelEntity {
    private long id;
    private String name;
    private Time insertTime;
    private Time updateTime;
    private Long userId;
    private Collection<StkLabelTextEntity> stkLabelTextsById;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NAME", nullable = true, length = 40)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    @Column(name = "UPDATE_TIME", nullable = true)
    public Time getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Time updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "USER_ID", nullable = true, precision = 0)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkLabelEntity that = (StkLabelEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(insertTime, that.insertTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, insertTime, updateTime, userId);
    }

    @OneToMany(mappedBy = "stkLabelByLabelId")
    public Collection<StkLabelTextEntity> getStkLabelTextsById() {
        return stkLabelTextsById;
    }

    public void setStkLabelTextsById(Collection<StkLabelTextEntity> stkLabelTextsById) {
        this.stkLabelTextsById = stkLabelTextsById;
    }
}
