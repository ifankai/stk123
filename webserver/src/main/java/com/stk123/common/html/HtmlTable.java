package com.stk123.common.html;

import java.util.ArrayList;
import java.util.List;

public class HtmlTable extends Tag {
	
	public List<HtmlTr> rows = new ArrayList<HtmlTr>();
	
	public String getTagName() {
		return "table";
	}
	
	public String toHtml() {
		StringBuffer sb = new StringBuffer(1024);
		sb.append(this.beginTagToHtml());
		for(HtmlTr tr:this.rows){
			sb.append(tr.beginTagToHtml());
			for(HtmlTd td:tr.columns){
				sb.append(td.beginTagToHtml());
				sb.append(td.text);
				sb.append(td.endTagToHtml());
			}
			sb.append(tr.endTagToHtml());
		}
		sb.append(this.endTagToHtml());
		return sb.toString();
	}
}
