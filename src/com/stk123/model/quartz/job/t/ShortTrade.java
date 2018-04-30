package com.stk123.model.quartz.job.t;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.model.Index;
import com.stk123.model.IndexUtils;
import com.stk123.model.strategy.StrategyManager;
import com.stk123.task.StkUtils;
import com.stk123.task.XueqiuUtils;
import com.stk123.tool.db.connection.Pool;
import com.stk123.tool.db.util.CloseUtil;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.JdbcUtils;
import com.util.Arrays;

public class ShortTrade implements Job{
	
	public static Logger log = Logger.getLogger("TRADE");
	
	public static boolean isTest = false;
	
	public static void logK(Share share, K k){
		if("002624".equals(share.getCode())){
			log.info(k);
			System.out.println(k);
		}
	}

	public static void main(String[] args) throws Exception {
		log.info("ShortTrade............");
		isTest = false;
		run();
	}
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			isTest = false;
			run();
		} catch (Exception e) {
			StringWriter aWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(aWriter));
			log.error(aWriter.getBuffer().toString());
			EmailUtils.send("ShortTrade Job Error", aWriter.getBuffer().toString());
		}
	}
	
	public static void run() throws Exception {
		Share sh = new Share("000001");
		String sh000001 = TradeUtils.getKsFromSina(sh);
		String shk = StringUtils.substringBetween(sh000001, "=\"", "\";");
		String today = shk.split(",")[30];
		TradeUtils.clearMessage();
		//TradeUtils.info("Today: "+today);
		
		//Collection sets = Arrays.toCollection(new String[]{"000710","300232","000962","300438","002792","300364","002139","600760","002624","002635","600699","600161"});
		//sets = Arrays.toCollection(new String[]{"300364"});
		
		Connection conn = null;
		List<Share> shares = new ArrayList<Share>();
		try{
			conn = Pool.getPool().getConnection();
			List<Index> indexs = StrategyManager.getIndexFromSearchCondition(conn, 49);
			List<String> codes = IndexUtils.indexToCode(indexs);
			//codes = JdbcUtils.list(conn, "select code from stk_cn where code in ('"+StringUtils.join(sets, "','")+"')", String.class);
			//codes.add("300131");
			TradeUtils.info("Monitor Shares:["+codes.size()+"] "+ codes);
			
			for(String code : codes){
				try{
					Share share = new Share(code);
					share.initK(today);
					share.getTopNAmount(5);//for stategy2
					System.out.println(code + ":" +share.topNAmountK);
					
					shares.add(share);
				}catch(Exception e){
					System.out.println("error:"+code);
					throw e;
				}
			}
		}finally{
			Pool.getPool().closeAllConnections();
		}
		//TradeUtils.info("K history data upated finished. Time:"+StkUtils.formatDate(new Date(), StkUtils.sf_ymd9));
		
		while(true){
			TradeUtils.updateSinaData(shares);
			//System.out.println("updateSinaData succ.");
			for(Share share : shares){
				share.runStrategy();
			}
			
			Date now = new Date();
			int hour = now.getHours();
			int minute = now.getMinutes();
			if(hour == 11 && minute >= 30)Thread.sleep(60*1000 * 88);
			if(hour >= 15)break;
			
			if(isTest)break;
			Thread.sleep(2*1000);
		}
		
		//	
	}

}
