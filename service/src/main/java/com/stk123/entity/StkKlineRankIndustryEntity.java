package com.stk123.entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "STK_KLINE_RANK_INDUSTRY", schema = "STK", catalog = "")
public class StkKlineRankIndustryEntity {
    private long rankId;
    private Long industryId;
    private String rankDate;
    private Long rankDays;
    private Long changePercent;
    private Long rank;
    private StkIndustryTypeEntity stkIndustryTypeByIndustryId;
    private Collection<StkKlineRankIndustryStockEntity> stkKlineRankIndustryStocksByRankId;

    @Id
    @Column(name = "RANK_ID", nullable = false, precision = 0)
    public long getRankId() {
        return rankId;
    }

    public void setRankId(long rankId) {
        this.rankId = rankId;
    }

    @Basic
    @Column(name = "INDUSTRY_ID", nullable = true, precision = 0)
    public Long getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Long industryId) {
        this.industryId = industryId;
    }

    @Basic
    @Column(name = "RANK_DATE", nullable = true, length = 8)
    public String getRankDate() {
        return rankDate;
    }

    public void setRankDate(String rankDate) {
        this.rankDate = rankDate;
    }

    @Basic
    @Column(name = "RANK_DAYS", nullable = true, precision = 0)
    public Long getRankDays() {
        return rankDays;
    }

    public void setRankDays(Long rankDays) {
        this.rankDays = rankDays;
    }

    @Basic
    @Column(name = "CHANGE_PERCENT", nullable = true, precision = 4)
    public Long getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(Long changePercent) {
        this.changePercent = changePercent;
    }

    @Basic
    @Column(name = "RANK", nullable = true, precision = 0)
    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkKlineRankIndustryEntity that = (StkKlineRankIndustryEntity) o;
        return rankId == that.rankId &&
                Objects.equals(industryId, that.industryId) &&
                Objects.equals(rankDate, that.rankDate) &&
                Objects.equals(rankDays, that.rankDays) &&
                Objects.equals(changePercent, that.changePercent) &&
                Objects.equals(rank, that.rank);
    }

    @Override
    public int hashCode() {

        return Objects.hash(rankId, industryId, rankDate, rankDays, changePercent, rank);
    }

    @ManyToOne
    @JoinColumn(name = "INDUSTRY_ID", referencedColumnName = "ID")
    public StkIndustryTypeEntity getStkIndustryTypeByIndustryId() {
        return stkIndustryTypeByIndustryId;
    }

    public void setStkIndustryTypeByIndustryId(StkIndustryTypeEntity stkIndustryTypeByIndustryId) {
        this.stkIndustryTypeByIndustryId = stkIndustryTypeByIndustryId;
    }

    @OneToMany(mappedBy = "stkKlineRankIndustryByRankId")
    public Collection<StkKlineRankIndustryStockEntity> getStkKlineRankIndustryStocksByRankId() {
        return stkKlineRankIndustryStocksByRankId;
    }

    public void setStkKlineRankIndustryStocksByRankId(Collection<StkKlineRankIndustryStockEntity> stkKlineRankIndustryStocksByRankId) {
        this.stkKlineRankIndustryStocksByRankId = stkKlineRankIndustryStocksByRankId;
    }
}
