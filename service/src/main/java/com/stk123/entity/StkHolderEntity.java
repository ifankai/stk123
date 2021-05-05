package com.stk123.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_HOLDER")
@IdClass(StkHolderEntity.CompositeKey.class)
@Setter
@Getter
@ToString
public class StkHolderEntity implements Serializable {

    @Id
    @Column(name = "CODE", nullable = true, length = 10)
    private String code;

    @Id
    @Column(name = "FN_DATE", nullable = true, length = 8)
    private String fnDate;

    @Basic
    @Column(name = "HOLDER", nullable = true, precision = 2)
    private Double holder;

    @Basic
    @Column(name = "HOLDING_AMOUNT", nullable = true, precision = 2)
    private Double holdingAmount;

    @Basic
    @Column(name = "HOLDER_CHANGE", nullable = true, precision = 2)
    private Double holderChange;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkHolderEntity that = (StkHolderEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(fnDate, that.fnDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, fnDate);
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CompositeKey implements Serializable {
        private String code;
        private String fnDate;
    }
}
