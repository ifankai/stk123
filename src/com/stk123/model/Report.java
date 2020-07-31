package com.stk123.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.tags.TableTag;

import com.stk123.bo.Stk;
import com.stk123.bo.StkFnData;
import com.stk123.bo.StkIndustryType;
import com.stk123.bo.StkKline;
import com.stk123.bo.cust.StkFnDataCust;
import com.stk123.task.StkUtils;
import com.stk123.tool.db.TableTools;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.HtmlUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;


@SuppressWarnings({ "unchecked" })
public class Report {
	
	public static final Date now = new Date();
	public static List<Stk> indexs = new ArrayList<Stk>();

	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			
			/*Index index = new Index(conn,"600140");
			System.out.println(index.getCloseChange(StkUtils.getToday(), 250));
			System.out.println(index.getNetProfitGrowthAverageValue(2,20));
			System.out.println(index.finance());
			System.out.println(index.getNetProfitByOneQuarter("20120331"));
			System.out.println(index.getPEByTTM("20120908"));

			//index.init();
			
			double closeChange = index.getCloseChange("20130311", 250);
			System.out.println("change:"+closeChange);
			*/
			long start = System.currentTimeMillis();
			String today = StkUtils.getToday();
			
			//股票池(成长池)财务分析
			//financeReport(conn, false);
			
			//成长股平均PE
			//reportGrowthPE(conn, today);
			
			//行业景气度分析
			//industryReport(conn);

			//2013 季度业绩预测  http://data.cfi.cn/cfidata.aspx
			//financeForecast(conn, "A0A1934A1939A1940A4553A4554");
			
			//2013 季度业绩
			//financeActual(conn,"20130331");
			
			//账户统计
			//chinaclear();
			
			//testRS(conn);
			//createHistoryHigh(conn);
			
			//updateKfromXueQiu(conn);
			
			Index index2 = new Index(conn,"600278");
			List<K> ks = index2.getKsHistoryHighPoint("20130809", 100, 10);
			for(K k : ks){
				System.out.println(k.getDate()+","+k.getHigh());
			}
			K k0 = ks.get(ks.size()-2);
			K k1 = ks.get(ks.size()-1);
			System.out.println(index2.getDaysBetween(k0.getDate(), k1.getDate()));
			double gap = (k0.getHigh()-k1.getHigh())/36;
			System.out.println(gap);
			K tmp = k1.after(3);
			double tmp2 = k1.getHigh()-gap*3;
			do{
				if(tmp.getHigh() >= (tmp2=tmp2-gap)){
					System.out.println(tmp.getDate()+","+tmp.getHigh()+","+tmp2);
					break;
				}
				tmp = tmp.after(1);
			}while(true);
			//initHsl(conn);
			
			//check(conn);
			
