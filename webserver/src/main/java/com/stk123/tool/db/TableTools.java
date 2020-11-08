package com.stk123.tool.db;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.stk123.bo.Stk;
import com.stk123.bo.StkIndustry;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JdbcUtils.Query;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class TableTools {

	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class,"db.properties");
		long start = System.currentTimeMillis();
		
		TableTools.createSourceCode();
		
		Connection conn = DBUtil.getConnection();
		/*List<Stk> stks = sql.list(conn);
		for(Stk stk:stks)
			System.out.println(stk);*/
		
		List params = new ArrayList();
		params.clear();
		params.add("002275");
		List<com.stk123.bo.StkIndustry> ind2 = sql2.list(conn,params);
		System.out.println(ind2);
		
		/*params.clear();
		params.add(StkUtils.formatDate(StkUtils.addDay(new Date(), -1), StkUtils.sf_ymd2));
		stks = sql3.list(conn, params);
		System.out.println(stks);*/
		
		long end = System.currentTimeMillis();
		System.out.println("time:"+((end-start)/1000D));
		
	}
	
	private static Query sql = JdbcUtils.query(com.stk123.bo.Stk.class, "s")
		.addJoin("s", "code", com.stk123.bo.StkFnData.class, "fd", "code(+)")
		.addJoin("fd", "type", com.stk123.bo.StkFnType.class, "ft", "type(+)").addCondition("s.code<'000011'");
	
	private static Query sql2 = JdbcUtils.query(com.stk123.bo.StkIndustry.class, "s")
		.addJoin("s", "industry", com.stk123.bo.StkIndustryType.class, "t", "id").addCondition("s.code=?");
	
	private static Query sql3 = JdbcUtils.query(Stk.class,"s")
	.addJoin("s","code",StkIndustry.class, "ind","code")
	.addJoin("ind", "industry", com.stk123.bo.StkIndustryType.class, "t", "id").addCondition("(s.listing_date is null or listing_date>=?)");
	
	/*
	private static Query q = DBUtils.query(TPolicyGeneral.class, "pg")
		.addJoin("pg", "policy_id", TInsuredList.class, "il", "policy_id")
		.addJoin("il", new String[]{"insured_id"}, TPolicyCt.class, "pc", new String[]{"insured_id"})
		.addCondition("pg.policy_id in (8769470,2804125)");
	
	public static void testDBUtils() throws Exception{
		Connection conn = DBUtil.getConnection().getConnection();
		System.out.println("-----------testSQLUtils---------------");
		
		String sql = "select a.* from t_policy_general a,t_insured_list b where a.policy_id=b.policy_id and a.policy_id in (2804125,8874838)";
		List<TPolicyGeneral> list = DBUtils.list(conn, sql, TPolicyGeneral.class);

		TPolicyGeneral g = list.get(0);
		System.out.println("g="+g);
		System.out.println("---------------");
		sql = "select a.* from t_policy_general a,t_insured_list b where a.policy_id=b.policy_id and a.policy_id in (2804125)";
		TPolicyGeneral dd = DBUtils.load(conn, sql, TPolicyGeneral.class);
		System.out.println("dd="+dd);
		System.out.println("---------------");
		List<Map> m = DBUtils.list2Map(conn, sql);
		//System.out.println(list.size());
		System.out.println(m.get(0).get("quote_no"));
		System.out.println(m.get(0).get("exp_date"));
		System.out.println(m.get(0).get("sgp"));
		System.out.println(m.get(0).get("in_test"));
		System.out.println("---------------");
		
		sql = "select mp_contract_id from t_policy_general where policy_id=2804125";
		Long testNull = DBUtils.load(conn, sql, Long.class);
		System.out.println("test null="+testNull);
		sql = "select renew_date from t_policy_general where policy_id=2804125";
		java.util.Date d = DBUtils.load(conn, sql, java.util.Date.class);
		System.out.println("test null="+d);
		
		sql = "select eff_date from t_policy_general where policy_id=2804125";
		Timestamp ts = DBUtils.load(conn, sql, Timestamp.class);
		System.out.println("test timestamp="+ts);
		System.out.println("---------------");
		String dt = DBUtils.load(conn, sql,String.class);
		System.out.println("test data type handler="+dt);
		
		long seq = DBUtils.getSequence(conn, "s_prdt_ct__ct_id");
		System.out.println(seq);
		
		sql = "update t_policy_general set eff_date=? where policy_id=?";
		List params = new ArrayList();
		params.add(new java.sql.Timestamp(new Date().getTime()));
		params.add(2804125L);
		//int ret = SQLUtils.update(conn, sql, params);
		System.out.println("------------------------------------------------");
		
		sql = "select doc_list_id,ccm_doc_url from t_document_content where doc_list_id=5401";
		TDocumentContent doc = DBUtils.load(conn, sql, new DBUtils.RowLoader<TDocumentContent>(){
			public void load(TDocumentContent obj, ResultSet rs)
					throws SQLException {
				obj.setCcmDocUrl(rs.getString("ccm_doc_url"));
				obj.setDocListId(rs.getLong("doc_list_id"));
			}
			public Class resultClass() {
				return TDocumentContent.class;
			}
			
		}); 
		System.out.println("test RowLoader="+doc);
		System.out.println("------------------------------------------------");
		
		final List ss = new ArrayList();
		doc = DBUtils.load(conn, sql, new DBUtils.ColumnLoader<TDocumentContent>() {
			public Class resultClass() {
				return TDocumentContent.class;
			}
			public boolean load(TDocumentContent obj,String columnName, ResultSet rs) throws SQLException {
				if("ccm_doc_url".equalsIgnoreCase(columnName)){
					ss.add(rs.getString(columnName)+" ===test");
					return true;
				}
				return false;
			}
		});
		System.out.println("test ColumnLoader="+ss);
		System.out.println("------------------------------------------------");
		
		System.out.println(q.toSQL());
		List<TPolicyGeneral> policy = q.list(conn);
		/*for(TPolicyGeneral pg:policy){
			System.out.println(pg);
			for(TInsuredList il:pg.getTInsuredList()){
				System.out.println("  "+il);
				for(TPolicyCt pc:il.getTPolicyCt()){
					System.out.println("    "+pc);
				}
			}
		}
	}
	*/
	private final static String PACKAGE_BO = "com.stk123.bo"; 
	public static void createSourceCode() throws IOException{
		Table t = Table.getInstance("stk");
		/*t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_pe");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_kline");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_industry_rank");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_internet_search");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_trans_account");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_organization");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_keyword");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_text");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_kline_rank_industry");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_industry_type");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_data_ppi_type");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_monitor");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_dictionary");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_report_daily");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_keyword_link");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_label");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		t = Table.getInstance("stk_label_text");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		
		t = Table.getInstance("stk_user");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_index_node");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_sync_table");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_investigation");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_care");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_restricted");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_capital_flow");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_xueqiu_user");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		
		t = Table.getInstance("stk_us_search_view");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_search_mview");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);			
		
		t = Table.getInstance("stk_search_condition");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
		t = Table.getInstance("stk_strategy");
		t.codePOJOBean("./src", PACKAGE_BO,true,true,true);*/

        t = Table.getInstance("stk_industry_type");
        t.codePOJOBean("./src", PACKAGE_BO,true,true,true);
		
	}
	
	
	

}

