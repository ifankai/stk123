package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

//@Entity
@Table(name = "STK_LABEL_TEXT")
public class StkLabelTextEntity {
    private long id;
    private Long labelId;
    private Long textId;
    private Time insertTime;
    private Time updateTime;
    private StkLabelEntity stkLabelByLabelId;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "LABEL_ID", nullable = true, precision = 0)
    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    @Basic
    @Column(name = "TEXT_ID", nullable = true, precision = 0)
    public Long getTextId() {
        return textId;
    }

    public void setTextId(Long textId) {
        this.textId = textId;
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
        StkLabelTextEntity that = (StkLabelTextEntity) o;
        return id == that.id &&
                Objects.equals(labelId, that.labelId) &&
                Objects.equals(textId, that.textId) &&
                Objects.equals(insertTime, that.insertTime) &&
                Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, labelId, textId, insertTime, updateTime);
    }

    @ManyToOne
    @JoinColumn(name = "LABEL_ID", referencedColumnName = "ID")
    public StkLabelEntity getStkLabelByLabelId() {
        return stkLabelByLabelId;
    }

    public void setStkLabelByLabelId(StkLabelEntity stkLabelByLabelId) {
        this.stkLabelByLabelId = stkLabelByLabelId;
    }
}
