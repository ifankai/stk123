package com.stk123.common.html;

public class HtmlA extends Tag {
	
	public HtmlA(){
		super.attributes.put("target", "_blank");
	}
	
	public String text = "";

	public String getTagName() {
		return "a";
	}
	
	public String toHtml() {
		StringBuffer sb = new StringBuffer(1024);
		sb.append(this.beginTagToHtml());
		sb.append(text);
		sb.append(this.endTagToHtml());
		return sb.toString();
	}
	
}
