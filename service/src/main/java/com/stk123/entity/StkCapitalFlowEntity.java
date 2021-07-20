package com.stk123.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "STK_CAPITAL_FLOW")
@IdClass(StkCapitalFlowEntity.CompositeKey.class)
@Getter
@Setter
public class StkCapitalFlowEntity implements Serializable {
    @Id
    @Column(name = "CODE", nullable = true, length = 10)
    private String code;

    @Id
    @Column(name = "FLOW_DATE", nullable = true, length = 8)
    private String flowDate;

    @Basic
    @Column(name = "MAIN_AMOUNT", nullable = true, precision = 2)
    private Double mainAmount;

    @Basic
    @Column(name = "MAIN_PERCENT", nullable = true, precision = 2)
    private Double mainPercent;

    @Basic
    @Column(name = "SUPER_LARGE_AMOUNT", nullable = true, precision = 2)
    private Double superLargeAmount;

    @Basic
    @Column(name = "SUPER_LARGE_PERCENT", nullable = true, precision = 2)
    private Double superLargePercent;

    @Basic
    @Column(name = "LARGE_AMOUNT", nullable = true, precision = 2)
    private Double largeAmount;

    @Basic
    @Column(name = "LARGE_PERCENT", nullable = true, precision = 2)
    private Double largePercent;

    @Basic
    @Column(name = "MIDDLE_AMOUNT", nullable = true, precision = 2)
    private Double middleAmount;

    @Basic
    @Column(name = "MIDDLE_PERCENT", nullable = true, precision = 2)
    private Double middlePercent;

    @Basic
    @Column(name = "SMALL_AMOUNT", nullable = true, precision = 2)
    private Double smallAmount;

    @Basic
    @Column(name = "SMALL_PERCENT", nullable = true, precision = 2)
    private Double smallPercent;

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    private Date insertTime;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkCapitalFlowEntity that = (StkCapitalFlowEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(flowDate, that.flowDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, flowDate);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CompositeKey implements Serializable {
        private String code;
        private String flowDate;
    }
}
