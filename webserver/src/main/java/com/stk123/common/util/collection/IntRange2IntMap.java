package com.stk123.common.util.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IntRange2IntMap {
	private IntRange range;
	private int count;
	private HashMap<IntRange, Integer> map = new HashMap<IntRange, Integer>();
	
	public IntRange2IntMap(){}
	
	public void define(int lower, int upper, int count){
		map.put(new IntRange(lower,upper), count);
	}
	
	public void define(int lower, int upper){
		map.put(new IntRange(lower,upper), 0);
	}
	
	public void addCount(int value){
		Map.Entry<IntRange, Integer> entry = this.getEntry(value);
		if(entry != null){
			entry.setValue(entry.getValue() + 1);
		}
	}
	
	public int getCount(int value){
		Map.Entry<IntRange, Integer> entry = this.getEntry(value);
		if(entry != null){
			return entry.getValue();
		}
		return -1;
	}
	
	public void print(){
		Set<Map.Entry<IntRange, Integer>> set = map.entrySet();
		for(Map.Entry<IntRange, Integer> entry : set){
			System.out.println("("+entry.getKey().getLower()+","+entry.getKey().getUpper()+")"+entry.getValue());
		}
	}
	
	public void printEntry(Map.Entry<IntRange, Integer> entry){
		System.out.println("("+entry.getKey().getLower()+","+entry.getKey().getUpper()+")"+entry.getValue());
	}
	
	//第一个满足lower <= value <= upper
	public Map.Entry<IntRange, Integer> getEntry(int value){
		Set<Map.Entry<IntRange, Integer>> set = map.entrySet();
		for(Map.Entry<IntRange, Integer> entry : set){
			if(value >= entry.getKey().getLower() && value <= entry.getKey().getUpper()){
				return entry;
			}
		}
		return null;
	}
	
	//所有满足lower <= value <= upper
	public List<Map.Entry<IntRange, Integer>> getEntries(int value){
		List<Map.Entry<IntRange, Integer>> entries = new ArrayList<Map.Entry<IntRange, Integer>>();
		Set<Map.Entry<IntRange, Integer>> set = map.entrySet();
		for(Map.Entry<IntRange, Integer> entry : set){
			if(value >= entry.getKey().getLower() && value <= entry.getKey().getUpper()){
				entries.add(entry);
			}
		}
		return entries;
	}
}
