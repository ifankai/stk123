package com.stk123.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_FN_DATA_HK")
@IdClass(StkFnDataHkEntity.CompositeKey.class)
public class StkFnDataHkEntity extends StkFnData {

}
