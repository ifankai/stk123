package com.stk123.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_FN_DATA")
@IdClass(StkFnDataEntity.CompositeKey.class)
public class StkFnDataEntity extends StkFnData {

}
