package com.stk123.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "STK_ORGANIZATION")
@Getter
@Setter
public class StkOrganizationEntity {

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_organization_id")
    @SequenceGenerator(name="s_organization_id", sequenceName="s_organization_id", allocationSize = 1)
    private Integer id;

    @Basic
    @Column(name = "NAME", nullable = true, length = 200)
    private String name;

    //private Collection<StkOwnershipEntity> stkOwnershipsById;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkOrganizationEntity that = (StkOrganizationEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /*@OneToMany(mappedBy = "stkOrganizationByOrgId")
    public Collection<StkOwnershipEntity> getStkOwnershipsById() {
        return stkOwnershipsById;
    }

    public void setStkOwnershipsById(Collection<StkOwnershipEntity> stkOwnershipsById) {
        this.stkOwnershipsById = stkOwnershipsById;
    }*/
}
