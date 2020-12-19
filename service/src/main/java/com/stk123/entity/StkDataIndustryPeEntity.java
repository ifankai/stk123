package com.stk123.entity;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "STK_DATA_INDUSTRY_PE")
@IdClass(StkDataIndustryPeEntity.CompositeKey.class)
@Setter
@Getter
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
    private Date insertTime;

    @Basic
    @Column(name = "PB", nullable = true, precision = 2)
    private Double pb;

    @Basic
    @Column(name = "ADR", nullable = true, precision = 2)
    private Double adr; //股息率

    @ManyToOne
    @JoinColumn(name = "INDUSTRY_ID", insertable = false, updatable = false)
    private StkIndustryTypeEntity stkIndustryTypeEntity;


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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CompositeKey implements Serializable {
        private Integer industryId;
        private String peDate;
    }
}


