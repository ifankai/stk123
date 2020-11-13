package com.stk123.task.quartz.job.t.strategy;

import java.util.ArrayList;
import java.util.List;

import com.stk123.task.quartz.job.t.K;
import com.stk123.task.quartz.job.t.Share;
import com.stk123.service.ServiceUtils;
import com.stk123.common.util.collection.Name2Value;

/**
 * 平台突破
 */

public class TradeStrategy1 extends Strategy<Output> {
	
	public static List<Input> inputs = new ArrayList<Input>();
	static{
		//inputs.add(new Input(30, 8, new Name2Value(20, new Name2Value(1.2, 1.5)), 6));
		inputs.add(new Input(31, 8, 1.05, new Name2Value(20, new Name2Value(1.2, 2.5)), 7));
		inputs.add(new Input(32, 8, 1.05, new Name2Value(20, new Name2Value(2.5, 4.0)), 8));
		inputs.add(new Input(33, 8, 1.05, new Name2Value(20, new Name2Value(4.0, null)), 9));
		
		inputs.add(new Input(35, 6, 1.05, new Name2Value(20, new Name2Value(1.2, 2.5)), 6));
		inputs.add(new Input(36, 6, 1.05, new Name2Value(20, new Name2Value(2.5, 4.0)), 7));
		inputs.add(new Input(37, 6, 1.05, new Name2Value(20, new Name2Value(4.0, null)), 8));
		
		inputs.add(new Input(50, 5, 1.05, new Name2Value(20, new Name2Value(1.2, 2.5)), 6));
		inputs.add(new Input(51, 5, 1.05, new Name2Value(20, new Name2Value(2.5, 4.0)), 7));
		inputs.add(new Input(52, 5, 1.05, new Name2Value(20, new Name2Value(4.0, null)), 8));
		
		//inputs.add(new Input(60, 3, new Name2Value(10, new Name2Value(1.5, 2.5)), 6));
		//inputs.add(new Input(61, 3, new Name2Value(10, new Name2Value(2.5, 4.0)), 7));
		//inputs.add(new Input(62, 3, new Name2Value(10, new Name2Value(4.0, null)), 8));
		
		inputs.add(new Input(20, 8, 1.05, new Name2Value(40, new Name2Value(1.2, 1.5)), 6));
		inputs.add(new Input(21, 8, 1.05, new Name2Value(40, new Name2Value(1.5, 2.5)), 7));
		inputs.add(new Input(22, 8, 1.05, new Name2Value(40, new Name2Value(2.5, 4.0)), 8));
		inputs.add(new Input(23, 8, 1.05, new Name2Value(40, new Name2Value(4.0, null)), 9));
		
		inputs.add(new Input(241, 3, 1.03, new Name2Value(30, new Name2Value(2.0, 4.0)), 4));
		inputs.add(new Input(24, 3, 1.03, new Name2Value(40, new Name2Value(2.0, 4.0)), 4));
		inputs.add(new Input(25, 3, 1.03, new Name2Value(40, new Name2Value(4.0, null)), 5));
		
		//inputs.add(new Input(1, 3, 1.03, new Name2Value(60, new Name2Value(1.5, 2.5)), 2));
		inputs.add(new Input(2, 3, 1.03, new Name2Value(60, new Name2Value(2.0, 4.0)), 3));
		inputs.add(new Input(3, 3, 1.03, new Name2Value(60, new Name2Value(4.0, null)), 4));
		
		//inputs.add(new Input(4, 3, 1.03, new Name2Value(120, new Name2Value(1.5, 2.5)), 3));
		inputs.add(new Input(5, 3, 1.03, new Name2Value(120, new Name2Value(2.0, 4.0)), 4));
		inputs.add(new Input(6, 3, 1.03, new Name2Value(120, new Name2Value(4.0, null)), 5));
		
		//inputs.add(new Input(7, 3, new Name2Value(180, new Name2Value(1.5, 2.5)), 4));
		//inputs.add(new Input(8, 3, new Name2Value(180, new Name2Value(2.5, 4.0)), 5));
		//inputs.add(new Input(9, 3, new Name2Value(180, new Name2Value(4.0, null)), 6));
		
		inputs.add(new Input(10, 5, 1.04, new Name2Value(120, new Name2Value(1.5, 2.5)), 4));
		inputs.add(new Input(11, 5, 1.04, new Name2Value(120, new Name2Value(2.5, 4.0)), 5));
		inputs.add(new Input(12, 5, 1.04, new Name2Value(120, new Name2Value(4.0, null)), 6));
		
		//inputs.add(new Input(13, 8, new Name2Value(60, new Name2Value(1.0, 1.5)), 6));
		inputs.add(new Input(14, 8, 1.05, new Name2Value(60, new Name2Value(1.2, 2.5)), 7));
		inputs.add(new Input(15, 8, 1.05, new Name2Value(60, new Name2Value(2.5, 4.0)), 8));
		inputs.add(new Input(16, 8, 1.05, new Name2Value(60, new Name2Value(4.0, null)), 9));
		
		
	}
	
