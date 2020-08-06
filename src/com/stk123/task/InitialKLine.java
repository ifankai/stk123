package com.stk123.task;

import java.io.File;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.tags.LinkTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stk123.bo.Stk;
import com.stk123.bo.StkImportInfoType;
import com.stk123.bo.StkKlineRankIndustry;
import com.stk123.bo.StkPe;
import com.stk123.json.MeiGuSina;
import com.stk123.model.Index;
import com.stk123.model.IndexContext;
import com.stk123.model.IndexUtils;
import com.stk123.model.Industry;
import com.stk123.model.K;
import com.stk123.model.News;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.StrategyManager;
import com.stk123.tool.db.TableTools;
import com.stk123.tool.db.connection.ConnectionPool;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.sync.Sync;
import com.stk123.tool.util.CacheUtils;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.HtmlUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.tool.util.Retry;
import com.stk123.tool.util.RetryUtils;
import com.stk123.tool.util.collection.IntRange2IntMap;
import com.stk123.web.StkConstant;
import com.stk123.web.action.ScreenerAction;
import com.stk123.web.monitor.Monitor;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class InitialKLine {
	
	private static Logger logger = LoggerFactory.getLogger(InitialKLine.class);
	
	private static final String yesterday = StkUtils.getYesterday();
	private static final Date now = new Date();
	private static final int DAYS_OF_NEWHIGHT_600 = 600;
	private static final int DAYS_OF_NEWHIGHT_250 = 250;
	private static final int DAYS_OF_NEWHIGHT_120 = 120;
	
	private static boolean initonly = false;
	private static boolean analyse = false;
	
	public static String today = StkUtils.getToday();//"20160923";
	public static final int US_STK_HOT = 200; //美股雪球关注人数下限
	
	public static boolean addToCareStks = true;
	public static Set<String> careStks = new HashSet<String>();
	
	public static boolean addToFlowStks = true;
	public static Set<Index> flowStks = new HashSet<Index>();
	
	public static void main(String[] args) throws Exception {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");//fix JDK1.7 error:java.lang.IllegalArgumentException: Comparison method violates its general contract!
		//System.setProperty("java.io.tmpdir", System.getProperty("java.io.tmpdir")+File.separator+"CN");
		ConfigUtils.setPropsFromResource(TableTools.class,"db.properties");
		ConfigUtils.setProp("sql_select_show", "N");
		Connection conn = null;
		try{
			int market = 1;//default A stock
			if(args != null && args.length > 0){
				for(String arg : args){
					if("US".equals(arg)){
						market = 2;
						System.setProperty("java.io.tmpdir", System.getProperty("java.io.tmpdir")+File.separator+"US");
					}
					if("initonly".equalsIgnoreCase(arg)){
						initonly = true;
					}
					if("analyse".equalsIgnoreCase(arg)){
						analyse = true;
					}
				}
			}
			long start = System.currentTimeMillis();
			conn = DBUtil.getConnection();
			CacheUtils.DISABLE = true;
			Index.KLineWhereClause = Index.KLINE_20140101;
			Index.FNDateWhereClause = Index.FNDate_20140101;
			if(market == 1){
				//------------- A --------------//
				try{
					JdbcUtils.delete(conn, "delete from stk_kline where kline_date>to_char(sysdate,'yyyymmdd')",null);
					initAStock(conn);
					JdbcUtils.delete(conn, "delete from stk_kline where kline_date>to_char(sysdate,'yyyymmdd')",null);
					
					//下载沪深300市盈率xls数据
					String url = "http://www.csindex.com.cn/sseportal/ps/zhs/hqjt/csi/Csi300Perf.xls";
					HttpUtils.download(url,null, ConfigUtils.getProp("initial_csi300")/*"d:\\share\\download\\"*/, "Csi300Perf_"+StkUtils.getToday()+".xls");
				}catch(Exception e){
					EmailUtils.send("Initial A Stock K Line Error", ExceptionUtils.getException(e));
				}
				
				//------------- HK --------------//
				try{
					initHKStock(conn);
				}catch(Exception e){
					EmailUtils.send("Initial HK Stock K Line Error", ExceptionUtils.getException(e));
				}
				
				//sync to www.stk123.cn
				//ScreenerAction.refreshStkSearchMview(conn);
				//Sync.run();
				
			}else if(market == 2){
				try{
					Index.KLineWhereClause = Index.KLINE_20140101;
					initUStock(conn);
				}catch(Exception e){
					EmailUtils.send("Initial US Stock K Line Error", ExceptionUtils.getException(e));
				}
			}
			//monitor task
			//Monitor.run(conn, Monitor.TYPE_KLINE);
			
			long end = System.currentTimeMillis();
			System.out.println("InitialKLine time:"+((end-start)/1000D));
		}finally{
			if(conn != null)conn.close();
			CacheUtils.close();
		}
				
	}
	
	public static void initHKStock(Connection conn) throws Exception {
		try{
			int dayOfWeek = StkUtils.getDayOfWeek(now);
			boolean flag = (dayOfWeek == 1 || dayOfWeek == 2 || dayOfWeek == 3 || dayOfWeek == 4 || dayOfWeek == 5)?true:false;
			
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_hk order by code", Stk.class);
			if(!analyse){
				initKLines(stks, flag, 3);
			}
		}catch(Exception e){
			EmailUtils.send("Initial HK Stock K Line Error", e);
			e.printStackTrace();
		}
	}
	
	public static void initUStock(Connection conn) throws Exception{
		try{
			int dayOfWeek = StkUtils.getDayOfWeek(now);
			boolean flag = (dayOfWeek == 2 || dayOfWeek == 3 || dayOfWeek == 4 || dayOfWeek == 5 || dayOfWeek == 6)?true:false;
			
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk where market=2 and cate=2 order by code", Stk.class);
			if(!analyse){
				initKLines(stks, flag, 1);
			}
			
			String today = JdbcUtils.load("select kline_date from (select kline_date from stk_kline_us where code='.DJI' order by kline_date desc) where rownum=1", String.class);
			System.out.println("today="+today);
			
			stks = JdbcUtils.list(conn, "select code,name from stk_us order by code", Stk.class);
			if(!analyse){
				initKLines(stks, flag, 3);
				//initUStkPE(conn);
			}
			
			Double avgPE = 0.0;
			//中概股pe
			List params = new ArrayList();
			/*params.clear();
			params.add(today);
			Double avgPE = JdbcUtils.load("select avg(pe_ttm) from stk_kline_us where kline_date=? and code in (select code from stk_industry where industry=885739) and pe_ttm is not null and pe_ttm>3 and pe_ttm<200", params, Double.class);
			avgPE = StkUtils.numberFormat(avgPE, 2);*/
			
			params.clear();
			params.add(today);
			params.add(today);
			JdbcUtils.insert(conn, "insert into stk_daily_report_us(report_date) select ? from dual where not exists (select 1 from stk_daily_report_us where report_date=?)", params);
			
			/*params.clear();
			params.add(avgPE);
			params.add(today);
			JdbcUtils.update(conn, "update stk_daily_report_us set result_1=? where report_date=?", params);*/
			
			//美股pe
			params.clear();
			params.add(today);
			Double avgPE2 = JdbcUtils.load("select avg(pe_ttm) from stk_kline_us where kline_date=? and pe_ttm is not null and pe_ttm>3 and pe_ttm<200", params, Double.class);
			avgPE2 = StkUtils.numberFormat(avgPE2, 2);
			
			params.clear();
			params.add(avgPE2);
			params.add(today);
			JdbcUtils.update(conn, "update stk_daily_report_us set result_2=? where report_date=?", params);
			EmailUtils.send("[美股]平均PE,日期:"+today+",中概:"+avgPE+",平均:"+avgPE2, "中概平均PE:"+avgPE+"<br>美股平均PE:"+avgPE2);
			
			System.out.println(".............. US ...................");
			List<Index> indexs = new ArrayList<Index>();
			for(Stk stk : stks){
				Index index = new Index(conn,stk.getCode(),stk.getName());
				if(index.getStk().getHot() < US_STK_HOT && !StringUtils.containsIgnoreCase(stk.getName(), "etf")){
					continue;
				}
				indexs.add(index);
			}
			
			System.out.println("1.创新高选股法");
			List<Index> newHighs = IndexUtils.getNewHighs(indexs, today, DAYS_OF_NEWHIGHT_250);
			/*if(newHighs.size() > 0){
				Collections.sort(newHighs, new Comparator<Index>(){
					@Override
					public int compare(Index o1, Index o2) {
						return (o2.getStk().getHot() - o1.getStk().getHot());
					}});
				EmailUtils.send("[美股]创"+DAYS_OF_NEWHIGHT_250+"日新高股,总计:"+newHighs.size()+",日期:"+today, StkUtils.createHtmlTable(today, newHighs));
			}*/
			
			newHighs = IndexUtils.getNewHighs(indexs, today, DAYS_OF_NEWHIGHT_120);
			/*if(newHighs.size() > 0){
				Collections.sort(newHighs, new Comparator<Index>(){
					@Override
					public int compare(Index o1, Index o2) {
						return (o2.getStk().getHot() - o1.getStk().getHot());
					}});
				EmailUtils.send("[美股]创"+DAYS_OF_NEWHIGHT_120+"日新高股,总计:"+newHighs.size()+",日期:"+today, StkUtils.createHtmlTable(today, newHighs));
			}*/
			
			System.out.println("2.黄金三角选股法");
			//checkUSGoldTriangle(today, indexs);
			
			System.out.println("3.K线缠绕选股法");
			//checkUSKIntersect(today, indexs);
			
			System.out.println("4.突破下降趋势选股法");
			//checkUSKUpTrendLine(today, indexs);
			
			System.out.println("4.[周线]趋势线突破");
			//checkWeeklyTrandLine(today, indexs);
			
			System.out.println("10.[月线]趋势线突破");
			//checkMonthlyTrandLine(today, indexs);
			
			System.out.println("5.天量后缩量");
			//checkHugeVolumeLittleVolume(indexs, today, 2);
			
			System.out.println("6.MACD一品抄底");
			//checkYiPinChaoDiMACD(conn, today, indexs, 2);
			
			System.out.println("7.二品抄底-买入时机");
			//checkErPinChaoDi(conn, today, indexs, 2, true, false);
			
			System.out.println("7.[周线]二品抄底-买入时机");
			//checkErPinChaoDi(conn, today, indexs, 2, true, true);
			
			System.out.println("8.[周线]MACD粘合");
			//checkMACDWeekly(today, indexs);
			
			System.out.println("10.周线6连阴后macd背离");
			
		}catch(Exception e){
			EmailUtils.send("Initial US Stock K Line Error", e);
			e.printStackTrace();
		}
	}
	
	public static void initAStock(final Connection conn) throws Exception{
		List params = new ArrayList();
		int dayOfWeek = StkUtils.getDayOfWeek(now);
		boolean flag = (dayOfWeek == 1 || dayOfWeek == 2 || dayOfWeek == 3 || dayOfWeek == 4 || dayOfWeek == 5)?true:false;
		//flag = true;
		if(!analyse){
			
			Stk sk = null;
			try{
				List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk where market=1 and cate=2 order by code", Stk.class);
				for(Stk stk : stks){
					System.out.println(stk.getCode());
					sk = stk;
					Index sh = new Index(conn,stk.getCode());
					if(flag){
						sh.initKLines(5);
					}else{
						sh.initKLines(30);
					}
				}
			}catch(Exception e){
				EmailUtils.send("[InitialKLine出错]大盘指数K线下载出错 stk="+sk.getCode(), e);
			}
			Index index1000 = new Index(conn, "01000852");
			final String todayK = index1000.getK(0).getDate();
			
			try{
				List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk where market=1 and cate=4 order by code", Stk.class);
				for(Stk stk : stks){
					System.out.println(stk.getCode());
					sk = stk;
					Index sh = new Index(conn,stk.getCode());
					sh.initKLine();
				}
				
				RetryUtils.retryIfException(new Retry(){
					@Override
					public void run() throws Exception {
						Industry.updateCapitalFlow(conn, todayK, "gnzjl");
						Industry.updateCapitalFlow(conn, todayK, "hyzjl");
					}
				});
				
			}catch(Exception e){
				EmailUtils.send("[InitialKLine出错]同花顺概念指数K线下载出错 stk="+sk.getCode(), e);
			}
		}
		
		try{
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			if(!analyse){
				System.out.println("initKLines..........");
				initKLines(stks, flag, 4);
				System.out.println("initKLines..........end");
				if(!flag){
					EmailUtils.send("周六数据同步完成！！！","...");
					return;
				}
				
				List<String> stksUnInit = JdbcUtils.list(conn, "select code from stk_cn where code not in (select code from stk_kline where kline_date=to_char(sysdate,'yyyymmdd'))", String.class);
				for(String code : stksUnInit){
					try{
						Index tmpIndex = new Index(conn, code);
						tmpIndex.initKLine();
					}catch(Exception e){
						EmailUtils.send("[InitialKLine出错]修补K线数据出错 stk="+ code, e);
					}
				}
				
				System.gc();
			}
			System.out.println(".............. A ................");
			if(initonly)return;
			
			//---------------------------------
			// check stk after initial k line
			//---------------------------------
			//IndexUtils.sortByCloseChange(context.indexs,StkUtils.getToday(),250);
			//IndexUtils.sortByCloseChange(contextYesterday.indexs,StkUtils.getYesterday(),250);
			
			Integer reportId =  JdbcUtils.load(conn, "select nvl(max(id)+1,100001) from stk_pe", Integer.class);
			params.clear();
			params.add(reportId);
			params.add(today);
			params.add(today);
			int i = JdbcUtils.insert(conn, "insert into stk_pe(id,report_date) select ?,? from dual where not exists (select 1 from stk_pe where report_date=?)", params);
			
			
			System.out.println("calculate peg."+new Date());
			//peg(today, context.indexs);
			
			params.clear();
			params.add(today);
			Double totalPE = JdbcUtils.load("select avg(pe_ttm) from stk_kline where kline_date=? and pe_ttm is not null and pe_ttm>3 and pe_ttm<200", params, Double.class);
			totalPE = StkUtils.numberFormat(totalPE, 2);
			Double totalPB = JdbcUtils.load("select avg(pb_ttm) from stk_kline where kline_date=? and pb_ttm is not null and pb_ttm>0 and pb_ttm<30", params, Double.class);
			totalPB = StkUtils.numberFormat(totalPB, 2);
			Double midPB = JdbcUtils.load("select median(pb_ttm) from stk_kline where kline_date=? and pb_ttm is not null and pb_ttm>0 and pb_ttm<30", params, Double.class);
			midPB = StkUtils.numberFormat(midPB, 2);
			Double midPE = JdbcUtils.load("select median(pe_ttm) from stk_kline where kline_date=? and pe_ttm is not null and pe_ttm>3 and pe_ttm<200", params, Double.class);
			midPE = StkUtils.numberFormat(midPE, 2);
			
			if(i >= 1){
				params.clear();
				params.add(totalPE);
				params.add(totalPB);
				params.add(midPB);
				params.add(midPE);
				params.add(reportId);
				JdbcUtils.update(conn, "update stk_pe set total_pe=?,total_pb=?,mid_pb=?,mid_pe=? where id=?", params);
				System.gc();
			}
			
			
			IndexContext context = new IndexContext();
			IndexContext contextYesterday = new IndexContext();
			for(Stk stk : stks){
				Index index = new Index(conn,stk.getCode(),stk.getName());
				context.indexs.add(index);
				contextYesterday.indexs.add(index);
				//更新非公开发行，员工持股价格溢价率
				NoticeRobot.updateRate(conn, index);
				
				//跟新pe/pb ntile
				index.updateNtile();
			}
			
			System.out.println("策略 at:"+new Date());
			strategy(conn,false);
			
			System.out.println("时间窗口 at:"+new Date());
			timeWindow(conn, today);
			
			System.out.println("行业分析");
			checkIndustry(conn, today, "10jqka_gn");
			checkIndustry(conn, today, "10jqka_thshy");
			
			//策略模型选股
			/*StrategyManager mgr = new StrategyManager(conn, today);
			mgr.init(context.indexs);
			mgr.execute();*/
			
			//List<Index> newHighs = new ArrayList<Index>();
			System.out.println("1.check股票有没有创600日新高");
			//1.check股票有没有创600日历史新高
			List<Index> newHighs = IndexUtils.getNewHighs(context.indexs, today, DAYS_OF_NEWHIGHT_600);
			/*if(newHighs.size() > 0){
				EmailUtils.sendAndReport("创"+DAYS_OF_NEWHIGHT_600+"日新高股,总计:"+newHighs.size()+",日期:"+today, StkUtils.createHtmlTable(today, newHighs));
			}*/
			
			newHighs = IndexUtils.getNewHighs(context.indexs, today, DAYS_OF_NEWHIGHT_120);
			/*if(newHighs.size() > 0){
				EmailUtils.sendAndReport("创"+DAYS_OF_NEWHIGHT_120+"日新高股,总计:"+newHighs.size()+",日期:"+today, StkUtils.createHtmlTable(today, newHighs));
			}*/
			System.out.println("1.1.接近600日新高");
			/*List<Index> closeNewHighs = IndexUtils.getCloseNewHighs(context.indexs, today, DAYS_OF_NEWHIGHT_600);
			if(closeNewHighs.size() > 0){
				EmailUtils.sendAndReport("接近"+DAYS_OF_NEWHIGHT_600+"日新高股,总计:"+closeNewHighs.size()+",日期:"+today, StkUtils.createHtmlTable(today, closeNewHighs));
			}*/
			System.out.println("1.2.接近120日新高");
			/*closeNewHighs = IndexUtils.getCloseNewHighs(context.indexs, today, DAYS_OF_NEWHIGHT_120);
			if(closeNewHighs.size() > 0){
				EmailUtils.sendAndReport("接近"+DAYS_OF_NEWHIGHT_120+"日新高股,总计:"+closeNewHighs.size()+",日期:"+today, StkUtils.createHtmlTable(today, closeNewHighs));
			}*/
			System.out.println("1.3.接近600日新高且K线缠绕");
			/*closeNewHighs = IndexUtils.getCloseNewHighsAndInteract(context.indexs, today, DAYS_OF_NEWHIGHT_600);
			if(closeNewHighs.size() > 0){
				EmailUtils.sendAndReport("接近"+DAYS_OF_NEWHIGHT_600+"日新高且K线缠绕股,总计:"+closeNewHighs.size()+",日期:"+today, StkUtils.createHtmlTable(today, closeNewHighs));
			}*/
			
			//3.search TODO 增加和昨天比较后新加的
			/*datas = IndexUtils.search(context.indexs, today, 3, 120, false);
			for(List data : datas){
				Index index = (Index)data.get(0);
				data.add(StkUtils.createWeeklyKLine(index.getCode()));
				data.add(StkUtils.createDailyKLine(index.getCode()));
			}
			if(datas.size() > 0){
				List<String> addtitle = new ArrayList<String>();
				addtitle.add("120日涨跌幅");
				addtitle.add("行业");
				addtitle.add("周线图");
				addtitle.add("日线图");
				EmailUtils.send("缩量创120日新高股,总计："+datas.size()+",日期:"+today, StkUtils.createHtmlTable(today, datas, addtitle));
			}*/
			
			//4.check RS and Net Profit
			/*
			int cnt = (int)(context.indexs.size()/10*1.5);
			IndexUtils.sortByCloseChangeAndNetProfit(context.indexs,today);
			IndexUtils.sortByCloseChangeAndNetProfit(contextYesterday.indexs,yesterday); 
			datas = new ArrayList<List>();
			List<List> datasYesterday = new ArrayList<List>();
			for(Index index : context.indexs){
				if(cnt-- == 0)break;
				List data = new ArrayList();
				data.add(index);
				data.add(StkUtils.formatNumber(index.getCanslim().getCloseChange()*100,2)+"%");
				data.add(index.getCanslim().getCloseChangeRank()+","+index.getCanslim().getNetProfitGrowthRank());
				datas.add(data);
				
				params.clear();
				params.add(today);
				params.add(index.getCode());
				params.add(index.getCanslim().getCloseChangeRS());
				params.add(StkUtils.formatNumber(index.getCanslim().getCloseChange()*100,2));
				params.add(index.getCode());
				params.add(today);
				JdbcUtils.insert(conn, "insert into stk_report_daily (type,report_date,code,remark,remark_2,insert_time) select 2,?,?,?,?,sysdate from dual where not exists (select 1 from stk_report_daily where type=2 and code=? and REPORT_DATE=?)", params);
			}
			cnt = (int)(contextYesterday.indexs.size()/10*1.5);
			for(Index index : contextYesterday.indexs){
				if(cnt-- == 0)break;
				List data = new ArrayList();
				data.add(index);
				data.add(StkUtils.formatNumber(index.getCanslim().getCloseChange()*100,2)+"%");
				data.add(index.getCanslim().getCloseChangeRank()+","+index.getCanslim().getNetProfitGrowthRank());
				datasYesterday.add(data);
			}
			List<List> added = notContain(datas,datasYesterday);//入榜
			List<List> deleted = notContain(datasYesterday,datas);//出榜
			if(datas.size() > 0){
				List<String> addtitle = new ArrayList<String>();
				addtitle.add("250日涨跌幅");
				addtitle.add("排行[涨跌幅,净利润增长]");
				EmailUtils.send("250日RS强度业绩排行,总计："+datas.size()+",日期:"+today, 
						"入榜：<br>"+StkUtils.createHtmlTable(today, added, addtitle) + "<br>" +
						"出榜：<br>"+StkUtils.createHtmlTable(today, deleted, addtitle) + "<br>" +
						StkUtils.createHtmlTable(today, datas, addtitle));
			}
			*/
			System.gc();
			//System.out.println("5.沧州老张选股法");
			//checkStkByLaoZhang(conn, context, contextYesterday, newHighs);
			
			//System.out.println("6.250日底部箱体突破股票，箱体最低到突破(最高)涨幅不超过60%");
			//checkStkCreate250DaysBoxHigh(conn, context);
			
			//System.out.println("7.短线60日股票强度排行");
			//check60DaysRS(conn, context, contextYesterday);
			
			//8.rank k line
			//K today = sh.getK();
			//rankIndustryByK(conn, today.getDate(), "cnindex");
			
			//System.out.println("9.回补前期跳空缺口");
			/*List<Index> gaps = IndexUtils.getUpGaps(context.indexs,today , 250, 30);
			if(gaps.size() > 0){
				StringBuffer sb = new StringBuffer();
				for(Index gap : gaps){
					sb.append(gap.toHtml()).append("<br>");
				}
				EmailUtils.sendAndReport("回补前期跳空缺口，总共："+gaps.size()+",日期:"+today, StkUtils.createHtmlTable(today, gaps));
			}*/
			
			System.out.println("10.K线缠绕");
			//checkKIntersect(today, context);
			
			//System.out.println("12.南京高人战法");
			//checkCreateHighStk(context);
			
			System.out.println("13.突破600日天量");
			//checkHighHugeVolume(context);
			
			//System.out.println("14.天量后缩量");
			//checkHugeVolumeLittleVolume(context.indexs, today, 1);
			
			//15.王朋选股
			//checkWangPeng(context);
			
			System.out.println("16.王朋选股优化");
			//checkWangPeng1(context);
			//checkWangPeng2(context);
			
			System.out.println("17.价格低于前高，量超前量");
			//checkVolumeGreaterThanLastVolume(today, context, 0.8);
			
			System.out.println("18.一品抄底-MACD");
			int cnt = checkYiPinChaoDiMACD(conn, today, context.indexs, 1);
			if(i >= 1){
				params.clear();
				params.add(cnt);
				params.add(today);
				JdbcUtils.update(conn, "update stk_pe set result_4=? where report_date=?", params);
			}
			
			System.out.println("18.一品抄底-底部");
			cnt = checkYpcdDiBu(conn, today, context.indexs, 1);
			if(i >= 1){
				params.clear();
				params.add(cnt);
				params.add(today);
				JdbcUtils.update(conn, "update stk_pe set result_3=? where report_date=?", params);
			}
			
			System.out.println("19.二品抄底-买入时机");
			try{
				List<Index> rslts = checkErPinChaoDi(conn, today, context.indexs, 1, true, false);
				if(i >= 1){
					params.clear();
					params.add(rslts.size());
					params.add(today);
					JdbcUtils.update(conn, "update stk_pe set result_5=? where report_date=?", params);
				}
			}catch(Exception e){
				EmailUtils.send("二品抄底-买入时机 - eror", e);
			}
			
			System.out.println("20.一品抄底-大势已去");
			//checkYiPinChaoDiDaShiYiQu(conn, today, context.indexs);
			
			System.out.println("21.连续涨停后开板");
			//checkKaiBan(today, context.indexs);
			
			System.out.println("22.连续涨停后马上开板股");
			//checkMaybeKaiBan(today, context.indexs);
			
			System.out.println("23.连续涨停，开板后，新高股");
			//checkKaiBanXinGao(today, context.indexs);
			
			System.out.println("24.资金流入且离底部不远");
			//checkChaoDiAndFlow(conn, today);
			
			
			//统计自选股入选股票
			if(addToCareStks){
				addToCareStks = false;
				List<Index> results = new ArrayList<Index>();
				for(String careStk : careStks){
					results.add(new Index(conn, careStk));
				}
				if(results.size() > 0){
					//Strategy.logToDB(conn, today, "自选股C", results);
					EmailUtils.sendAndReport("自选股C, 个数："+results.size()+" 日期:"+today, 
						StkUtils.createHtmlTable(today, results));
				}
				addToCareStks = true;
			}
			
			//统计资金连续流入股票
			/*if(addToFlowStks){
				addToFlowStks = false;
				List<Index> results = new ArrayList<Index>(flowStks);
				Collections.sort(results, new Comparator<Index>(){
					@Override
					public int compare(Index o1, Index o2) {
						int i = o2.NumberOfCapitalFlowPostive - o1.NumberOfCapitalFlowPostive;
						return i;
					}});
				if(results.size() > 0)
				EmailUtils.sendAndReport("资金持续流入, 个数："+results.size()+" 日期:"+today, 
						StkUtils.createHtmlTable(today, results));
				addToFlowStks = true;
			}*/
			
			
			//Integer reportId =  JdbcUtils.load(conn, "select nvl(max(id)+1,100001) from stk_pe", Integer.class);
			
			
			
			System.out.println("2.check growth stk average pe "+new Date());
			//2.check growth stk average pe
			//String htmlMsg = IndexUtils.reportGrowthPE(conn, context, today);
			
			double gtotalPE = 0.0;
			double gtotalPB = 0.0;
			int pbCnt = 0;
			Industry myInd = Industry.getIndustry(conn, "1783");
			for(Index gIdx : myInd.getIndexs()){
				System.out.println("growth stock="+gIdx.getCode());
				Double pe = gIdx.getPETTM(today);
				if(pe == null || pe == 0 || pe < 5 || pe > 200){
					continue;
				}
				double pb = gIdx.getPBTTM(today);
				if(pb > 0){
					pbCnt ++;
					gtotalPB += pb;
				}
				gIdx.changePercent = pe;
				context.indexsGrowth.add(gIdx);
				gtotalPE += pe;
			}
			System.out.println("=====");
			Collections.sort(context.indexsGrowth, new Comparator<Index>(){
				public int compare(Index arg0, Index arg1) {
					double d0 = arg0.changePercent;
					double d1 = arg1.changePercent;
					return (int)((d1-d0)*100);
				}
			});
			context.averagePE = StkUtils.number2String(gtotalPE/context.indexsGrowth.size(), 2);
			context.averagePB = StkUtils.number2String(gtotalPB/pbCnt, 2);
			
			if(i >= 1){
				params.clear();
				//params.add(JdbcUtils.createClob(context.averagePEIndexs));
				params.add(context.averagePE);
				params.add(context.averagePB);
				params.add(reportId);
				JdbcUtils.update(conn, "update stk_pe set average_pe=?,avg_pb=? where id=?", params);
			
				//System.gc();
				//eneStatistics(conn, today, context, true);
				System.gc();
				biasStatistics(conn, today, context);
			}
			
			
			String htmlMsg = StkUtils.createHtmlTable(today, context.indexsGrowth);
			
			
			String peAndpeg = "市场整体中位PB低点大约是2PB：<br>" +
							  "2008年最低点约1700点附近，中位数市净率约2倍。<br>" +
							  "2012年前后最低点大约2000点，中位数市净率约2倍。<br>" +
							  "2016-2017年前后，大约2366点对应中位数2倍市净率。<br><br>" +
					"成长股最佳PE投资区间是20PE-50PE。<br>" +
					"低于20PE而其业绩却超过30%增长的话，要小心业绩陷阱。<br>" +
					"高于50PE要注意泡沫风险，就不要盲目杀入了。<br>"
					+ StkUtils.createPEAndPEG()+"<br>";
			List<List<String>> pe = new ArrayList<List<String>>();
			List<String> pe1 = new ArrayList<String>();
			pe1.add("");pe1.add("平均PE");pe1.add("中位PE");pe1.add("平均PB");pe1.add("中位PB");pe1.add("统计数");
			pe.add(pe1);
			List<String> pe2 = new ArrayList<String>();
			pe2.add("成长股");pe2.add(context.averagePE);pe2.add("");pe2.add(context.averagePB);pe2.add("");pe2.add(String.valueOf(context.indexsGrowth.size()));
			pe.add(pe2);
			List<String> pe3 = new ArrayList<String>();
			pe3.add("全市场");pe3.add(String.valueOf(totalPE));pe3.add(String.valueOf(midPE));pe3.add(String.valueOf(totalPB));pe3.add(String.valueOf(midPB));pe3.add("");
			pe.add(pe3);
			EmailUtils.sendAndReport("成长股平均PE："+context.averagePE+",PB："+context.averagePB+
					"；市场整体PE："+totalPE+",中位PE："+midPE+",整体PB："+totalPB+",中位PB："+midPB+",日期:"+today, 
					StkUtils.createHtmlTable(null, pe) + "<br>" + peAndpeg + htmlMsg);
			
			System.out.println("更新非公开发行，员工持股价格溢价率");
			for(Index index : context.indexs){
				//更新非公开发行，员工持股价格溢价率
				NoticeRobot.updateRate(conn, index);
				
				//盈利预期PE
				index.updateEarningsForecastPE();
			}
			System.out.println("Initial A Stock K Line End.");
		}catch(Exception e){
			e.printStackTrace();
			ExceptionUtils.insertLog(conn, e);
			logger.error(ExceptionUtils.getException(e));
			EmailUtils.send("Initial A Stock K Line Error - InitialKLine", e);
		}
	}
	
	public static void eneStatistics(Connection conn, String date /*yyyyMMdd*/, IndexContext context, boolean sendMail) throws Exception{
		double eneUpperCnt = 0;
		double eneLowerCnt = 0;
		for(Index index : context.indexs){
			K k = index.getK(date);
			if(k == null)continue;
			double eneLower = k.getEne().getLower();
			double lower = k.getLow();
			if(lower <= eneLower){
				eneLowerCnt ++;
			}else{
				double upper = k.getHigh();
				double eneUpper = k.getEne().getUpper();
				if(upper >= eneUpper){
					eneUpperCnt ++;
				}
			}
		}
		List params = new ArrayList();
		if(sendMail){
			int total = JdbcUtils.load(conn, "select count(1) from stk_cn", Integer.class);
			params.add(eneUpperCnt);
			params.add(eneLowerCnt);
			params.add(eneUpperCnt/total * 100);
			params.add(eneLowerCnt/total * 100);
			params.add(date);
			JdbcUtils.update(conn, "update stk_pe set ene_upper_cnt=?,ene_lower_cnt=?,ene_upper=?,ene_lower=? where report_date=?", params);
			EmailUtils.send("ENE统计,lower:"+(int)eneLowerCnt+"，upper:"+(int)eneUpperCnt+",日期:"+date, ".");
		}
		if(eneUpperCnt >= eneLowerCnt){
			params.clear();
			params.add(date);
			StkPe pe = JdbcUtils.load("select * from (select * from stk_pe where report_date<>? order by report_date desc) a where rownum=1",params, StkPe.class);
			if(pe != null && pe.getEneUpper() < pe.getEneLower()){
				EmailUtils.send(EmailUtils.IMPORTANT+"ENE统计:Upper大于Lower,日期:"+date, ".");
			}
		}
		
	}
	
	public static void biasStatistics(Connection conn, String date, IndexContext context) throws Exception{
		double bias = 0.0;
		int n = 0;
		for(Index index : context.indexs){
			K k = index.getK(date);
			if(k != null){
				bias += k.getBIAS(30);
				n++;
			}
		}
		List params = new ArrayList();
		params.add(bias/n);
		params.add(date);
		JdbcUtils.update(conn, "update stk_pe set bias=? where report_date=?", params);
	}
	
	public final static int RANK_STK_SAMPLE = 10;
	public final static int RANK_STK_STORE = 10;
	public final static int[] RANK_STK_DAYS = {2,5,10,20,30,60,120,250};
	
	public static void rankIndustryByK(Connection conn, final String date, String industrySource) throws Exception{
		List params = new ArrayList();
		List<Industry> inds = Industry.getIndsutriesBySource(conn, industrySource);
		final Index sh = new Index(conn, "999999");
		for(Industry ind : inds){
			List<Index> indexs = ind.getIndexs();
			if(indexs.size() > RANK_STK_SAMPLE){
				for(final int n : RANK_STK_DAYS){
					//System.out.println(n);
					Collections.sort(indexs, new Comparator<Index>(){
						public int compare(Index arg0, Index arg1) {
							try{
								K arg0k = arg0.getK(n-1);
								K arg1k = arg1.getK(n-1);
								K shk = sh.getK(n-1);
								if(arg0k.getDate().equals(shk.getDate()) && arg1k.getDate().equals(shk.getDate())){
									arg0.changePercent = arg0.getCloseChange(date, n-1);
									arg1.changePercent = arg1.getCloseChange(date, n-1);
								}else{
									return 0;
								}
							} catch (Exception e) {
								e.printStackTrace();
								return 0;
							}
							//return (int)((arg1.changePercent-arg0.changePercent)*100);
							if(arg1.changePercent > arg0.changePercent){
								return 1;
							}else if(arg1.changePercent < arg0.changePercent){
								return -1;
							}else{
								return 0;
							}
						}
					});
					ind.changePercent = 0;
					for(int i=0;i<RANK_STK_SAMPLE;i++){
						Index index = indexs.get(i);
						ind.changePercent += index.changePercent;
					}
					ind.changePercent = ind.changePercent/RANK_STK_SAMPLE;
					long seq = JdbcUtils.getSequence(conn, "s_kline_rank_ind_id");
					params.clear();
					params.add(seq);
					params.add(ind.getType().getId());
					params.add(date);
					params.add(n);
					params.add(ind.changePercent);
					params.add(ind.getType().getId());
					params.add(date);
					params.add(n);
					int x = JdbcUtils.insert(conn, "insert into stk_kline_rank_industry (rank_id,industry_id,rank_date,rank_days,change_percent) select ?,?,?,?,? from dual where not exists (select 1 from stk_kline_rank_industry where industry_id=? and rank_date=? and rank_days=?)", params);
					if(x > 0){
						int m = 0;
						for(Index index : indexs){
							params.clear();
							params.add(seq);
							params.add(index.getCode());
							params.add(index.changePercent);
							params.add(seq);
							params.add(index.getCode());
							JdbcUtils.insert(conn, "insert into stk_kline_rank_industry_stock (rank_id,code,change_percent) select ?,?,? from dual where not exists (select 1 from stk_kline_rank_industry_stock where rank_id=? and code=?)", params);
							if(m++ >= RANK_STK_STORE)break;
						}
					}
				}
			}
			//break;
		}
		for(int n : RANK_STK_DAYS){
			params.clear();
			params.add(date);
			params.add(n);
			List<StkKlineRankIndustry> ranks = JdbcUtils.list(conn, "select * from stk_kline_rank_industry where rank_date=? and rank_days=? order by change_percent desc", params, StkKlineRankIndustry.class);
			int i = 0;
			for(StkKlineRankIndustry rank : ranks){
				i ++;
				params.clear();
				params.add(i);
				params.add(rank.getRankId());
				JdbcUtils.update(conn, "update stk_kline_rank_industry set rank=? where rank_id=?", params);
			}
		}
	}
	
	
	
	//多线程 workers
	public static void initKLines(List<Stk> stks, final boolean flag,int numberOfWorker) throws Exception {
		ConnectionPool pool = null;
		try {
			pool = ConnectionPool.getInstance();
			
			final CountDownLatch countDownLatch = new CountDownLatch(stks.size());
			ExecutorService exec = Executors.newFixedThreadPool(numberOfWorker);
			for(final Stk stk : stks){
				Runnable run = new Runnable() {
					public void run() {
						Connection conn = null;
						Index index = null;
						try{
							conn = ConnectionPool.getInstance().getConnection();
							index = new Index(conn, stk.getCode());
							System.out.println("initKLines=="+index.getCode());
							if(flag){
								index.initKLine();
							}else{
								index.initKLines(30);
							}
						}catch(Exception e){
							ExceptionUtils.insertLog(conn, index.getCode(), e);
                            e.printStackTrace();
						}finally{
							ConnectionPool.getInstance().release(conn);
							countDownLatch.countDown();
						}
						
					}
				};
				exec.execute(run);
			}
			exec.shutdown();
			countDownLatch.await();
			
		} finally {
			if (pool != null) pool.closePool();
		}
	}
	
	public static void initUStkPE(Connection conn) throws Exception {
		int pageNum = 1;
		List params = new ArrayList();
		String page = HttpUtils.get("http://hq.sinajs.cn/rn="+Math.random()+"&list=gb_dji", null, "GBK");
		String dateTmp = page.split(",")[25];
		String[] tmp = dateTmp.split(" ");
		String date = StkUtils.sf_ymd2.format(StkUtils.sf_ymd7.parse(tmp[0]+" "+tmp[1]+" "+StkUtils.YEAR));

		while(true){
			page = HttpUtils.get("http://stock.finance.sina.com.cn/usstock/api/jsonp.php/IO.XSRV2.CallbackList"+URLEncoder.encode("['f0j3ltzVzdo2Fo4p']","utf-8")+"/US_CategoryService.getList?page="+pageNum+"&num=60&sort=&asc=0&market=&id=", null, "GBK");
			//System.out.println(page);
			Map<String, Class> m = new HashMap<String, Class>();
	        m.put("data", Map.class);
			MeiGuSina meiGu = (MeiGuSina)JsonUtils.getObject4Json(StringUtils.substringBetween(page, "((", "));"), MeiGuSina.class, m);
			if(meiGu == null || meiGu.getData() == null){
				break;
			}
			for(Map map : meiGu.getData()){
				String code = (String)map.get("symbol");
				String peTTM = String.valueOf(map.get("pe"));
				params.clear();
				params.add("null".equals(peTTM)?null:peTTM);
				params.add(code);
				params.add(date);
				Index index = new Index(conn,code);
				index.updatePE(params);
			}
			pageNum ++;
		}
	}
	
	public static void checkStkByLaoZhang(Connection conn,IndexContext context, IndexContext contextYesterday, List<Index> newHighs) throws Exception {
		List params = new ArrayList();
		//股价排名前200
		IndexUtils.sortByClose(context.indexs, today);
		IndexUtils.sortByClose(contextYesterday.indexs, yesterday);
		int cnt = 200;
		List<Index> rs = new ArrayList<Index>();
		List<Index> rsYesterday = new ArrayList<Index>();
		for(Index index : context.indexs){
			if(cnt-- == 0)break;
			rs.add(index);
		}
		cnt = 200;
		for(Index index : contextYesterday.indexs){
			if(cnt-- == 0)break;
			rsYesterday.add(index);
		}
		
		//每月RS排名前200
		
		IndexUtils.sortByCloseChange(context.indexs, today, 20);
		IndexUtils.sortByCloseChange(contextYesterday.indexs, yesterday, 20);
		cnt = 200;
		List<Index> results = new ArrayList<Index>();
		List<Index> resultsYesterday = new ArrayList<Index>();
		for(Index index : context.indexs){
			if(cnt-- == 0)break;
			if(rs.contains(index)){
				results.add(index);
				
				params.clear();
				params.add(today);
				params.add(index.getCode());
				params.add(index.getCanslim().getCloseChangeRS());
				params.add(index.getCode());
				params.add(today);
				JdbcUtils.insert(conn, "insert into stk_report_daily (type,report_date,code,remark,insert_time) select 3,?,?,?,sysdate from dual where not exists (select 1 from stk_report_daily where type=3 and code=? and REPORT_DATE=?)", params);
			}
		}
		cnt = 200;
		for(Index index : contextYesterday.indexs){
			if(cnt-- == 0)break;
			if(rsYesterday.contains(index)){
				resultsYesterday.add(index);
			}
		}
		List<List> datas = new ArrayList<List>();
		List<List> datasYesterday = new ArrayList<List>();
		for(Index index : results){
			List data = new ArrayList();
			data.add(index);
			if(newHighs.contains(index)){
				data.add("新高");
			}else{
				data.add("&nbsp;");
			}
			datas.add(data);
		}
		for(Index index : resultsYesterday){
			List data = new ArrayList();
			data.add(index);
			if(newHighs.contains(index)){
				data.add("新高");
			}else{
				data.add("&nbsp;");
			}
			datasYesterday.add(data);
		}
		List<List> added = notContain(datas,datasYesterday);//入榜
		List<List> deleted = notContain(datasYesterday,datas);//出榜
		
		List<String> addtitle = new ArrayList<String>();
		addtitle.add("创"+DAYS_OF_NEWHIGHT_600+"日新高");
		
		String czlz = "1、创历史新高的股票，这一点（熊市末期）非常重要。<br>"+
				      "2、每月涨幅前200名，每两周翻一遍。<br>"+
				      "3、每天涨幅榜前100名的股票。<br>"+
				      "4、股价排名前200，每两周翻一遍。<br>"+
				      "再结合基本面及技术面找出最强的10只及次强的10只作为自选股观察，不定期的再筛选新强加入，弱股剔除。<br>";
		EmailUtils.send("沧州老张选股法,总计："+datas.size()+",日期:"+today, czlz + 
				"入榜：<br>"+StkUtils.createHtmlTable(today, added, addtitle) + "<br>" +
				"出榜：<br>"+StkUtils.createHtmlTable(today, deleted, addtitle) + "<br>" +
				StkUtils.createHtmlTable(today, datas, addtitle));
	}
	
	public static void checkStkCreate250DaysBoxHigh(Connection conn, IndexContext context) throws Exception {
		List params = new ArrayList();
		List<Index> results = new ArrayList<Index>();
		for(Index index : context.indexs){
			if(!index.isStop(today) && !index.getK().isUpLimit() && index.getKs().size() >= 350){
				K startK = index.getK(today, 300);
				K todayK = index.getK();
				K yesterdayK = index.getK(1);
				K last20dayK = index.getK(20);
				K highK = index.getKByHHV(startK.getDate(), yesterdayK.getDate());
				
				//如果最高点就是开始那天，则把开始那天移后10天再算
				if(highK.getDate().equals(startK.getDate())){
					startK = startK.after(10);
					highK = index.getKByHHV(startK.getDate(), yesterdayK.getDate());
				}

				if(highK.getDate().compareTo(last20dayK.getDate()) <= 0
						&& todayK.getClose() * 1.05 >= highK.getClose() 
						&& yesterdayK.getClose() * 1.08 <= highK.getClose()){//创新高
					K lowK = index.getKByLLV(startK.getDate(), today);
					if(lowK.getClose() * 1.8 >= highK.getClose()){//箱体
						results.add(index);
					}
				}
			}
		}
		List<List> datas = new ArrayList<List>();
		for(Index index : results){
			List data = new ArrayList();
			data.add(index);
			data.add(StkUtils.createWeeklyKLine(index));
			datas.add(data);
			
			params.clear();
			params.add(today);
			params.add(index.getCode());
			params.add(index.getCanslim().getCloseChangeRS());
			params.add(index.getCode());
			params.add(today);
			//JdbcUtils.insert(conn, "insert into stk_report_daily (type,report_date,code,remark,insert_time) select 4,?,?,?,sysdate from dual where not exists (select 1 from stk_report_daily where type=4 and code=? and REPORT_DATE=?)", params);
		}
		if(results.size() > 0){
			List<String> addtitle = new ArrayList<String>();
			addtitle.add("周线图");
			String czlz = "重点：突破时候量能不能超过前期高点的量能，且每次调整都是缩量的，而上涨则是温和放量的。<br>";
			EmailUtils.sendAndReport("突破300日、振幅80%以内箱体的股票,总计:"+results.size()+",日期:"+today, czlz + StkUtils.createHtmlTable(today, datas, addtitle)  );
		}
	}
	
	public static void check60DaysRS(Connection conn, IndexContext context, IndexContext contextYesterday) throws Exception {
		List params = new ArrayList();
		Index dp = new Index(conn, "999999");
		K k60 = dp.getK(today, 60);
		K kLow = dp.getKByLLV(k60.getDate(),today);
		if(kLow.getDate().equals(today)){
			EmailUtils.send("大盘创60日新低"+",日期:"+today,"");
		}
		K k60Yesterday = dp.getK(yesterday, 60);
		K kLowYesterday = dp.getKByLLV(k60Yesterday.getDate(),yesterday);
		IndexUtils.sortByCloseChange(context.indexs, kLow.getDate(), today);
		IndexUtils.sortByCloseChange(contextYesterday.indexs, kLowYesterday.getDate(), yesterday);
		
		int cnt = context.indexs.size()/100;
		List<Index> results = new ArrayList<Index>();
		List<Index> resultsYesterday = new ArrayList<Index>();
		for(Index index : context.indexs){
			if(cnt-- == 0)break;
			results.add(index);
		}
		cnt = contextYesterday.indexs.size()/100;
		for(Index index : contextYesterday.indexs){
			if(cnt-- == 0)break;
			resultsYesterday.add(index);
		}
		List<List> datas = new ArrayList<List>();
		List<List> datasYesterday = new ArrayList<List>();
		for(Index index : results){
			List data = new ArrayList();
			data.add(index);
			data.add(StkUtils.createDailyKLine(index));
			datas.add(data);
			
			params.clear();
			params.add(today);
			params.add(index.getCode());
			params.add(index.getCanslim().getCloseChangeRS());
			params.add(index.getCode());
			params.add(today);
			JdbcUtils.insert(conn, "insert into stk_report_daily (type,report_date,code,remark,insert_time) select 5,?,?,?,sysdate from dual where not exists (select 1 from stk_report_daily where type=5 and code=? and REPORT_DATE=?)", params);
		}
		for(Index index : resultsYesterday){
			List data = new ArrayList();
			data.add(index);
			data.add(StkUtils.createDailyKLine(index));
			datasYesterday.add(data);
		}
		List<List> added = notContain(datas,datasYesterday);//入榜
		List<List> deleted = notContain(datasYesterday,datas);//出榜
		if(results.size() > 0){
			List<String> addtitle = new ArrayList<String>();
			addtitle.add("日线图");
			EmailUtils.send("股价60日最强，从大盘"+kLow.getLow()+"["+kLow.getDate()+"]开始计算"+",日期:"+today, 
					"入榜：<br>"+StkUtils.createHtmlTable(today, added, addtitle) + "<br>" +
					"出榜：<br>"+StkUtils.createHtmlTable(today, deleted, addtitle) + "<br>" +
					StkUtils.createHtmlTable(today, datas, addtitle));
		}
	}
	
	//返回src有dest没有的element
	public static List<List> notContain(List<List> src, List<List> dest){
		List<List> results = new ArrayList<List>();
		for(List data : src){
			Index tmp = (Index)data.get(0);
			boolean isContain = false;
			for(List dataYesterday : dest){
				Index tmp2 = (Index)dataYesterday.get(0);
				if(tmp.getCode().equals(tmp2.getCode())){
					isContain = true;
					break;
				}
			}
			if(!isContain){
				results.add(data);
			}
		}
		return results;
	}
	
	public static void checkKIntersect(String today, IndexContext context) throws Exception{
		List<Index> results = new ArrayList<Index>();
		List<Index> results2 = new ArrayList<Index>();
		List<Index> results3 = new ArrayList<Index>();
		List<Index> results4 = new ArrayList<Index>();
		List<Index> results5 = new ArrayList<Index>();
		for(Index index : context.indexs){
			K todayK = index.getK(today);
			if(!index.isStop(today) && todayK != null && !todayK.isUpLimit()){
				K yk = todayK.before(1);
				double todayk120 = todayK.getMA(K.Close, 120);
				double todayk250 = todayK.getMA(K.Close, 250);
				double yk120 = yk.getMA(K.Close, 120);
				double yk250 = yk.getMA(K.Close, 250);
				
				//120日或250日均线走平或上升才研究均线粘合
				if(yk120 <= todayk120 || yk250 <= todayk250){
					if(index.isKIntersect1(today,0)){
						results.add(index);
					}
					if(index.isKIntersect2(today)){
						results2.add(index);
					}
					if(index.isKIntersect3(today)){
						results3.add(index);
					}
					if(index.isKIntersect4(today)){
						results4.add(index);
					}
					if(index.isKIntersect5(today)){
						results5.add(index);
					}
				}
			}
		}
		Collections.sort(results, new Comparator<Index>(){
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		Collections.sort(results2, new Comparator<Index>(){
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		Collections.sort(results3, new Comparator<Index>(){
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		Collections.sort(results4, new Comparator<Index>(){
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		Collections.sort(results5, new Comparator<Index>(){
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		int cnt = results.size()+results2.size()+results3.size()+results4.size()+results5.size();
		if(cnt > 0){
			EmailUtils.sendAndReport("K线缠绕,个数：" + cnt
					+ ",日期:" + today, "<b>切记最好等均线走出多头排列后再入，短期均线金叉长期均线</b>"
					+ "<br>10,20,30,60,120日K线缠绕<br>"	+ StkUtils.createHtmlTable(today, results2)  
					+ "<br><br>10,20,30,60,250日K线缠绕<br>" + StkUtils.createHtmlTable(today, results3) 
					+ "<br><br>10,20,30,60日K线缠绕<br>" + StkUtils.createHtmlTable(today, results4) 
					+ "<br><br>" + StkUtils.createHtmlTable(today, results) 
					+ "<br><br>10,20,30日K线缠绕<br>" + StkUtils.createHtmlTable(today, results5) 
					);
		}
	}
	
	public static void checkCreateHighStk(IndexContext context) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : context.indexs){
			K endK = index.getK(today);
			if(!index.isStop(today) && index.getKs().size() >= 300){
				if(endK.getClose() < endK.getOpen()){
					K startK = endK.before(5);
					K highK = index.getKByHHV(startK.getDate(), endK.getDate());
					if((highK.getOpen()-highK.getClose())/highK.getClose() <= 0.08){//最高点那天不能是大阴线
						K startK2 = endK.before(120);
						K highK2 = index.getKByHHV(startK2.getDate(), endK.getDate());
						//System.out.println("highK="+highK.getDate());
						if(highK.getDate().equals(highK2.getDate())){
							K lowK = index.getKByLLV(highK.before(5).getDate(), highK.getDate());
							K highK3 = index.getKByHHV(endK.before(30).getDate(),lowK.getDate());
							//System.out.println("highK3="+highK3.getDate());
							if((highK.getHigh() - highK3.getHigh())/highK3.getHigh() > 0.15){
								double percent = (highK.getClose()-lowK.getClose())/lowK.getOpen();
								//System.out.println(percent);
								if(percent >= 0.15){
									//System.out.println(index.getCode()+","+index.getName());
									results.add(index);
								}
							}
						}
					}
				}
			}
		}
		if(results.size() > 0){
			EmailUtils.sendAndReport("南京高人战法,个数："+results.size()+",日期:"+today,StkUtils.createHtmlTable(today, results));
		}
	}
	
	public static void checkHighHugeVolume(IndexContext context) throws Exception {
		List<Index> results = new ArrayList<Index>();
		List<Index> results2 = new ArrayList<Index>();
		for(Index index : context.indexs){
			K todayK = index.getK(today);
			if(!index.isStop(today) && todayK != null && !todayK.isUpLimit() && index.getKs().size() > 600){
				K k = index.getKByHVV(today, 600);
				//System.out.println(k.getDate()+","+k.getHigh());
				K day = index.getK(today);
				if(index.getDaysBetween(k.getDate(), today) > 30 && k.getHigh() < day.getHigh() && k.getHigh() > day.before(1).getHigh()){
					results.add(index);
					
					K k2 = index.getKByHVV(today, index.getKs().size()-2);
					if(k2 != null && k2.getDate().equals(k.getDate())){
						results2.add(index);
					}
				}
				
			}
		}
		if((results.size()+results2.size()) > 0){
			EmailUtils.sendAndReport("突破天量,个数："+results.size()+",日期:"+today,
					"突破历史天量：<br>"+StkUtils.createHtmlTable(today, results2)   + "<br><br>" +
					"突破600日天量：<br>" + StkUtils.createHtmlTable(today, results)  );
		}
	}
	
	public static void checkHugeVolumeLittleVolume(List<Index> indexs, String today, int market) throws Exception {
		List<Index> results = new ArrayList<Index>();
		List<Index> results2 = new ArrayList<Index>();
		for(Index index : indexs){
			if(market == 2 && index.getStk().getHot() < US_STK_HOT){
				continue;
			}
			K todayK = index.getK(today);
			if(todayK != null && !index.isStop(today) && !todayK.isUpLimit()){
				String yesterday = todayK.before(1).getDate();
				//缩量1/8
				if(isHugeVolumeLittleVolume2(index, today)){
					if(!isHugeVolumeLittleVolume2(index, yesterday)){
						index.isNew = true;
					}
					results.add(index);
				}else if(isHugeVolumeLittleVolume2(index, yesterday)){
					index.isDeleted = true;
					results.add(index);
				}
				//缩量1/5
				if(isHugeVolumeLittleVolume3(index, today,5)){
					if(!isHugeVolumeLittleVolume3(index, yesterday,5)){
						index.isNew = true;
					}
					results2.add(index);
				}else if(isHugeVolumeLittleVolume3(index, yesterday,5)){
					index.isDeleted = true;
					results2.add(index);
				}
			}
		}
		int cnt = results.size() + results2.size();
		if(cnt > 0){
			if(market == 1){
				Collections.sort(results, new Comparator<Index>(){
					@Override
					public int compare(Index o1, Index o2) {
						int i = (int)((o2.changePercent - o1.changePercent)*10000);
						return i;
					}});
				Collections.sort(results2, new Comparator<Index>(){
					@Override
					public int compare(Index o1, Index o2) {
						int i = (int)((o2.changePercent - o1.changePercent)*10000);
						return i;
					}});
				EmailUtils.sendAndReport("天量后缩量,个数："+cnt+",日期:"
		                   +today,"突破下降趋势线后 或 碰到120/250均线 才能买入,或越多均线粘合时越好"
		                   +"<br><br>长期缩量1/8票<br>"+StkUtils.createHtmlTable(today, results)  
		                   +"<br><br>短期60日缩量1/5票<br>"+StkUtils.createHtmlTable(today, results2)  
		                   );
			}else if(market == 2){
				Collections.sort(results, new Comparator<Index>(){
					public int compare(Index o1, Index o2) {
						return (o2.getStk().getHot() - o1.getStk().getHot());
					}});
				Collections.sort(results2, new Comparator<Index>(){
					public int compare(Index o1, Index o2) {
						return (o2.getStk().getHot() - o1.getStk().getHot());
					}});
				EmailUtils.send("[美股]天量后缩量,个数："+cnt+",日期:"+today,
						"长期缩量1/8票<br>"+ StkUtils.createHtmlTable(today, results)
						+"<br><br>短期60日缩量1/5票<br>"+StkUtils.createHtmlTable(today, results2)
						);
			}
		}
	}
	
	private static boolean isHugeVolumeLittleVolume2(Index index, String today) throws Exception {
		K todayK = index.getK(today);
		if(index.getKs().size() > 600){
			K startK = index.getK(todayK.getDate(), 500);
			K highK = index.getKByHHV(startK.getDate(), todayK.getDate());
			K lowK = index.getKByLLV(startK.getDate(), todayK.getDate());
			
			//System.out.println((lowK.getClose() * 3)+","+highK.getClose());
			if(lowK.getClose() * 3 >= highK.getClose()){//箱体
				K k = index.getKByHVV(todayK.getDate(), 500);
				//System.out.println("highK="+k.getDate());
				int betweenDays = index.getDaysBetween(k.getDate(), today);
				if(betweenDays < 120 && betweenDays > 60){ 
					K k2 = k.after(4).getMax(K.Volumn, 5, K.MA, 5);
					double ma = k2.getMA(K.Volumn, 5);
					double maToday = todayK.getMA(K.Volumn, 5);
					//System.out.println("ma="+ma+",maToday="+maToday);
					if(ma/maToday >= 8){
						//System.out.println(index.getCode()+","+index.getName());
						index.changePercent = ma/maToday;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean isHugeVolumeLittleVolume3(Index index, String today, int flag) throws Exception {
		K todayK = index.getK(today);
		if(index.getKs().size() > 120){
			K startK = index.getK(todayK.getDate(), 60);
			K highK = index.getKByHHV(startK.getDate(), todayK.getDate());
			K lowK = index.getKByLLV(startK.getDate(), todayK.getDate());
			
			//System.out.println((lowK.getClose() * 2.5)+","+highK.getClose());
			if(lowK.getClose() * 2.5 >= highK.getClose()){//箱体
				K k = index.getKByHVV(todayK.getDate(), 120);
				//System.out.println("highK="+k.getDate());
				int betweenDays = index.getDaysBetween(k.getDate(), today);
				if(betweenDays <= 60){
					K k2 = k.after(4).getMax(K.Volumn, 5, K.MA, 5);
					double ma = k2.getMA(K.Volumn, 5);
					double maToday = todayK.getMA(K.Volumn, 5);
					//System.out.println("short ma="+ma+",maToday="+maToday+",ma/maToday="+(ma/maToday));
					if(ma/maToday >= flag){
						//System.out.println(index.getCode()+","+index.getName());
						index.changePercent = ma/maToday;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean isHugeVolumeLittleVolume4(Index index, String today, int flag) throws Exception {
		K todayK = index.getK(today);
		if(index.getKs().size() > 60){
			K startK = index.getK(todayK.getDate(), 30);
			K highK = index.getKByHHV(startK.getDate(), todayK.getDate());
			K lowK = index.getKByLLV(startK.getDate(), todayK.getDate());
			
			//System.out.println((lowK.getClose() * 2.5)+","+highK.getClose());
			if(lowK.getClose() * 2 >= highK.getClose()){//箱体
				K k = index.getKByHVV(todayK.getDate(), 30);
				//System.out.println("highK="+k.getDate());
				int betweenDays = index.getDaysBetween(k.getDate(), today);
				if(betweenDays <= 30){
					K k2 = k.after(4).getMax(K.Volumn, 5, K.MA, 5);
					if(k2 != null){
						double ma = k2.getMA(K.Volumn, 5);
						double maToday = todayK.getMA(K.Volumn, 5);
						//System.out.println("short ma="+ma+",maToday="+maToday+",ma/maToday="+(ma/maToday));
						if(ma/maToday >= flag){
							//System.out.println(index.getCode()+","+index.getName());
							index.changePercent = ma/maToday;
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public static void checkWangPeng(IndexContext context) throws Exception {
		/**
		 * 1. 10日内有9.5%以上涨幅的
		 * 2. 今天量能缩到前面一半以下
		 * 3. 今天收盘在10日线上的
		 * 4. 今日收盘离250日线排序
		 */
		List<Index> indexs = new ArrayList<Index>();
		//today = "20150403";
		for(Index index : context.indexs){
			K todayK = index.getK(today);
			if(index.isStop(today) || (todayK != null && todayK.getClose() <= 8))continue;
			K k = index.getKWithCondition(today, 10, new K.Condition(){
				public boolean pass(K k) throws Exception {
					if(k.getChangeOfClose() >= 9.5){
						return true;
					}
					return false;
				}
			});
			if(k != null){
				
				if(todayK.getVolumn() <= k.getVolumn() * 0.5 
						//&& todayK.getClose() >= k.getClose()
						&& todayK.getClose() >= todayK.getMA(K.Close, 10)
						&& (todayK.getClose() - k.getClose())/k.getClose() <= 0.1){
	
					/*double ma10 = todayK.getMA(K.Close, 10);
					double absChange = Math.abs((todayK.getClose() - ma10)/ma10);
					if(absChange <= 0.01){*/
						//System.out.println(index.getCode()+","+index.getName());
						double ma250 = todayK.getMA(K.Close, 250);
						index.changePercent = (todayK.getClose() - ma250)/ma250;
						indexs.add(index);
					//}
				}
			}
		}
		Collections.sort(indexs, new Comparator<Index>(){
			@Override
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		if(indexs.size() > 0){
			EmailUtils.sendAndReport("王朋选股,个数："+indexs.size()+",日期:"+today,StkUtils.createHtmlTable(today, indexs));
			//EmailUtils.send(new String[]{"66933859@qq.com","327226952@qq.com"}, "选股,个数："+indexs.size()+",日期:"+today,StkUtils.createHtmlTable2(today, indexs));
		}
	}
	
	
	public static void checkWangPeng1(IndexContext context) throws Exception {
		List<Index> indexs = new ArrayList<Index>();
		//today = "20150403";
		for(Index index : context.indexs){
			K todayK = index.getK(today);
			if(index.isStop(today) || (todayK != null && todayK.isUpLimit())){
				continue;
			}
			K k = index.getKWithCondition(today, 10, new K.Condition(){
				public boolean pass(K k) throws Exception {
					if(k.getChangeOfClose() >= 6){
						return true;
					}
					return false;
				}
			});
			if(k != null){
				int cnt = index.getKCountWithCondition(k.after(1).getDate(), today, new K.Condition(){
					public boolean pass(K k) throws Exception {
						if(Math.abs(k.getChangeOfClose()) > 3){
							return true;
						}
						return false;
					}
				});
				if(cnt > 0){
					int betweenDays = index.getDaysBetween(k.getDate(), today);
					if(((double)cnt)/betweenDays >= 0.25){
						continue;
					}
				}
				
				if(todayK.getVolumn() <= k.getVolumn() * 0.5 
						&& todayK.getClose() >= k.getClose() * 0.97
						&& todayK.getClose() >= todayK.getMA(K.Close, 10)){
					double ma10 = todayK.getMA(K.Close, 10);
					double absChange = Math.abs((todayK.getClose() - ma10)/ma10);
					if(absChange <= 0.03){
						//System.out.println(index.getCode()+","+index.getName());
						double ma250 = todayK.getMA(K.Close, 250);
						index.changePercent = (todayK.getClose() - ma250)/ma250;
						indexs.add(index);
					}
				}
			}
		}
		Collections.sort(indexs, new Comparator<Index>(){
			@Override
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		if(indexs.size() > 0){
			EmailUtils.sendAndReport("王朋选股优化-1,个数："+indexs.size()+",日期:"+today,StkUtils.createHtmlTable(today, indexs));
		}
	}
	
	public static void checkWangPeng2(IndexContext context) throws Exception {
		List<Index> indexs = new ArrayList<Index>();
		//today = "20150403";
		for(Index index : context.indexs){
			K todayK = index.getK(today);
			if(index.isStop(today) || todayK == null || todayK.isUpLimit() || index.getStock().getTotalCapital()/10000 >= 25)continue;
			
			K k = index.getKWithCondition(todayK.before(1).getDate(), 10, new K.Condition(){
				public boolean pass(K k) throws Exception {
					if(k.getChangeOfClose() >= 5){
						return true;
					}
					return false;
				}
			});
			//System.out.println(k);
			if(k != null){
				//判断有没有长上影线，有则排除
				int ds = index.getDaysBetween(k.getDate(), today);
				K k3 = index.getKWithCondition(today, ds, new K.Condition(){
					public boolean pass(K k) {
						double maxOpenAndClose = Math.max(k.getOpen(), k.getClose());
						if((k.getHigh() - maxOpenAndClose)/Math.abs(k.getOpen() - k.getClose()) > 6.0
								&& (k.getHigh() - maxOpenAndClose)/maxOpenAndClose > 0.05){
							return true;
						}
						return false;
					}
				});
				//System.out.println(k3);
				if(k3 == null){
					double ma10 = todayK.getMA(K.Close, 10);
					//System.out.println(ma10+","+todayK.getClose());
					K kv = index.getKByHVV(k.after(3).getDate(), 6);
					//最大量必须是阳线
					if(kv.getOpen() > kv.getClose())continue;
					
					if(todayK.getVolumn() <= kv.getVolumn() * 0.6
							&& (k.before(1).getClose()+k.getClose())/2 < todayK.getClose()
							//&& (todayK.getClose() - k.getClose())/k.getClose() <= 0.1
							&& todayK.getClose() >= ma10 * 0.96
							&& todayK.getClose() <= ma10 * 1.20){
						K k2 = index.isKIntersect(today, 60);
						//System.out.println(k2);
						if(k2 != null){
							K k4 = index.getKByHVV(k2.getDate(), today);
							ds = index.getDaysBetween(k4.getDate(), k.getDate());
							if(ds >= 10)continue;
							
							//一字涨停太多不在考虑范围
							ds = index.getKCountWithCondition(k2.before(3).getDate(), today, new K.Condition(){
								public boolean pass(K k) {
									if(k.isUpLimit()){
										return true;
									}
									return false;
								}
							});
							//System.out.println(ds+","+kv);
							if(ds >= 3 && todayK.getClose() < kv.getClose())continue;
							//System.out.println(index.getCode()+","+index.getName()+",k intersect="+k2.getDate());
							ds = index.getDaysBetween(k2.getDate(), today);
							/*ds = index.getKCountWithCondition(k2.getDate(), today, new K.Condition(){
								public boolean pass(K k) {
									if(k.getOpen() > k.getClose()){
										return true;
									}
									return false;
								}
							});*/
							//System.out.println(index.getCode()+","+ds+","+k2.getDate()+",");
							index.changePercent = ds;
							indexs.add(index);
						}
					}
				}
			}
		}
		Collections.sort(indexs, new Comparator<Index>(){
			@Override
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		if(indexs.size() > 0){
			EmailUtils.sendAndReport("王朋选股优化-2,个数："+indexs.size()+",日期:"+today,StkUtils.createHtmlTable(today, indexs));
		}
	}
	
	//底部放量，博彦科技，克明面业
	public static void checkVolumeGreaterThanLastVolume(String today,IndexContext context, double flag)  throws Exception  {
		List<Index> indexs = new ArrayList<Index>();
		List<Index> indexs2 = new ArrayList<Index>();
		//today = "20150403";
		for(Index index : context.indexs){
			if(checkVolumeGreaterThanLastVolume(index, today, 120, 50, 20, flag)){
				indexs.add(index);
			}
			if(checkVolumeGreaterThanLastVolume(index, today, 50, 20, 8, 1)){
				indexs2.add(index);
			}
		}
		
		int cnt = indexs.size() + indexs2.size();
		if(cnt > 0){
			Collections.sort(indexs, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o1.changePercent - o2.changePercent)*10000);
					return i;
				}});
			Collections.sort(indexs2, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o1.changePercent - o2.changePercent)*10000);
					return i;
				}});
			EmailUtils.sendAndReport("价格低于前高，量超前量,个数："+ cnt +",日期:"+today,
					"长期票<br>" + StkUtils.createHtmlTable(today, indexs) +
					"<br><br>短期票<br>" + StkUtils.createHtmlTable(today, indexs2)
					);
		}

	}
	
	public static boolean checkVolumeGreaterThanLastVolume(Index index, String today, int m, int n, int x, double flag) throws Exception {
		K todayK = index.getK(today);
		if(todayK != null && !index.isStop(today)){
			//List<K> ks = index.getKsHistoryHigh(today, 120, 50);
			List<K> ks = index.getKsHistoryHighPoint(today, m, n);
			//System.out.println("ks="+ks);
			if(ks != null && ks.size() > 0){
				K k = ks.get(ks.size()-1);
				K lowK = index.getKByLCV(k.getDate(), today);
				if(lowK != null) {
					K highK = index.getKByHHV(lowK.getDate(), today);
					//System.out.println("k="+k.getDate()+",lowk="+lowK.getDate()+",highk="+highK.getDate());
					if(highK.getHigh() < k.getHigh() && todayK.getClose() < k.getClose() && todayK.getClose() >= (lowK.getClose() + k.getClose())/2){
						double sum = k.after(5).getSUM(K.Volumn, x);
						double sum2 = todayK.getSUM(K.Volumn, x);
						if(sum * flag <= sum2){
							//System.out.println(index.getCode()+","+index.getName()+",k="+k.getDate()+","+sum+","+sum2);
							index.changePercent = sum/sum2 * Math.pow(todayK.getClose()/k.getHigh(),5);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	//黄金三角：10日均线上穿20日均线，20日均线上穿30日均线
	public static void checkUSGoldTriangle(String today, List<Index> indexs) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			K todayK = index.getK(today);
			if(todayK != null && !index.isStop(today) && todayK.getClose() > 3 && !todayK.isNoVolumeOrNoChange()){
				double ma20 = todayK.getMA(K.Close, 20);
				double ma30 = todayK.getMA(K.Close, 30);
				if(ma20 >= ma30){
					K yesterdayK = todayK.before(1);
					ma20 = yesterdayK.getMA(K.Close, 20);
					ma30 = yesterdayK.getMA(K.Close, 30);
					if(ma20 <= ma30){
						K k = index.getKWithCondition(todayK.getDate(), 12, new K.Condition(){
							public boolean pass(K k) throws Exception {
								double ma10 = k.getMA(K.Close, 10);
								double ma20 = k.getMA(K.Close, 20);
								if(ma10 >= ma20){
									K yesterdayK = k.before(1);
									ma10 = yesterdayK.getMA(K.Close, 10);
									ma20 = yesterdayK.getMA(K.Close, 20);
									if(ma10 <= ma20){
										return true;
									}
								}
								return false;
							}
						});
						if(k != null){
							results.add(index);
						}
					}
				}
			}
		}
		
		if(results.size() > 0){
			Collections.sort(results, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					return (o2.getStk().getHot() - o1.getStk().getHot());
				}});
			EmailUtils.send("[美股]黄金三角,总计:"+results.size()+",日期:"+today, StkUtils.createHtmlTable(today, results));
		}
	}
	
	
	public static void checkUSKIntersect(String today, List<Index> indexs) throws Exception{
		List<Index> results = new ArrayList<Index>();
		List<Index> results2 = new ArrayList<Index>();
		List<Index> results3 = new ArrayList<Index>();
		List<Index> results4 = new ArrayList<Index>();
		for(Index index : indexs){
			K todayK = index.getK(today);
			if(!index.isStop(today) && todayK != null && !todayK.isUpLimit() && index.getStk().getHot() >= US_STK_HOT){
				String yesterday = todayK.before(1).getDate();
				if(index.isKIntersect3(today)){
					boolean b = index.isKIntersect3(yesterday);
					if(!b){
						index.isNew = true;
					}
					results3.add(index);
				}else{
					if(index.isKIntersect3(yesterday)){
						index.isDeleted = true;
						results3.add(index);
					}
					if(index.isKIntersect2(today)){
						boolean b = index.isKIntersect2(yesterday);
						if(!b){
							index.isNew = true;
						}
						results2.add(index);
					}else{
						if(index.isKIntersect2(yesterday)){
							index.isDeleted = true;
							results2.add(index);
						}
						if(index.isKIntersect1(today,0)){
							boolean b = index.isKIntersect1(yesterday,0);
							if(!b){
								index.isNew = true;
							}
							results.add(index);
						}
						if(index.isKIntersect4(today)){
							boolean b = index.isKIntersect4(yesterday);
							if(!b){
								index.isNew = true;
							}
							results4.add(index);
						}
					}
				}
			}
		}
		int cnt = results.size()+results2.size()+results3.size()+results4.size();
		if(cnt > 0){
			Collections.sort(results, new Comparator<Index>(){
				public int compare(Index o1, Index o2) {
					return (o2.getStk().getHot() - o1.getStk().getHot());
				}});
			Collections.sort(results2, new Comparator<Index>(){
				public int compare(Index o1, Index o2) {
					return (o2.getStk().getHot() - o1.getStk().getHot());
				}});
			Collections.sort(results3, new Comparator<Index>(){
				public int compare(Index o1, Index o2) {
					return (o2.getStk().getHot() - o1.getStk().getHot());
				}});
			Collections.sort(results4, new Comparator<Index>(){
				public int compare(Index o1, Index o2) {
					return (o2.getStk().getHot() - o1.getStk().getHot());
				}});
			EmailUtils.send("[美股]K线缠绕,个数：" + cnt
					+ ",日期:" + today, "<b>切记最好等均线走出多头排列后再入</b>"
					+ "<br>10,20,30,60,250日K线缠绕<br>" + StkUtils.createHtmlTable(today, results3)
					+ "<br><br>10,20,30,60,120日K线缠绕<br>" + StkUtils.createHtmlTable(today, results2)
					+ "<br><br>10,20,30,60日K线缠绕<br>" + StkUtils.createHtmlTable(today, results4)
					+ "<br><br>" + StkUtils.createHtmlTable(today, results));
		}
	}
	
	public static void checkUSKUpTrendLine(String today, List<Index> indexs) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			K todayK = index.getK(today);
			if(todayK != null && !index.isStop(today) && todayK.getClose() > 3 && !todayK.isNoVolumeOrNoChange()){
				if(index.isBreakOutShortTrendLine(today)){
					results.add(index);
				}
			}
		}
		if(results.size() > 0){
			Collections.sort(results, new Comparator<Index>(){
				public int compare(Index o1, Index o2) {
					return (o2.getStk().getHot() - o1.getStk().getHot());
				}});
			EmailUtils.send("[美股]K线突破下降趋势线,个数："+results.size()+",日期:"+today,StkUtils.createHtmlTable(today, results));
		}
	}
	
	//一品抄底-底部
	public static int checkYpcdDiBu(Connection conn, String today, List<Index> indexs, int market) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			if(market == 2 && index.getStk().getHot() < US_STK_HOT){
				continue;
			}
			K k = index.getK(today);
			if(k == null || index.isStop(today) || k.getClose() <= 3 || k.isNoVolumeOrNoChange()){
				continue;
			}
			if(k.getYpcd()){
				results.add(index);
			}
		}
		if(results.size() > 0){
			if(market == 1){
				Strategy.logToDB(conn, today, "一品抄底-底部", results);
				EmailUtils.sendAndReport("一品抄底-底部,个数："+results.size()+",日期:"+today,"一品抄底要点：三根线要贴近0轴，MACD也要贴近0轴<br>"+
						StkUtils.createHtmlTable(today, results)  );
			}
		}
		return results.size();
	}
	
	//一品抄底-MACD
	public static int checkYiPinChaoDiMACD(Connection conn, String today, List<Index> indexs, int market) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			K k = index.getK(today);
			if(k == null || index.isStop(today) || k.getClose() <= 3 || k.isNoVolumeOrNoChange()){
				continue;
			}
			double macd = getMACD(k);
			//System.out.println(k.getDate()+"="+macd);
			if(macd >= 4){
				K yk = k.before(1);
				double ymacd = getMACD(yk);
				if(ymacd < 4 && ymacd > -2){
					int cnt = k.getKCountWithCondition(10, new K.Condition() {
						@Override
						public boolean pass(K k) throws Exception {
							double m = getMACD(k);
							if(m >= 5)return true;
							return false;
						}
					});
					int cnt2 = k.getKCountWithCondition(10, new K.Condition() {
						@Override
						public boolean pass(K k) throws Exception {
							double m = getMACD(k);
							if(m < 3 && m > -3)return true;
							return false;
						}
					});
					
					if(cnt > 0 || cnt2 >= 6){
						cnt = k.getKCountWithCondition(8, new K.Condition() {
							@Override
							public boolean pass(K k) throws Exception {
								double m = getMACD(k);
								if(m < -5)return true;
								return false;
							}
						});
						cnt += k.getKCountWithCondition(20, new K.Condition() {
							@Override
							public boolean pass(K k) throws Exception {
								double m = getMACD(k);
								if(m > 40)return true;
								return false;
							}
						});
						//System.out.println(cnt);
						if(cnt == 0){
							cnt = k.getKCountWithCondition(10, new K.Condition() {
								@Override
								public boolean pass(K k) throws Exception {
									if(getMACD(k) > -3)return true;
									return false;
								}
							});
							//System.out.println(cnt);
							if(cnt >= 5){
								double trend = getTrend(k);
								if(trend < 25){
									//System.out.println(index.getCode()+","+index.getName()+"="+macd);
									results.add(index);
								}
							}
						}
					}
				}
			}
		}
		
		if(results.size() > 0){
			if(market == 1){
				Strategy.logToDB(conn, today, "一品抄底-MACD", results);
				EmailUtils.sendAndReport("一品抄底-MACD,个数："+results.size()+",日期:"+today,"一品抄底要点：三根线要贴近0轴，MACD也要贴近0轴<br>"+
						StkUtils.createHtmlTable(today, results)  );
			}else if(market == 2){
				Collections.sort(results, new Comparator<Index>(){
					public int compare(Index o1, Index o2) {
						return (o2.getStk().getHot() - o1.getStk().getHot());
					}});
				EmailUtils.send("[美股]一品抄底-MACD,个数："+results.size()+",日期:"+today,StkUtils.createHtmlTable(today, results));
			}
		}
		return results.size();
	}
	
	//一品抄底-趋势线
	public static double getTrend(K k) throws Exception {
		double trend = k.getMA(2, new K.Calculator() {
			@Override
			public double calc(K k) throws Exception {
				double sma_a4_6 = k.getEMA(6, 1, new K.Calculator() {
					@Override
					public double calc(K k) throws Exception {
						double llv21 = k.getLLV(21);
						double a4 = ((k.getClose() - llv21)/(k.getHHV(21) - llv21)) * 100;
						return a4;
					}
				});
				
				double sma_a4_5_2 = k.getEMA(5, 1, new K.Calculator() {
					@Override
					public double calc(K k) throws Exception {
						double sma_a4_5 = k.getEMA(5, 1, new K.Calculator() {
							@Override
							public double calc(K k) throws Exception {
								double llv21 = k.getLLV(21);
								double a4 = ((k.getClose() - llv21)/(k.getHHV(21) - llv21)) * 100;
								return a4;
							}
						});
						return sma_a4_5;
					}
				});
				return 3 * sma_a4_6 - 2 * sma_a4_5_2;
			}
		});
		return trend;
	}
	//一品抄底-黑马线
	public static double getHorse(K k) throws Exception {
		double horse = k.getEMA(5, 2, new K.Calculator() {
			@Override
			public double calc(K k) throws Exception {
				return k.getEMA(5, 1, new K.Calculator() {
					@Override
					public double calc(K k) throws Exception {
						double llv55 = k.getLLV(55);
						return ((k.getClose() - llv55)/(k.getHHV(55) - llv55)) * 100;
					}
				});
			}
		});
		return horse;
	}
	//一品抄底-MACD
	public static double getMACD(K k) throws Exception {
		double horse = getHorse(k);
		double trend = getTrend(k);
		double macd = trend - horse;
		return macd;
	}
	
	//一品抄底-大势已去
	public static void checkYiPinChaoDiDaShiYiQu(Connection conn, String today, List<Index> indexs) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			K k = index.getK(today);
			if(k == null || index.isStop(today) || k.isNoVolumeOrNoChange()){
				continue;
			}
			double trend = InitialKLine.getTrend(k);
			double horse = InitialKLine.getHorse(k);
			if(trend > 90 && horse > 86){
				k = k.before(1);
				double ytrend = InitialKLine.getTrend(k);
				double yhorse = InitialKLine.getHorse(k);
				if(ytrend > trend && yhorse > horse){
					//System.out.println(k.getDate()+"="+trend+","+horse);
					results.add(index);
				}
			}
		}
		if(results.size() > 0){
			String title = "一品抄底-大势已去,个数："+results.size()+",日期:"+today;
			if(results.size() > 100){
				title = EmailUtils.IMPORTANT + title;
			}
			EmailUtils.sendAndReport(title,StkUtils.createHtmlTable(today, results));
			List params = new ArrayList();
			params.add(results.size());
			params.add(today);
			JdbcUtils.update(conn, "update stk_pe set result_2=? where report_date=?", params);
		}
	}
	
	//二品抄底-买入时机
	public static List<Index> checkErPinChaoDi(Connection conn, String today, List<Index> indexs, int market, boolean sendMail, boolean weekly) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			/*if(market == 2 && index.getStk().getHot() < US_STK_HOT){
				continue;
			}*/
			K k = index.getK(today);
			if(k != null && !index.isStop(today)){
				if(weekly)index.getKsWeekly(true);
				double d = InitialKLine.maiRuShiJi(index, k.getDate());
				double yd = InitialKLine.maiRuShiJi(index, k.before(1).getDate());;
				if(d < 5 && d - yd >= 3){
					//System.out.println(k.getDate()+"="+d);
					if(market == 2){
						index.changePercent = index.getK().getSlopeOfMA(30, 5);
					}else{
						index.changePercent = d;
					}
					results.add(index);
				}
			}
			if(weekly)index.gc();
		}
		if(results.size() > 0 && sendMail){
			if(market == 1){
				Strategy.logToDB(conn, today, "二品抄底-买入时机", results);
				String title = "二品抄底-买入时机,个数："+results.size()+",日期:"+	today;
				if(results.size() >= 150){
					title = EmailUtils.IMPORTANT + title;
				}
				EmailUtils.sendAndReport(title,"重点观察：一品抄底 趋势线 拐头向上 或 已经上叉黑马线在上方运行；同时ENE Upper快大于Lower数<br>" +
						StkUtils.createHtmlTable(today, results)  );
				List params = new ArrayList();
				params.add(results.size());
				params.add(today);
				JdbcUtils.update(conn, "update stk_pe set result_1=? where report_date=?", params);
			}else if(market == 2){
				Collections.sort(results, new Comparator<Index>(){
					public int compare(Index o1, Index o2) {
						return (int)((o2.changePercent - o1.changePercent)*10000);
					}});
				/*for(Index index : results){
					System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
				}*/
				EmailUtils.send("[美股]"+(weekly?"[周线]":"")+"二品抄底-买入时机,个数："+results.size()+",日期:"+today,StkUtils.createHtmlTable(today, results));
			}
		}
		return results;
	}
	
	//二品抄底-买入时机
	public static double maiRuShiJi(K k) throws Exception {
		if(k != null){
			//K:=SMA(RSV,3,1);
			double y = k.getEMA(3, 1, new K.Calculator(){
				public double calc(K k) throws Exception {
					double llv = k.getLLV(9);
					double hhv = k.getHHV(9);
					return (k.getClose() - llv) / (hhv - llv) * 100;
				}}
			);
			//D:=SMA(K,3,1);
			double z = k.getEMA(3, 1, new K.Calculator(){
				public double calc(K k) throws Exception {
					double y = k.getEMA(3, 1, new K.Calculator(){
						public double calc(K k) throws Exception {
							double llv = k.getLLV(9);
							double hhv = k.getHHV(9);
							return (k.getClose() - llv) / (hhv - llv) * 100;
						}}
					);
					return y;
				}}
			);
			//J:=3*K-2*D;
			double v = 3*y - 2*z;
			/*if(v <= 5){
				//System.out.println(index.getCode()+","+index.getName()+"="+v);
				System.out.println(today+"="+v);
			}*/
			return v;
		}
		return 1000;
	}
	public static double maiRuShiJi(Index index, String today) throws Exception {
		K k = index.getK(today);
		return maiRuShiJi(k);
	}
	
	//连续涨停后开板
	public static void checkKaiBan(String today, List<Index> indexs) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			K k = index.getK(today);
			if(k != null && !index.isStop(today)){
				if(!k.isUpLimit()){
					k = k.before(1);
					if(k != null && k.isUpLimit()){
						k = k.before(1);
						if(k != null && k.isUpLimit()){
							results.add(index);
						}
					}
				}
			}
		}
		if(results.size() > 0){
			EmailUtils.sendAndReport("连续涨停后开板,个数："+results.size()+",日期:"+today,StkUtils.createHtmlTable(today, results));
		}
	}
	
	public static void checkMaybeKaiBan(String today, List<Index> indexs) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			K k = index.getK(today);
			if(k != null && !index.isStop(today)){
				if(k.isUpLimit()){
					K yk = k.before(1);
					if(yk != null && yk.isUpLimit()){
						if(k.getVolumn() >= yk.getVolumn() * 2){
							results.add(index);
						}
					}
				}
			}
		}
		if(results.size() > 0){
			EmailUtils.send("连续涨停后马上开板股,个数："+results.size()+",日期:"+today,StkUtils.createHtmlTable(today, results));
		}
	}
	
	public static void checkWeeklyTrandLine(String today, List<Index> indexs) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			//System.out.println(index.getCode());
			K todayK = index.getK(today);
			if(todayK != null && !index.isStop(today) && todayK.getClose() > 3 
					&& !todayK.isNoVolumeOrNoChange()){
				index.getKsWeekly(true);
	
				List<List<K>> list2 = index.isBreakOutTrendLine3(today, 60, 5, 0);
				if(list2.size() > 0 || index.isBreakOutTrendLine2(today,60,6,0.05)){
					index.changePercent = todayK.getSlopeOfMA(30, 5);
					results.add(index);
				}
			}
			index.gc();
		}
		if(results.size() > 0){
			Collections.sort(results, new Comparator<Index>(){
				public int compare(Index o1, Index o2) {
					return (int)((o2.changePercent - o1.changePercent)*10000);
				}});
			/*for(Index index : results){
				System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
			}*/
			EmailUtils.send("[美股][周线]趋势线突破,个数："+results.size()+",日期:"+today,StkUtils.createHtmlTable(today, results));
		}
	}
	
	public static void checkMonthlyTrandLine(String today, List<Index> indexs) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			//System.out.println(index.getCode());
			K todayK = index.getK(today);
			if(todayK != null && !index.isStop(today) && todayK.getClose() > 3 
					&& !todayK.isNoVolumeOrNoChange()){
				index.getKsMonthly(true);
	
				List<List<K>> list2 = index.isBreakOutTrendLine3(today, 30, 5, 1);
				if(list2.size() > 0 || index.isBreakOutTrendLine2(today,30,5,0.05)){
					index.changePercent = todayK.getSlopeOfMA(30, 5);
					results.add(index);
				}
			}
			index.gc();
		}
		if(results.size() > 0){
			Collections.sort(results, new Comparator<Index>(){
				public int compare(Index o1, Index o2) {
					return (int)((o2.changePercent - o1.changePercent)*10000);
				}});
			/*for(Index index : results){
				System.out.println(index.getCode()+","+index.getName()+",change="+index.changePercent);
			}*/
			EmailUtils.send("[美股][月线]趋势线突破,个数："+results.size()+",日期:"+today,StkUtils.createHtmlTable(today, results));
		}
	}
	
	public static void checkMACDWeekly(String today, List<Index> indexs) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			//System.out.println(index.getCode());
			K todayK = index.getK(today);
			if(todayK != null && !index.isStop(today) && todayK.getClose() > 3 
					&& !todayK.isNoVolumeOrNoChange()){
				List<K> ks = index.getKsWeekly();
				if(ks != null && ks.size() > 0){
					K k = ks.get(0);
					K.MACD m = k.getMACD();
					K.MACD my = k.before(1).getMACD();
					//System.out.println(k.getDate()+","+k.getMACD());
					double d = Math.abs(m.dea-m.dif);
					if(m.macd <= 0.4 && m.macd >= -0.3 
							&& ((m.dea >= m.dif && d <= 0.3) || (m.dea < m.dif && d <= 0.2 && m.macd > my.macd)) 
							&& m.dif >= my.dif){
						index.changePercent = todayK.getSlopeOfMA(30, 5);
						results.add(index);
					}
				}
			}
			index.gc();
		}
		if(results.size() > 0){
			Collections.sort(results, new Comparator<Index>(){
				public int compare(Index o1, Index o2) {
					return (int)((o2.changePercent - o1.changePercent)*10000);
				}});
			EmailUtils.send("[美股][周线]MACD粘合选股,个数："+results.size()+",日期:"+today,StkUtils.createHtmlTable(today, results));
		}
	}
	
	//开板新高股
	public static void checkKaiBanXinGao(String today, List<Index> indexs) throws Exception {
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			K k = index.getK(today);
			if(k != null && !k.isUpLimit()){
				int cnt = k.getKCountWithCondition(10, new K.Condition() {
					public boolean pass(K k) throws Exception {
						if(k.isUpLimit())return true;
						return false;
					}
				});
				if(cnt >= 3){
					K ky = k.before(1);
					if(k.getClose() == k.getHCV(10) && 
						((ky.getHCV(10) != ky.getClose() && !ky.isUpLimit()) || ky.isUpLimit())
						&& k.getOpen() != k.getClose()
					  ){
						//System.out.println(index.getCode()+","+index.getName());
						results.add(index);
					}
				}
			}
		}
		if(results.size() > 0){
			EmailUtils.send("连续涨停，开板后，新高股,个数："+results.size()+",日期:"+today,StkUtils.createHtmlTable(today, results));
		}
	}
	
	public static void checkIndustryReport(Connection conn) throws Exception{
		StkImportInfoType type = News.getType(220);
		List<String> results = new ArrayList<String>();
		String page = HttpUtils.get("http://www.hibor.com.cn/elitelist_1_0.html", "GBK");
		List<Node> nodes = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "div", "class", "classbaogao_sousuo_list");
		for(Node node : nodes){			
			String title = ((LinkTag)HtmlUtils.getNodeByTagName(node, "a")).getAttribute("title");
			boolean match = false;
			String[] patterns = type.getMatchPattern().split(StkConstant.MARK_SEMICOLON);
			for(String pattern : patterns){
				if(StkUtils.getMatchString(title, pattern) != null){
					if(type.getNotMatchPattern() != null && StkUtils.getMatchString(title, type.getNotMatchPattern().split(StkConstant.MARK_SEMICOLON)) != null){
						continue;
					}
					match = true;
					break;
				}
			}
			if(match){
				results.add(node.toHtml());
			}
		}
		System.out.println(results);
	}
	
	public static void checkChaoDiAndFlow(Connection conn,String today) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("select s.code,s.name,f.flow_date,f.main_amount,f.main_percent+f.super_large_percent percent ");
		sql.append("from stk_capital_flow f, stk_cn s, stk_kline k ");
		sql.append("where f.code=s.code and f.code=k.code and f.flow_date=k.kline_date and f.flow_date=? ");
		sql.append("and k.open!=k.close and k.open!=k.high ");
		sql.append("and f.main_percent+f.super_large_percent>=25 ");
		sql.append("order by f.flow_date,f.main_percent+f.super_large_percent desc");

		List param = new ArrayList();
		param.add(today);
		List<Map> list = JdbcUtils.list2Map(conn, sql.toString(), param);

		List<Index> results = new ArrayList<Index>();
		for(Map m : list){
			String code = (String)m.get("code");
			String date = (String)m.get("flow_date");
			Index index = new Index(conn, code);
			if(index.getTotalMarketValue() > 300)continue;
			K k = index.getK(date);
			int cnt = k.getKCountWithCondition(10, new K.Condition() {
				public boolean pass(K k) throws Exception {
					return k.getEpcd() || k.getYpcd();
				}
			});
			if(cnt > 0){
				results.add(index);
			}
		}
		if(results.size() > 0){
			EmailUtils.sendAndReport("资金流入且离底部不远,个数："+results.size()+",日期:"+today,"抄底要点：一品抄底三根线要贴近0轴，MACD也要贴近0轴<br>"+
					StkUtils.createHtmlTable(today, results)  );
		}
	}
	
	public static void strategy(Connection conn, boolean realtime) throws Exception {
		Index idx =  new Index(conn,"399300","沪深300");
		K k = idx.getK(0);
		if(realtime){
			//idx.getKsRealTimeOnDay();
			k = IndexUtils.getKsRealTime(conn, idx.getCode());
			idx.addK(k);
		}
		K k20 = k.before(20);
		double bias1 = k.getBIAS4(6, 33);
		
		Index idx2 =  new Index(conn,"01000852","中证1000");
		K kk = idx2.getK(0);
		if(realtime){
			//idx2.getKsRealTimeOnDay();
			kk = IndexUtils.getKsRealTime(conn, idx2.getCode());
			K k2 = idx2.getK(0);
			kk.setBefore(idx2.getK(0));
			k2.setAfter(kk);
		}
		K kk20 = kk.before(20);
		double bias2 = k.getBIAS4(7, 37);
		
		List<List<String>> pe = new ArrayList<List<String>>();
		List<String> pe1 = new ArrayList<String>();
		pe1.add("");pe1.add("20日涨幅");
		pe.add(pe1);
		List<String> pe2 = new ArrayList<String>();
		pe2.add("沪深300");pe2.add(StkUtils.numberFormat2Digits((k.getClose()/k20.getClose()-1)*100)+"%");
		pe.add(pe2);
		List<String> pe3 = new ArrayList<String>();
		pe3.add("中证1000");pe3.add(StkUtils.numberFormat2Digits((kk.getClose()/kk20.getClose()-1)*100)+"%");
		pe.add(pe3);
		
		
		/////////////////////////////////
		//bias4.0策略
		List<List<String>> bb = new ArrayList<List<String>>();
		List<String> b1 = new ArrayList<String>();
		b1.add("");b1.add("Bias");b1.add("优化参数");
		bb.add(b1);
		List<String> b2 = new ArrayList<String>();
		b2.add("沪深300");b2.add(StkUtils.numberFormat2Digits(bias1));b2.add("(17,0,-12)");
		bb.add(b2);
		List<String> b3 = new ArrayList<String>();
		b3.add("中证1000");b3.add(StkUtils.numberFormat2Digits(bias2));b3.add("(18,3,-8)");
		bb.add(b3);
		
		
		/////////////////////////////////
		//最大最小市值强弱
		String date = today;
		if(realtime){
			date = JdbcUtils.load("select kline_date from (select kline_date from stk_kline where code='999999' order by kline_date desc) where rownum=1", String.class);;
		}
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
		List<Index> indexs = new ArrayList<Index>();
		for(Stk stk : stks){
			Index index =  new Index(conn,stk.getCode(),stk.getName());
			if(!index.isStop(date)){
				index.changePercent = index.getTotalMarketValue();
				if(index.changePercent > 0){
					indexs.add(index);
				}
			}
		}	
		Collections.sort(indexs, new Comparator<Index>(){
			@Override
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		
		int x = 0;
		
		for(int m=0;m<100;m++){
			Index index = indexs.get(m);
			if(realtime){
				index.getKsRealTimeOnDay();
			}
			k = index.getK(0);
			if(k != null){
				double ma = k.getMA(K.Close, 20);
				if(k.getClose() > ma){
					x ++;
				}
			}else{
				//System.out.println(index.getCode()+","+index.getName());
			}
		}
		
		int y = 0;
		for(int m=indexs.size()-100;m<indexs.size();m++){
			Index index = indexs.get(m);
			if(realtime){
				index.getKsRealTimeOnDay();
			}
			k = index.getK(0);
			double ma = k.getMA(K.Close, 20);
			if(k.getClose() > ma){
				y ++;
			}
		}
		List<List<String>> list = new ArrayList<List<String>>();
		List<String> pe21 = new ArrayList<String>();
		pe21.add("");pe21.add("在20日均线上个数");
		list.add(pe21);
		List<String> pe22 = new ArrayList<String>();
		pe22.add("最小市值100股");pe22.add(x+"");
		list.add(pe22);
		List<String> pe23 = new ArrayList<String>();
		pe23.add("最大市值100股");pe23.add(y+"");
		list.add(pe23);
		
		x = 0;
		List<List<String>> gnList = new ArrayList<List<String>>();
		if(!realtime){
			/////////////////////////////////
			//同花顺概念指数排行
			List<Index> indexs1 = new ArrayList<Index>();
			stks = JdbcUtils.list(conn, "select code,name from stk where market=1 and cate=4 order by code", Stk.class);
			
			for(Stk stk : stks){
				idx = new Index(conn,stk.getCode(),stk.getName());
				k = idx.getK(0);
				if(k == null)continue;
				k20 = k.before(20);
				idx.changePercent = k.getClose()/k20.getClose() - 1;
				if(idx.changePercent > 0)x ++;
				indexs1.add(idx);
			}
			Collections.sort(indexs1, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o2.changePercent - o1.changePercent)*10000);
					return i;
				}});
			List<Index> indexs2 = new ArrayList<Index>();
			for(Stk stk : stks){
				idx = new Index(conn,stk.getCode(),stk.getName());
				k = idx.getK(0);
				if(k == null)continue;
				k20 = k.before(40);
				idx.changePercent = k.getClose()/k20.getClose() - 1;
				indexs2.add(idx);
			}
			Collections.sort(indexs2, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o2.changePercent - o1.changePercent)*10000);
					return i;
				}});
			List<Index> indexs3 = new ArrayList<Index>();
			for(Stk stk : stks){
				idx = new Index(conn,stk.getCode(),stk.getName());
				k = idx.getK(0);
				if(k == null)continue;
				k20 = k.before(60);
				idx.changePercent = k.getClose()/k20.getClose() - 1;
				indexs3.add(idx);
			}
			Collections.sort(indexs3, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o2.changePercent - o1.changePercent)*10000);
					return i;
				}});
			
			//60日行业涨幅排行 - 20日行业涨幅排行 越大排在越前
			List<Index> indexs4 = new ArrayList<Index>();
			for(int i=0;i<indexs1.size();i++){
				Index index = indexs1.get(i);
				int j = IndexUtils.indexOf(indexs3, index.getCode());
				index.changePercent = j - i;
				indexs4.add(index);
			}
			Collections.sort(indexs4, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o2.changePercent - o1.changePercent)*10000);
					return i;
				}});
			
			List<String> gn = new ArrayList<String>();
			gn.add("涨幅榜");gn.add("20日排行");gn.add("20日涨幅");gn.add("40日排行");gn.add("40日涨幅");gn.add("60日排行");gn.add("60日涨幅");
			gn.add("60日-20日排行");gn.add("60日-20日");
			gnList.add(gn);
			for(int i=0;i<x;i++){
				gn = new ArrayList<String>();
				gn.add(String.valueOf(i+1));
				gn.add(get10jqkaIndustryUrl(indexs1.get(i)));gn.add(StkUtils.number2String(indexs1.get(i).changePercent*100, 2)+"%");
				gn.add(get10jqkaIndustryUrl(indexs2.get(i)));gn.add(StkUtils.number2String(indexs2.get(i).changePercent*100, 2)+"%");
				gn.add(get10jqkaIndustryUrl(indexs3.get(i)));gn.add(StkUtils.number2String(indexs3.get(i).changePercent*100, 2)+"%");
				gn.add(get10jqkaIndustryUrl(indexs4.get(i)));gn.add(StkUtils.number2String(indexs4.get(i).changePercent, 0));
				gnList.add(gn);
			}
		}
		
		if(realtime){
			EmailUtils.sendImport("【策略】",StkUtils.createHtmlTable(null, pe)
					+ "<br>"+StkUtils.createHtmlTable(null, list)
					+ "<br>"+StkUtils.createHtmlTable(null, bb)
					);
		}else{
			EmailUtils.sendAndReport("【策略】",StkUtils.createHtmlTable(null, pe)  
					+ "<br>"+  StkUtils.createHtmlTable(null, list)
					+ "<br>"+  StkUtils.createHtmlTable(null, bb)
					+ "<br><a href='http://q.10jqka.com.cn/stock/gn/' target='_blank'>同花顺概念板块</a>"
					+ "<br>"+ "20日正收益行业个数："+ x //+ " (抄底找60日排名差，40日其次，20日排名好且开始变正值的行业)"
					+ "<br>"+StkUtils.createHtmlTable(null, gnList)
					);
		}
	}
	
	private static String get10jqkaIndustryUrl(Index index) {
		return "<a href='http://data.10jqka.com.cn/funds/ggzjl/' target='_blank'>"+index.getName()+"</a>";
	}
	
	public static void peg(String today, List<Index> indexs) throws Exception {
		List<Index> results = new ArrayList<Index>();
		
		for(Index index : indexs){
			if(index.isStop(today))continue;
			index.changePercent = index.getPEG();
			if(index.changePercent > 0 && index.changePercent <= 2){
				double gm = index.getFnDataLastestByType(index.FN_GROSS_MARGIN).getFnValue();
				if(gm >= 30){
					results.add(index);
				}
			}
		}
		Collections.sort(results, new Comparator<Index>(){
			@Override
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*10000);
				return i;
			}});
		if(results.size() > 0){
			List<List> datas = new ArrayList<List>();
			for(Index result : results){
				List data = new ArrayList();
				data.add(result);
				data.add(StkUtils.numberFormat(result.changePercent, 2));
				data.add(StkUtils.numberFormat(result.getPETTM(), 2));
				datas.add(data);
			}
			List<String> addtitle = new ArrayList<String>();
			addtitle.add("PEG");
			addtitle.add("PE(TTM)");
			EmailUtils.sendAndReport("PEG排序,个数："+results.size()+",日期:"+today, StkUtils.createHtmlTable(today, datas, addtitle) );
		}
	}
	
	
	public static void checkIndustry(Connection conn, String today, String category) throws Exception {
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk where market=1 and cate=4 and address='"+category+"'"
				+ "and code not in ('883900','883902','883907','883905') order by code", Stk.class);
		
		List<Index> indexs = new ArrayList<Index>();
		for(Stk stk : stks){
			Index index = new Index(conn,stk.getCode(),stk.getName());
			indexs.add(index);
		}
		
		List<Index> results = new ArrayList<Index>();
		for(Index index : indexs){
			K k = index.getK(today);
			if(k == null)continue;
			double ma20 = k.getMA(K.Close, 20);
			if(k.getClose() >= ma20){
				K yk = k.before(1);
				double yma20 = yk.getMA(K.Close, 20);
				if(yk.getClose() <= yma20){
					results.add(index);
				}
			}
		}
		Index idx = new Index(conn, "01000905");
		String date10 = idx.getK(0).before(10).getDate();
		//突破20日均线
		List<List<String>> table1 = buildIndustryTable(results, date10);

		results.clear();
		for(Index index : indexs){
			K k = index.getK(today);
			if(k == null)continue;
			if(k.getLow() <= k.getMA(K.Close, 20)){
				int cnt = k.getKCountWithCondition(15, new K.Condition() {
					public boolean pass(K k) throws Exception {
						return k.getKByHHV(60).getDate().equals(k.getDate());
					}
				});
				if(cnt > 0){
					results.add(index);
				}
			}
		}
		//创120日新高后回抽20日均线
		List<List<String>> table2 = buildIndustryTable(results, date10);
		/*for(Index index : results){
			System.out.println(index.getName());
		}*/
		
		results.clear();
		for(Index index : indexs){
			if(index.getK() != null && !index.isStop(today) && !index.getK().isUpLimit()
					&& (index.isBreakOutTrendLine2(today,60,6,0.05) ||
						index.isBreakOutShortTrendLine(today) ||
						index.isBreakOutTrendLine3(today, 30, 8, 0).size() > 0
						)
					){
				results.add(index);
			}
		}
		List<List<String>> table3 = buildIndustryTable(results, date10);
		
		//
		K k1 = null;
		K k2 = null;
		List<List<String>> table4 = null;
		List<List<String>> table5 = null;
		
		List<K> ks = idx.getKsHistoryHighAndLow(today, 60, 8);
		if(ks.size() >= 2){
			k1 = ks.get(0);
			for(Index index : indexs){
				K k = index.getK(today);
				if(k == null)continue;
				K k3 = index.getK(k1.getDate());
				index.changePercent = (k.getClose() - k3.getClose())/k3.getClose() * 100;
			}
			Collections.sort(indexs, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o2.changePercent - o1.changePercent)*10000);
					return i;
				}});
			table4 = buildIndustryTable(indexs.subList(0, 10), k1.getDate());
			
			k2 = ks.get(1);
			for(Index index : indexs){
				K k = index.getK(today);
				if(k == null)continue;
				K k3 = index.getK(k2.getDate());
				index.changePercent = (k.getClose() - k3.getClose())/k3.getClose() * 100;
			}
			Collections.sort(indexs, new Comparator<Index>(){
				@Override
				public int compare(Index o1, Index o2) {
					int i = (int)((o2.changePercent - o1.changePercent)*10000);
					return i;
				}});
			table5 = buildIndustryTable(indexs.subList(0, 10), k2.getDate());
		}
		
		//过去10涨幅排行
		List<List<String>> table6 = null;
		for(Index index : indexs){
			K k = index.getK(today);
			if(k == null)continue;
			K k3 = k.before(10);
			index.changePercent = (k.getClose() - k3.getClose())/k3.getClose() * 100;
		}
		Collections.sort(indexs, new Comparator<Index>(){
			@Override
			public int compare(Index o1, Index o2) {
				int i = (int)((o2.changePercent - o1.changePercent)*10000);
				return i;
			}});
		table6 = buildIndustryTable(indexs.subList(0, 10), date10);
		
		EmailUtils.sendAndReport("行业分析 - "+category+",日期:"+today, "<a href='http://q.10jqka.com.cn/gn/' target='_blank'>同花顺概念板块</a><br><a href='http://q.10jqka.com.cn/thshy/' target='_blank'>同花顺行业</a><br><br>"
				+ (k1 != null ? StkUtils.formatDate(k1.getDate())+"以来涨幅:<br>"+StkUtils.createHtmlTable(null, table4) : "") + "<br>"
				+ (k2 != null ? StkUtils.formatDate(k2.getDate())+"以来涨幅:<br>"+StkUtils.createHtmlTable(null, table5) : "") + "<br>"
				+ "10日涨幅:<br>" + StkUtils.createHtmlTable(null, table6) + "<br>"
				+ "突破下降趋势线:<br>" + StkUtils.createHtmlTable(null, table3) + "<br>"
				+ "突破20日均线:<br>" + StkUtils.createHtmlTable(null, table1) + "<br>"
				+ "创60日新高后回抽20日均线:<br>" + StkUtils.createHtmlTable(null, table2) + "<br>"
				);
	}
	
	private static List<List<String>> buildIndustryTable(List<Index> results, String date) throws Exception{
		List<List<String>> table = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();
		row.add("");row.add("行业");row.add("涨幅");row.add("资金流入");row.add("60日线");row.add("120日线");row.add("250日线");
		table.add(row);
		int i = 1;
		for(Index index : results){
			row = new ArrayList<String>();
			row.add(i+"");
			row.add(get10jqkaIndustryUrl(index));
			row.add(StkUtils.number2String(index.changePercent, 2)+"%");
			row.add(index.getCapitalFlowImageOnMain(date));
			K k = index.getK(today);
			double ma60 = k.getMA(K.Close, 60);
			double ma120 = k.getMA(K.Close, 120);
			double ma250 = k.getMA(K.Close, 250);
			K yk = k.before(1);
			double yma60 = yk.getMA(K.Close, 60);
			double yma120 = yk.getMA(K.Close, 120);
			double yma250 = yk.getMA(K.Close, 250);
			if(ma60 > yma60){row.add("上升");}else{row.add("");}
			if(ma120 > yma120){row.add("上升");	}else{row.add("");}
			if(ma250 > yma250){row.add("上升");	}else{row.add("");}
			table.add(row);
			i++;
		}
		return table;
	}
	
	/**
	 * 斐波那契数列 时间窗口 择时
	 */
	public static void timeWindow(Connection conn, String today) throws Exception { 
		Index index =  new Index(conn,"01000905");
		K todayK = index.getK(0);
		
		List<K> kks = new ArrayList<K>();
		K k = index.getK(450);
		do{
			k = k.after(150);
			List<K> ks = index.getKsHistoryHighAndLow(k.getDate(), 150, 14);
			kks.addAll(ks);

			if(k.dateAfterOrEquals(todayK))break;
		}while(true);
		
		IntRange2IntMap ir = new IntRange2IntMap();
		ir.define(7, 8);
		ir.define(11, 13);
		ir.define(18, 21);
		ir.define(29, 34);
		ir.define(42, 55);
		ir.define(68, 89);
		ir.define(110, 144);
		ir.define(178, 233);
		
		IntRange2IntMap ir2 = new IntRange2IntMap();
		ir2.define(8, 8);
		ir2.define(13-1, 13);
		ir2.define(21-2, 21);
		ir2.define(34-3, 34);
		ir2.define(55-5, 55);
		ir2.define(89-8, 89);
		ir2.define(144-13, 144);
		ir2.define(233-21, 233);
		
		Set<String> dates = new HashSet<String>();
		for(K k1 : kks){
			dates.add(k1.getDate());
		}
		//System.out.println(dates);
		
		k = index.getK(today).before(1);
		int vv = 0;
		int vv2 = 0;
		
		List<TimeWindowResult> results = new ArrayList<TimeWindowResult>();
		do{
			int v = 0;
			int v2 = 0;
			for(String date : dates){
				int b = index.getDaysBetween(date, k.getDate());
				v += ir.getEntries(b).size();
				v2 += ir2.getEntries(b).size();
			}
			int flag = (v-vv)>=2||(v2-vv2)>=2?(v-vv)+(v2-vv2):0;
			//String log = k.getDate()+",count1="+v+",diff1="+(v-vv)+",count2="+v2+",diff2="+(v2-vv2)+", result="+flag;
			//System.out.println(log);
			
			TimeWindowResult tw = new TimeWindowResult();
			tw.date = k.getDate();
			tw.count1 = v;
			tw.diff1 = v-vv;
			tw.count2 = v2;
			tw.diff2 = v2-vv2;
			tw.result = flag;
			results.add(tw);
			
			vv = v;
			vv2 = v2;
			
			if(k.dateAfterOrEquals(todayK)){
				List params = new ArrayList();
				params.add(flag);
				params.add(k.getDate());
				JdbcUtils.update(conn, "update stk_pe set result_6=? where report_date=?", params);
				break;
			}
			
			k = k.after(1);
		}while(true);
		
		if(results.size() > 0){
			results.remove(0);

			for(int i=1;i<=10;i++){
				int v = 0;
				int v2 = 0;
				for(String date : dates){
					int b = index.getDaysBetween(date, todayK.getDate()) + i;
					v += ir.getEntries(b).size();
					v2 += ir2.getEntries(b).size();
				}
				int flag = (v-vv)>=2||(v2-vv2)>=2?(v-vv)+(v2-vv2):0;
				TimeWindowResult tw = new TimeWindowResult();
				tw.date = StkUtils.sf_ymd.format(StkUtils.addDayOfWorking(todayK.getDate(), i));
				tw.count1 = v;
				tw.diff1 = v-vv;
				tw.count2 = v2;
				tw.diff2 = v2-vv2;
				tw.result = flag;
				results.add(tw);
				
				vv = v;
				vv2 = v2;
			}
			
			boolean trigger = false;
			for(int i=0;i<results.size();i++){
				TimeWindowResult tw = results.get(i);
				if(tw.result >= 2){
					trigger = true;
				}
			}
			
			EmailUtils.sendAndReport("时间窗口 斐波那契数列 择时,日期:"+today, 
					"斐波那契数列 择时: result>=2时开启时间窗口<br/>"+StringUtils.join(results, "<br/>"));
		}
	}
	
	static class TimeWindowResult{
		String date;
		int count1;
		int diff1;
		int count2;
		int diff2;
		int result;
		
		public String toString(){
			return date+",c1="+count1+",d1="+diff1+",c2="+count2+",d2="+diff2+", result="+(result>=2?result+" 《===":result);
		}
	}
	

}

