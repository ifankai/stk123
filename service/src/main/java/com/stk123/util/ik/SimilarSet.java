package com.stk123.util.ik;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SimilarSet<E> extends LinkedHashSet<String> {
	
	private double similarRate = 0.7;
	private List similarList = new ArrayList();

	public SimilarSet(){}

	public SimilarSet(double similarRate){
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
				if(StringSimilarUtils.getSimilarRateByIKAnalyzer(str.toString(), s) >= similarRate){
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
	

	
	public void addSimilar(String s){
		this.similarList.add(s);
	}
	
	public List<String> getSimilarList(){
		return this.similarList;
	}

}
