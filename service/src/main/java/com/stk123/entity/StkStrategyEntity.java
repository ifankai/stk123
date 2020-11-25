package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_STRATEGY")
public class StkStrategyEntity {
    private long id;
    private String strategyDate;
    private String name;
    private String text;
    private Time insertTime;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "STRATEGY_DATE", nullable = true, length = 10)
    public String getStrategyDate() {
        return strategyDate;
    }

    public void setStrategyDate(String strategyDate) {
        this.strategyDate = strategyDate;
    }

    @Basic
    @Column(name = "NAME", nullable = true, length = 400)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "TEXT", nullable = true, length = 4000)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkStrategyEntity that = (StkStrategyEntity) o;
        return id == that.id &&
                Objects.equals(strategyDate, that.strategyDate) &&
                Objects.equals(name, that.name) &&
                Objects.equals(text, that.text) &&
                Objects.equals(insertTime, that.insertTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, strategyDate, name, text, insertTime);
    }
}
