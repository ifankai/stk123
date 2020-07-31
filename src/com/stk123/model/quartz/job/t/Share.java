package com.stk123.model.quartz.job.t;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.stk123.model.quartz.job.t.K.Calc;
import com.stk123.model.quartz.job.t.strategy.Output;
import com.stk123.model.quartz.job.t.strategy.Strategy;
import com.stk123.model.quartz.job.t.strategy.TradeStrategy1;
import com.stk123.model.quartz.job.t.strategy.TradeStrategy2;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.connection.Pool;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JsonUtils;

public class Share {
	
	private String code;
	private String name;
	private List<K> ks = new ArrayList<K>();
	private Output output = new Output();
	
	public List<K> topNAmountK = null;
	public boolean close20ChangeGreaterThan0 = false; //股价20天>=0 
	
	public Share(String code, String name){
		this.code = code;
		this.name = name;
	}
	
	public Share(String code){
		this(code, null);
	}
	
	public String getLocation() {
		return TradeUtils.getLocation(this.code);
	}
	
	public String getLocationCode() {
		return this.getLocation()+this.code;
	}
	
	public K getK(int n){
		if(n >= ks.size() || n < 0)return null;
		return this.ks.get(n);
	}
	public K getK(){
		return this.getK(ks.size()-1);
	}
	
	
	public boolean addK(K k){
		Date date = k.getTime();
		int hour = date.getHours();
		int minute = date.getMinutes();
		if(hour >= 15 /*&& minute > 0*/) return false;
		
		K latestK = this.getK();
		if(latestK != null && latestK.getTime().getTime() >= k.getTime().getTime())return false;
		/*if(k.id != null && k.id <= latestK.id){
			return false;
		}*/
		k.setBefore(latestK);
		if(k.getBefore() != null){
			if(hour == 11 && minute > 30) return false;
			k.setChange(StkUtils.numberFormat(k.getClose() - k.getBefore().getClose(), 2));
			k.setLastClose(k.getBefore().getClose());
		}
		this.ks.add(k);
		return true;
	}
	
	public void initK(String today) throws Exception {
		String scode = this.getLocationCode();
		//http://stockhtm.finance.qq.com/sstock/ggcx/002635.shtml
		String page = HttpUtils.get("http://web.ifzq.gtimg.cn/appstock/app/day/query?_var=fdays_data_"+scode+"&code="+scode, null);
		String p = StringUtils.substringAfter(page, "=");
		Map map = JsonUtils.testJson(p);
		List<Map> data = (List)((Map)((Map)map.get("data")).get(scode)).get("data");
		Collections.reverse(data);
		for(int i=0; i<data.size(); i++){
			Map dd = (Map)data.get(i);
			String date = (String)dd.get("date");
			if(!StringUtils.replace(today, "-", "").equals(date)){
				List<String> dt = (List)dd.get("data");
				int tmpv = -1;
				double tmpa = -1;
				for(String d : dt){
					if(d.length() == 0)continue;
					String[] s = d.split(" ");
					int v = Integer.parseInt(s[2]);
					double a = Double.parseDouble(s[3]);
					if(tmpv != -1){
						v -= tmpv;
						a -= tmpa;
					}
					tmpv = Integer.parseInt(s[2]);
					tmpa = Double.parseDouble(s[3]);
					double c = Double.parseDouble(s[1]);
					K k = new K();
					k.setClose(c);
					k.setVolume(v);
					k.setAmount(a);
					k.setTime(StkUtils.sf_ymd12.parse(date+s[0]+"00"));
					this.addK(k);
				}
			}
		}
		
		if(ShortTrade.isTest) this.getTopNAmount(5);
		
		//--------
		int pageNumber = 0;
		do{
			//http://stockhtm.finance.qq.com/sstock/quotpage/q/002635.htm#detail
			page = HttpUtils.get("http://stock.gtimg.cn/data/index.php?appn=detail&action=data&c="+scode+"&p="+pageNumber, null);
			if(page == null || page.length() == 0)break;
			
			String ks = StringUtils.substringBetween(page, "\"", "\"");
			String[] ss = StringUtils.split(ks, "|");
			for(int i=0; i<ss.length; i++){
				String[] s = ss[i].split("/");
				K k = new K();
				k.setId(Integer.parseInt(s[0]));
				k.setTime(StkUtils.sf_ymd9.parse(today +" "+ s[1]));
				k.setClose(Double.parseDouble(s[2]));
				k.setChange(Double.parseDouble(s[3]));
				k.setVolume(Integer.parseInt(s[4]));
				k.setAmount(Double.parseDouble(s[5]));				
				k.setFlag(s[6]);				
				
				this.addK(k);
				
				if(ShortTrade.isTest){
					//System.out.println(k);
					this.runStrategy();
				}
			}
			pageNumber++;
		}while(true);
	}
	
	public static List<Strategy> strategys = new ArrayList<Strategy>();
	static{
		//strategys.add(new TradeStrategy1());
		strategys.add(new TradeStrategy2());
	}
	
	public void runStrategy() {
		for(Strategy strategy : strategys){
			strategy.run(this);
		}
	}
	
	public void calcBigBuy(Date date){
		double totalAmount = 0.0;
		double totalBigBuy = 0.0;
		int countBigBuy = 0;
		for(K k : this.ks){
			if(date.getDate() == k.getTime().getDate()){
				totalAmount += k.getAmount();
				if(k.getAmount() >= 1000000 && "B".equals(k.getFlag())){
					countBigBuy ++;
					totalBigBuy += k.getAmount();
				}
			}
		}
		System.out.println(code+","+countBigBuy+","+(totalBigBuy/totalAmount));
	}
	

	public List<K> getTopNAmount(final int n){
		K k = this.getK();
		return this.topNAmountK = k.getValue(60*60*16, new Calc<List<K>>() {
			@Override
			public List<K> calc(K k, List<K> d) {
				if(d == null) d = new ArrayList();
				if(d.size() < n){
					d.add(k);
				}else{
					K min = d.get(d.size()-1);
					if(min.getAmount() < k.getAmount()){
						d.set(d.size()-1, k);
					}
				}
				Collections.sort(d, new Comparator<K>(){
					@Override
					public int compare(K o1, K o2) {
						return (int)(o2.getAmount()-o1.getAmount());
					}
					
				});
				return d;
			}
			
		});
	}
	
	public boolean getClose20ChangeGreaterThan0() throws Exception{
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			return true;
		}finally{
			Pool.getPool().free(conn);
		}
	}
	

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Output getOutput() {
		return output;
	}

	public void setOutput(Output output) {
		this.output = output;
	}

	@Override
	public String toString() {
		return "Share [code=" + code + ", name=" + name + ", ks=" + ks + "]";
	}
	
	
}
