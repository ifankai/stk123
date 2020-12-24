package com.stk123.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_KLINE")
@IdClass(StkKlineEntity.CompositeKey.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@ToString
public class StkKlineEntity implements Serializable {
    @Id
    @Column(name = "CODE", nullable = true, length = 10)
    @JsonIgnore
    private String code;

    @Id
    @Column(name = "KLINE_DATE", nullable = true, length = 8)
    @JsonProperty("d")
    private String klineDate;

    @Basic
    @Column(name = "OPEN", nullable = true, precision = 2)
    @JsonProperty("o")
    private Double open;

    @Basic
    @Column(name = "CLOSE", nullable = true, precision = 2)
    @JsonProperty("c")
    private Double close;

    @Basic
    @Column(name = "LAST_CLOSE", nullable = true, precision = 2)
    @JsonProperty("lc")
    private Double lastClose;

    @Basic
    @Column(name = "HIGH", nullable = true, precision = 2)
    @JsonProperty("h")
    private Double high;

    @Basic
    @Column(name = "LOW", nullable = true, precision = 2)
    @JsonProperty("l")
    private Double low;

    @Basic
    @Column(name = "VOLUMN", nullable = true, precision = 2)
    @JsonProperty("v")
    private Double volumn;

    @Basic
    @Column(name = "AMOUNT", nullable = true, precision = 2)
    @JsonProperty("a")
    private Double amount;

    @Basic
    @Column(name = "CLOSE_CHANGE", nullable = true, precision = 6)
    @JsonIgnore
    private Double closeChange;

    @Basic
    @Column(name = "HSL", nullable = true, precision = 2)
    @JsonIgnore
    private Double hsl;

    @Basic
    @Column(name = "PE_TTM", nullable = true, precision = 2)
    @JsonIgnore
    private Double peTtm;

    @Basic
    @Column(name = "PE_LYR", nullable = true, precision = 2)
    @JsonIgnore
    private Double peLyr;

    @Basic
    @Column(name = "PERCENTAGE", nullable = true, precision = 2)
    @JsonProperty("p")
    private Double percentage;

    @Basic
    @Column(name = "PS_TTM", nullable = true, precision = 2)
    @JsonIgnore
    private Double psTtm;

    @Basic
    @Column(name = "PB_TTM", nullable = true, precision = 2)
    @JsonIgnore
    private Double pbTtm;

    @Basic
    @Column(name = "PE_NTILE", nullable = true, precision = 0)
    @JsonIgnore
    private Integer peNtile;

    @Basic
    @Column(name = "PB_NTILE", nullable = true, precision = 0)
    @JsonIgnore
    private Integer pbNtile;

    @Basic
    @Column(name = "PS_NTILE", nullable = true, precision = 0)
    @JsonIgnore
    private Integer psNtile;

    //private StkEntity stkByCode;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkKlineEntity that = (StkKlineEntity) o;
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
                Objects.equals(percentage, that.percentage) &&
                Objects.equals(psTtm, that.psTtm) &&
                Objects.equals(pbTtm, that.pbTtm) &&
                Objects.equals(peNtile, that.peNtile) &&
                Objects.equals(pbNtile, that.pbNtile) &&
                Objects.equals(psNtile, that.psNtile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, klineDate, open, close, lastClose, high, low, volumn, amount, closeChange, hsl, peTtm, peLyr, percentage, psTtm, pbTtm, peNtile, pbNtile, psNtile);
    }

//    @ManyToOne
//    @JoinColumn(name = "CODE", referencedColumnName = "CODE")
//    public StkEntity getStkByCode() {
//        return stkByCode;
//    }
//
//    public void setStkByCode(StkEntity stkByCode) {
//        this.stkByCode = stkByCode;
//    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CompositeKey implements Serializable {
        private String code;
        private String klineDate;
    }
}
