package com.stk123.model.quartz.job.t.strategy;

import org.apache.commons.lang.StringUtils;

import com.stk123.model.quartz.job.t.K;
import com.stk123.model.quartz.job.t.Share;
import com.stk123.model.quartz.job.t.ShortTrade;
import com.stk123.model.quartz.job.t.TradeUtils;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.ChineseUtils;

public class TradeStrategy2 extends Strategy<Output> {

	public TradeStrategy2() {
		this.name = "µ×²¿·ÅÁ¿";
	}

	@Override
	public Output run(Share share) {
		Output op = share.getOutput();
		if(op.strategy2 == 1)return op;
		K k = share.getK();
		/*if("002624".equals(share.getCode())){
			System.out.println(k.getClose() - k.getLastClose());
		}*/
		if(k.getClose() - k.getLastClose() < 0){
			final double topNAmount = share.topNAmountK.get(share.topNAmountK.size()-1).getAmount();
			//System.out.println("topNK====="+share.topNAmountK.get(share.topNAmountK.size()-1));
			/*K bigbuyK = k.getValue(120, new K.Calc<K>() {
				@Override
				public K calc(K k, K d) {
					if(k.getAmount() >= topNAmount || k.getAmount() >= 1000000){
						d = k;
					}
					return d;
				}
			});
			if(bigbuyK != null && k.getClose() >= bigbuyK.getClose()){
				op.strategy2 = 1;
				sendMessage(share);
			}*/
			//System.out.println(k.getASum(60)+","+ topNAmount);
			double sum = k.getASum(60);
			/*if("002624".equals(share.getCode())){
				ShortTrade.log.info("sum=="+sum);
				System.out.println("sum=="+sum);
			}*/
			if(sum >= topNAmount && sum >= 1000000){
				op.strategy2 = 1;
				sendMessage(share);
			}
		}
		return op;
	}

}
