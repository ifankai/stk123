package com.stk123.bo.cust;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import com.stk123.bo.StkFnData;
import com.stk123.model.Index;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.collection.TableCell;
import com.stk123.web.StkConstant;


public class StkFnDataCust extends StkFnData implements TableCell {
	
	private StkFnDataCust before;
	private int number;//4 ���ļ���,3��������,2�ڶ�����,1��һ����
	private List<StkFnDataCust> fnData;
	
	//����  �����������ͣ����Ե�������������õ������������ݣ�����������ǰ����������֮��
	public Double getFnDataByFnQuarter(){
		double total = 0.0;
		if(this.getFnValue() != null)total = this.getFnValue();
		for(int i=number;i>=1;i--){
			if(this.before != null && this.before.getFnValue() != null){
				total += this.before.getFnValue();
			}else{
				return null;
			}
		}
		return total;
	}
	
	//����  �����񼾶ȵ����ݣ����Ե�������������õ������������ݣ��������������ǣ����������� �� ���������ݣ�
	public Double getFnDateByOneQuarter() throws Exception{
		if(this.getNumber() == 1)return this.getFnValue();
		if(this.getBefore() != null){
			return this.getFnValue() - this.getBefore().getFnValue();
		}
		return null;
	}
	
	public Double getFnDataByTTM() throws Exception{
		return this.getFnDataByTTM(false);
	}
	
	//����ǰ��4����������֮��
	public Double getFnDataByTTM(boolean isSingleQuarter) throws Exception{
		Double total = 0.0;
		int y = 4;
		StkFnDataCust data = this;
		while(true){
			if(data == null)return null;
			Double value = null;
			if(!isSingleQuarter){
				value = data.getFnDateByOneQuarter();
			}else{
				value = data.getFnValue();
			}
			if(value == null)return null;
			total = total + value;
			data = data.getBefore();
			y--;
			if(y == 0)break;
		}
		return total;
	}
	
	
	/*
	 * ͬ��
	 * ��������� ���������ݣ�isFnYear��Ϊfalse�����Ϊ ��������ͬ��
	 * ��������� �������ݣ�isFnYear��Ϊtrue�����Ϊ ��������ͬ�ȣ�
	 *                    isFnYear��Ϊfalse�����Ϊ ������ͬ��
	 */
	public Double getRateOfYear(boolean isFnYear) throws Exception{
		String year = "";
		String MMdd = "";
		try {
			year = StkUtils.formatDate(StkUtils.sf_ymd2.parse(this.getFnDate()), StkUtils.sf_yyyy);
			MMdd = StkUtils.formatDate(StkUtils.sf_ymd2.parse(this.getFnDate()), StkUtils.sf_MMdd);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String lastyyyyMMdd = (Integer.parseInt(year)-1)+MMdd;
		StkFnDataCust lastData = this.getFnData(this.getStkFnType().getType().toString(), lastyyyyMMdd);
		if(isFnYear){
			if(this.getFnDataByFnQuarter() != null && lastData.getFnDataByFnQuarter() != null){
				if(lastData.getFnDataByFnQuarter().doubleValue() < 0 && this.getFnDataByFnQuarter().doubleValue() < 0)return null;
				return (this.getFnDataByFnQuarter()-lastData.getFnDataByFnQuarter())/lastData.getFnDataByFnQuarter()*100;
			}
		}else{
			if(this.getFnValue() != null && lastData != null && lastData.getFnValue() != null){
				if(lastData.getFnValue().doubleValue() < 0 && this.getFnValue().doubleValue() < 0)return null;
				if(lastData.getFnValue().doubleValue() < 0 && this.getFnValue().doubleValue() > 0){
					return null;//-(this.getFnValue()-lastData.getFnValue())/lastData.getFnValue()*100;
				}
				return (this.getFnValue()-lastData.getFnValue())/lastData.getFnValue()*100;
			}
		}
		return null;
	}
	
	private StkFnDataCust getFnData(String type,String yyyyMMdd) throws Exception{
		//this.getFnData();
		if(this.fnData == null)return null;
		for(StkFnDataCust data:this.fnData){
			if(type.equals(data.getType().toString()) && yyyyMMdd.equals(data.getFnDate())){
				return data;
			}
		}
		return null;
	}
	
	//����
	public Double getRateOfQuarter(){
		Double dValue = this.getFnValue();
		if(dValue != null && before != null && before.getFnValue() != null){
			return (dValue-before.getFnValue())/before.getFnValue()*100;
		}
		return null;
	}
	
	private Double fnValue = null;
	public Double getFnValue(){
		if(this.fnValue != null)return this.fnValue;
		Double fnValue = super.getFnValue();
		try{
			if(this.getStkFnType() != null){
				if(this.getStkFnType().getReCalc() != null){
					StkFnDataCust data = this.getFnData(this.getStkFnType().getReCalc(), this.getFnDate());
					if(data != null){
						return data.getRateOfYear(false);
					}
				}
				if(this.getStkFnType().getCurrencyUnitAdjust().compareTo(BigDecimal.ONE) != 0){
					return this.getStkFnType().getCurrencyUnitAdjust().multiply(new BigDecimal(fnValue)).doubleValue();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		this.fnValue = fnValue;
		return this.fnValue;
	}
	
	public String getFnValueToString(){
		Double dValue = this.getFnValue();
		if(dValue == null)return StkConstant.MARK_DOUBLE_HYPHEN;
		if(this.getStkFnType() != null){
			String fnValue = StkUtils.number2String(dValue, this.getStkFnType().getPrecision());
			if(this.getStkFnType().getIsPercent() == 1){
				return fnValue + StkConstant.MARK_PERCENTAGE;
			}
			return fnValue;
		}
		return StkUtils.number2String(dValue, 2);
	}
	
	public StkFnDataCust getBefore() throws Exception {
		if(this.before == null){
			this.before = this.getFnData(this.getStkFnType().getType().toString(), StkUtils.getPrevQuarter(this.getFnDate()));
		}
		return before;
	}

	public void setBefore(StkFnDataCust before) {
		this.before = before;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public List<StkFnDataCust> getFnData() {
		return this.fnData;
	}

	public void setFnData(List<StkFnDataCust> fnData) {
		this.fnData = fnData;
	}

	@Override
	public String getX() {
		return this.getStkFnType().getType().toString();
	}

	@Override
	public String getY() {
		return this.getFnDate();
	}
	
	
}
