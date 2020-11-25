package com.stk123.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_HOLDER")
public class StkHolderEntity implements Serializable {
    private String code;
    private String fnDate;
    private Long holder;
    private StkEntity stkByCode;

    @Id
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Id
    @Column(name = "FN_DATE", nullable = true, length = 8)
    public String getFnDate() {
        return fnDate;
    }

    public void setFnDate(String fnDate) {
        this.fnDate = fnDate;
    }

    @Basic
    @Column(name = "HOLDER", nullable = true, precision = 2)
    public Long getHolder() {
        return holder;
    }

    public void setHolder(Long holder) {
        this.holder = holder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkHolderEntity that = (StkHolderEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(fnDate, that.fnDate) &&
                Objects.equals(holder, that.holder);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, fnDate, holder);
    }

//    @ManyToOne
//    @JoinColumn(name = "CODE", referencedColumnName = "CODE")
//    public StkEntity getStkByCode() {
//        return stkByCode;
//    }
//
//    public void setStkByCode(StkEntity stkByCode) {
//        this.stkByCode = stkByCode;
//    }
}
