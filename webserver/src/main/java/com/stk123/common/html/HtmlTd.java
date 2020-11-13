package com.stk123.common.html;

import java.util.HashMap;
import java.util.Map;

public class HtmlTd extends Tag {
	
	public static Map<String,String> AlignRight = new HashMap();
	static{
		AlignRight.put("align","right");
	}
	public static Map<String,String> AlignMiddle = new HashMap();
	static{
		AlignMiddle.put("align","middle");
	}
	
	public String text = "";
	
	public String getTagName() {
		return "td";
	}
	
	public static HtmlTd getInstance(String text){
		HtmlTd td = new HtmlTd();
		td.text = text;
		return td;
	}
	
	public static HtmlTd getInstanceAlignMiddle(String text){
		return HtmlTd.getInstance(text, AlignMiddle);
	}
	public static HtmlTd getInstanceAlignRight(String text){
		return HtmlTd.getInstance(text, AlignRight);
	}
	public static HtmlTd getInstanceAlignRight(Double text){
		return HtmlTd.getInstance(String.valueOf(text), AlignRight);
	}
	
	public static HtmlTd getInstance(String text, Map<String, String> attributes){
		HtmlTd td = new HtmlTd();
		td.text = text;
		td.attributes.putAll(attributes);
		return td;
	}
	
}
