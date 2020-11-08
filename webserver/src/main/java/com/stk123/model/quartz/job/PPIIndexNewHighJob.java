package com.stk123.model.quartz.job;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.bo.StkDataPpi;
import com.stk123.bo.StkDataPpiType;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.JdbcUtils;

public class PPIIndexNewHighJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("PPIIndexNewHighJob");
		try {
			run();
		} catch (Exception e) {
			EmailUtils.send("PPIIndexNewHighJob Error", ExceptionUtils.getExceptionAsString(e));
		}
	}
	
	public static void run() throws Exception {
		Connection conn = null;
		try{
			List params = new ArrayList();
			conn = DBUtil.getConnection();
			List<List<String>> datas = new ArrayList<List<String>>();
			
			List<StkDataPpiType> types = JdbcUtils.list(conn, "select * from stk_data_ppi_type order by id", StkDataPpiType.class);
			for(StkDataPpiType type : types){
				params.clear();
				params.add(type.getId());
				StkDataPpi latest = JdbcUtils.load(conn, "select * from stk_data_ppi where type_id=? and ppi_date between to_char(sysdate-60,'yyyymmdd') and to_char(sysdate,'yyyymmdd') order by ppi_date desc", params, StkDataPpi.class);
				
				
				if(latest != null){		
					//System.out.println(type.getName()+","+latest.getValue());
					Double max = JdbcUtils.load(conn, "select max(value) from stk_data_ppi where type_id=? and ppi_date between to_char(sysdate-350,'yyyymmdd') and to_char(sysdate,'yyyymmdd')", params, Double.class);
					if(latest.getValue() >= max){
						List<String> data = new ArrayList<String>();
						data.add("<a href='http://localhost/data?s="+type.getName()+"' target='_black'>"+type.getName()+"</a>");
						data.add("<a href='http://www.iwencai.com/stockpick/search?typed=1&preParams=&ts=1&f=1&qs=result_rewrite&selfsectsn=&querytype=&searchfilter=&tid=stockpick&w="+type.getName()+"&queryarea=' target='_black'>查问财</a>");
						
						StkDataPpi latest2 = JdbcUtils.load(conn, 2, "select * from stk_data_ppi where type_id=? and ppi_date between to_char(sysdate-60,'yyyymmdd') and to_char(sysdate,'yyyymmdd') order by ppi_date desc", params, StkDataPpi.class);
						params.add(latest2.getPpiDate());
						max = JdbcUtils.load(conn, "select max(value) from stk_data_ppi where type_id=? and ppi_date between to_char(sysdate-350,'yyyymmdd') and ?", params, Double.class);
						if(latest2.getValue() < max){
							data.add("新加入");
						}else{
							data.add("");
						}
						datas.add(data);
						System.out.println("new high:"+type.getName());
					}
				}
			}
			if(datas.size() > 0){
				List<String> titles = new ArrayList<String>();
				titles.add("大宗商品");
				titles.add("");
				titles.add("");
				EmailUtils.send("大宗商品 new high, 个数："+datas.size(), StkUtils.createHtmlTable(titles, datas));
			}
		}finally{
			if (conn != null)conn.close();
		}
	}

	public static void main(String[] args) throws Exception {
		run();
	}

}
