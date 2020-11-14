package com.stk123.common.html;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Tag {
	
	public Map<String,String> attributes = new HtmlHashMap<String,String>();
	
	public abstract String getTagName();
	
	public String attributeToHtml(){
		StringBuffer sb= new StringBuffer(1024);
		Set<Map.Entry<String, String>> set = attributes.entrySet();
		for(Map.Entry<String, String> kv:set){
			sb.append(" ").append(kv.getKey()).append("='").append(kv.getValue()).append("'");
		}
		return sb.toString();
	}
	
	public String beginTagToHtml(){
		StringBuffer sb= new StringBuffer(1024);
		return sb.append("<").append(this.getTagName()).append(this.attributeToHtml()).append(">").toString();
	}
	
	public String endTagToHtml(){
		StringBuffer sb= new StringBuffer(1024);
		return sb.append("</").append(this.getTagName()).append(">").toString();
	}

}

class HtmlHashMap<K,V> extends HashMap<String,String>{
	public String put(String key,String value){
		String ret = super.get(key);
		if(ret != null){
			super.put(key, ret+"; "+value);
		}else{
			super.put(key, value);
		}
		return ret;
	}
}

