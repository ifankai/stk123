package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_INVESTIGATION", schema = "STK", catalog = "")
public class StkInvestigationEntity {
    private long id;
    private String code;
    private String title;
    private String investigator;
    private Long investigatorCount;
    private String text;
    private Long textCount;
    private Time investDate;
    private Time insertDate;
    private String sourceUrl;
    private String sourceType;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
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
    @Column(name = "TITLE", nullable = true, length = 1000)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "INVESTIGATOR", nullable = true, length = 4000)
    public String getInvestigator() {
        return investigator;
    }

    public void setInvestigator(String investigator) {
        this.investigator = investigator;
    }

    @Basic
    @Column(name = "INVESTIGATOR_COUNT", nullable = true, precision = 0)
    public Long getInvestigatorCount() {
        return investigatorCount;
    }

    public void setInvestigatorCount(Long investigatorCount) {
        this.investigatorCount = investigatorCount;
    }

    @Basic
    @Column(name = "TEXT", nullable = true)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic
    @Column(name = "TEXT_COUNT", nullable = true, precision = 0)
    public Long getTextCount() {
        return textCount;
    }

    public void setTextCount(Long textCount) {
        this.textCount = textCount;
    }

    @Basic
    @Column(name = "INVEST_DATE", nullable = true)
    public Time getInvestDate() {
        return investDate;
    }

    public void setInvestDate(Time investDate) {
        this.investDate = investDate;
    }

    @Basic
    @Column(name = "INSERT_DATE", nullable = true)
    public Time getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Time insertDate) {
        this.insertDate = insertDate;
    }

    @Basic
    @Column(name = "SOURCE_URL", nullable = true, length = 1000)
    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    @Basic
    @Column(name = "SOURCE_TYPE", nullable = true, length = 10)
    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkInvestigationEntity that = (StkInvestigationEntity) o;
        return id == that.id &&
                Objects.equals(code, that.code) &&
                Objects.equals(title, that.title) &&
                Objects.equals(investigator, that.investigator) &&
                Objects.equals(investigatorCount, that.investigatorCount) &&
                Objects.equals(text, that.text) &&
                Objects.equals(textCount, that.textCount) &&
                Objects.equals(investDate, that.investDate) &&
                Objects.equals(insertDate, that.insertDate) &&
                Objects.equals(sourceUrl, that.sourceUrl) &&
                Objects.equals(sourceType, that.sourceType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, code, title, investigator, investigatorCount, text, textCount, investDate, insertDate, sourceUrl, sourceType);
    }
}
