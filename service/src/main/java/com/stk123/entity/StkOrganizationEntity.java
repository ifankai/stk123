package com.stk123.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

//@Entity
@Table(name = "STK_ORGANIZATION")
public class StkOrganizationEntity {
    private long id;
    private String name;
    private Collection<StkOwnershipEntity> stkOwnershipsById;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NAME", nullable = true, length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkOrganizationEntity that = (StkOrganizationEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @OneToMany(mappedBy = "stkOrganizationByOrgId")
    public Collection<StkOwnershipEntity> getStkOwnershipsById() {
        return stkOwnershipsById;
    }

    public void setStkOwnershipsById(Collection<StkOwnershipEntity> stkOwnershipsById) {
        this.stkOwnershipsById = stkOwnershipsById;
    }
}
