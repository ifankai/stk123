package com.stk123.common.util;

import com.google.common.collect.Comparators;

import java.util.*;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;

public class ListUtils {
	
	public static void main(String[] args) {  
		Integer[] i = {11,888,33,100,200,500,100,465,132,464,489,156,789};
	    int n = 5;
	    List<Integer> il = new ArrayList(Arrays.asList(i));
	    ListUtils.getTopN(il, n, new ListUtils.Get(){
			public double get(Object o) {
				return (Integer)o;
			}});
	    System.out.println(il);
	    
	    int j = 0xFFFFFFF1;
	    System.out.println(j);
	    int x = ~j;
	    System.out.println(j);
	    System.out.println(x);
    }
	
	/**
	 * 先取得n+1个element到sublist里,进行比较,删除最小的,然后再往里面加1个element,继续比较
	 * @param list
	 * @param n
	 * @param get
	 */
	public static void getTopN(List list, int n, Get get){
		if(list == null)return;
		while (list.size() > n){
			List sublist = list.subList(0, n+1);
			int x = 0;
			Object y = sublist.get(0);
			for(int i=0;i<sublist.size();i++){
				if(get.get(sublist.get(i)) < get.get(y)){
					y = sublist.get(i);
					x = i;
				}
			}
	        list.remove(x);
	    }
	}
	
	public interface Get{
		public double get(Object o);
	}

    public static <T> List<T> greatest(List<T> list, int n, Function<T,Double> function){
        return list.stream().collect(Comparators.greatest(n, Comparator.comparing(function)));
    }

	public static <E> Collection<E> add(Collection collection, E... elements){
		collection.addAll(Arrays.asList(elements));
		return collection;
	}
	public static <E> List<E> createList(E... elements){
		List list = new ArrayList<>();
		list.addAll(Arrays.asList(elements));
		return list;
	}

    public static <T,R> List<R> eachSubList(List<T> list, int cnt, Function<List<T>,List<R>> function){
        List<R> results = new ArrayList<>();
        int start = 0;
        while(true){
            int end = start+cnt >= list.size() ? list.size() : start+cnt;
            List<T> subCodes = list.subList(start, end);
            List<R> subList = function.apply(subCodes);
            results.addAll(subList);
            if(end >= list.size())break;
            start = end;
        }
        return results;
    }
    public static <T> void eachSubList(List<T> list, int cnt, Consumer<List<T>> consumer){
        int start = 0;
        while(true){
            int end = start+cnt >= list.size() ? list.size() : start+cnt;
            List<T> subCodes = list.subList(start, end);
            consumer.accept(subCodes);
            if(end >= list.size())break;
            start = end;
        }
    }
}
