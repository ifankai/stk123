package com.stk123.task.schedule;

import java.sql.Connection;
import java.util.List;

import com.stk123.util.ServiceUtils;
import com.stk123.model.Index;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.ConfigUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.common.util.JdbcUtils;


public class InitialKLineRepairKLine {

	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class,"db.properties");
		Connection conn = null;
		StringBuffer sb = null;
		try{
			conn = DBUtil.getConnection();
			//List<String> stksUnInit = JdbcUtils.list(conn, "select code from stk_kline where kline_date>=date_format(sysdate(),'%Y%m%d') and pe_ttm is null", String.class);
			List<String> stksUnInit = JdbcUtils.list(conn, "select code from stk_kline where kline_date>=to_char(sysdate,'yyyymmdd') and pe_ttm is null", String.class);
			sb = new StringBuffer();
			for(String code : stksUnInit){
				try{
					Index tmpIndex = new Index(conn, code);
					tmpIndex.initKLine();
				}catch(Exception e){
					sb.append(code).append("<br>");
					e.printStackTrace();
				}
			}
			int dayOfWeek = ServiceUtils.getDayOfWeek(ServiceUtils.now);
			if(sb.length() > 0 && dayOfWeek < 6){
				EmailUtils.send("Repair K Line Unsccessfully", sb.toString());
			}
		} finally{
			if(conn != null)conn.close();
		}
	}

}
