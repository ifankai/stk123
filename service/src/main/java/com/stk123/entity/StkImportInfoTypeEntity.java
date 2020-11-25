package com.stk123.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "STK_IMPORT_INFO_TYPE", schema = "STK", catalog = "")
public class StkImportInfoTypeEntity {
    private long type;
    private String name;
    private String matchPattern;
    private String notMatchPattern;
    private Collection<StkImportInfoEntity> stkImportInfosByType;

    @Id
    @Column(name = "TYPE", nullable = false, precision = 0)
    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
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
    @Column(name = "MATCH_PATTERN", nullable = true, length = 400)
    public String getMatchPattern() {
        return matchPattern;
    }

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }

    @Basic
    @Column(name = "NOT_MATCH_PATTERN", nullable = true, length = 400)
    public String getNotMatchPattern() {
        return notMatchPattern;
    }

    public void setNotMatchPattern(String notMatchPattern) {
        this.notMatchPattern = notMatchPattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkImportInfoTypeEntity that = (StkImportInfoTypeEntity) o;
        return type == that.type &&
                Objects.equals(name, that.name) &&
                Objects.equals(matchPattern, that.matchPattern) &&
                Objects.equals(notMatchPattern, that.notMatchPattern);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, name, matchPattern, notMatchPattern);
    }

    @OneToMany(mappedBy = "stkImportInfoTypeByType")
    public Collection<StkImportInfoEntity> getStkImportInfosByType() {
        return stkImportInfosByType;
    }

    public void setStkImportInfosByType(Collection<StkImportInfoEntity> stkImportInfosByType) {
        this.stkImportInfosByType = stkImportInfosByType;
    }
}
