package com.stk123.tool.db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.stk123.bo.StkPe;
import com.stk123.bo.StkText;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.JdbcUtils;

public class MysqlTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Connection conn = null;
		try {
			conn = DBUtil.getOracleDBConnection("oracle").getConnection();
			Connection con = DBUtil.getConnection();
			
			/*List<StkText> list = JdbcUtils.list(conn, "select * from stk_text", StkText.class);
			for(StkText st : list){
				System.out.println(st.getId());
				List params = new ArrayList();
				params.add(st.getId());
				params.add(st.getType());
				params.add(st.getCode());
				params.add(st.getCodeType());
				params.add(st.getTitle());
				params.add(st.getText());
				params.add(st.getInsertTime());
				params.add(st.getUpdateTime());
				params.add(st.getDispOrder());
				JdbcUtils.insert(con, "insert into stk_text select ?,?,?,?,?,?,?,?,? from dual", params);
			}*/
			
			/*List<StkPe> pes = JdbcUtils.list(conn, "select * from stk_pe", StkPe.class);
			for(StkPe pe : pes){
				System.out.println(pe.getId());
				List params = new ArrayList();
				params.add(pe.getId());
				params.add(pe.getReportDate());
				params.add(pe.getReportText());
				params.add(pe.getAveragePe());
				params.add(pe.getEneUpperCnt());
				params.add(pe.getEneLowerCnt());
				params.add(pe.getUpper1());
				params.add(pe.getLower1());
				params.add(pe.getBias());
				JdbcUtils.insert(con, "insert into stk_pe select ?,?,?,?,?,?,?,?,? from dual", params);
			}*/
			
			List<Map> pes = JdbcUtils.list2UpperKeyMap(conn, "select * from stk_capital_flow where flow_date>='20140401' and flow_date<='20140701'");
			int cnt = 0;
			List p = new ArrayList();
			for(Map pe : pes){
				System.out.println(pe.get("CODE")+","+pe.get("FLOW_DATE"));
				List params = new ArrayList();
				params.add(pe.get("CODE"));
				params.add(pe.get("FLOW_DATE"));
				params.add(pe.get("MAIN_AMOUNT"));
				params.add(pe.get("MAIN_PERCENT"));
				params.add(pe.get("SUPER_LARGE_AMOUNT"));
				params.add(pe.get("SUPER_LARGE_PERCENT"));
				params.add(pe.get("LARGE_AMOUNT"));
				params.add(pe.get("LARGE_PERCENT"));
				params.add(pe.get("MIDDLE_AMOUNT"));
				params.add(pe.get("MIDDLE_PERCENT"));
				params.add(pe.get("SMALL_AMOUNT"));
				params.add(pe.get("SMALL_PERCENT"));
				params.add(pe.get("INSERT_TIME"));
				p.add(params);
			}
			JdbcUtils.updateBatch(con, "insert into stk_capital_flow select ?,?,?,?,?,?,?,?,?,?,?,?,str_to_date(?,'%Y-%m-%d') from dual", p, 100);
			
		} finally {
			if (conn != null) conn.close();
		}
	}

}
