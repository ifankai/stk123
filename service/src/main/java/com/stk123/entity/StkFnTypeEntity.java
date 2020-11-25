package com.stk123.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "STK_FN_TYPE")
public class StkFnTypeEntity {
    private long type;
    private String name;
    private String nameAlias;
    private Long source;
    private Long status;
    private Boolean market;
    private Boolean isPercent;
    private Long currencyUnitAdjust;
    private String dispName;
    private Long dispOrder;
    private String reCalc;
    private Boolean tab;
    private Boolean precision;
    private Boolean colspan;
    private Collection<StkFnDataEntity> stkFnDataByType;
    private Collection<StkFnDataHkEntity> stkFnDataHksByType;
    private Collection<StkFnDataUsEntity> stkFnDataUsByType;

    @Id
    @Column(name = "TYPE", nullable = false, precision = 0)
    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    @Basic
    @Column(name = "NAME", nullable = true, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "NAME_ALIAS", nullable = true, length = 100)
    public String getNameAlias() {
        return nameAlias;
    }

    public void setNameAlias(String nameAlias) {
        this.nameAlias = nameAlias;
    }

    @Basic
    @Column(name = "SOURCE", nullable = true, precision = 0)
    public Long getSource() {
        return source;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    @Basic
    @Column(name = "STATUS", nullable = true, precision = 0)
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Basic
    @Column(name = "MARKET", nullable = true, precision = 0)
    public Boolean getMarket() {
        return market;
    }

    public void setMarket(Boolean market) {
        this.market = market;
    }

    @Basic
    @Column(name = "IS_PERCENT", nullable = true, precision = 0)
    public Boolean getPercent() {
        return isPercent;
    }

    public void setPercent(Boolean percent) {
        isPercent = percent;
    }

    @Basic
    @Column(name = "CURRENCY_UNIT_ADJUST", nullable = true, precision = 8)
    public Long getCurrencyUnitAdjust() {
        return currencyUnitAdjust;
    }

    public void setCurrencyUnitAdjust(Long currencyUnitAdjust) {
        this.currencyUnitAdjust = currencyUnitAdjust;
    }

    @Basic
    @Column(name = "DISP_NAME", nullable = true, length = 200)
    public String getDispName() {
        return dispName;
    }

    public void setDispName(String dispName) {
        this.dispName = dispName;
    }

    @Basic
    @Column(name = "DISP_ORDER", nullable = true, precision = 0)
    public Long getDispOrder() {
        return dispOrder;
    }

    public void setDispOrder(Long dispOrder) {
        this.dispOrder = dispOrder;
    }

    @Basic
    @Column(name = "RE_CALC", nullable = true, length = 20)
    public String getReCalc() {
        return reCalc;
    }

    public void setReCalc(String reCalc) {
        this.reCalc = reCalc;
    }

    @Basic
    @Column(name = "TAB", nullable = true, precision = 0)
    public Boolean getTab() {
        return tab;
    }

    public void setTab(Boolean tab) {
        this.tab = tab;
    }

    @Basic
    @Column(name = "PRECISION", nullable = true, precision = 0)
    public Boolean getPrecision() {
        return precision;
    }

    public void setPrecision(Boolean precision) {
        this.precision = precision;
    }

    @Basic
    @Column(name = "COLSPAN", nullable = true, precision = 0)
    public Boolean getColspan() {
        return colspan;
    }

    public void setColspan(Boolean colspan) {
        this.colspan = colspan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkFnTypeEntity that = (StkFnTypeEntity) o;
        return type == that.type &&
                Objects.equals(name, that.name) &&
                Objects.equals(nameAlias, that.nameAlias) &&
                Objects.equals(source, that.source) &&
                Objects.equals(status, that.status) &&
                Objects.equals(market, that.market) &&
                Objects.equals(isPercent, that.isPercent) &&
                Objects.equals(currencyUnitAdjust, that.currencyUnitAdjust) &&
                Objects.equals(dispName, that.dispName) &&
                Objects.equals(dispOrder, that.dispOrder) &&
                Objects.equals(reCalc, that.reCalc) &&
                Objects.equals(tab, that.tab) &&
                Objects.equals(precision, that.precision) &&
                Objects.equals(colspan, that.colspan);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, name, nameAlias, source, status, market, isPercent, currencyUnitAdjust, dispName, dispOrder, reCalc, tab, precision, colspan);
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
