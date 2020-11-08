/*
package com.stk123.tool.ml;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.dtw.TimeWarpInfo;
import com.stk123.bo.Stk;
import com.stk123.model.Index;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.collection.Name2Value;
import com.timeseries.TimeSeries;
import com.timeseries.TimeSeriesPoint;
import com.util.DistanceFunction;
import com.util.DistanceFunctionFactory;

public class StkTimeSeries extends TimeSeries {
	
	int kCount = 0;
	
	public static void main(String[] args) throws Exception {
		//TimeSeries ts = new TimeSeries("d:\\ts.csv", true, true, ',');
		//System.out.println(ts);
		
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String colToInclude = "volumn";
			TimeSeries tsI = new StkTimeSeries(conn, "600516", "20170418", "20170522",colToInclude);
			//TimeSeries tsI = new StkTimeSeries(conn, "300176", "20160330", "20170208",colToInclude);
			//System.out.println(tsI);
			*/
/*TimeSeries tsJ = new StkTimeSeries(conn, "601229", "20170523", "20170712",null);
			
			final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
			final TimeWarpInfo info = com.dtw.DTW.getWarpInfoBetween(tsI, tsJ, distFn);
			
			System.out.println("=====================");
	        System.out.println("Warp Distance: " + info.getDistance());
	        System.out.println("Warp Path:     " + info.getPath());*//*

	        
	        String codes = "";
	        String sql = null;
	        if(codes != null && codes.length() > 0){
				sql = "select code,name from stk where market=1 and code in ("+codes+") order by code";
			}else{
				sql = "select code,name from stk where market=1 and cate = 1 and code>=000001 order by code";
			}
			List<Stk> stks = JdbcUtils.list(conn, sql, Stk.class);
	        List<Name2Value<Double,String>> results = new ArrayList<Name2Value<Double,String>>();
	        String today = StkUtils.getToday();
			for(Stk stk : stks){
				try{
					//Index index = new Index(conn, stk.getCode());
					//if(index.isStop(StkUtils.getToday()))continue;
					TimeSeries tsJ = new StkTimeSeries(conn, stk.getCode(), "20170620", today, colToInclude);
					final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
					final TimeWarpInfo info = com.dtw.DTW.getWarpInfoBetween(tsI, tsJ, distFn);
					double distance = info.getDistance();
					results.add(new Name2Value(distance, stk.getCode()));
				}catch(Exception e){
					//System.err.println(stk.getCode());
				}
			}
			Collections.sort(results, new Comparator<Name2Value<Double,String>>(){
				@Override
				public int compare(Name2Value<Double,String> arg0, Name2Value<Double,String> arg1) {
					// TODO Auto-generated method stub
					return (int)((arg0.getName()-arg1.getName())*1000);
				}
				
			});
			int i = 0;
			
			List<String> idxs = new ArrayList<String>();
			for(Name2Value<Double,String> nv : results){
				Index idx = new Index(conn, nv.getValue());
				//if(idx.isStop(today))continue;
				if(i++<=10){
					System.out.println(nv.getName()+","+idx.getName()+"["+idx.getCode()+"]");
				}
				if(i > 300){
					break;
				}
				idxs.add(nv.getValue());
			}
			System.out.println("============================"+idxs.size());
			
			results.clear();
			colToInclude = "close";
			tsI = new StkTimeSeries(conn, "600343", "20170602", "20170706",colToInclude);
			for(String code : idxs){
				try{
					//Index index = new Index(conn, stk.getCode());
					//if(index.isStop(StkUtils.getToday()))continue;
					TimeSeries tsJ = new StkTimeSeries(conn, code, "20170601", today, colToInclude);
					final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
					final TimeWarpInfo info = com.dtw.DTW.getWarpInfoBetween(tsI, tsJ, distFn);
					double distance = info.getDistance();
					results.add(new Name2Value(distance, code));
				}catch(Exception e){
					//System.err.println(stk.getCode());
				}
			}
			Collections.sort(results, new Comparator<Name2Value<Double,String>>(){
				@Override
				public int compare(Name2Value<Double,String> arg0, Name2Value<Double,String> arg1) {
					// TODO Auto-generated method stub
					return (int)((arg0.getName()-arg1.getName())*1000);
				}
				
			});
			i = 0;
			for(Name2Value<Double,String> nv : results){
				Index idx = new Index(conn, nv.getValue());
				//if(idx.isStop(today))continue;
				if(i++<=10){
					System.out.println(nv.getName()+","+idx.getName()+"["+idx.getCode()+"]");
				}else{
					break;
				}
				
			}
		}finally{
			if (conn != null) conn.close();
		}
	}
	
	public StkTimeSeries(Connection conn, String code, String startDate, String endDate,
			String colToInclude) {
		super();
		boolean isFirstColTime = true;

		StringBuffer sql = new StringBuffer();
		sql.append("with firstrow as "); 
		sql.append("(select * from (select sk.*,rownum num from stk_kline sk where sk.code=? and sk.kline_date between ? and ? order by kline_date asc) a where a.num=1) ");
		sql.append("select k.kline_date time ");
		sql.append(",(avg(k.close_change) over (order by k.kline_date desc rows between 0 following and 4 following)/f.close_change-1)*100 close ");
		//sql.append(",(avg(k.volumn) over (order by k.kline_date desc rows between 0 following and 4 following)/f.volumn-1)*10 volumn ");
		sql.append(",case when k.open>=k.close then k.volumn/f.volumn else -k.volumn/f.volumn end volumn");
		sql.append(",(case when k.close>k.open then 1 when k.close<k.open then -1 else 0 end) change ");
		sql.append(",(k.close-k.open)/k.open*100 close_change ");
		sql.append(",(k.high/f.high-1)*100 high");
		sql.append(",(k.low/f.low-1)*100 low");
		sql.append(" from stk_kline k, firstrow f  ");
		sql.append("where k.code=f.code and k.kline_date between ? and ? order by k.kline_date");
		
		List params = new ArrayList();
		params.add(code);
		params.add(startDate);
		params.add(endDate);
		params.add(startDate);
		params.add(endDate);
		List<Map> datas = JdbcUtils.list2Map(conn, sql.toString(), params);
		
		kCount = datas.size();
		Map<String, Object> firstRow = datas.get(0);

		if ((colToInclude == null) || (colToInclude.length() == 0)) {
			super.labels.add("Time");
			for(int currentCol = 1; currentCol < firstRow.size(); currentCol++) {
				labels.add(new String("c" + currentCol++)); 
			}
		} else {
			colToInclude = colToInclude.toUpperCase();
			String[] colToIncludes = colToInclude.split(",");
			labels.add("Time");
			for (int c = 0; c < colToIncludes.length; c++)
				labels.add(new String("c" + c));
		}

		for (Map<String, Object> row : datas)
		{
				final ArrayList currentLineValues = new ArrayList();
				if(colToInclude == null || colToInclude.length() == 0){
					currentLineValues.addAll(row.values());
				}else{
					String[] colToIncludes = colToInclude.split(",");
					currentLineValues.add(row.get("TIME"));
					for(String col : colToIncludes){
						currentLineValues.add(row.get(col));
					}
				}

				if (isFirstColTime)
					timeReadings.add(currentLineValues.get(0));
				else
					timeReadings.add(new Double(timeReadings.size()));
				final int firstMeasurement;
				if (isFirstColTime)
					firstMeasurement = 1;
				else
					firstMeasurement = 0;
				final TimeSeriesPoint readings = new TimeSeriesPoint(
						currentLineValues.subList(firstMeasurement, currentLineValues.size()));
				tsArray.add(readings);
		} 

	}
}
*/
