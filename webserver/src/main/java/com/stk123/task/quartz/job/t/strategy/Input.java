package com.stk123.task.quartz.job.t.strategy;

import com.stk123.task.quartz.job.t.K;
import com.stk123.common.util.collection.Name2Value;

public class Input {
	
	private int id = 0;
	/**
	 * 总的时间段,单位:小时
	 */
	private double hours = 0.0;
	
	/**
	 * 平台最高点与最低点比值
	 */
	private double percent = 0.0;
	
	/**
	 * 多久时间内发生amount的倍量对应的rank
	 * <atime, [amount multiple]>
	 */
	private Name2Value<Integer, Name2Value<Double,Double>> timeAmountMultiple = null;
	
	private int rank = 0;
	
	//----------------------------------------------
	//----------------------------------------------
	private K k = null; //满足条件的k线
	private double multiple = 0.0;//量能倍数
	
	public Input(int id, double hours, Name2Value<Integer, Name2Value<Double,Double>> timeAmountMultiple, int rank){
		this(id, hours,0, timeAmountMultiple, rank);
	}
	
	public Input(int id, double hours,double percent, Name2Value<Integer, Name2Value<Double,Double>> timeAmountMultiple, int rank){
		this.id = id;
		this.hours = hours;
		this.percent = percent;
		this.timeAmountMultiple = timeAmountMultiple;
		this.rank = rank;
	}
	
	public boolean inAmountTimeMultiple(double asummax){
		Name2Value<Double,Double> range = this.timeAmountMultiple.getValue();
		if((range.getName() == null || range.getName().doubleValue() < asummax) 
				&& (range.getValue() == null || range.getValue().doubleValue() >= asummax)){
			return true;
		}
		return false;
	}
	
	public int getAmountTime(){
		return this.timeAmountMultiple.getName().intValue();
	}
	

	public double getPercent() {
		return percent;
	}

	public int getRank() {
		return rank;
	}
	
	public Double getHours(){
		return this.hours;
	}

	public K getK() {
		return k;
	}

	public void setK(K k) {
		this.k = k;
	}
	
	public double getMultiple() {
		return multiple;
	}

	public void setMultiple(double multiple) {
		this.multiple = multiple;
	}

	@Override
	public String toString() {
		return "Input [id=" + id + ", hours=" + hours + ", timeAmountMultiple=" + timeAmountMultiple + ", rank=" + rank
				+ ", k=" + k + "]";
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Input other = (Input) obj;
		if(this.id == other.id)
			return true;
		return false;
	}
	
	
}
