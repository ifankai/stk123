package com.stk123.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STK_KLINE_RANK_INDUSTRY_STOCK", schema = "STK", catalog = "")
public class StkKlineRankIndustryStockEntity {
    private Long rankId;
    private String code;
    private Long changePercent;
    private StkKlineRankIndustryEntity stkKlineRankIndustryByRankId;

    @Basic
    @Column(name = "RANK_ID", nullable = true, precision = 0)
    public Long getRankId() {
        return rankId;
    }

    public void setRankId(Long rankId) {
        this.rankId = rankId;
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
    @Column(name = "CHANGE_PERCENT", nullable = true, precision = 4)
    public Long getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(Long changePercent) {
        this.changePercent = changePercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkKlineRankIndustryStockEntity that = (StkKlineRankIndustryStockEntity) o;
        return Objects.equals(rankId, that.rankId) &&
                Objects.equals(code, that.code) &&
                Objects.equals(changePercent, that.changePercent);
    }

    @Override
    public int hashCode() {

        return Objects.hash(rankId, code, changePercent);
    }

    @ManyToOne
    @JoinColumn(name = "RANK_ID", referencedColumnName = "RANK_ID")
    public StkKlineRankIndustryEntity getStkKlineRankIndustryByRankId() {
        return stkKlineRankIndustryByRankId;
    }

    public void setStkKlineRankIndustryByRankId(StkKlineRankIndustryEntity stkKlineRankIndustryByRankId) {
        this.stkKlineRankIndustryByRankId = stkKlineRankIndustryByRankId;
    }
}
