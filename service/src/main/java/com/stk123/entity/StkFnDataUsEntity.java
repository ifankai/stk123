package com.stk123.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_FN_DATA_US")
@IdClass(StkFnDataUsEntity.CompositeKey.class)
public class StkFnDataUsEntity extends StkFnData {

    @Basic
    @Column(name = "FISCAL_YEAR_ENDS", nullable = true)
    private Integer fiscalYearEnds;

}
