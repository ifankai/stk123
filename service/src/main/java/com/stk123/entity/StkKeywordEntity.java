package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "STK_KEYWORD", schema = "STK", catalog = "")
public class StkKeywordEntity {
    private long id;
    private String name;
    private Long boost;
    private Time insertTime;
    private Boolean status;
    private Collection<StkKeywordLinkEntity> stkKeywordLinksById;

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

    @Basic
    @Column(name = "BOOST", nullable = true, precision = 0)
    public Long getBoost() {
        return boost;
    }

    public void setBoost(Long boost) {
        this.boost = boost;
    }

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    public Time getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Time insertTime) {
        this.insertTime = insertTime;
    }

    @Basic
    @Column(name = "STATUS", nullable = true, precision = 0)
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkKeywordEntity that = (StkKeywordEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(boost, that.boost) &&
                Objects.equals(insertTime, that.insertTime) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, boost, insertTime, status);
    }

    @OneToMany(mappedBy = "stkKeywordByKeywordId")
    public Collection<StkKeywordLinkEntity> getStkKeywordLinksById() {
        return stkKeywordLinksById;
    }

    public void setStkKeywordLinksById(Collection<StkKeywordLinkEntity> stkKeywordLinksById) {
        this.stkKeywordLinksById = stkKeywordLinksById;
    }
}
