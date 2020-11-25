package com.stk123.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STK_SEQUENCE")
public class StkSequenceEntity {
    private String seqName;
    private Integer seqValue;

    @Id
    @Column(name = "SEQ_NAME", nullable = false, length = 30)
    public String getSeqName() {
        return seqName;
    }

    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }

    @Basic
    @Column(name = "SEQ_VALUE", nullable = true, precision = 0)
    public Integer getSeqValue() {
        return seqValue;
    }

    public void setSeqValue(Integer seqValue) {
        this.seqValue = seqValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkSequenceEntity that = (StkSequenceEntity) o;
        return Objects.equals(seqName, that.seqName) &&
                Objects.equals(seqValue, that.seqValue);
    }

    @Override
    public int hashCode() {

        return Objects.hash(seqName, seqValue);
    }
}
