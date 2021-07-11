package com.stk123.entity;

import lombok.*;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Setter
@Getter
@ToString
@MappedSuperclass
public abstract class StkFnData implements Serializable {

    @Id
    @Column(name = "CODE", nullable = true, length = 10)
    private String code;

    @Id
    @Column(name = "TYPE", nullable = true, precision = 0)
    private Integer type;

    @Id
    @Column(name = "FN_DATE", nullable = true, length = 8)
    private String fnDate;

    @Basic
    @Column(name = "FN_VALUE", nullable = true, precision = 4)
    private Double fnValue;

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    private Date insertTime;

    @Basic
    @Column(name = "UPDATE_TIME", nullable = true)
    private Date updateTime;

//    private StkEntity stkByCode;
//    private StkFnTypeEntity stkFnTypeByType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkFnData that = (StkFnData) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(type, that.type) &&
                Objects.equals(fnDate, that.fnDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, type, fnDate);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CompositeKey implements Serializable {
        private String code;
        private Integer type;
        private String fnDate;
    }
}
