package com.stk123.entity;

import javax.persistence.*;
import java.util.Objects;

//@Entity
@Table(name = "STK_SYNC_TABLE")
public class StkSyncTableEntity {
    private String name;
    private String pk;

    @Id
    @Column(name = "NAME", nullable = true, length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "PK", nullable = true, length = 200)
    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkSyncTableEntity that = (StkSyncTableEntity) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(pk, that.pk);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, pk);
    }
}
