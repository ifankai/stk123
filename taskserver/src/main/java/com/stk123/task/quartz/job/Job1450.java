package com.stk123.task.quartz.job;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.model.bo.Stk;
import com.stk123.model.Index;
import com.stk123.model.IndexContext;
import com.stk123.model.IndexUtils;
import com.stk123.model.K;
import com.stk123.task.schedule.InitialKLine;
import com.stk123.util.ServiceUtils;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.EmailUtils;
import com.stk123.common.util.JdbcUtils;

public class Job1450 implements Job {

	public static void run() throws Exception {
		Connection conn = null;
		try{
			conn = DBUtil.getConnection();
			Map<String,K> map = IndexUtils.getKsRealTime(conn);
			System.out.println(map.size());
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			IndexContext context = new IndexContext();
			Index.KLineWhereClause = "and kline_date>='" + ServiceUtils.formatDate(ServiceUtils.addDay(ServiceUtils.now, -300),ServiceUtils.sf_ymd2)+"'";
			for(Stk stk : stks){
				Index index = new Index(conn, stk.getCode());
				K k = index.getK(0);
				K kRealTime = map.get(index.getCode());
				
				if(kRealTime != null && k != null){
					index.addK(kRealTime);
					context.indexs.add(index);
				}
			}
			String today = ServiceUtils.getToday();
			
			InitialKLine.eneStatistics(conn, today, context, false);
			
			/*List<Index> results = InitialKLine.checkKUpTrendLine(conn,today, context, false);
			if(results.size() >= 80){
				EmailUtils.sendImport("K线突破下降趋势个数："+results.size()+",日期:"+ today, "");
			}*/
			
			List<Index> results = InitialKLine.checkErPinChaoDi(conn, today, context.indexs, 1, false, false);
			if(results.size() >= 150){
				EmailUtils.sendImport("二品抄底-买入时机,个数："+results.size()+",日期:"+ today, "");
			}
			
			InitialKLine.strategy(conn, true);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	public static void main(String[] args) throws Exception {
		run();
	}
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			run();
		} catch (Exception e) {
			StringWriter aWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(aWriter));
			EmailUtils.send("Job1450 Error", aWriter.getBuffer().toString());
		}
	}

}
