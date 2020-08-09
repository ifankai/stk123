package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import java.util.List;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_INDUSTRY_TYPE")
public class StkIndustryType implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="NAME")
    private String name;

    @Column(name="SOURCE")
    private String source;

    @Column(name="CARE_FLAG")
    private Integer careFlag;

    @Column(name="PARENT_ID")
    private Integer parentId;

    @Column(name="US_NAME")
    private String usName;

    @Column(name="CODE")
    private String code;

    @Column(name="PARENT_CODE")
    private String parentCode;

    private List<StkIndustry> stkIndustry;

    private List<StkIndustryRank> stkIndustryRank;

    private StkKlineRankIndustry stkKlineRankIndustry;


    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getSource(){
        return this.source;
    }
    public void setSource(String source){
        this.source = source;
    }

    public Integer getCareFlag(){
        return this.careFlag;
    }
    public void setCareFlag(Integer careFlag){
        this.careFlag = careFlag;
    }

    public Integer getParentId(){
        return this.parentId;
    }
    public void setParentId(Integer parentId){
        this.parentId = parentId;
    }

    public String getUsName(){
        return this.usName;
    }
    public void setUsName(String usName){
        this.usName = usName;
    }

    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public String getParentCode(){
        return this.parentCode;
    }
    public void setParentCode(String parentCode){
        this.parentCode = parentCode;
    }

    public List<StkIndustry> getStkIndustry(){
        return this.stkIndustry;
    }
    public void setStkIndustry(List<StkIndustry> stkIndustry){
        this.stkIndustry = stkIndustry;
    }

    public List<StkIndustryRank> getStkIndustryRank(){
        return this.stkIndustryRank;
    }
    public void setStkIndustryRank(List<StkIndustryRank> stkIndustryRank){
        this.stkIndustryRank = stkIndustryRank;
    }

    public StkKlineRankIndustry getStkKlineRankIndustry(){
        return this.stkKlineRankIndustry;
    }
    public void setStkKlineRankIndustry(StkKlineRankIndustry stkKlineRankIndustry){
        this.stkKlineRankIndustry = stkKlineRankIndustry;
    }


    public String toString(){
        return "id="+id+",name="+name+",source="+source+",careFlag="+careFlag+",parentId="+parentId+",usName="+usName+",code="+code+",parentCode="+parentCode+",stkIndustry="+stkIndustry+",stkIndustryRank="+stkIndustryRank+",stkKlineRankIndustry="+stkKlineRankIndustry;
    }

}
