package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@ToString
@MappedSuperclass
@JsonView(View.Default.class)
public abstract class StkKline implements Serializable {
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
    private Double hsl;

    @Basic
    @Column(name = "PE_TTM", nullable = true, precision = 2)
    private Double peTtm;

    @Basic
    @Column(name = "PE_LYR", nullable = true, precision = 2)
    private Double peLyr;

    @Basic
    @Column(name = "PERCENTAGE", nullable = true, precision = 2)
    @JsonProperty("p")
    private Double percentage;

    @Basic
    @Column(name = "PS_TTM", nullable = true, precision = 2)
    private Double psTtm;

    @Basic
    @Column(name = "PB_TTM", nullable = true, precision = 2)
    private Double pbTtm;

//    @Basic
//    @Column(name = "PE_NTILE", nullable = true, precision = 0)
//    @JsonIgnore
//    private Integer peNtile;
//
//    @Basic
//    @Column(name = "PB_NTILE", nullable = true, precision = 0)
//    @JsonIgnore
//    private Integer pbNtile;
//
//    @Basic
//    @Column(name = "PS_NTILE", nullable = true, precision = 0)
//    @JsonIgnore
//    private Integer psNtile;

    //private StkEntity stkByCode;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkKline that = (StkKline) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(klineDate, that.klineDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, klineDate);
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
