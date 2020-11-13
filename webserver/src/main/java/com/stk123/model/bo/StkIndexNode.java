package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_INDEX_NODE")
public class StkIndexNode implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="PARENT_ID")
    private Integer parentId;

    @Column(name="NAME")
    private String name;

    @Column(name="DISP_ORDER")
    private Integer dispOrder;

    @Column(name="NODE_LEVEL")
    private Integer nodeLevel;

    @Column(name="CHART_TEMPLATE")
    private String chartTemplate;


    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public Integer getParentId(){
        return this.parentId;
    }
    public void setParentId(Integer parentId){
        this.parentId = parentId;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public Integer getDispOrder(){
        return this.dispOrder;
    }
    public void setDispOrder(Integer dispOrder){
        this.dispOrder = dispOrder;
    }

    public Integer getNodeLevel(){
        return this.nodeLevel;
    }
    public void setNodeLevel(Integer nodeLevel){
        this.nodeLevel = nodeLevel;
    }

    public String getChartTemplate(){
        return this.chartTemplate;
    }
    public void setChartTemplate(String chartTemplate){
        this.chartTemplate = chartTemplate;
    }


    public String toString(){
        return "id="+id+",parentId="+parentId+",name="+name+",dispOrder="+dispOrder+",nodeLevel="+nodeLevel+",chartTemplate="+chartTemplate;
    }

}
