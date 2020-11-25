package com.stk123.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STK_INDEX_NODE")
public class StkIndexNodeEntity {
    private long id;
    private Long parentId;
    private String name;
    private Long dispOrder;
    private Long nodeLevel;
    private String chartTemplate;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "PARENT_ID", nullable = true, precision = 0)
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "NAME", nullable = true, length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "DISP_ORDER", nullable = true, precision = 0)
    public Long getDispOrder() {
        return dispOrder;
    }

    public void setDispOrder(Long dispOrder) {
        this.dispOrder = dispOrder;
    }

    @Basic
    @Column(name = "NODE_LEVEL", nullable = true, precision = 0)
    public Long getNodeLevel() {
        return nodeLevel;
    }

    public void setNodeLevel(Long nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    @Basic
    @Column(name = "CHART_TEMPLATE", nullable = true, length = 1000)
    public String getChartTemplate() {
        return chartTemplate;
    }

    public void setChartTemplate(String chartTemplate) {
        this.chartTemplate = chartTemplate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkIndexNodeEntity that = (StkIndexNodeEntity) o;
        return id == that.id &&
                Objects.equals(parentId, that.parentId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(dispOrder, that.dispOrder) &&
                Objects.equals(nodeLevel, that.nodeLevel) &&
                Objects.equals(chartTemplate, that.chartTemplate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, parentId, name, dispOrder, nodeLevel, chartTemplate);
    }
}
