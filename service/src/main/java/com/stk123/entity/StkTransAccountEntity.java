package com.stk123.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STK_TRANS_ACCOUNT", schema = "STK", catalog = "")
public class StkTransAccountEntity {
    private long id;
    private String weekStartDate;
    private String weekEndDate;
    private Long validAccount;
    private Long newAccount;
    private Long holdAAccount;
    private Long transAAccount;
    private Long holdTransActivity;
    private Long validTransActivity;
    private Long newTransActivity;
    private Long result1;
    private Long result2;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "WEEK_START_DATE", nullable = true, length = 8)
    public String getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(String weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    @Basic
    @Column(name = "WEEK_END_DATE", nullable = true, length = 8)
    public String getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(String weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    @Basic
    @Column(name = "VALID_ACCOUNT", nullable = true, precision = 2)
    public Long getValidAccount() {
        return validAccount;
    }

    public void setValidAccount(Long validAccount) {
        this.validAccount = validAccount;
    }

    @Basic
    @Column(name = "NEW_ACCOUNT", nullable = true, precision = 2)
    public Long getNewAccount() {
        return newAccount;
    }

    public void setNewAccount(Long newAccount) {
        this.newAccount = newAccount;
    }

    @Basic
    @Column(name = "HOLD_A_ACCOUNT", nullable = true, precision = 2)
    public Long getHoldAAccount() {
        return holdAAccount;
    }

    public void setHoldAAccount(Long holdAAccount) {
        this.holdAAccount = holdAAccount;
    }

    @Basic
    @Column(name = "TRANS_A_ACCOUNT", nullable = true, precision = 2)
    public Long getTransAAccount() {
        return transAAccount;
    }

    public void setTransAAccount(Long transAAccount) {
        this.transAAccount = transAAccount;
    }

    @Basic
    @Column(name = "HOLD_TRANS_ACTIVITY", nullable = true, precision = 4)
    public Long getHoldTransActivity() {
        return holdTransActivity;
    }

    public void setHoldTransActivity(Long holdTransActivity) {
        this.holdTransActivity = holdTransActivity;
    }

    @Basic
    @Column(name = "VALID_TRANS_ACTIVITY", nullable = true, precision = 4)
    public Long getValidTransActivity() {
        return validTransActivity;
    }

    public void setValidTransActivity(Long validTransActivity) {
        this.validTransActivity = validTransActivity;
    }

    @Basic
    @Column(name = "NEW_TRANS_ACTIVITY", nullable = true, precision = 4)
    public Long getNewTransActivity() {
        return newTransActivity;
    }

    public void setNewTransActivity(Long newTransActivity) {
        this.newTransActivity = newTransActivity;
    }

    @Basic
    @Column(name = "RESULT_1", nullable = true, precision = 4)
    public Long getResult1() {
        return result1;
    }

    public void setResult1(Long result1) {
        this.result1 = result1;
    }

    @Basic
    @Column(name = "RESULT_2", nullable = true, precision = 4)
    public Long getResult2() {
        return result2;
    }

    public void setResult2(Long result2) {
        this.result2 = result2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkTransAccountEntity that = (StkTransAccountEntity) o;
        return id == that.id &&
                Objects.equals(weekStartDate, that.weekStartDate) &&
                Objects.equals(weekEndDate, that.weekEndDate) &&
                Objects.equals(validAccount, that.validAccount) &&
                Objects.equals(newAccount, that.newAccount) &&
                Objects.equals(holdAAccount, that.holdAAccount) &&
                Objects.equals(transAAccount, that.transAAccount) &&
                Objects.equals(holdTransActivity, that.holdTransActivity) &&
                Objects.equals(validTransActivity, that.validTransActivity) &&
                Objects.equals(newTransActivity, that.newTransActivity) &&
                Objects.equals(result1, that.result1) &&
                Objects.equals(result2, that.result2);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, weekStartDate, weekEndDate, validAccount, newAccount, holdAAccount, transAAccount, holdTransActivity, validTransActivity, newTransActivity, result1, result2);
    }
}
