package com.stk123.common.html;

import java.util.ArrayList;
import java.util.List;

public class HtmlTr extends Tag {
	
	public List<HtmlTd> columns = new ArrayList<HtmlTd>();
	
	public String getTagName() {
		return "tr";
	}
	
	public String toHtml(){
		StringBuffer sb = new StringBuffer(1024);
		sb.append(this.beginTagToHtml());
		for(HtmlTd td:this.columns){
			sb.append(td.beginTagToHtml());
			sb.append(td.text);
			sb.append(td.endTagToHtml());
		}
		sb.append(this.endTagToHtml());
		return sb.toString();
	}
}