			long end = System.currentTimeMillis();
			System.out.println("time:"+((end-start)/1000D)+"s");
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	public static void check(Connection conn) throws Exception{
		String today = StkUtils.getToday();
		List<String> results = new ArrayList<String>();
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
		for(Stk stk : stks){
			try{
				Index index = new Index(conn,stk.getCode(),stk.getName());
				//System.out.println(index.getCode());
				double ma5 = index.getKValue(today, K.Close, K.MA, 5);
				double ma10 = index.getKValue(today, K.Close, K.MA, 10);
				double ma20 = index.getKValue(today, K.Close, K.MA, 20);
				double ma30 = index.getKValue(today, K.Close, K.MA, 30);
				double ma60 = index.getKValue(today, K.Close, K.MA, 60);
				double ma120 = index.getKValue(today, K.Close, K.MA, 120);
				//System.out.println(ma5+","+ma10+","+ma20+","+ma30+","+ma60+","+ma120);
				double maMax = ma5;
				double maMin = ma5;
				if(ma10 > maMax)maMax = ma10;
				if(ma10 < maMin)maMin = ma10;
				if(ma20 > maMax)maMax = ma20;
				if(ma20 < maMin)maMin = ma20;
				if(ma30 > maMax)maMax = ma30;
				if(ma30 < maMin)maMin = ma30;
				if(ma60 > maMax)maMax = ma60;
				if(ma60 < maMin)maMin = ma60;
				if(ma120 > maMax)maMax = ma120;
				if(ma120 < maMin)maMin = ma120;
				if(maMin * 1.1 >= maMax /*&& (index.isGrowth() || index.isPotential())*/){
					//System.out.println(maMin+","+maMax);
					K maxK = index.getK(today).getMax(K.Hsl, 300, K.SUM, 30);
					double maxHsl = maxK.getValue(K.Hsl, K.SUM, 30);
					//System.out.println("max="+maxK.getDate()+","+maxHsl);
					if(maxHsl >= 400 
							&& StkUtils.getDaysBetween(StkUtils.sf_ymd2.parse(today), StkUtils.sf_ymd2.parse(maxK.getDate())) > 60){
						System.out.println(index.getCode()+","+index.getName());
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		for(String s : results){
			System.out.println(s);
		}
	}
	
	public static void checkReversePriceAndHsl(Connection conn) throws Exception{
		int i = 0;
		List<String> results = new ArrayList<String>();
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn where code>'000544' order by code", Stk.class);
		for(Stk stk : stks){
			try{
				Index index = new Index(conn,stk.getCode(),stk.getName());
				System.out.println(index.getCode());
				K k = index.getK("20130906");
				double hsl = k.getValue(K.Hsl, K.SUM, 30);
				System.out.println("20130906,"+hsl+","+k.getClose());
				List<K> ks = index.getKsHistoryHighPoint("20130906", 250, 30);
				if(ks != null && ks.size() > 0){
					String highDate = ks.get(ks.size()-1).getDate();
					K k2 = index.getK(highDate);
					double hsl2 = k2.getValue(K.Hsl, K.SUM, 30);
					System.out.println(k2.getDate()+","+hsl2+","+k2.getHigh());
					
					if(hsl2 >= hsl && k2.getClose() <= k.getHigh() && k.before(1).getClose() < k2.getClose()
							&& k.getClose() >= k.getValue(K.Close, K.MA, 10)){
						System.out.println(index.getCode()+"....."+(++i));
						results.add(index.getCode()+","+index.getName()+","+k2.getDate());
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		for(String s : results){
			System.out.println(s);
		}
	}
	
	//修复某天K线同步失败
	public static void updateKfromXueQiu(Connection conn) throws Exception{
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
		for(Stk stk : stks){
			Index index = new Index(conn,stk.getCode(),stk.getName());
			System.out.println(index.getCode());
			List<String> yyyyMMdds = new ArrayList<String>();
			yyyyMMdds.add("20130919");
			yyyyMMdds.add("20130920");
			index.initKLinesFromXueQiu("20130919");
		}
	}
	
	public static void initHsl(Connection conn){
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
		for(Stk stk : stks){
			try{
				Index index = new Index(conn,stk.getCode(),stk.getName());
				System.out.println(index.getCode());
				//Thread.currentThread().sleep(1000);
				//index.getHighPoints("20130820", 250, 30);
				index.initHsl();
				index.gc();
				index = null;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void createHistoryHigh(Connection conn) throws Exception {
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
		List<Index> results = new ArrayList<Index>();
		for(Stk stk : stks){
			try{
				Index index = new Index(conn,stk.getCode(),stk.getName());
				if(index.getKs().size() >= 250){
					K startK = index.getK(StkUtils.getToday(), 250);
					K highK = index.getKByHHV(startK.getDate(), StkUtils.getToday());
					K todayK = index.getK();
					K yesterdayK = index.getK(1);
					K last20dayK = index.getK(20);
					if(highK.getDate().compareTo(last20dayK.getDate()) <= 0
							&& todayK.getClose() >= highK.getClose() 
							&& yesterdayK.getClose() < highK.getClose()){//创新高
						K lowK = index.getKByLLV(startK.getDate(), StkUtils.getToday());
						if(lowK.getClose()*1.6 >= highK.getClose()){//箱体
							results.add(index);
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(stk.getCode());
			}
		}
		for(Index index : results){
			System.out.println(index.getCode()+","+index.getName()+","+index.getK().getClose());
		}
		
	}
	
	public static void testRS(Connection conn) throws Exception {
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn where code like '300%' order by code", Stk.class);
		IndexContext context = new IndexContext();
		for(Stk stk : stks){
			Index index = new Index(conn,stk.getCode(),stk.getName());
			context.indexs.add(index);
		}
		int cnt = 50;
		IndexUtils.sortByCloseChange(context.indexs, StkUtils.getToday(), 20);
		List<Index> rs = new ArrayList<Index>();
		for(Index index : context.indexs){
			if(cnt-- == 0)break;
			rs.add(index);
			System.out.println(index.getCode()+","+index.getName()+","+index.getCanslim().getCloseChange());
		}
		Collections.sort(context.indexs, new Comparator<Index>(){
			public int compare(Index arg0, Index arg1) {
				try {
					if(arg1.getK().getClose() > arg0.getK().getClose()){
						return 1;
					}else if(arg1.getK().getClose() == arg0.getK().getClose()){
						return 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return -1;
			}
		});
		cnt = 50;
		List<Index> results = new ArrayList<Index>();
		for(Index index : context.indexs){
			if(cnt-- == 0)break;
			if(rs.contains(index)){
				results.add(index);
			}
			System.out.println(index.getCode()+","+index.getName()+","+index.getK().getClose());
		}
		for(Index index : results){
			System.out.println(index.getCode()+","+index.getName()+","+index.getK().getClose()+","+index.getCanslim().getCloseChange());
		}
	}
	
	
	public static void financeForecast(Connection conn, String url) throws Exception {
		List<List<String>> datas = new ArrayList<List<String>>();
		boolean flag = true;
		int curpage = 1;
		while(flag){
			String page = HttpUtils.get("http://data.cfi.cn/cfidata.aspx?sortfd=%e9%a2%84%e5%91%8a%e7%b1%bb%e5%9e%8b&sortway=desc&fr=content&ndk="+url+"&xztj=&mystock=&curpage="+curpage, null, "gbk");
			Node table = HtmlUtils.getNodeByAttribute(page, "", "class", "table_data");
			List<List<String>> tmp = HtmlUtils.getListFromTable((TableTag)table, 0);
			for(List data:tmp){
				if("预亏".equals(data.get(3))){
					flag = false;
					break;
				}
				datas.add(data);
			}
			curpage ++;
		}
		for(List data:datas){
			Index index = new Index(conn, String.valueOf(data.get(1)));
			String desc = String.valueOf(data.get(4));
			desc = desc.replaceAll(",", "");
			data.set(4, desc);
			if(StringUtils.indexOf(desc, "万元") > 0){
				data.add(StringUtils.substringBetween(desc, "净利润", "万").replaceAll("约", "").replaceAll(",", "") );
			}else if(StringUtils.indexOf(desc, "万元") <= 0 && StringUtils.indexOf(desc, "亿") > 0){
				String value = StringUtils.substringBetween(desc, "净利润", "亿").replaceAll("约", "").replaceAll(",", "");
				data.add( Double.parseDouble(value)*10000 );
			}else if(StringUtils.indexOf(desc, "万元") <= 0 && StringUtils.indexOf(desc, "元") > 0){
				String value = StringUtils.substringBetween(desc, "净利润", "元").replaceAll("约", "").replaceAll(",", "");
				data.add( Double.parseDouble(value)/10000 );
			}else{
				String percent = StringUtils.substringBetween(desc, "同比增长", "%");
				if(percent != null){
					percent = percent.replaceAll("约", "");
				}
				data.add("0");
			}
			String fnDate = (StkUtils.YEAR-1)+"1231";
			Map<String,StkFnDataCust> fnData = index.getFnDataByDate(fnDate);
			if(fnData != null && fnData.get(index.FN_JLR) != null){
				double d = Double.parseDouble(String.valueOf(data.get(5)))/10000/fnData.get(index.FN_JLR).getFnValue() * 100;
				data.add(StkUtils.number2String(d,2));
			}else{
				data.add("0");
			}
			data.add(StringUtils.join(index.getIndustryName(), "/"));
		};
		Collections.sort(datas, new Comparator(){
			public int compare(Object arg0, Object arg1) {
				double d0 = Double.parseDouble(String.valueOf(((List)arg0).get(6)));
				double d1 = Double.parseDouble(String.valueOf(((List)arg1).get(6)));
				return (int)((d1-d0)*100);
			}
		});
		for(List data:datas){
			System.out.println(StringUtils.replaceEach(data.toString(), new String[]{"[","]"," "}, new String[]{"","",""}));
		}
	}
	
	public static void financeActual(Connection conn, String date) throws Exception {
		List<List> datas = new ArrayList<List>();
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
		for(Stk stk : stks){
			Index index = new Index(conn,stk.getCode());
			Map<String,StkFnDataCust> fnData = index.getFnDataByDate(date);
			if(fnData == null)continue;
			if(fnData.get(index.FN_JLR) == null)continue;
			String year = StkUtils.formatDate(StkUtils.sf_ymd2.parse(date), StkUtils.sf_yyyy);
			//String MMdd = StkUtils.formatDate(StkUtils.sf_ymd2.parse(date), StkUtils.sf_MMdd);
			String lastDate = (Integer.parseInt(year)-1)+"1231";
			Map<String,StkFnDataCust> lastFnData = index.getFnDataByDate(lastDate);
			if(lastFnData.get(index.FN_JLR) == null)continue;
			if(fnData.get(index.FN_JLR).getFnValue().doubleValue() > 0 &&
				lastFnData.get(index.FN_JLR).getFnValue().doubleValue() > 0){
				List<String> data = new ArrayList<String>();
				data.add(StkUtils.formatDate(fnData.get(index.FN_JLR).getInsertTime()));
				data.add(index.getCode());
				data.add(index.getStk().getName());
				data.add(String.valueOf(fnData.get(index.FN_JLR).getFnValue()));
				data.add(String.valueOf(lastFnData.get(index.FN_JLR).getFnValue()));
				data.add(StkUtils.number2String(fnData.get(index.FN_JLR).getFnValue() / lastFnData.get(index.FN_JLR).getFnValue() * 100,2));
				data.add(StringUtils.join(index.getIndustryName(), "/"));
				datas.add(data);
			}
		}
		Collections.sort(datas, new Comparator(){
			public int compare(Object arg0, Object arg1) {
				double d0 = Double.parseDouble(String.valueOf(((List)arg0).get(5)));
				double d1 = Double.parseDouble(String.valueOf(((List)arg1).get(5)));
				return (int)((d1-d0)*100);
			}
		});
		for(List data:datas){
			System.out.println(StringUtils.replaceEach(data.toString(), new String[]{"[","]"," "}, new String[]{"","",""}));
		}
	}
	
	public static void chinaclear() throws Exception{
		int curpage = 1;
		while(true){
			String page = HttpUtils.get("http://www.chinaclear.cn/main/03/0303/030305/030305_"+curpage+".html", null, "GBK");
			if("404".equals(page))break;
			Node table = HtmlUtils.getNodeByAttribute(page, null, "id", "listbody");
			List<Node> links = HtmlUtils.getNodeListByTagName(table, "a");
			for(Node link:links){
				page = HttpUtils.get("http://www.chinaclear.cn"+((Tag)link).getAttribute("href"), null, "GBK");
				String date = StringUtils.substringBetween(page, "一周股票账户情况统计表（","-");
				Node tab = HtmlUtils.getNodeByAttribute(page, null, "class", "MsoNormalTable");
				List<List<String>> datas = HtmlUtils.getListFromTable((TableTag)tab, 0);
				System.out.println(StringUtils.replace(date, ".", "/")+","+trim(datas.get(0).get(3))+","+trim(datas.get(2).get(3))+","+trim(datas.get(10).get(3))+","+trim(datas.get(11).get(3)));
			}
			curpage++;
		}
	}
	private static String trim(String s){
		return StringUtils.replace(StringUtils.replace(s, "&nbsp;", ""),",","").trim();
	}
	
	/**
	 * @param onlyGrowing 如果是true，只查成长性股票，如果是false，则是全部
	 */
	public static void financeReport(Connection conn, boolean onlyGrowing) throws Exception{
		List<Stk> stks = JdbcUtils.list(conn, "select s.code,s.name from stk_cn s,stk_industry i,stk_industry_type t where s.market=1 and s.code=i.code and i.industry=t.id and t.source='wind' order by t.id,s.code", Stk.class);
		List<List<String>> indexs = new ArrayList<List<String>>();
		for(Stk stk : stks){
			try {
				Index index = new Index(conn,stk.getCode());
				double value = 0;
				if(onlyGrowing){
					if((value = index.getNetProfitGrowthAverageValue(10,10)) == 0
							/*&& !index.valuationByGrowing(Index.FN_ZYSRZZL)*/){
						continue;
					}
				}
				List<String> e = new ArrayList<String>();
				e.add(index.finance());
				e.add(String.valueOf(value));
				indexs.add(e);
			}catch(Exception e){
				System.out.println("error:"+stk.getCode());
				e.printStackTrace();
				throw e;
			}
		}
		if(onlyGrowing){
			Collections.sort(indexs, new Comparator<List<String>>(){
				public int compare(List<String> arg0, List<String> arg1) {
					double d0 = Double.parseDouble(arg0.get(1));
					double d1 = Double.parseDouble(arg1.get(1));
					return (int)((d1-d0)*100);
				}
			});
		}
		for(List<String> index:indexs){
			System.out.println(index.get(0));
		}
		System.out.println("total:"+indexs.size());
	}
	
}

