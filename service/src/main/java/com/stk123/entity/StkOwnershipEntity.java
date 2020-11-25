package com.stk123.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STK_OWNERSHIP", schema = "STK", catalog = "")
public class StkOwnershipEntity {
    private String code;
    private String fnDate;
    private Long orgId;
    private Long stkNum;
    private Long rate;
    private Long numChange;
    private Long numChangeRate;
    private StkOrganizationEntity stkOrganizationByOrgId;

    @Basic
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "FN_DATE", nullable = true, length = 8)
    public String getFnDate() {
        return fnDate;
    }

    public void setFnDate(String fnDate) {
        this.fnDate = fnDate;
    }

    @Basic
    @Column(name = "ORG_ID", nullable = true, precision = 0)
    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    @Basic
    @Column(name = "STK_NUM", nullable = true, precision = 2)
    public Long getStkNum() {
        return stkNum;
    }

    public void setStkNum(Long stkNum) {
        this.stkNum = stkNum;
    }

    @Basic
    @Column(name = "RATE", nullable = true, precision = 2)
    public Long getRate() {
        return rate;
    }

    public void setRate(Long rate) {
        this.rate = rate;
    }

    @Basic
    @Column(name = "NUM_CHANGE", nullable = true, precision = 2)
    public Long getNumChange() {
        return numChange;
    }

    public void setNumChange(Long numChange) {
        this.numChange = numChange;
    }

    @Basic
    @Column(name = "NUM_CHANGE_RATE", nullable = true, precision = 2)
    public Long getNumChangeRate() {
        return numChangeRate;
    }

    public void setNumChangeRate(Long numChangeRate) {
        this.numChangeRate = numChangeRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkOwnershipEntity that = (StkOwnershipEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(fnDate, that.fnDate) &&
                Objects.equals(orgId, that.orgId) &&
                Objects.equals(stkNum, that.stkNum) &&
                Objects.equals(rate, that.rate) &&
                Objects.equals(numChange, that.numChange) &&
                Objects.equals(numChangeRate, that.numChangeRate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, fnDate, orgId, stkNum, rate, numChange, numChangeRate);
    }

    @ManyToOne
    @JoinColumn(name = "ORG_ID", referencedColumnName = "ID")
    public StkOrganizationEntity getStkOrganizationByOrgId() {
        return stkOrganizationByOrgId;
    }

    public void setStkOrganizationByOrgId(StkOrganizationEntity stkOrganizationByOrgId) {
        this.stkOrganizationByOrgId = stkOrganizationByOrgId;
    }
}
