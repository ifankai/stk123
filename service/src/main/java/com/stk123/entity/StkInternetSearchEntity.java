package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_INTERNET_SEARCH")
public class StkInternetSearchEntity {
    private Long searchSource;
    private String searchUrl;
    private String lastSearchText;
    private Time updateTime;
    private Long status;
    private String desc1;

    @Id
    @Column(name = "SEARCH_SOURCE", nullable = true, precision = 0)
    public Long getSearchSource() {
        return searchSource;
    }

    public void setSearchSource(Long searchSource) {
        this.searchSource = searchSource;
    }

    @Basic
    @Column(name = "SEARCH_URL", nullable = true, length = 1000)
    public String getSearchUrl() {
        return searchUrl;
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    @Basic
    @Column(name = "LAST_SEARCH_TEXT", nullable = true, length = 1000)
    public String getLastSearchText() {
        return lastSearchText;
    }

    public void setLastSearchText(String lastSearchText) {
        this.lastSearchText = lastSearchText;
    }

    @Basic
    @Column(name = "UPDATE_TIME", nullable = true)
    public Time getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Time updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "STATUS", nullable = true, precision = 0)
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Basic
    @Column(name = "DESC_1", nullable = true, length = 500)
    public String getDesc1() {
        return desc1;
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkInternetSearchEntity that = (StkInternetSearchEntity) o;
        return Objects.equals(searchSource, that.searchSource) &&
                Objects.equals(searchUrl, that.searchUrl) &&
                Objects.equals(lastSearchText, that.lastSearchText) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(status, that.status) &&
                Objects.equals(desc1, that.desc1);
    }

    @Override
    public int hashCode() {

        return Objects.hash(searchSource, searchUrl, lastSearchText, updateTime, status, desc1);
    }
}
