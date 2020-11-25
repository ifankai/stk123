package com.stk123.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STK_WORD")
public class StkWordEntity {
    private int id;
    private boolean type;
    private Long subType;
    private String name;
    private Long weight;
    private Integer parentId;
    private Integer childId;
    private String description;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "TYPE", nullable = false, precision = 0)
    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    @Basic
    @Column(name = "SUB_TYPE", nullable = true, precision = 0)
    public Long getSubType() {
        return subType;
    }

    public void setSubType(Long subType) {
        this.subType = subType;
    }

    @Basic
    @Column(name = "NAME", nullable = false, length = 40)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "WEIGHT", nullable = true, precision = 0)
    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    @Basic
    @Column(name = "PARENT_ID", nullable = true, precision = 0)
    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "CHILD_ID", nullable = true, precision = 0)
    public Integer getChildId() {
        return childId;
    }

    public void setChildId(Integer childId) {
        this.childId = childId;
    }

    @Basic
    @Column(name = "DESCRIPTION", nullable = true, length = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkWordEntity that = (StkWordEntity) o;
        return id == that.id &&
                type == that.type &&
                Objects.equals(subType, that.subType) &&
                Objects.equals(name, that.name) &&
                Objects.equals(weight, that.weight) &&
                Objects.equals(parentId, that.parentId) &&
                Objects.equals(childId, that.childId) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, type, subType, name, weight, parentId, childId, description);
    }
}
