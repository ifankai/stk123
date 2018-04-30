package com.stk123.tool.util.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.stk123.tool.ik.IKUtils;


public class SimilarSet<E> extends HashSet<String> {
	
	private static double similarRate = 0.7;
	private List similarList = new ArrayList();
	
	public void HashSet(double similarRate){
		this.similarRate = similarRate;
	}
	
	public boolean add(String str){
		if(this.contains(str)){
			this.similarList.add(str);
			return false;
		}
		return super.add(str);
	}
	
	public boolean contains(Object str){
		Iterator it = this.iterator();
		while(it.hasNext()){
			String s = (String)it.next();
			try {
				if(this.similar(str.toString(), s) >= similarRate){
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.contains(str);
	}

	public static void main(String[] args) throws Exception {
		SimilarSet ss = new SimilarSet();
		ss.add("两公司推股权激励 硕贝德要求4年后净利增长两倍_财经频道_证券之星");
		ss.add("两公司推股权激励 硕贝德要求4年后净利增长两倍_中国江苏网");
		ss.add("圈子- 两公司推股权激励 硕贝德要求4年后净利增长两倍");
		ss.add("两公司推股权激励 硕贝德要求4年后净利增长两倍 - 滚动新闻 - ...");
		ss.add("硕贝德:拟推950万份股权激励计划-股票频道-金融界");
		ss.add("硕贝德推股权激励_上证早知道_股票内参_云财经股市情报网");
		ss.add("硕贝德推股权激励方案 未来四年净利或增200%-硕贝德(300322)-股票...");
		System.out.println(ss);
	}
	
	public double similar(String o1, String o2) throws Exception{
		List<String> chs1 = IKUtils.split(o1.toString());
		List<String> chs2 = IKUtils.split(o2.toString());
		List<String> min = chs1;
		List<String> max = chs2;
		if(chs1.size() > chs2.size()){
			min = chs2;
			max = chs1;
		}
		double match = 0;
		for(String str : min){
			if(max.contains(str)){
				match ++;
			}
		}
		//System.out.println("similar rate="+(match/min.size()));
		return match/min.size();
	}
	
	public void addSimilar(String s){
		this.similarList.add(s);
	}
	
	public List<String> getSimilarList(){
		return this.similarList;
	}

}
