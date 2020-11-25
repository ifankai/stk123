package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_IMPORT_INFO", schema = "STK", catalog = "")
public class StkImportInfoEntity {
    private int id;
    private String code;
    private Long type;
    private Time insertTime;
    private Long careFlag;
    private String info;
    private String title;
    private String urlSource;
    private String urlTarget;
    private Time infoCreateTime;
    private StkEntity stkByCode;
    private StkImportInfoTypeEntity stkImportInfoTypeByType;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "TYPE", nullable = true, precision = 0)
    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
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
    @Column(name = "CARE_FLAG", nullable = true, precision = 0)
    public Long getCareFlag() {
        return careFlag;
    }

    public void setCareFlag(Long careFlag) {
        this.careFlag = careFlag;
    }

    @Basic
    @Column(name = "INFO", nullable = true, length = 4000)
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Basic
    @Column(name = "TITLE", nullable = true, length = 1000)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "URL_SOURCE", nullable = true, length = 1000)
    public String getUrlSource() {
        return urlSource;
    }

    public void setUrlSource(String urlSource) {
        this.urlSource = urlSource;
    }

    @Basic
    @Column(name = "URL_TARGET", nullable = true, length = 1000)
    public String getUrlTarget() {
        return urlTarget;
    }

    public void setUrlTarget(String urlTarget) {
        this.urlTarget = urlTarget;
    }

    @Basic
    @Column(name = "INFO_CREATE_TIME", nullable = true)
    public Time getInfoCreateTime() {
        return infoCreateTime;
    }

    public void setInfoCreateTime(Time infoCreateTime) {
        this.infoCreateTime = infoCreateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkImportInfoEntity that = (StkImportInfoEntity) o;
        return id == that.id &&
                Objects.equals(code, that.code) &&
                Objects.equals(type, that.type) &&
                Objects.equals(insertTime, that.insertTime) &&
                Objects.equals(careFlag, that.careFlag) &&
                Objects.equals(info, that.info) &&
                Objects.equals(title, that.title) &&
                Objects.equals(urlSource, that.urlSource) &&
                Objects.equals(urlTarget, that.urlTarget) &&
                Objects.equals(infoCreateTime, that.infoCreateTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, code, type, insertTime, careFlag, info, title, urlSource, urlTarget, infoCreateTime);
    }

    @ManyToOne
    @JoinColumn(name = "CODE", referencedColumnName = "CODE")
    public StkEntity getStkByCode() {
        return stkByCode;
    }

    public void setStkByCode(StkEntity stkByCode) {
        this.stkByCode = stkByCode;
    }

    @ManyToOne
    @JoinColumn(name = "TYPE", referencedColumnName = "TYPE")
    public StkImportInfoTypeEntity getStkImportInfoTypeByType() {
        return stkImportInfoTypeByType;
    }

    public void setStkImportInfoTypeByType(StkImportInfoTypeEntity stkImportInfoTypeByType) {
        this.stkImportInfoTypeByType = stkImportInfoTypeByType;
    }
}
