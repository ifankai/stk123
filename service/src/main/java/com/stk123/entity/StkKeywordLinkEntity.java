package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

//@Entity
@Table(name = "STK_KEYWORD_LINK")
public class StkKeywordLinkEntity {
    private long id;
    private String code;
    private Long codeType;
    private Long keywordId;
    private Long boost;
    private Long linkType;
    private Time insertTime;
    private StkKeywordEntity stkKeywordByKeywordId;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "CODE", nullable = true, length = 20)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "CODE_TYPE", nullable = true, precision = 0)
    public Long getCodeType() {
        return codeType;
    }

    public void setCodeType(Long codeType) {
        this.codeType = codeType;
    }

    @Basic
    @Column(name = "KEYWORD_ID", nullable = true, precision = 0)
    public Long getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(Long keywordId) {
        this.keywordId = keywordId;
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
    @Column(name = "LINK_TYPE", nullable = true, precision = 0)
    public Long getLinkType() {
        return linkType;
    }

    public void setLinkType(Long linkType) {
        this.linkType = linkType;
    }

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    public Time getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Time insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkKeywordLinkEntity that = (StkKeywordLinkEntity) o;
        return id == that.id &&
                Objects.equals(code, that.code) &&
                Objects.equals(codeType, that.codeType) &&
                Objects.equals(keywordId, that.keywordId) &&
                Objects.equals(boost, that.boost) &&
                Objects.equals(linkType, that.linkType) &&
                Objects.equals(insertTime, that.insertTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, code, codeType, keywordId, boost, linkType, insertTime);
    }

//    @ManyToOne
//    @JoinColumn(name = "KEYWORD_ID", referencedColumnName = "ID")
//    public StkKeywordEntity getStkKeywordByKeywordId() {
//        return stkKeywordByKeywordId;
//    }
//
//    public void setStkKeywordByKeywordId(StkKeywordEntity stkKeywordByKeywordId) {
//        this.stkKeywordByKeywordId = stkKeywordByKeywordId;
//    }
}
