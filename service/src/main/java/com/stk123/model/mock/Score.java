package com.stk123.model.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Score {
	
	public int points;
	public List<Factor> fs = new ArrayList<Factor>();
	
	public Score(int points){
		this.points = points;
	}
	public void add(Factor f){
		this.points = this.points + f.points;
		fs.add(f);
	}
	public String toString(){
		Collections.sort(fs, new Comparator<Factor>(){
			@Override
			public int compare(Factor o1, Factor o2) {
				return o2.points - o1.points;
				//return o1.getCode()-o2.getCode();
			}});
		return "point="+points+", factor="+fs;
	}
	
}
