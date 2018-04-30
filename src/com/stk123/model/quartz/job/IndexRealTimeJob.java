package com.stk123.model.quartz.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.model.K.MACD;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.tool.util.collection.Name2Value;

public class IndexRealTimeJob implements Job {
	
	public static List<Name2Value<String,String>> CODES = new ArrayList<Name2Value<String,String>>();
	static{
		//http://quote.hexun.com/default.html
		CODES.add(new Name2Value("创业板","szse399006"));
		CODES.add(new Name2Value("中证1000","sse000852"));
		CODES.add(new Name2Value("中证500","sse000905"));
		CODES.add(new Name2Value("上证50","sse000016"));
		CODES.add(new Name2Value("中小板指","szse399005"));
	}

	public static void run() throws Exception {
		try{
			StringBuffer sb = new StringBuffer();
			for(Name2Value<String,String> pair : CODES){
				Index idx =  new Index(null,pair.getValue(),pair.getName());
				System.out.println(idx.getCode());
				idx.getKsRealTimeOnHalfHour();
				K k = idx.getK();
				String code = StringUtils.substring(pair.getValue(), pair.getValue().length()-6);
				boolean b = idx.isBreakOutTrendLine2(k.getDate(),60, 6, 0);
				if(b){
					System.out.println("60="+k.getDate()+"="+b);
					sb.append(pair.getName()+"["+code+"]"+" 突破(60,6)下降趋势线.").append("<br>");
				}
				boolean c = idx.isBreakOutTrendLine2(k.getDate(),120, 12, 0);
				if(c){
					System.out.println("120="+k.getDate()+"="+b);
					sb.append(pair.getName()+"["+code+"]"+" 突破(120,12)下降趋势线.").append("<br>");
				}
				if(b || c){
					idx.getKsRealTimeOnDay();
					k = idx.getK();
					double ma20 = k.getMA(K.Close, 20);
					sb.append(pair.getName()+"["+code+"]"+"20日平均线："+ma20).append("<br><br>");
				}
				
				//TODO MACD 顶背离
				if("szse399006".equals(idx.getCode())){
					K yk = k.before(1);
					MACD macd = k.getMACD();
					MACD ymacd = yk.getMACD();
					if(macd.dif <= macd.dea && ymacd.dif > ymacd.dea){
						EmailUtils.send(EmailUtils.IMPORTANT + "[警告]"+pair.getName()+"["+code+"]"+"MACD 30分钟背离","");
					}
				}

			}
			if(sb.length() > 0){
				EmailUtils.send(EmailUtils.IMPORTANT + "指数突破下降趋势线", sb.toString()+"<br>"+JobUtils.getMoneyFlow());					
			}
		}catch(Exception e){
			EmailUtils.send("IndexMonitor价格监控出错", e);
			e.printStackTrace();
		}

	}
	
	private SimpleDateFormat DateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");  
	private Date d = new Date();  
	private String returnstr = DateFormat.format(d);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println(arg0.getJobDetail().getKey().getName()+"["+returnstr+"]★IndexMonitor");
		try {
			run();
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

}
