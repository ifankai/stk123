package com.stk123.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_DICTIONARY")
public class StkDictionaryEntity implements Serializable {
    private Long type;
    private String key;
    private String text;
    private String remark;
    private String param;
    private String param2;
    private String param3;
    private String param4;
    private String param5;

    @Id
    @Column(name = "TYPE", nullable = true, precision = 0)
    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    @Id
    @Column(name = "KEY", nullable = true, length = 40)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Basic
    @Column(name = "TEXT", nullable = true, length = 200)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic
    @Column(name = "REMARK", nullable = true, length = 400)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Basic
    @Column(name = "PARAM", nullable = true, length = 200)
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Basic
    @Column(name = "PARAM_2", nullable = true, length = 100)
    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    @Basic
    @Column(name = "PARAM_3", nullable = true, length = 100)
    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    @Basic
    @Column(name = "PARAM_4", nullable = true, length = 100)
    public String getParam4() {
        return param4;
    }

    public void setParam4(String param4) {
        this.param4 = param4;
    }

    @Basic
    @Column(name = "PARAM_5", nullable = true, length = 100)
    public String getParam5() {
        return param5;
    }

    public void setParam5(String param5) {
        this.param5 = param5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkDictionaryEntity that = (StkDictionaryEntity) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(key, that.key) &&
                Objects.equals(text, that.text) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(param, that.param) &&
                Objects.equals(param2, that.param2) &&
                Objects.equals(param3, that.param3) &&
                Objects.equals(param4, that.param4) &&
                Objects.equals(param5, that.param5);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, key, text, remark, param, param2, param3, param4, param5);
    }
}
