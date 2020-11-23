package com.stk123.entity;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;

@Entity
@Table(name = "STK_DATA_INDUSTRY_PE")
@IdClass(StkDataIndustryPeEntity.CompositeKey.class)
public class StkDataIndustryPeEntity {

    @Id
    @Column(name = "INDUSTRY_ID", nullable = true, precision = 0)
    private Integer industryId;

    @Id
    @Column(name = "PE_DATE", nullable = true, length = 8)
    private String peDate;

    @Basic
    @Column(name = "TYPE", nullable = true, precision = 0)
    private Integer type;

    @Basic
    @Column(name = "PE", nullable = true, precision = 2)
    private Double pe;

    @Basic
    @Column(name = "PE_TTM", nullable = true, precision = 2)
    private Double peTtm;

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    private Time insertTime;

    @Basic
    @Column(name = "PB", nullable = true, precision = 2)
    private Double pb;

    @Basic
    @Column(name = "ADR", nullable = true, precision = 2)
    private Double adr; //股息率

    @ManyToOne
    @JoinColumn(name = "INDUSTRY_ID", insertable = false, updatable = false)
    private StkIndustryTypeEntity stkIndustryTypeEntity;

    public Integer getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Integer industryId) {
        this.industryId = industryId;
    }

    public String getPeDate() {
        return peDate;
    }

    public void setPeDate(String peDate) {
        this.peDate = peDate;
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


    public Double getPe() {
        return pe;
    }

    public void setPe(Double pe) {
        this.pe = pe;
    }


    public Double getPeTtm() {
        return peTtm;
    }

    public void setPeTtm(Double peTtm) {
        this.peTtm = peTtm;
    }


    public Time getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Time insertTime) {
        this.insertTime = insertTime;
    }


    public Double getPb() {
        return pb;
    }

    public void setPb(Double pb) {
        this.pb = pb;
    }


    public Double getAdr() {
        return adr;
    }

    public void setAdr(Double adr) {
        this.adr = adr;
    }

    public StkIndustryTypeEntity getStkIndustryTypeEntity() {
        return stkIndustryTypeEntity;
    }

    public void setStkIndustryTypeEntity(StkIndustryTypeEntity stkIndustryTypeEntity) {
        this.stkIndustryTypeEntity = stkIndustryTypeEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StkDataIndustryPeEntity that = (StkDataIndustryPeEntity) o;

        if (industryId != null ? !industryId.equals(that.industryId) : that.industryId != null) return false;
        if (peDate != null ? !peDate.equals(that.peDate) : that.peDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = industryId != null ? industryId.hashCode() : 0;
        result = 31 * result + (peDate != null ? peDate.hashCode() : 0);
        return result;
    }

    @Component
    public static class CompositeKey implements Serializable {
        private Integer industryId;
        private String peDate;

        public CompositeKey(){}

        public CompositeKey(Integer industryId, String peDate){
            this.industryId = industryId;
            this.peDate = peDate;
        }
    }
}


