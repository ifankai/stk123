package com.stk123.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_HK_MONEY")
@Getter
@Setter
public class StkHkMoneyEntity implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_hk_money_id")
    @SequenceGenerator(name="s_hk_money_id", sequenceName="s_hk_money_id", allocationSize = 1)
    private Integer id;

    @Column(name = "CODE")
    private String code;

    @Basic
    @Column(name = "MONEY_DATE")
    private String moneyDate;

    @Basic
    @Column(name = "HOLDING_VOLUME")
    private Double holdingVolume;

    @Basic
    @Column(name = "HOLDING_AMOUNT")
    private Double holdingAmount;

    @Basic
    @Column(name = "HOLDING_RATE")
    private Double holdingRate;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkHkMoneyEntity that = (StkHkMoneyEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
