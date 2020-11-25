package com.stk123.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_CARE", schema = "STK", catalog = "")
public class StkCareEntity {
    private String code;
    private String type;
    private String info;
    private String url;
    private String memo;
    private Time insertTime;
    private Time infoCreateTime;
    private String param1;
    private String param2;

    @Basic
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "TYPE", nullable = true, length = 100)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "INFO", nullable = true, length = 1000)
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Basic
    @Column(name = "URL", nullable = true, length = 1000)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Basic
    @Column(name = "MEMO", nullable = true, length = 4000)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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
    @Column(name = "INFO_CREATE_TIME", nullable = true)
    public Time getInfoCreateTime() {
        return infoCreateTime;
    }

    public void setInfoCreateTime(Time infoCreateTime) {
        this.infoCreateTime = infoCreateTime;
    }

    @Basic
    @Column(name = "PARAM1", nullable = true, length = 200)
    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    @Basic
    @Column(name = "PARAM2", nullable = true, length = 200)
    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkCareEntity that = (StkCareEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(type, that.type) &&
                Objects.equals(info, that.info) &&
                Objects.equals(url, that.url) &&
                Objects.equals(memo, that.memo) &&
                Objects.equals(insertTime, that.insertTime) &&
                Objects.equals(infoCreateTime, that.infoCreateTime) &&
                Objects.equals(param1, that.param1) &&
                Objects.equals(param2, that.param2);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, type, info, url, memo, insertTime, infoCreateTime, param1, param2);
    }
}
