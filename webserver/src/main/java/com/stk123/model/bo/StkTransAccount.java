package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_TRANS_ACCOUNT")
public class StkTransAccount implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="WEEK_START_DATE")
    private String weekStartDate;

    @Column(name="WEEK_END_DATE")
    private String weekEndDate;

    @Column(name="VALID_ACCOUNT")
    private Double validAccount;

    @Column(name="NEW_ACCOUNT")
    private Double newAccount;

    @Column(name="HOLD_A_ACCOUNT")
    private Double holdAAccount;

    @Column(name="TRANS_A_ACCOUNT")
    private Double transAAccount;

    @Column(name="HOLD_TRANS_ACTIVITY")
    private Double holdTransActivity;

    @Column(name="VALID_TRANS_ACTIVITY")
    private Double validTransActivity;

    @Column(name="NEW_TRANS_ACTIVITY")
    private Double newTransActivity;

    @Column(name="RESULT_1")
    private Double result1;

    @Column(name="RESULT_2")
    private Double result2;


    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getWeekStartDate(){
        return this.weekStartDate;
    }
    public void setWeekStartDate(String weekStartDate){
        this.weekStartDate = weekStartDate;
    }

    public String getWeekEndDate(){
        return this.weekEndDate;
    }
    public void setWeekEndDate(String weekEndDate){
        this.weekEndDate = weekEndDate;
    }

    public Double getValidAccount(){
        return this.validAccount;
    }
    public void setValidAccount(Double validAccount){
        this.validAccount = validAccount;
    }

    public Double getNewAccount(){
        return this.newAccount;
    }
    public void setNewAccount(Double newAccount){
        this.newAccount = newAccount;
    }

    public Double getHoldAAccount(){
        return this.holdAAccount;
    }
    public void setHoldAAccount(Double holdAAccount){
        this.holdAAccount = holdAAccount;
    }

    public Double getTransAAccount(){
        return this.transAAccount;
    }
    public void setTransAAccount(Double transAAccount){
        this.transAAccount = transAAccount;
    }

    public Double getHoldTransActivity(){
        return this.holdTransActivity;
    }
    public void setHoldTransActivity(Double holdTransActivity){
        this.holdTransActivity = holdTransActivity;
    }

    public Double getValidTransActivity(){
        return this.validTransActivity;
    }
    public void setValidTransActivity(Double validTransActivity){
        this.validTransActivity = validTransActivity;
    }

    public Double getNewTransActivity(){
        return this.newTransActivity;
    }
    public void setNewTransActivity(Double newTransActivity){
        this.newTransActivity = newTransActivity;
    }

    public Double getResult1(){
        return this.result1;
    }
    public void setResult1(Double result1){
        this.result1 = result1;
    }

    public Double getResult2(){
        return this.result2;
    }
    public void setResult2(Double result2){
        this.result2 = result2;
    }


    public String toString(){
        return "id="+id+",weekStartDate="+weekStartDate+",weekEndDate="+weekEndDate+",validAccount="+validAccount+",newAccount="+newAccount+",holdAAccount="+holdAAccount+",transAAccount="+transAAccount+",holdTransActivity="+holdTransActivity+",validTransActivity="+validTransActivity+",newTransActivity="+newTransActivity+",result1="+result1+",result2="+result2;
    }

}
