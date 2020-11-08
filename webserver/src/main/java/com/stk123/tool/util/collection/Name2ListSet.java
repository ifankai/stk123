package com.stk123.tool.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * name/list的set集，集里name是唯一的
 */
public class Name2ListSet<K,M> {
	
	private List<Name2Value<K, List<M>>> list;
	
	public Name2ListSet(){
		list = new ArrayList<Name2Value<K, List<M>>>();
	}

	public void add(K name, M element){
		for(Name2Value<K, List<M>> pair : list){
			if(pair.getName().equals(name)){
				pair.getValue().add(element);
				return;
			}
		}
		List l = new ArrayList<String>();
		l.add(element);
		list.add(new Name2Value(name,l));
	}
	
	public void add(K name, Collection<M> element){
		for(Name2Value<K, List<M>> pair : list){
			if(pair.getName().equals(name)){
				pair.getValue().addAll(element);
				return;
			}
		}
		List l = new ArrayList<String>();
		l.addAll(element);
		list.add(new Name2Value(name,l));
	}
	
	public void addIfNotExist(K name, M element){
		for(Name2Value<K, List<M>> pair : list){
			if(pair.getName().equals(name)){
				if(!pair.getValue().contains(element)){
					pair.getValue().add(element);
				}
				return;
			}
		}
		List l = new ArrayList<String>();
		l.add(element);
		list.add(new Name2Value(name,l));
	}
	
	public Name2Value<K, List<M>> get(String name){
		for(Name2Value<K, List<M>> pair : list){
			if(pair.getName().equals(name)){
				return pair;
			}
		}
		return null;
	}
	
	public int size(){
		return list.size();
	}
	
	public void sort(){
		Collections.sort(list, new Comparator(){
			public int compare(Object arg0, Object arg1) {
				Name2Value p0 = (Name2Value)arg0;
				Name2Value p1 = (Name2Value)arg1;
				return p1.getName().hashCode() - p0.getName().hashCode();
			}});
	}
	
	public List<Name2Value<K, List<M>>> getList(){
		return list;
	}

}
