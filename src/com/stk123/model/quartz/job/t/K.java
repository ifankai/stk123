package com.stk123.model.quartz.job.t;

import java.util.Date;

import com.stk123.task.StkUtils;

public class K {
	
	private int id = -1;
	private double close;
	private double change;
	private Date time;
	
	private double amount;
	private double a;
	private int volume;
	private int v;
	private String flag;
	private double lastClose;
	
	private K before;
	
	public interface Calc<T> {
		public T calc(K k, T d);
	}
	
	public <T> T getValue(int sec, Calc<T> func){
		K k = this;
		T result = null;
		while(true){
			if(func != null){
				result = func.calc(k, result);
			}
			long t = k.time.getTime()/1000;
			long tb = k.before.time.getTime()/1000;
			long diff = t - tb;
			if(diff > 600){
				diff = 60 - k.before.time.getSeconds() + k.time.getSeconds();
			}
			sec -= diff;
			
			if(sec == 0){
				if(func != null){
					return result;
				}
				return (T)k.before;
			}else if(sec < 0){
				if(func != null){
					return result;
				}
				return (T)k;
			}
			k = k.before;
			if(k == null)break;
		}
		return null;
	}
	
	/**
	 * @return 返回的K线包含在sec秒内，是sec秒内第一根K线
	 */
	public K getK(int sec) {
		return this.getValue(sec, null);
	}
	
	/**
	 * @return 返回sec秒内close值最大的K线
	 */
	public K getKCH(int sec){
		return this.getValue(sec, new Calc<K>(){
			@Override
			public K calc(K k, K d) {
				if(d == null || k.getClose() >= d.getClose()){
					return k;
				}
				return d;
			}
		});
	}
	
	public K getKCL(int sec){
		return this.getValue(sec, new Calc<K>(){
			@Override
			public K calc(K k, K d) {
				if(d == null || k.getClose() <= d.getClose()){
					return k;
				}
				return d;
			}
		});
	}
	
	/**
	 * @return 返回sec秒内amount的和
	 */
	public double getASum(int sec){
		Double result = this.getValue(sec, new Calc<Double>(){
			@Override
			public Double calc(K k, Double d) {
				if(d == null) return d = k.getAmount();
				return d += k.getAmount();
			}
		});
		return result==null?0.0:result;
	}
	
	/**
	 * @return 返回sec秒内，任意step秒内amount之和最大的k线
	 */
	public K getKAMax(final int sec, final int step){//get end k
		return this.getValue(sec, new Calc<K>(){
			@Override
			public K calc(K k, K d) {
				if(d == null) return d = k;
				double sum = k.getASum(step);
				double sum1 = d.getASum(step);
				if(sum > sum1){
					return k;
				}
				return d;
			}
		});
	}
	
	public double getAMax(int sec, int step){
		return this.getKAMax(sec, step).getASum(step);
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getV() {
		return v;
	}

	public void setV(int v) {
		this.v = v;
	}

	public K getBefore() {
		return before;
	}

	public void setBefore(K before) {
		this.before = before;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public double getLastClose() {
		return lastClose;
	}

	public void setLastClose(double lastClose) {
		this.lastClose = lastClose;
	}

	@Override
	public String toString() {
		return "\n K [id=" + id + ", close=" + close + ", lastClose=" + lastClose + ", change=" + change + ", time=" + StkUtils.formatDate(time, StkUtils.sf_ymd9) + ", amount=" + amount
				+ ", a=" + a + ", volume=" + volume + ", v=" + v + ", flag=" + flag + ", before=" + (before==null?"0":"1") + "]";
	}
	
	
}
