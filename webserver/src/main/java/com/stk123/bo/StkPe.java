package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_PE")
public class StkPe implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="REPORT_DATE")
    private String reportDate;

    @Column(name="REPORT_TEXT")
    private String reportText;

    @Column(name="AVERAGE_PE")
    private Double averagePe;

    @Column(name="ENE_UPPER_CNT")
    private Integer eneUpperCnt;

    @Column(name="ENE_LOWER_CNT")
    private Integer eneLowerCnt;

    @Column(name="UPPER_1")
    private Integer upper1;

    @Column(name="LOWER_1")
    private Integer lower1;

    @Column(name="BIAS")
    private Double bias;

    @Column(name="ENE_UPPER")
    private Double eneUpper;

    @Column(name="ENE_LOWER")
    private Double eneLower;

    @Column(name="RESULT_1")
    private Double result1;

    @Column(name="RESULT_2")
    private Double result2;

    @Column(name="AVG_PB")
    private Double avgPb;

    @Column(name="TOTAL_PE")
    private Double totalPe;

    @Column(name="TOTAL_PB")
    private Double totalPb;

    @Column(name="MID_PB")
    private Double midPb;

    @Column(name="MID_PE")
    private Double midPe;

    @Column(name="RESULT_3")
    private Double result3;

    @Column(name="RESULT_4")
    private Double result4;

    @Column(name="RESULT_5")
    private Double result5;

    @Column(name="RESULT_6")
    private Double result6;

    @Column(name="RESULT_7")
    private Double result7;

    @Column(name="RESULT_8")
    private Double result8;

    @Column(name="RESULT_9")
    private Double result9;

    @Column(name="RESULT_10")
    private Double result10;

    @Column(name="RESULT_11")
    private Double result11;

    @Column(name="RESULT_12")
    private Double result12;


    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getReportDate(){
        return this.reportDate;
    }
    public void setReportDate(String reportDate){
        this.reportDate = reportDate;
    }

    public String getReportText(){
        return this.reportText;
    }
    public void setReportText(String reportText){
        this.reportText = reportText;
    }

    public Double getAveragePe(){
        return this.averagePe;
    }
    public void setAveragePe(Double averagePe){
        this.averagePe = averagePe;
    }

    public Integer getEneUpperCnt(){
        return this.eneUpperCnt;
    }
    public void setEneUpperCnt(Integer eneUpperCnt){
        this.eneUpperCnt = eneUpperCnt;
    }

    public Integer getEneLowerCnt(){
        return this.eneLowerCnt;
    }
    public void setEneLowerCnt(Integer eneLowerCnt){
        this.eneLowerCnt = eneLowerCnt;
    }

    public Integer getUpper1(){
        return this.upper1;
    }
    public void setUpper1(Integer upper1){
        this.upper1 = upper1;
    }

    public Integer getLower1(){
        return this.lower1;
    }
    public void setLower1(Integer lower1){
        this.lower1 = lower1;
    }

    public Double getBias(){
        return this.bias;
    }
    public void setBias(Double bias){
        this.bias = bias;
    }

    public Double getEneUpper(){
        return this.eneUpper;
    }
    public void setEneUpper(Double eneUpper){
        this.eneUpper = eneUpper;
    }

    public Double getEneLower(){
        return this.eneLower;
    }
    public void setEneLower(Double eneLower){
        this.eneLower = eneLower;
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

    public Double getAvgPb(){
        return this.avgPb;
    }
    public void setAvgPb(Double avgPb){
        this.avgPb = avgPb;
    }

    public Double getTotalPe(){
        return this.totalPe;
    }
    public void setTotalPe(Double totalPe){
        this.totalPe = totalPe;
    }

    public Double getTotalPb(){
        return this.totalPb;
    }
    public void setTotalPb(Double totalPb){
        this.totalPb = totalPb;
    }

    public Double getMidPb(){
        return this.midPb;
    }
    public void setMidPb(Double midPb){
        this.midPb = midPb;
    }

    public Double getMidPe(){
        return this.midPe;
    }
    public void setMidPe(Double midPe){
        this.midPe = midPe;
    }

    public Double getResult3(){
        return this.result3;
    }
    public void setResult3(Double result3){
        this.result3 = result3;
    }

    public Double getResult4(){
        return this.result4;
    }
    public void setResult4(Double result4){
        this.result4 = result4;
    }

    public Double getResult5(){
        return this.result5;
    }
    public void setResult5(Double result5){
        this.result5 = result5;
    }

    public Double getResult6(){
        return this.result6;
    }
    public void setResult6(Double result6){
        this.result6 = result6;
    }

    public Double getResult7(){
        return this.result7;
    }
    public void setResult7(Double result7){
        this.result7 = result7;
    }

    public Double getResult8(){
        return this.result8;
    }
    public void setResult8(Double result8){
        this.result8 = result8;
    }

    public Double getResult9(){
        return this.result9;
    }
    public void setResult9(Double result9){
        this.result9 = result9;
    }

    public Double getResult10(){
        return this.result10;
    }
    public void setResult10(Double result10){
        this.result10 = result10;
    }

    public Double getResult11(){
        return this.result11;
    }
    public void setResult11(Double result11){
        this.result11 = result11;
    }

    public Double getResult12(){
        return this.result12;
    }
    public void setResult12(Double result12){
        this.result12 = result12;
    }


    public String toString(){
        return "id="+id+",reportDate="+reportDate+",reportText="+reportText+",averagePe="+averagePe+",eneUpperCnt="+eneUpperCnt+",eneLowerCnt="+eneLowerCnt+",upper1="+upper1+",lower1="+lower1+",bias="+bias+",eneUpper="+eneUpper+",eneLower="+eneLower+",result1="+result1+",result2="+result2+",avgPb="+avgPb+",totalPe="+totalPe+",totalPb="+totalPb+",midPb="+midPb+",midPe="+midPe+",result3="+result3+",result4="+result4+",result5="+result5+",result6="+result6+",result7="+result7+",result8="+result8+",result9="+result9+",result10="+result10+",result11="+result11+",result12="+result12;
    }

}
