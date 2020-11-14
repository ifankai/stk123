//package com.stk123.tool.ml;
//
//import java.sql.Connection;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//import org.apache.commons.lang.ArrayUtils;
//
//import com.stk123.bo.Stk;
//import com.stk123.model.Index;
//import com.stk123.model.K;
//import com.stk123.tool.util.StkUtils;
//import com.stk123.tool.db.util.DBUtil;
//import com.stk123.tool.util.JdbcUtils;
//import com.stk123.tool.util.collection.Name2Value;
//
//public class StkDtw {
//
//	public static void main(String[] args) throws Exception {
//
//		Connection conn = null;
//		try {
//			conn = DBUtil.getConnection();
//
//			String codes = "";
//			String sql = null;
//			if(codes != null && codes.length() > 0){
//				sql = "select code,name from stk where market=1 and code in ("+codes+") order by code";
//			}else{
//				sql = "select code,name from stk where market=1 and cate in (1) and code>=601001 order by code";
//			}
//			List<Stk> stks = JdbcUtils.list(conn, sql, Stk.class);
//			List<Index> indexs = new ArrayList<Index>();
//
//			String today = StkUtils.getToday();
//			//today = "20151104";
//			Index.KLineWhereClause = Index.KLINE_20140101;
//
//			Index templateIndex = new Index(conn, "601117");
//
//			String startDate = "20170523";
//			String endDate = "20170711";
//			double[] Y = StkDtw.getTemplate(templateIndex, startDate, endDate);
//			for(double y : Y){
//				System.out.print(y+", ");
//			}
//			System.out.println();
//			List<Name2Value<Double,String>> results = new ArrayList<Name2Value<Double,String>>();
//			for(Stk stk : stks){
//				try{
//					Index index = new Index(conn, stk.getCode());
//					double[] X = StkDtw.getTemplate(index, startDate, "20170712");
//					DTW dtw = new DTW(X, Y);
//					double distance = dtw.getDistance();
//					results.add(new Name2Value(distance, index.getName()));
//				}catch(Exception e){
//					System.err.println(stk.getCode());
//				}
//			}
//			Collections.sort(results, new Comparator<Name2Value<Double,String>>(){
//				@Override
//				public int compare(Name2Value<Double,String> arg0, Name2Value<Double,String> arg1) {
//					// TODO Auto-generated method stub
//					return (int)((arg0.getName()-arg1.getName())*1000);
//				}
//
//			});
//			int i = 0;
//			for(Name2Value nv : results){
//				System.out.println(nv.getName()+","+nv.getValue());
//				if(i++ > 10)break;
//			}
//		}finally{
//			if (conn != null) conn.close();
//		}
//	}
//
//	public static double[] getTemplate(Index index, String startDate, String endDate) throws Exception {
//		List<Double> ds = new ArrayList();
//		K k = index.getK(startDate);
//		double d = k.getClose();
//		K endK = index.getK(endDate);
//		do{
//			ds.add((k.getClose()/d-1)*100);
//			k = k.getAfter();
//			if(k == null || k.dateAfter(endK)){
//				break;
//			}
//		}while(true);
//		return ArrayUtils.toPrimitive(ds.toArray(new Double[ds.size()]));
//	}
//
//}
