package com.stk123.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "STK_DATA_PPI_TYPE", schema = "STK", catalog = "")
public class StkDataPpiTypeEntity {
    private long id;
    private String name;
    private Long parentId;
    private String url;
    private Collection<StkDataPpiEntity> stkDataPpisById;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NAME", nullable = true, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    @Column(name = "URL", nullable = true, length = 100)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkDataPpiTypeEntity that = (StkDataPpiTypeEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(parentId, that.parentId) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, parentId, url);
    }

    @OneToMany(mappedBy = "stkDataPpiTypeByTypeId")
    public Collection<StkDataPpiEntity> getStkDataPpisById() {
        return stkDataPpisById;
    }

    public void setStkDataPpisById(Collection<StkDataPpiEntity> stkDataPpisById) {
        this.stkDataPpisById = stkDataPpisById;
    }
}
