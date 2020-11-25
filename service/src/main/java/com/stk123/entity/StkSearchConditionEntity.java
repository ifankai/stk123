package com.stk123.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_SEARCH_CONDITION", schema = "STK", catalog = "")
public class StkSearchConditionEntity {
    private Long id;
    private String type;
    private String name;
    private String text;
    private Time insertTime;
    private Time updateTime;

    @Basic
    @Column(name = "ID", nullable = true, precision = 0)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "TYPE", nullable = true, length = 20)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
    @Column(name = "TEXT", nullable = true)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkSearchConditionEntity that = (StkSearchConditionEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(type, that.type) &&
                Objects.equals(name, that.name) &&
                Objects.equals(text, that.text) &&
                Objects.equals(insertTime, that.insertTime) &&
                Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, type, name, text, insertTime, updateTime);
    }
}
