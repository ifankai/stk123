package com.stk123.model.strategy.result;

import com.stk123.common.html.HtmlTable;
import com.stk123.common.html.HtmlTd;
import com.stk123.common.html.HtmlTr;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class Table {
    //<y , <x, td>>
    private Map<String, Map<String, TableTd>> datas = new TreeMap<>();
    private Set<String> xTitles = new LinkedHashSet<>();//TreeSet<>(Comparator.reverseOrder());
    private String name;

    public Table(String name){
        this.name = name;
    }

    public Table add(TableTd td){
        if(!xTitles.contains(td.getXTitle()))
            xTitles.add(td.getXTitle());
        Map<String, TableTd> xMap = datas.get(td.getYTitle());
        if(xMap == null){
            xMap = new TreeMap<>();
            datas.put(td.getYTitle(), xMap);
        }
        TableTd cell = xMap.get(td.getXTitle());
        if(cell == null){
            xMap.put(td.getXTitle(), td);
        }else{
            cell.add(td.getContents());
        }
        return this;
    }

    public String toHtml(){
        HtmlTable tab = new HtmlTable();
        tab.attributes.put("border", "1");
        tab.attributes.put("cellspacing", "0");
        tab.attributes.put("cellpadding", "0");
        tab.attributes.put("style", "font:10px;word-break:keep-all;white-space:nowrap;");

        HtmlTr tr = new HtmlTr();
        if(datas.size() > 0){
            tr.columns.add(HtmlTd.getInstance(name));
            for(String title : xTitles){
                HtmlTd headTd = new HtmlTd();
                headTd.text = title;
                tr.columns.add(headTd);
            }
            tab.rows.add(tr);
        }
        for(String yTitle : datas.keySet()){
            tr = new HtmlTr();
            tr.columns.add(HtmlTd.getInstance(yTitle));
            for(String xTitle : xTitles){
                HtmlTd td = new HtmlTd();
                td.text = StringUtils.join(datas.get(yTitle).get(xTitle).getContents(), "<br/>");
                td.attributes.put("style", "word-break:keep-all;white-space:nowrap;");
                tr.columns.add(td);
            }
            tab.rows.add(tr);
        }
        return tab.toHtml();
    }
}
