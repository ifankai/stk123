package com.stk123.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "STK_FN_TYPE")
@Getter
@Setter
public class StkFnTypeEntity {

    @Id
    @Column(name = "TYPE", nullable = false, precision = 0)
    private Integer type;

    @Basic
    @Column(name = "NAME", nullable = true, length = 100)
    private String name;

    @Basic
    @Column(name = "NAME_ALIAS", nullable = true, length = 100)
    private String nameAlias;

    @Basic
    @Column(name = "SOURCE", nullable = true, precision = 0)
    private Integer source;

    @Basic
    @Column(name = "STATUS", nullable = true, precision = 0)
    private Integer status;

    @Basic
    @Column(name = "MARKET", nullable = true, precision = 0)
    private Integer market;

    @Basic
    @Column(name = "IS_PERCENT", nullable = true, precision = 0)
    private Boolean isPercent;

    @Basic
    @Column(name = "CURRENCY_UNIT_ADJUST", nullable = true, precision = 8)
    private Double currencyUnitAdjust;

    @Basic
    @Column(name = "DISP_NAME", nullable = true, length = 200)
    private String dispName;

    @Basic
    @Column(name = "DISP_ORDER", nullable = true, precision = 0)
    private Integer dispOrder;

    @Basic
    @Column(name = "RE_CALC", nullable = true, length = 20)
    private String reCalc;

    @Basic
    @Column(name = "TAB", nullable = true, precision = 0)
    private Integer tab;

    @Basic
    @Column(name = "PRECISION", nullable = true, precision = 0)
    private Integer precision;

    @Basic
    @Column(name = "COLSPAN", nullable = true, precision = 0)
    private Integer colspan;

    @Basic
    @Column(name = "CODE", nullable = true, length = 20)
    private String code;

//    private Collection<StkFnDataEntity> stkFnDataByType;
//    private Collection<StkFnDataHkEntity> stkFnDataHksByType;
//    private Collection<StkFnDataUsEntity> stkFnDataUsByType;




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkFnTypeEntity that = (StkFnTypeEntity) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

//    @OneToMany(mappedBy = "stkFnTypeByType")
//    public Collection<StkFnDataEntity> getStkFnDataByType() {
//        return stkFnDataByType;
//    }
//
//    public void setStkFnDataByType(Collection<StkFnDataEntity> stkFnDataByType) {
//        this.stkFnDataByType = stkFnDataByType;
//    }
//
//    @OneToMany(mappedBy = "stkFnTypeByType")
//    public Collection<StkFnDataHkEntity> getStkFnDataHksByType() {
//        return stkFnDataHksByType;
//    }
//
//    public void setStkFnDataHksByType(Collection<StkFnDataHkEntity> stkFnDataHksByType) {
//        this.stkFnDataHksByType = stkFnDataHksByType;
//    }
//
//    @OneToMany(mappedBy = "stkFnTypeByType")
//    public Collection<StkFnDataUsEntity> getStkFnDataUsByType() {
//        return stkFnDataUsByType;
//    }
//
//    public void setStkFnDataUsByType(Collection<StkFnDataUsEntity> stkFnDataUsByType) {
//        this.stkFnDataUsByType = stkFnDataUsByType;
//    }
}
