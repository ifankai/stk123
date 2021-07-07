package com.stk123.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_OWNERSHIP")
@IdClass(StkOwnershipEntity.CompositeKey.class)
@Setter
@Getter
public class StkOwnershipEntity implements Serializable {
    @Id
    @Column(name = "CODE", nullable = true, length = 10)
    private String code;

    @Id
    @Column(name = "FN_DATE", nullable = true, length = 8)
    private String fnDate;

    @Basic
    @Column(name = "ORG_ID", nullable = true, precision = 0)
    private Integer orgId;

    @Basic
    @Column(name = "STK_NUM", nullable = true, precision = 2)
    private Double stkNum;

    @Basic
    @Column(name = "RATE", nullable = true, precision = 2)
    private Double rate;

    @Basic
    @Column(name = "NUM_CHANGE", nullable = true, precision = 2)
    private Double numChange;

    @Basic
    @Column(name = "NUM_CHANGE_RATE", nullable = true, precision = 2)
    private Double numChangeRate;

    //private StkOrganizationEntity stkOrganizationByOrgId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkOwnershipEntity that = (StkOwnershipEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(fnDate, that.fnDate) &&
                Objects.equals(orgId, that.orgId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, fnDate, orgId);
    }

    /*@ManyToOne
    @JoinColumn(name = "ORG_ID", referencedColumnName = "ID")
    public StkOrganizationEntity getStkOrganizationByOrgId() {
        return stkOrganizationByOrgId;
    }

    public void setStkOrganizationByOrgId(StkOrganizationEntity stkOrganizationByOrgId) {
        this.stkOrganizationByOrgId = stkOrganizationByOrgId;
    }*/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CompositeKey implements Serializable {
        private String code;
        private String fnDate;
        private Integer orgId;
    }
}
