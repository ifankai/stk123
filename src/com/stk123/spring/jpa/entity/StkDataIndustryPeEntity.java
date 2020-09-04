package com.stk123.spring.jpa.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;

@Entity
@Table(name = "STK_DATA_INDUSTRY_PE")
@IdClass(StkDataIndustryPeEntity.CompositeKey.class)
public class StkDataIndustryPeEntity {

    public static class CompositeKey implements Serializable {
        public CompositeKey(Long industryId, String peDate){
            this.industryId = industryId;
            this.peDate = peDate;
        }
        private Long industryId;
        private String peDate;
    }

    @Id
    private Long industryId;
    @Id
    private String peDate;

    private Integer type;
    private Double pe;
    private Double peTtm;
    private Time insertTime;
    private Double pb;
    private Double adr; //股息率

    @Basic
    @Column(name = "INDUSTRY_ID", nullable = true, precision = 0)
    public Long getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Long industryId) {
        this.industryId = industryId;
    }

    @Basic
    @Column(name = "PE_DATE", nullable = true, length = 8)
    public String getPeDate() {
        return peDate;
    }

    public void setPeDate(String peDate) {
        this.peDate = peDate;
    }

    @Basic
    @Column(name = "TYPE", nullable = true, precision = 0)
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Basic
    @Column(name = "PE", nullable = true, precision = 2)
    public Double getPe() {
        return pe;
    }

    public void setPe(Double pe) {
        this.pe = pe;
    }

    @Basic
    @Column(name = "PE_TTM", nullable = true, precision = 2)
    public Double getPeTtm() {
        return peTtm;
    }

    public void setPeTtm(Double peTtm) {
        this.peTtm = peTtm;
    }

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    public Time getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Time insertTime) {
        this.insertTime = insertTime;
    }

    @Basic
    @Column(name = "PB", nullable = true, precision = 2)
    public Double getPb() {
        return pb;
    }

    public void setPb(Double pb) {
        this.pb = pb;
    }

    @Basic
    @Column(name = "ADR", nullable = true, precision = 2)
    public Double getAdr() {
        return adr;
    }

    public void setAdr(Double adr) {
        this.adr = adr;
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
}


