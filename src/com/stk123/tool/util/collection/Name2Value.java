package com.stk123.tool.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Name2Value<K,V> {
	
	protected K name;
	protected V value;
	
	public Name2Value(){}
	
	public Name2Value(K name, V value){
		this.name = name;
		this.value = value;
	}
	
	public K getName() {
		return name;
	}
	public void setName(K name) {
		this.name = name;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
	
	public String toString(){
		return "name:"+name+",value:"+value;
	}
	
	public static List<Name2Value> containName(Collection<Name2Value> collection,String str){
		String name = StringUtils.trim(str);
		List<Name2Value> results = new ArrayList<Name2Value>();
		for(Name2Value pair : collection){
			String n = String.valueOf(pair.getName());
			if(name.indexOf(n) >= 0 || n.indexOf(name) >= 0){
				results.add(pair);
			}
		}
		return results;
	}
	
	public static List<Name2Value> containValue(Collection<Name2Value> collection,String str){
		String value = StringUtils.trim(str);
		List<Name2Value> results = new ArrayList<Name2Value>();
		for(Name2Value pair : collection){
			String n = String.valueOf(pair.getValue());
			if(value.indexOf(n) >= 0 || n.indexOf(value) >= 0){
				results.add(pair);
			}
		}
		return results;
	}
	
}
