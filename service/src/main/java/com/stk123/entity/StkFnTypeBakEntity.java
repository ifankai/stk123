package com.stk123.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "STK_FN_TYPE_BAK", schema = "STK", catalog = "")
public class StkFnTypeBakEntity {
    private Long type;
    private String name;
    private String sinaTypecode;
    private Long notLessValue;
    private Long notGreatValue;
    private Long source;
    private Long status;
    private Boolean market;
    private Boolean isPercent;
    private Long currencyUnitAdjust;
    private String dispName;
    private Long dispOrder;

    @Basic
    @Column(name = "TYPE", nullable = true, precision = 0)
    public Long getType() {
        return type;
    }

    public void setType(Long type) {
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
    @Column(name = "SINA_TYPECODE", nullable = true, length = 20)
    public String getSinaTypecode() {
        return sinaTypecode;
    }

    public void setSinaTypecode(String sinaTypecode) {
        this.sinaTypecode = sinaTypecode;
    }

    @Basic
    @Column(name = "NOT_LESS_VALUE", nullable = true, precision = 2)
    public Long getNotLessValue() {
        return notLessValue;
    }

    public void setNotLessValue(Long notLessValue) {
        this.notLessValue = notLessValue;
    }

    @Basic
    @Column(name = "NOT_GREAT_VALUE", nullable = true, precision = 2)
    public Long getNotGreatValue() {
        return notGreatValue;
    }

    public void setNotGreatValue(Long notGreatValue) {
        this.notGreatValue = notGreatValue;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkFnTypeBakEntity that = (StkFnTypeBakEntity) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(name, that.name) &&
                Objects.equals(sinaTypecode, that.sinaTypecode) &&
                Objects.equals(notLessValue, that.notLessValue) &&
                Objects.equals(notGreatValue, that.notGreatValue) &&
                Objects.equals(source, that.source) &&
                Objects.equals(status, that.status) &&
                Objects.equals(market, that.market) &&
                Objects.equals(isPercent, that.isPercent) &&
                Objects.equals(currencyUnitAdjust, that.currencyUnitAdjust) &&
                Objects.equals(dispName, that.dispName) &&
                Objects.equals(dispOrder, that.dispOrder);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, name, sinaTypecode, notLessValue, notGreatValue, source, status, market, isPercent, currencyUnitAdjust, dispName, dispOrder);
    }
}
