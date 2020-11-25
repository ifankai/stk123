package com.stk123.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_INDUSTRY_RANK")
public class StkIndustryRankEntity implements Serializable {
    private Long industryId;
    private Long period;
    private String rankDate;
    private Long rank;
    private Long closeChange;
    private String rankDesc;
    private StkIndustryTypeEntity stkIndustryTypeByIndustryId;

    @Id
    @Column(name = "INDUSTRY_ID", nullable = true, precision = 0)
    public Long getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Long industryId) {
        this.industryId = industryId;
    }

    @Id
    @Column(name = "PERIOD", nullable = true, precision = 0)
    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
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
    @Column(name = "RANK", nullable = true, precision = 0)
    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    @Basic
    @Column(name = "CLOSE_CHANGE", nullable = true, precision = 2)
    public Long getCloseChange() {
        return closeChange;
    }

    public void setCloseChange(Long closeChange) {
        this.closeChange = closeChange;
    }

    @Basic
    @Column(name = "RANK_DESC", nullable = true)
    public String getRankDesc() {
        return rankDesc;
    }

    public void setRankDesc(String rankDesc) {
        this.rankDesc = rankDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkIndustryRankEntity that = (StkIndustryRankEntity) o;
        return Objects.equals(industryId, that.industryId) &&
                Objects.equals(period, that.period) &&
                Objects.equals(rankDate, that.rankDate) &&
                Objects.equals(rank, that.rank) &&
                Objects.equals(closeChange, that.closeChange) &&
                Objects.equals(rankDesc, that.rankDesc);
    }

    @Override
    public int hashCode() {

        return Objects.hash(industryId, period, rankDate, rank, closeChange, rankDesc);
    }

//    @ManyToOne
//    @JoinColumn(name = "INDUSTRY_ID", referencedColumnName = "ID")
//    public StkIndustryTypeEntity getStkIndustryTypeByIndustryId() {
//        return stkIndustryTypeByIndustryId;
//    }
//
//    public void setStkIndustryTypeByIndustryId(StkIndustryTypeEntity stkIndustryTypeByIndustryId) {
//        this.stkIndustryTypeByIndustryId = stkIndustryTypeByIndustryId;
//    }
}