	public TradeStrategy1(){
		this.name = "平台突破";
	}

	@Override
	public Output run(Share share) {
		Output output = share.getOutput();
		Input tmp = null;
		for(Input input : inputs){
			//if(output.contain(input))continue;//发生过
			if(input.getRank() <= output.getMaxRank()) continue;//
			K k = share.getK();
			int at = input.getAmountTime();
			K kb = k.getK(at).getBefore();
			
			double hours = input.getHours();
			K cmaxk = kb.getKCH((int)hours*3600);
			
			if(k.getClose() < cmaxk.getClose()){//没有创新高
				continue;
			}
			K cmink = kb.getKCL((int)hours*3600);
			if(input.getPercent() > 0 && cmaxk.getClose()/cmink.getClose() > input.getPercent()){//有平台高度限制
				continue;
			}
			/*if(k.getTime().getHours() == 14 && k.getTime().getMinutes() == 12){
				System.out.println("hour=="+k.getClose()+","+cmaxk.getClose());
			}*/
			
			K amaxk = kb.getKAMax((int)hours*3600, at);
			double amax = amaxk.getASum(at);
			double asum = k.getASum(at);
			double asummax = asum/amax;
			/*if(k.getTime().getHours() == 14 && k.getTime().getMinutes() == 12){
				System.out.println(k.getTime()+",asummax=="+StkUtils.numberFormat2Digits(asummax)+",amaxk="+amaxk.getTime());
			}*/
			if(input.inAmountTimeMultiple(asummax)){
				input.setMultiple(asummax);
				input.setK(k);
				tmp = input;
			}
		}
		if(tmp != null){
			output.setCurrentHappen(tmp);
			output.add(tmp);
			/*String leftPad = StringUtils.repeat("&nbsp;", 8-ChineseUtils.length(share.getName()));
			String message = leftPad+share.getName()+"["+share.getCode()+"]"+this.getName()+" Rank:"+tmp.getRank()+ " -"
					+"在"+tmp.getHours()+"小时内,"+(tmp.getAmountTime()/60==0?tmp.getAmountTime()+"秒":tmp.getAmountTime()/60+"分钟")+"放量"+StkUtils.numberFormat2Digits(tmp.getMultiple())+"倍."
					+" K["+StkUtils.formatDate(tmp.getK().getTime(), StkUtils.sf_ymd9)+"] 价格:"+ tmp.getK().getClose();
			//System.out.println(message);
			Utils.info(message);*/
			
			String message = " Rank:"+tmp.getRank()+ " 在"+tmp.getHours()+"小时内,"
					+(tmp.getAmountTime()/60==0?tmp.getAmountTime()+"秒":tmp.getAmountTime()/60+"分钟")
					+"放量"+ServiceUtils.numberFormat2Digits(tmp.getMultiple())+"倍.";
			sendMessage(share, message);
		}
		return output;
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
