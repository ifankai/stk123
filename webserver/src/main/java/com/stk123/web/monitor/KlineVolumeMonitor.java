package com.stk123.web.monitor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.tags.TableTag;

import com.stk123.model.bo.Stk;
import com.stk123.model.bo.StkMonitor;
import com.stk123.model.Index;
import com.stk123.model.Industry;
import com.stk123.model.K;
import com.stk123.service.ServiceUtils;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.ConfigUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.service.ExceptionUtils;
import com.stk123.common.util.HtmlUtils;
import com.stk123.service.HttpUtils;
import com.stk123.common.util.JdbcUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.common.util.ListUtils;


//14:50 trigger every working day.
public class KlineVolumeMonitor {
	
	//实时监控数据 -- volume
	public static void main(String[] args) throws Exception {
		System.out.println(KlineVolumeMonitor.class.getName());
		ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
		Connection conn = null;
		try {
			long start = System.currentTimeMillis();
			conn = DBUtil.getConnection();
			List<StkMonitor> sms = JdbcUtils.list(conn, "select * from stk_monitor where type=1 and trigger_date is null order by insert_date asc", StkMonitor.class);
			List<String> results = new ArrayList<String>();
			List<String> resultsEne = new ArrayList<String>();
			for(StkMonitor sm : sms){
				Index index = new Index(conn, sm.getCode());
				System.out.println(index.getCode());
				if(index.getMarket() != 1)continue;
				String result = null;
				if("5".equals(sm.getParam1())){
					result = Monitor.getInstance(sm).execute(sm,conn,false);
				}else{
					result = parseVolume(index);
				}
				if(result != null){
					addResult(results,index,"[监控股]"+result);
				}
				//Ene
				parseEne(conn,index,"[监控股]",resultsEne);
			}
			List<Index> indexs = new ArrayList<Index>();
			List<Stk> careStks = JdbcUtils.list(conn, "select code,name from stk_cn where status=1", Stk.class);
			for(Stk stk : careStks){
				indexs.add(new Index(conn, stk.getCode()));
			}
			for(Index index : indexs){
				String result = parseVolume(index);
				if(result != null){
					addResult(results,index,"[自选股]"+result);
				}
				//Ene
				parseEne(conn,index,"[自选股]",resultsEne);
			}
			results.add("<br>");
			resultsEne.add("<br>");
			//业绩预增  >= 50%
			indexs = getPerformanceGrow(conn);
			for(Index index : indexs){
				String result = parseVolume(index);
				if(result != null){
					addResult(results,index,"[业绩预增股]"+result);
				}
				//Ene
				parseEne(conn,index,"[业绩预增股]",resultsEne);
			}
			results.add("<br>");
			resultsEne.add("<br>");
			//盈利预测增长大于80%
			indexs = getEarningForeast(conn);
			for(Index index : indexs){
				String result = parseVolume(index);
				if(result != null){
					addResult(results,index,"[盈利预测增长股]"+result);
				}
				//Ene
				parseEne(conn,index,"[盈利预测增长股]",resultsEne);
			}
			results.add("<br>");
			resultsEne.add("<br>");
			
			
			//反转股
			Industry ind = Industry.getIndustry(conn, "1782");
			indexs = ind.getIndexs();
			for(Index index : indexs){
				String result = parseVolume(index);
				if(result != null){
					addResult(results,index,"[反转股]"+result);
				}
				//Ene
				parseEne(conn,index,"[反转股]",resultsEne);
			}
			results.add("<br>");
			resultsEne.add("<br>");
			
			//成长股
			ind = Industry.getIndustry(conn, "1783");
			indexs = ind.getIndexs();
			for(Index index : indexs){
				String result = parseVolume(index);
				if(result != null){
					addResult(results,index,"[成长股]"+result);
				}
				//Ene
				parseEne(conn,index,"[成长股]",resultsEne);
			}
			results.add("<br>");
			resultsEne.add("<br>");
			//潜力股
			ind = Industry.getIndustry(conn, "1781");
			indexs = ind.getIndexs();
			for(Index index : indexs){
				String result = parseVolume(index);
				if(result != null){
					addResult(results,index,"[潜力股]"+result);
				}
				//Ene
				parseEne(conn,index,"[潜力股]",resultsEne);
			}
			long end = System.currentTimeMillis();
			System.out.println("time:"+((end-start)/1000D)+"s");
			if(results.size()-5 > 0){
				EmailUtils.send("数据监控-量能，总数："+(results.size()-5), StringUtils.join(results, "<br/>"));
			}
			if(resultsEne.size()-5 > 0){
				EmailUtils.send("数据监控-ENE，总数："+(resultsEne.size()-5), StringUtils.join(resultsEne, "<br/>"));
			}
		}catch(Exception e){
			EmailUtils.send("量能监控出错", e);
			e.printStackTrace();
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	private static Set stks = new HashSet();
	
	public static String parseVolume(Index index) throws Exception{
		if(stks.contains(index.getCode()))return null;
		stks.add(index.getCode());
		String page = HttpUtils.get("http://hq.sinajs.cn/list="+(index.getLoc()==1?"sh":"sz")+index.getCode(), null, "");
		//System.out.println(page);
		String str = StringUtils.substringBetween(page, "=\"", "\";");
		if(str != null){
			String[] ss = str.split(",");
			if(ss.length < 8)return null;
			double v = Double.parseDouble(ss[8]);
			if(v == 0)return null;
			if(index.getKs().size() >= 60){
				List<K> sub = index.getKs().subList(0, 60);
				ListUtils.getTopN(sub, 4, new ListUtils.Get() {
					public double get(Object o) {
						K k = (K)o;
						return -k.getVolumn();
					}
				});
				//System.out.println(v);
				/*for(K k : sub){
					System.out.println(k.getDate()+","+k.getVolumn());
				}*/
				int trigger = 0;
				for(K k : sub){
					double volume = k.getVolumn();
					if(volume >= v){
						trigger++;
					}
				}
				if(trigger > 0){
					//Ene ene = index.getK().getEne(index);
					StringBuffer sb = new StringBuffer();
					sb.append(index.getName()+"["+ServiceUtils.wrapCodeLink(index.getCode())+"]");
					sb.append("量能"+v+"在60日内最小量排名第"+(5-trigger));
					return sb.toString();
				}
			}
		}
		return null;
	}
	
	//业绩预增排行
	public static List<Index> getPerformanceGrow(Connection conn) throws Exception {
		String page = HttpUtils.get("http://vip.stock.finance.sina.com.cn/q/go.php/vFinanceAnalyze/kind/performance/index.phtml?num=600&order=xiaxian%7C2", "GB2312");
		Node table = HtmlUtils.getNodeByAttribute(page, null, "id", "dataTable");
		List<List<String>> datas = HtmlUtils.getListFromTable((TableTag)table,0);
		List<Index> results = new ArrayList<Index>();
		for(List<String> data : datas){
			double percentige = ServiceUtils.percentigeGreatThan(data.get(7));
			boolean jk = "减亏".equals(data.get(2));
			if(percentige >= 50 && !jk){
				Index index = new Index(conn,data.get(0));
				results.add(index);
			}
		}
		return results;
	}
	
	//盈利预测排行 http://data.eastmoney.com/report/ylyc.html
	public static List<Index> getEarningForeast(Connection conn) throws Exception {
		//String page = HttpUtils.get("http://data.eastmoney.com/report/data.aspx?style=newylyc&field=IndustryID&bkcode=all&page=1&pageSize=5000&sortType=D&sortRule=-1&jsname=zupPdVyy&rt=46271865", null, "gb2312");
		String page = HttpUtils.get("http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=C._A&sty=GEMCPF&st=(AllNum)&sr=-1&p=2&ps=5000&cb=&js=&token=3a965a43f705cf1d9ad7e1a3e429d622&rt=47445483", null, "gb2312");
		List<String[]> sd = new ArrayList<String[]>();
		Map map = (Map)JsonUtils.getObject4Json("{\"data\":"+StringUtils.substringBetween(page, "(", ")")+"}", Map.class,null);
		List<String> data = (List)map.get("data");
		for(String str : data){
			//System.out.println(str);
			String[] ss = StringUtils.split(str,",");
			/*System.out.println(ss[10]);//2012
			System.out.println(ss[12]);//2013
			System.out.println(ss[14]);//2014
			System.out.println(ss[16]);//2015*/	
			if("-".equals(ss[12]) || Double.parseDouble(ss[12]) == 0)continue;
			if("-".equals(ss[14]))continue;
			if(ss.length < 15)continue;
			sd.add(ss);
		}
		System.out.println(sd.size());
		Collections.sort(sd, new Comparator(){
			public int compare(Object arg0, Object arg1) {
				String[] s1 = (String[])arg0;
				String[] s2 = (String[])arg1;
				double d1 = (Double.parseDouble(s1[14])-Double.parseDouble(s1[12]))/Double.parseDouble(s1[12]);
				double d2 = (Double.parseDouble(s2[14])-Double.parseDouble(s2[12]))/Double.parseDouble(s2[12]);
				return (int)((d2-d1)*100);
			}
			
		});
		List<Index> results = new ArrayList<Index>();
		for(String[] s : sd){
			double er = (Double.parseDouble(s[14])-Double.parseDouble(s[12]))/Double.parseDouble(s[12])*100;
			if(er >= 50){
				Index index = new Index(conn,s[1]);
				Stk stk = index.getStk();
				if(stk != null && stk.getTotalCapital()/10000 < 5){
					results.add(index);
				}
			}
			//System.out.println(s[0]+","+s[1]+","+s[12]+","+s[14]+",,"+(Double.parseDouble(s[14])-Double.parseDouble(s[12]))/Double.parseDouble(s[12])*100);
		}
		return results;
	}
	
	private static Set stksEne = new HashSet();
	public static void parseEne(Connection conn,Index index,String title,List resultsEne){
		if(stksEne.contains(index.getCode()))return;
		stksEne.add(index.getCode());
		try{
			if(index.getKs().size() > 10){
				index.initKLines(30);
				//index.getKs().clear();
				index.gc();
				if(index.getK(ServiceUtils.getToday()).getEne().getLower() >= index.getK(ServiceUtils.getToday()).getLow()){
					//resultsEne.add(title+index.getName()+"["+index.getCode()+"]");
					addResult(resultsEne,index,title+index.getName()+"["+ServiceUtils.wrapCodeLink(index.getCode())+"]");
				}
			}
			index.gc();
		}catch(Exception e){
			e.printStackTrace();
			ExceptionUtils.insertLog(conn, index.getCode(), e);
		}
	}
	
	public static void addResult(List results, Index index, String str) throws Exception{
		K todayK = index.getK(ServiceUtils.getToday());
		if(todayK != null && !todayK.isUpLimit()){
			results.add(str+",[市值:"+ServiceUtils.number2String(index.getTotalMarketValue(),2)+"亿]");
		}
	}

}
