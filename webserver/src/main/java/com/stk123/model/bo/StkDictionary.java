package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_DICTIONARY")
public class StkDictionary implements Serializable {

    @Column(name="TYPE")
    private Integer type;

    @Column(name="KEY")
    private String key;

    @Column(name="TEXT")
    private String text;

    @Column(name="REMARK")
    private String remark;

    @Column(name="PARAM")
    private String param;

    @Column(name="PARAM_2")
    private String param2;

    @Column(name="PARAM_3")
    private String param3;

    @Column(name="PARAM_4")
    private String param4;

    @Column(name="PARAM_5")
    private String param5;


    public Integer getType(){
        return this.type;
    }
    public void setType(Integer type){
        this.type = type;
    }

    public String getKey(){
        return this.key;
    }
    public void setKey(String key){
        this.key = key;
    }

    public String getText(){
        return this.text;
    }
    public void setText(String text){
        this.text = text;
    }

    public String getRemark(){
        return this.remark;
    }
    public void setRemark(String remark){
        this.remark = remark;
    }

    public String getParam(){
        return this.param;
    }
    public void setParam(String param){
        this.param = param;
    }

    public String getParam2(){
        return this.param2;
    }
    public void setParam2(String param2){
        this.param2 = param2;
    }

    public String getParam3(){
        return this.param3;
    }
    public void setParam3(String param3){
        this.param3 = param3;
    }

    public String getParam4(){
        return this.param4;
    }
    public void setParam4(String param4){
        this.param4 = param4;
    }

    public String getParam5(){
        return this.param5;
    }
    public void setParam5(String param5){
        this.param5 = param5;
    }


    public String toString(){
        return "type="+type+",key="+key+",text="+text+",remark="+remark+",param="+param+",param2="+param2+",param3="+param3+",param4="+param4+",param5="+param5;
    }

}
