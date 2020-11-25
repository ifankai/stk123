package com.stk123.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "STK_DEPT_TYPE")
public class StkDeptTypeEntity {
    private long deptId;
    private String deptName;
    private Collection<StkBillboardEntity> stkBillboardsByDeptId;

    @Id
    @Column(name = "DEPT_ID", nullable = false, precision = 0)
    public long getDeptId() {
        return deptId;
    }

    public void setDeptId(long deptId) {
        this.deptId = deptId;
    }

    @Basic
    @Column(name = "DEPT_NAME", nullable = true, length = 200)
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkDeptTypeEntity that = (StkDeptTypeEntity) o;
        return deptId == that.deptId &&
                Objects.equals(deptName, that.deptName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(deptId, deptName);
    }

//    @OneToMany(mappedBy = "stkDeptTypeByDeptId")
//    public Collection<StkBillboardEntity> getStkBillboardsByDeptId() {
//        return stkBillboardsByDeptId;
//    }
//
//    public void setStkBillboardsByDeptId(Collection<StkBillboardEntity> stkBillboardsByDeptId) {
//        this.stkBillboardsByDeptId = stkBillboardsByDeptId;
//    }
}
