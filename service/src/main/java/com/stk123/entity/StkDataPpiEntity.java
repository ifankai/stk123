package com.stk123.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_DATA_PPI")
public class StkDataPpiEntity implements Serializable {
    private Long typeId;
    private String ppiDate;
    private Long value;
    private Time insertTime;
    private StkDataPpiTypeEntity stkDataPpiTypeByTypeId;

    @Id
    @Column(name = "TYPE_ID", nullable = true, precision = 0)
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    @Id
    @Column(name = "PPI_DATE", nullable = true, length = 10)
    public String getPpiDate() {
        return ppiDate;
    }

    public void setPpiDate(String ppiDate) {
        this.ppiDate = ppiDate;
    }

    @Basic
    @Column(name = "VALUE", nullable = true, precision = 2)
    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
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
        StkDataPpiEntity that = (StkDataPpiEntity) o;
        return Objects.equals(typeId, that.typeId) &&
                Objects.equals(ppiDate, that.ppiDate) &&
                Objects.equals(value, that.value) &&
                Objects.equals(insertTime, that.insertTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(typeId, ppiDate, value, insertTime);
    }

    @ManyToOne
    @JoinColumn(name = "TYPE_ID", referencedColumnName = "ID")
    public StkDataPpiTypeEntity getStkDataPpiTypeByTypeId() {
        return stkDataPpiTypeByTypeId;
    }

    public void setStkDataPpiTypeByTypeId(StkDataPpiTypeEntity stkDataPpiTypeByTypeId) {
        this.stkDataPpiTypeByTypeId = stkDataPpiTypeByTypeId;
    }
}
