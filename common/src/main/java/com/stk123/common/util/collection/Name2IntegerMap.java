package com.stk123.common.util.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "serial", "hiding" })
public class Name2IntegerMap<T, V> extends LinkedHashMap {
	
	public void add(String name, Integer v){
		if(this.containsKey(name)){
			Integer i = (Integer)super.get(name);
			super.put(name, i+v);
		}else{
			super.put(name, v);
		}
	}
	
	public void add(String name){
		add(name, 1);
	}
	
	public List<Map.Entry<String,Integer>> sort(){
		List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(this.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
		    public int compare(Map.Entry<String, Integer> o1,
		            Map.Entry<String, Integer> o2) {
		        return (o2.getValue() - o1.getValue());
		    }
		});
		return list;
	}
	
	public List<Map.Entry<String,Integer>> filterGreaterThan(List<Map.Entry<String,Integer>> list, int i, String excludes){
		List<Map.Entry<String,Integer>> result = new ArrayList<Map.Entry<String,Integer>>();
		for(Map.Entry<String,Integer> entry : list){
			if((entry.getValue() >= i && !excludes.contains(entry.getKey())) 
					|| (result.size() < 10 && entry.getValue() < i)){
				result.add(entry);
			}
		}
		return result;
	}
}
