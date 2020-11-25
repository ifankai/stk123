package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_MONITOR")
public class StkMonitorEntity {
    private long id;
    private String code;
    private Long type;
    private Boolean status;
    private Time triggerDate;
    private Time insertDate;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private String param5;
    private String result1;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "TYPE", nullable = true, precision = 0)
    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    @Basic
    @Column(name = "STATUS", nullable = true, precision = 0)
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Basic
    @Column(name = "TRIGGER_DATE", nullable = true)
    public Time getTriggerDate() {
        return triggerDate;
    }

    public void setTriggerDate(Time triggerDate) {
        this.triggerDate = triggerDate;
    }

    @Basic
    @Column(name = "INSERT_DATE", nullable = true)
    public Time getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Time insertDate) {
        this.insertDate = insertDate;
    }

    @Basic
    @Column(name = "PARAM_1", nullable = true, length = 40)
    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    @Basic
    @Column(name = "PARAM_2", nullable = true, length = 40)
    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    @Basic
    @Column(name = "PARAM_3", nullable = true, length = 40)
    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    @Basic
    @Column(name = "PARAM_4", nullable = true, length = 40)
    public String getParam4() {
        return param4;
    }

    public void setParam4(String param4) {
        this.param4 = param4;
    }

    @Basic
    @Column(name = "PARAM_5", nullable = true, length = 40)
    public String getParam5() {
        return param5;
    }

    public void setParam5(String param5) {
        this.param5 = param5;
    }

    @Basic
    @Column(name = "RESULT_1", nullable = true, length = 400)
    public String getResult1() {
        return result1;
    }

    public void setResult1(String result1) {
        this.result1 = result1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkMonitorEntity that = (StkMonitorEntity) o;
        return id == that.id &&
                Objects.equals(code, that.code) &&
                Objects.equals(type, that.type) &&
                Objects.equals(status, that.status) &&
                Objects.equals(triggerDate, that.triggerDate) &&
                Objects.equals(insertDate, that.insertDate) &&
                Objects.equals(param1, that.param1) &&
                Objects.equals(param2, that.param2) &&
                Objects.equals(param3, that.param3) &&
                Objects.equals(param4, that.param4) &&
                Objects.equals(param5, that.param5) &&
                Objects.equals(result1, that.result1);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, code, type, status, triggerDate, insertDate, param1, param2, param3, param4, param5, result1);
    }
}
