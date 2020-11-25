package com.stk123.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STK_KLINE_HK", schema = "STK", catalog = "")
public class StkKlineHkEntity {
    private String code;
    private String klineDate;
    private Long open;
    private Long close;
    private Long lastClose;
    private Long high;
    private Long low;
    private Long volumn;
    private Long amount;
    private Long closeChange;
    private Long hsl;
    private Long peTtm;
    private Long peLyr;
    private Long psTtm;
    private Long pbTtm;
    private Long percentage;
    private StkEntity stkByCode;

    @Basic
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "KLINE_DATE", nullable = true, length = 8)
    public String getKlineDate() {
        return klineDate;
    }

    public void setKlineDate(String klineDate) {
        this.klineDate = klineDate;
    }

    @Basic
    @Column(name = "OPEN", nullable = true, precision = 3)
    public Long getOpen() {
        return open;
    }

    public void setOpen(Long open) {
        this.open = open;
    }

    @Basic
    @Column(name = "CLOSE", nullable = true, precision = 3)
    public Long getClose() {
        return close;
    }

    public void setClose(Long close) {
        this.close = close;
    }

    @Basic
    @Column(name = "LAST_CLOSE", nullable = true, precision = 3)
    public Long getLastClose() {
        return lastClose;
    }

    public void setLastClose(Long lastClose) {
        this.lastClose = lastClose;
    }

    @Basic
    @Column(name = "HIGH", nullable = true, precision = 3)
    public Long getHigh() {
        return high;
    }

    public void setHigh(Long high) {
        this.high = high;
    }

    @Basic
    @Column(name = "LOW", nullable = true, precision = 3)
    public Long getLow() {
        return low;
    }

    public void setLow(Long low) {
        this.low = low;
    }

    @Basic
    @Column(name = "VOLUMN", nullable = true, precision = 2)
    public Long getVolumn() {
        return volumn;
    }

    public void setVolumn(Long volumn) {
        this.volumn = volumn;
    }

    @Basic
    @Column(name = "AMOUNT", nullable = true, precision = 2)
    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    @Basic
    @Column(name = "CLOSE_CHANGE", nullable = true, precision = 6)
    public Long getCloseChange() {
        return closeChange;
    }

    public void setCloseChange(Long closeChange) {
        this.closeChange = closeChange;
    }

    @Basic
    @Column(name = "HSL", nullable = true, precision = 2)
    public Long getHsl() {
        return hsl;
    }

    public void setHsl(Long hsl) {
        this.hsl = hsl;
    }

    @Basic
    @Column(name = "PE_TTM", nullable = true, precision = 2)
    public Long getPeTtm() {
        return peTtm;
    }

    public void setPeTtm(Long peTtm) {
        this.peTtm = peTtm;
    }

    @Basic
    @Column(name = "PE_LYR", nullable = true, precision = 2)
    public Long getPeLyr() {
        return peLyr;
    }

    public void setPeLyr(Long peLyr) {
        this.peLyr = peLyr;
    }

    @Basic
    @Column(name = "PS_TTM", nullable = true, precision = 2)
    public Long getPsTtm() {
        return psTtm;
    }

    public void setPsTtm(Long psTtm) {
        this.psTtm = psTtm;
    }

    @Basic
    @Column(name = "PB_TTM", nullable = true, precision = 2)
    public Long getPbTtm() {
        return pbTtm;
    }

    public void setPbTtm(Long pbTtm) {
        this.pbTtm = pbTtm;
    }

    @Basic
    @Column(name = "PERCENTAGE", nullable = true, precision = 2)
    public Long getPercentage() {
        return percentage;
    }

    public void setPercentage(Long percentage) {
        this.percentage = percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkKlineHkEntity that = (StkKlineHkEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(klineDate, that.klineDate) &&
                Objects.equals(open, that.open) &&
                Objects.equals(close, that.close) &&
                Objects.equals(lastClose, that.lastClose) &&
                Objects.equals(high, that.high) &&
                Objects.equals(low, that.low) &&
                Objects.equals(volumn, that.volumn) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(closeChange, that.closeChange) &&
                Objects.equals(hsl, that.hsl) &&
                Objects.equals(peTtm, that.peTtm) &&
                Objects.equals(peLyr, that.peLyr) &&
                Objects.equals(psTtm, that.psTtm) &&
                Objects.equals(pbTtm, that.pbTtm) &&
                Objects.equals(percentage, that.percentage);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, klineDate, open, close, lastClose, high, low, volumn, amount, closeChange, hsl, peTtm, peLyr, psTtm, pbTtm, percentage);
    }

    @ManyToOne
    @JoinColumn(name = "CODE", referencedColumnName = "CODE")
    public StkEntity getStkByCode() {
        return stkByCode;
    }

    public void setStkByCode(StkEntity stkByCode) {
        this.stkByCode = stkByCode;
    }
}
