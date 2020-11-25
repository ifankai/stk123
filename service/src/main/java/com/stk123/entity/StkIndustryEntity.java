package com.stk123.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_INDUSTRY")
public class StkIndustryEntity implements Serializable {
    private String code;
    private Long industry;
    private StkEntity stkByCode;
    private StkIndustryTypeEntity stkIndustryTypeByIndustry;

    @Id
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Id
    @Column(name = "INDUSTRY", nullable = true, precision = 0)
    public Long getIndustry() {
        return industry;
    }

    public void setIndustry(Long industry) {
        this.industry = industry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkIndustryEntity that = (StkIndustryEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(industry, that.industry);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, industry);
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
//
//    @ManyToOne
//    @JoinColumn(name = "INDUSTRY", referencedColumnName = "ID")
//    public StkIndustryTypeEntity getStkIndustryTypeByIndustry() {
//        return stkIndustryTypeByIndustry;
//    }
//
//    public void setStkIndustryTypeByIndustry(StkIndustryTypeEntity stkIndustryTypeByIndustry) {
//        this.stkIndustryTypeByIndustry = stkIndustryTypeByIndustry;
//    }
}
