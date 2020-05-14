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
	public static final int US_STK_HOT = 200; //����ѩ���ע��������
	
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
					
					//���ػ���300��ӯ��xls����
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
			//�иŹ�pe
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
			
			//����pe
			params.clear();
			params.add(today);
			Double avgPE2 = JdbcUtils.load("select avg(pe_ttm) from stk_kline_us where kline_date=? and pe_ttm is not null and pe_ttm>3 and pe_ttm<200", params, Double.class);
			avgPE2 = StkUtils.numberFormat(avgPE2, 2);
			
			params.clear();
			params.add(avgPE2);
			params.add(today);
			JdbcUtils.update(conn, "update stk_daily_report_us set result_2=? where report_date=?", params);
			EmailUtils.send("[����]ƽ��PE,����:"+today+",�и�:"+avgPE+",ƽ��:"+avgPE2, "�и�ƽ��PE:"+avgPE+"<br>����ƽ��PE:"+avgPE2);
			
			System.out.println(".............. US ...................");
			List<Index> indexs = new ArrayList<Index>();
			for(Stk stk : stks){
				Index index = new Index(conn,stk.getCode(),stk.getName());
				if(index.getStk().getHot() < US_STK_HOT && !StringUtils.containsIgnoreCase(stk.getName(), "etf")){
					continue;
				}
				indexs.add(index);
			}
			
			System.out.println("1.���¸�ѡ�ɷ�");
			List<Index> newHighs = IndexUtils.getNewHighs(indexs, today, DAYS_OF_NEWHIGHT_250);
			if(newHighs.size() > 0){
				Collections.sort(newHighs, new Comparator<Index>(){
					@Override
					public int compare(Index o1, Index o2) {
						return (o2.getStk().getHot() - o1.getStk().getHot());
					}});
				EmailUtils.send("[����]��"+DAYS_OF_NEWHIGHT_250+"���¸߹�,�ܼ�:"+newHighs.size()+",����:"+today, StkUtils.createHtmlTable(today, newHighs));
			}
			
			newHighs = IndexUtils.getNewHighs(indexs, today, DAYS_OF_NEWHIGHT_120);
			if(newHighs.size() > 0){
				Collections.sort(newHighs, new Comparator<Index>(){
					@Override
					public int compare(Index o1, Index o2) {
						return (o2.getStk().getHot() - o1.getStk().getHot());
					}});
				EmailUtils.send("[����]��"+DAYS_OF_NEWHIGHT_120+"���¸߹�,�ܼ�:"+newHighs.size()+",����:"+today, StkUtils.createHtmlTable(today, newHighs));
			}
			
			System.out.println("2.�ƽ�����ѡ�ɷ�");
			checkUSGoldTriangle(today, indexs);
			
			System.out.println("3.K�߲���ѡ�ɷ�");
			checkUSKIntersect(today, indexs);
			
			System.out.println("4.ͻ���½�����ѡ�ɷ�");
			checkUSKUpTrendLine(today, indexs);
			
			System.out.println("4.[����]������ͻ��");
			checkWeeklyTrandLine(today, indexs);
			
			System.out.println("10.[����]������ͻ��");
			checkMonthlyTrandLine(today, indexs);
			
			System.out.println("5.����������");
			checkHugeVolumeLittleVolume(indexs, today, 2);
			
			System.out.println("6.MACDһƷ����");
			checkYiPinChaoDiMACD(conn, today, indexs, 2);
			
			System.out.println("7.��Ʒ����-����ʱ��");
			checkErPinChaoDi(conn, today, indexs, 2, true, false);
			
			System.out.println("7.[����]��Ʒ����-����ʱ��");
			checkErPinChaoDi(conn, today, indexs, 2, true, true);
			
			System.out.println("8.[����]MACDճ��");
			checkMACDWeekly(today, indexs);
			
			System.out.println("10.����6������macd����");
			
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
				EmailUtils.send("[InitialKLine����]����ָ��K�����س��� stk="+sk.getCode(), e);
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
				EmailUtils.send("[InitialKLine����]ͬ��˳����ָ��K�����س��� stk="+sk.getCode(), e);
			}
		}
		
		try{
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			if(!analyse){
				System.out.println("initKLines..........");
				initKLines(stks, flag, 4);
				System.out.println("initKLines..........end");
				if(!flag){
					EmailUtils.send("��������ͬ����ɣ�����","...");
					return;
				}
				
				List<String> stksUnInit = JdbcUtils.list(conn, "select code from stk_cn where code not in (select code from stk_kline where kline_date=to_char(sysdate,'yyyymmdd'))", String.class);
				for(String code : stksUnInit){
					try{
						Index tmpIndex = new Index(conn, code);
						tmpIndex.initKLine();
					}catch(Exception e){
						EmailUtils.send("[InitialKLine����]�޲�K�����ݳ��� stk="+ code, e);
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
				//���·ǹ������У�Ա���ֹɼ۸������
				NoticeRobot.updateRate(conn, index);
				
				//����pe/pb ntile
				index.updateNtile();
			}
			
			System.out.println("���� at:"+new Date());
			strategy(conn,false);
			
			System.out.println("ʱ�䴰�� at:"+new Date());
			timeWindow(conn, today);
			
			System.out.println("��ҵ����");
			checkIndustry(conn, today, "10jqka_gn");
			checkIndustry(conn, today, "10jqka_thshy");
			
			//����ģ��ѡ��
			StrategyManager mgr = new StrategyManager(conn, today);
			mgr.init(context.indexs);
			mgr.execute();
			
			//List<Index> newHighs = new ArrayList<Index>();
			System.out.println("1.check��Ʊ��û�д�600���¸�");
			//1.check��Ʊ��û�д�600����ʷ�¸�
			List<Index> newHighs = IndexUtils.getNewHighs(context.indexs, today, DAYS_OF_NEWHIGHT_600);
			if(newHighs.size() > 0){
				EmailUtils.sendAndReport("��"+DAYS_OF_NEWHIGHT_600+"���¸߹�,�ܼ�:"+newHighs.size()+",����:"+today, StkUtils.createHtmlTable(today, newHighs));
			}
			
			newHighs = IndexUtils.getNewHighs(context.indexs, today, DAYS_OF_NEWHIGHT_120);
			if(newHighs.size() > 0){
				EmailUtils.sendAndReport("��"+DAYS_OF_NEWHIGHT_120+"���¸߹�,�ܼ�:"+newHighs.size()+",����:"+today, StkUtils.createHtmlTable(today, newHighs));
			}
			System.out.println("1.1.�ӽ�600���¸�");
			List<Index> closeNewHighs = IndexUtils.getCloseNewHighs(context.indexs, today, DAYS_OF_NEWHIGHT_600);
			if(closeNewHighs.size() > 0){
				EmailUtils.sendAndReport("�ӽ�"+DAYS_OF_NEWHIGHT_600+"���¸߹�,�ܼ�:"+closeNewHighs.size()+",����:"+today, StkUtils.createHtmlTable(today, closeNewHighs));
			}
			System.out.println("1.2.�ӽ�120���¸�");
			closeNewHighs = IndexUtils.getCloseNewHighs(context.indexs, today, DAYS_OF_NEWHIGHT_120);
			if(closeNewHighs.size() > 0){
				EmailUtils.sendAndReport("�ӽ�"+DAYS_OF_NEWHIGHT_120+"���¸߹�,�ܼ�:"+closeNewHighs.size()+",����:"+today, StkUtils.createHtmlTable(today, closeNewHighs));
			}
			System.out.println("1.3.�ӽ�600���¸���K�߲���");
			closeNewHighs = IndexUtils.getCloseNewHighsAndInteract(context.indexs, today, DAYS_OF_NEWHIGHT_600);
			if(closeNewHighs.size() > 0){
				EmailUtils.sendAndReport("�ӽ�"+DAYS_OF_NEWHIGHT_600+"���¸���K�߲��ƹ�,�ܼ�:"+closeNewHighs.size()+",����:"+today, StkUtils.createHtmlTable(today, closeNewHighs));
			}
			
			//3.search TODO ���Ӻ�����ȽϺ��¼ӵ�
			/*datas = IndexUtils.search(context.indexs, today, 3, 120, false);
			for(List data : datas){
				Index index = (Index)data.get(0);
				data.add(StkUtils.createWeeklyKLine(index.getCode()));
				data.add(StkUtils.createDailyKLine(index.getCode()));
			}
			if(datas.size() > 0){
				List<String> addtitle = new ArrayList<String>();
				addtitle.add("120���ǵ���");
				addtitle.add("��ҵ");
				addtitle.add("����ͼ");
				addtitle.add("����ͼ");
				EmailUtils.send("������120���¸߹�,�ܼƣ�"+datas.size()+",����:"+today, StkUtils.createHtmlTable(today, datas, addtitle));
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
			List<List> added = notContain(datas,datasYesterday);//���
			List<List> deleted = notContain(datasYesterday,datas);//����
			if(datas.size() > 0){
				List<String> addtitle = new ArrayList<String>();
				addtitle.add("250���ǵ���");
				addtitle.add("����[�ǵ���,����������]");
				EmailUtils.send("250��RSǿ��ҵ������,�ܼƣ�"+datas.size()+",����:"+today, 
						"���<br>"+StkUtils.createHtmlTable(today, added, addtitle) + "<br>" +
						"����<br>"+StkUtils.createHtmlTable(today, deleted, addtitle) + "<br>" +
						StkUtils.createHtmlTable(today, datas, addtitle));
			}
			*/
			System.gc();
			//System.out.println("5.��������ѡ�ɷ�");
			//checkStkByLaoZhang(conn, context, contextYesterday, newHighs);
			
			//System.out.println("6.250�յײ�����ͻ�ƹ�Ʊ��������͵�ͻ��(���)�Ƿ�������60%");
			//checkStkCreate250DaysBoxHigh(conn, context);
			
			//System.out.println("7.����60�չ�Ʊǿ������");
			//check60DaysRS(conn, context, contextYesterday);
			
			//8.rank k line
			//K today = sh.getK();
			//rankIndustryByK(conn, today.getDate(), "cnindex");
			
			//System.out.println("9.�ز�ǰ������ȱ��");
			/*List<Index> gaps = IndexUtils.getUpGaps(context.indexs,today , 250, 30);
			if(gaps.size() > 0){
				StringBuffer sb = new StringBuffer();
				for(Index gap : gaps){
					sb.append(gap.toHtml()).append("<br>");
				}
				EmailUtils.sendAndReport("�ز�ǰ������ȱ�ڣ��ܹ���"+gaps.size()+",����:"+today, StkUtils.createHtmlTable(today, gaps));
			}*/
			
			System.out.println("10.K�߲���");
			//checkKIntersect(today, context);
			
			//System.out.println("12.�Ͼ�����ս��");
			//checkCreateHighStk(context);
			
			System.out.println("13.ͻ��600������");
			//checkHighHugeVolume(context);
			
			//System.out.println("14.����������");
			//checkHugeVolumeLittleVolume(context.indexs, today, 1);
			
			//15.����ѡ��
			//checkWangPeng(context);
			
			System.out.println("16.����ѡ���Ż�");
			//checkWangPeng1(context);
			//checkWangPeng2(context);
			
			System.out.println("17.�۸����ǰ�ߣ�����ǰ��");
			//checkVolumeGreaterThanLastVolume(today, context, 0.8);
			
			System.out.println("18.һƷ����-MACD");
			int cnt = checkYiPinChaoDiMACD(conn, today, context.indexs, 1);
			if(i >= 1){
				params.clear();
				params.add(cnt);
				params.add(today);
				JdbcUtils.update(conn, "update stk_pe set result_4=? where report_date=?", params);
			}
			
			System.out.println("18.һƷ����-�ײ�");
			cnt = checkYpcdDiBu(conn, today, context.indexs, 1);
			if(i >= 1){
				params.clear();
				params.add(cnt);
				params.add(today);
				JdbcUtils.update(conn, "update stk_pe set result_3=? where report_date=?", params);
			}
			
			System.out.println("19.��Ʒ����-����ʱ��");
			try{
				List<Index> rslts = checkErPinChaoDi(conn, today, context.indexs, 1, true, false);
				if(i >= 1){
					params.clear();
					params.add(rslts.size());
					params.add(today);
					JdbcUtils.update(conn, "update stk_pe set result_5=? where report_date=?", params);
				}
			}catch(Exception e){
				EmailUtils.send("��Ʒ����-����ʱ�� - eror", e);
			}
			
			System.out.println("20.һƷ����-������ȥ");
			//checkYiPinChaoDiDaShiYiQu(conn, today, context.indexs);
			
			System.out.println("21.������ͣ�󿪰�");
			//checkKaiBan(today, context.indexs);
			
			System.out.println("22.������ͣ�����Ͽ����");
			//checkMaybeKaiBan(today, context.indexs);
			
			System.out.println("23.������ͣ��������¸߹�");
			//checkKaiBanXinGao(today, context.indexs);
			
			System.out.println("24.�ʽ���������ײ���Զ");
			//checkChaoDiAndFlow(conn, today);
			
			
			//ͳ����ѡ����ѡ��Ʊ
			if(addToCareStks){
				addToCareStks = false;
				List<Index> results = new ArrayList<Index>();
				for(String careStk : careStks){
					results.add(new Index(conn, careStk));
				}
				if(results.size() > 0){
					//Strategy.logToDB(conn, today, "��ѡ��C", results);
					EmailUtils.sendAndReport("��ѡ��C, ������"+results.size()+" ����:"+today, 
						StkUtils.createHtmlTable(today, results));
				}
				addToCareStks = true;
			}
			
			//ͳ���ʽ����������Ʊ
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
				EmailUtils.sendAndReport("�ʽ��������, ������"+results.size()+" ����:"+today, 
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
			
			
			String peAndpeg = "�г�������λPB�͵��Լ��2PB��<br>" +
							  "2008����͵�Լ1700�㸽������λ���о���Լ2����<br>" +
							  "2012��ǰ����͵��Լ2000�㣬��λ���о���Լ2����<br>" +
							  "2016-2017��ǰ�󣬴�Լ2366���Ӧ��λ��2���о��ʡ�<br><br>" +
					"�ɳ������PEͶ��������20PE-50PE��<br>" +
					"����20PE����ҵ��ȴ����30%�����Ļ���ҪС��ҵ�����塣<br>" +
					"����50PEҪע����ĭ���գ��Ͳ�ҪäĿɱ���ˡ�<br>"
					+ StkUtils.createPEAndPEG()+"<br>";
			List<List<String>> pe = new ArrayList<List<String>>();
			List<String> pe1 = new ArrayList<String>();
			pe1.add("");pe1.add("ƽ��PE");pe1.add("��λPE");pe1.add("ƽ��PB");pe1.add("��λPB");pe1.add("ͳ����");
			pe.add(pe1);
			List<String> pe2 = new ArrayList<String>();
			pe2.add("�ɳ���");pe2.add(context.averagePE);pe2.add("");pe2.add(context.averagePB);pe2.add("");pe2.add(String.valueOf(context.indexsGrowth.size()));
			pe.add(pe2);
			List<String> pe3 = new ArrayList<String>();
			pe3.add("ȫ�г�");pe3.add(String.valueOf(totalPE));pe3.add(String.valueOf(midPE));pe3.add(String.valueOf(totalPB));pe3.add(String.valueOf(midPB));pe3.add("");
			pe.add(pe3);
			EmailUtils.sendAndReport("�ɳ���ƽ��PE��"+context.averagePE+",PB��"+context.averagePB+
					"���г�����PE��"+totalPE+",��λPE��"+midPE+",����PB��"+totalPB+",��λPB��"+midPB+",����:"+today, 
					StkUtils.createHtmlTable(null, pe) + "<br>" + peAndpeg + htmlMsg);
			
			System.out.println("���·ǹ������У�Ա���ֹɼ۸������");
			for(Index index : context.indexs){
				//���·ǹ������У�Ա���ֹɼ۸������
				NoticeRobot.updateRate(conn, index);
				
				//ӯ��Ԥ��PE
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
			EmailUtils.send("ENEͳ��,lower:"+(int)eneLowerCnt+"��upper:"+(int)eneUpperCnt+",����:"+date, ".");
		}
		if(eneUpperCnt >= eneLowerCnt){
			params.clear();
			params.add(date);
			StkPe pe = JdbcUtils.load("select * from (select * from stk_pe where report_date<>? order by report_date desc) a where rownum=1",params, StkPe.class);
			if(pe != null && pe.getEneUpper() < pe.getEneLower()){
				EmailUtils.send(EmailUtils.IMPORTANT+"ENEͳ��:Upper����Lower,����:"+date, ".");
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
	
	
	
	//���߳� workers
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
							//e.printStackTrace();
							ExceptionUtils.insertLog(conn, index.getCode(), e);
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
		//�ɼ�����ǰ200
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
		
		//ÿ��RS����ǰ200
		
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
				data.add("�¸�");
			}else{
				data.add("&nbsp;");
			}
			datas.add(data);
		}
		for(Index index : resultsYesterday){
			List data = new ArrayList();
			data.add(index);
			if(newHighs.contains(index)){
				data.add("�¸�");
			}else{
				data.add("&nbsp;");
			}
			datasYesterday.add(data);
		}
		List<List> added = notContain(datas,datasYesterday);//���
		List<List> deleted = notContain(datasYesterday,datas);//����
		
		List<String> addtitle = new ArrayList<String>();
		addtitle.add("��"+DAYS_OF_NEWHIGHT_600+"���¸�");
		
		String czlz = "1������ʷ�¸ߵĹ�Ʊ����һ�㣨����ĩ�ڣ��ǳ���Ҫ��<br>"+
				      "2��ÿ���Ƿ�ǰ200����ÿ���ܷ�һ�顣<br>"+
				      "3��ÿ���Ƿ���ǰ100���Ĺ�Ʊ��<br>"+
				      "4���ɼ�����ǰ200��ÿ���ܷ�һ�顣<br>"+
				      "�ٽ�ϻ����漰�������ҳ���ǿ��10ֻ����ǿ��10ֻ��Ϊ��ѡ�ɹ۲죬�����ڵ���ɸѡ��ǿ���룬�����޳���<br>";
		EmailUtils.send("��������ѡ�ɷ�,�ܼƣ�"+datas.size()+",����:"+today, czlz + 
				"���<br>"+StkUtils.createHtmlTable(today, added, addtitle) + "<br>" +
				"����<br>"+StkUtils.createHtmlTable(today, deleted, addtitle) + "<br>" +
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
				
				//�����ߵ���ǿ�ʼ���죬��ѿ�ʼ�����ƺ�10������
				if(highK.getDate().equals(startK.getDate())){
					startK = startK.after(10);
					highK = index.getKByHHV(startK.getDate(), yesterdayK.getDate());
				}

				if(highK.getDate().compareTo(last20dayK.getDate()) <= 0
						&& todayK.getClose() * 1.05 >= highK.getClose() 
						&& yesterdayK.getClose() * 1.08 <= highK.getClose()){//���¸�
					K lowK = index.getKByLLV(startK.getDate(), today);
					if(lowK.getClose() * 1.8 >= highK.getClose()){//����
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
			addtitle.add("����ͼ");
			String czlz = "�ص㣺ͻ��ʱ�����ܲ��ܳ���ǰ�ڸߵ�����ܣ���ÿ�ε������������ģ������������ºͷ����ġ�<br>";
			EmailUtils.sendAndReport("ͻ��300�ա����80%��������Ĺ�Ʊ,�ܼ�:"+results.size()+",����:"+today, czlz + StkUtils.createHtmlTable(today, datas, addtitle)  );
		}
	}
	
	public static void check60DaysRS(Connection conn, IndexContext context, IndexContext contextYesterday) throws Exception {
		List params = new ArrayList();
		Index dp = new Index(conn, "999999");
		K k60 = dp.getK(today, 60);
		K kLow = dp.getKByLLV(k60.getDate(),today);
		if(kLow.getDate().equals(today)){
			EmailUtils.send("���̴�60���µ�"+",����:"+today,"");
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
		List<List> added = notContain(datas,datasYesterday);//���
		List<List> deleted = notContain(datasYesterday,datas);//����
		if(results.size() > 0){
			List<String> addtitle = new ArrayList<String>();
			addtitle.add("����ͼ");
			EmailUtils.send("�ɼ�60����ǿ���Ӵ���"+kLow.getLow()+"["+kLow.getDate()+"]��ʼ����"+",����:"+today, 
					"���<br>"+StkUtils.createHtmlTable(today, added, addtitle) + "<br>" +
					"����<br>"+StkUtils.createHtmlTable(today, deleted, addtitle) + "<br>" +
					StkUtils.createHtmlTable(today, datas, addtitle));
		}
	}
	
	//����src��destû�е�element
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
				
				//120�ջ�250�վ�����ƽ���������о�����ճ��
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
			EmailUtils.sendAndReport("K�߲���,������" + cnt
					+ ",����:" + today, "<b>�м���õȾ����߳���ͷ���к����룬���ھ��߽�泤�ھ���</b>"
					+ "<br>10,20,30,60,120��K�߲���<br>"	+ StkUtils.createHtmlTable(today, results2)  
					+ "<br><br>10,20,30,60,250��K�߲���<br>" + StkUtils.createHtmlTable(today, results3) 
					+ "<br><br>10,20,30,60��K�߲���<br>" + StkUtils.createHtmlTable(today, results4) 
					+ "<br><br>" + StkUtils.createHtmlTable(today, results) 
					+ "<br><br>10,20,30��K�߲���<br>" + StkUtils.createHtmlTable(today, results5) 
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
					if((highK.getOpen()-highK.getClose())/highK.getClose() <= 0.08){//��ߵ����첻���Ǵ�����
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
			EmailUtils.sendAndReport("�Ͼ�����ս��,������"+results.size()+",����:"+today,StkUtils.createHtmlTable(today, results));
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
			EmailUtils.sendAndReport("ͻ������,������"+results.size()+",����:"+today,
					"ͻ����ʷ������<br>"+StkUtils.createHtmlTable(today, results2)   + "<br><br>" +
					"ͻ��600��������<br>" + StkUtils.createHtmlTable(today, results)  );
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
				//����1/8
				if(isHugeVolumeLittleVolume2(index, today)){
					if(!isHugeVolumeLittleVolume2(index, yesterday)){
						index.isNew = true;
					}
					results.add(index);
				}else if(isHugeVolumeLittleVolume2(index, yesterday)){
					index.isDeleted = true;
					results.add(index);
				}
				//����1/5
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
				EmailUtils.sendAndReport("����������,������"+cnt+",����:"
		                   +today,"ͻ���½������ߺ� �� ����120/250���� ��������,��Խ�����ճ��ʱԽ��"
		                   +"<br><br>��������1/8Ʊ<br>"+StkUtils.createHtmlTable(today, results)  
		                   +"<br><br>����60������1/5Ʊ<br>"+StkUtils.createHtmlTable(today, results2)  
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
				EmailUtils.send("[����]����������,������"+cnt+",����:"+today,
						"��������1/8Ʊ<br>"+ StkUtils.createHtmlTable(today, results)
						+"<br><br>����60������1/5Ʊ<br>"+StkUtils.createHtmlTable(today, results2)
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
			if(lowK.getClose() * 3 >= highK.getClose()){//����
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
			if(lowK.getClose() * 2.5 >= highK.getClose()){//����
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
			if(lowK.getClose() * 2 >= highK.getClose()){//����
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
		 * 1. 10������9.5%�����Ƿ���
		 * 2. ������������ǰ��һ������
		 * 3. ����������10�����ϵ�
		 * 4. ����������250��������
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
			EmailUtils.sendAndReport("����ѡ��,������"+indexs.size()+",����:"+today,StkUtils.createHtmlTable(today, indexs));
			//EmailUtils.send(new String[]{"66933859@qq.com","327226952@qq.com"}, "ѡ��,������"+indexs.size()+",����:"+today,StkUtils.createHtmlTable2(today, indexs));
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
			EmailUtils.sendAndReport("����ѡ���Ż�-1,������"+indexs.size()+",����:"+today,StkUtils.createHtmlTable(today, indexs));
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
				//�ж���û�г���Ӱ�ߣ������ų�
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
					//���������������
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
							
							//һ����̫ͣ�಻�ڿ��Ƿ�Χ
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
			EmailUtils.sendAndReport("����ѡ���Ż�-2,������"+indexs.size()+",����:"+today,StkUtils.createHtmlTable(today, indexs));
		}
	}
	
	//�ײ�����������Ƽ���������ҵ
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
			EmailUtils.sendAndReport("�۸����ǰ�ߣ�����ǰ��,������"+ cnt +",����:"+today,
					"����Ʊ<br>" + StkUtils.createHtmlTable(today, indexs) +
					"<br><br>����Ʊ<br>" + StkUtils.createHtmlTable(today, indexs2)
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
	
	//�ƽ����ǣ�10�վ����ϴ�20�վ��ߣ�20�վ����ϴ�30�վ���
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
			EmailUtils.send("[����]�ƽ�����,�ܼ�:"+results.size()+",����:"+today, StkUtils.createHtmlTable(today, results));
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
			EmailUtils.send("[����]K�߲���,������" + cnt
					+ ",����:" + today, "<b>�м���õȾ����߳���ͷ���к�����</b>"
					+ "<br>10,20,30,60,250��K�߲���<br>" + StkUtils.createHtmlTable(today, results3)
					+ "<br><br>10,20,30,60,120��K�߲���<br>" + StkUtils.createHtmlTable(today, results2)
					+ "<br><br>10,20,30,60��K�߲���<br>" + StkUtils.createHtmlTable(today, results4)
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
			EmailUtils.send("[����]K��ͻ���½�������,������"+results.size()+",����:"+today,StkUtils.createHtmlTable(today, results));
		}
	}
	
	//һƷ����-�ײ�
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
				Strategy.logToDB(conn, today, "һƷ����-�ײ�", results);
				EmailUtils.sendAndReport("һƷ����-�ײ�,������"+results.size()+",����:"+today,"һƷ����Ҫ�㣺������Ҫ����0�ᣬMACDҲҪ����0��<br>"+
						StkUtils.createHtmlTable(today, results)  );
			}
		}
		return results.size();
	}
	
	//һƷ����-MACD
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
				Strategy.logToDB(conn, today, "һƷ����-MACD", results);
				EmailUtils.sendAndReport("һƷ����-MACD,������"+results.size()+",����:"+today,"һƷ����Ҫ�㣺������Ҫ����0�ᣬMACDҲҪ����0��<br>"+
						StkUtils.createHtmlTable(today, results)  );
			}else if(market == 2){
				Collections.sort(results, new Comparator<Index>(){
					public int compare(Index o1, Index o2) {
						return (o2.getStk().getHot() - o1.getStk().getHot());
					}});
				EmailUtils.send("[����]һƷ����-MACD,������"+results.size()+",����:"+today,StkUtils.createHtmlTable(today, results));
			}
		}
		return results.size();
	}
	
	//һƷ����-������
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
	//һƷ����-������
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
	//һƷ����-MACD
	public static double getMACD(K k) throws Exception {
		double horse = getHorse(k);
		double trend = getTrend(k);
		double macd = trend - horse;
		return macd;
	}
	
	//һƷ����-������ȥ
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
			String title = "һƷ����-������ȥ,������"+results.size()+",����:"+today;
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
	
	//��Ʒ����-����ʱ��
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
				Strategy.logToDB(conn, today, "��Ʒ����-����ʱ��", results);
				String title = "��Ʒ����-����ʱ��,������"+results.size()+",����:"+	today;
				if(results.size() >= 150){
					title = EmailUtils.IMPORTANT + title;
				}
				EmailUtils.sendAndReport(title,"�ص�۲죺һƷ���� ������ ��ͷ���� �� �Ѿ��ϲ���������Ϸ����У�ͬʱENE Upper�����Lower��<br>" +
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
				EmailUtils.send("[����]"+(weekly?"[����]":"")+"��Ʒ����-����ʱ��,������"+results.size()+",����:"+today,StkUtils.createHtmlTable(today, results));
			}
		}
		return results;
	}
	
	//��Ʒ����-����ʱ��
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
	
	//������ͣ�󿪰�
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
			EmailUtils.sendAndReport("������ͣ�󿪰�,������"+results.size()+",����:"+today,StkUtils.createHtmlTable(today, results));
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
			EmailUtils.send("������ͣ�����Ͽ����,������"+results.size()+",����:"+today,StkUtils.createHtmlTable(today, results));
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
			EmailUtils.send("[����][����]������ͻ��,������"+results.size()+",����:"+today,StkUtils.createHtmlTable(today, results));
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
			EmailUtils.send("[����][����]������ͻ��,������"+results.size()+",����:"+today,StkUtils.createHtmlTable(today, results));
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
			EmailUtils.send("[����][����]MACDճ��ѡ��,������"+results.size()+",����:"+today,StkUtils.createHtmlTable(today, results));
		}
	}
	
	//�����¸߹�
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
			EmailUtils.send("������ͣ��������¸߹�,������"+results.size()+",����:"+today,StkUtils.createHtmlTable(today, results));
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
			EmailUtils.sendAndReport("�ʽ���������ײ���Զ,������"+results.size()+",����:"+today,"����Ҫ�㣺һƷ����������Ҫ����0�ᣬMACDҲҪ����0��<br>"+
					StkUtils.createHtmlTable(today, results)  );
		}
	}
	
	public static void strategy(Connection conn, boolean realtime) throws Exception {
		Index idx =  new Index(conn,"399300","����300");
		K k = idx.getK(0);
		if(realtime){
			//idx.getKsRealTimeOnDay();
			k = IndexUtils.getKsRealTime(conn, idx.getCode());
			idx.addK(k);
		}
		K k20 = k.before(20);
		double bias1 = k.getBIAS4(6, 33);
		
		Index idx2 =  new Index(conn,"01000852","��֤1000");
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
		pe1.add("");pe1.add("20���Ƿ�");
		pe.add(pe1);
		List<String> pe2 = new ArrayList<String>();
		pe2.add("����300");pe2.add(StkUtils.numberFormat2Digits((k.getClose()/k20.getClose()-1)*100)+"%");
		pe.add(pe2);
		List<String> pe3 = new ArrayList<String>();
		pe3.add("��֤1000");pe3.add(StkUtils.numberFormat2Digits((kk.getClose()/kk20.getClose()-1)*100)+"%");
		pe.add(pe3);
		
		
		/////////////////////////////////
		//bias4.0����
		List<List<String>> bb = new ArrayList<List<String>>();
		List<String> b1 = new ArrayList<String>();
		b1.add("");b1.add("Bias");b1.add("�Ż�����");
		bb.add(b1);
		List<String> b2 = new ArrayList<String>();
		b2.add("����300");b2.add(StkUtils.numberFormat2Digits(bias1));b2.add("(17,0,-12)");
		bb.add(b2);
		List<String> b3 = new ArrayList<String>();
		b3.add("��֤1000");b3.add(StkUtils.numberFormat2Digits(bias2));b3.add("(18,3,-8)");
		bb.add(b3);
		
		
		/////////////////////////////////
		//�����С��ֵǿ��
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
		pe21.add("");pe21.add("��20�վ����ϸ���");
		list.add(pe21);
		List<String> pe22 = new ArrayList<String>();
		pe22.add("��С��ֵ100��");pe22.add(x+"");
		list.add(pe22);
		List<String> pe23 = new ArrayList<String>();
		pe23.add("�����ֵ100��");pe23.add(y+"");
		list.add(pe23);
		
		x = 0;
		List<List<String>> gnList = new ArrayList<List<String>>();
		if(!realtime){
			/////////////////////////////////
			//ͬ��˳����ָ������
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
			
			//60����ҵ�Ƿ����� - 20����ҵ�Ƿ����� Խ������Խǰ
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
			gn.add("�Ƿ���");gn.add("20������");gn.add("20���Ƿ�");gn.add("40������");gn.add("40���Ƿ�");gn.add("60������");gn.add("60���Ƿ�");
			gn.add("60��-20������");gn.add("60��-20��");
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
			EmailUtils.sendImport("�����ԡ�",StkUtils.createHtmlTable(null, pe)
					+ "<br>"+StkUtils.createHtmlTable(null, list)
					+ "<br>"+StkUtils.createHtmlTable(null, bb)
					);
		}else{
			EmailUtils.sendAndReport("�����ԡ�",StkUtils.createHtmlTable(null, pe)  
					+ "<br>"+  StkUtils.createHtmlTable(null, list)
					+ "<br>"+  StkUtils.createHtmlTable(null, bb)
					+ "<br><a href='http://q.10jqka.com.cn/stock/gn/' target='_blank'>ͬ��˳������</a>"
					+ "<br>"+ "20����������ҵ������"+ x //+ " (������60�������40����Σ�20���������ҿ�ʼ����ֵ����ҵ)"
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
			EmailUtils.sendAndReport("PEG����,������"+results.size()+",����:"+today, StkUtils.createHtmlTable(today, datas, addtitle) );
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
		//ͻ��20�վ���
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
		//��120���¸ߺ�س�20�վ���
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
		
		//��ȥ10�Ƿ�����
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
		
		EmailUtils.sendAndReport("��ҵ���� - "+category+",����:"+today, "<a href='http://q.10jqka.com.cn/gn/' target='_blank'>ͬ��˳������</a><br><a href='http://q.10jqka.com.cn/thshy/' target='_blank'>ͬ��˳��ҵ</a><br><br>"
				+ (k1 != null ? StkUtils.formatDate(k1.getDate())+"�����Ƿ�:<br>"+StkUtils.createHtmlTable(null, table4) : "") + "<br>"
				+ (k2 != null ? StkUtils.formatDate(k2.getDate())+"�����Ƿ�:<br>"+StkUtils.createHtmlTable(null, table5) : "") + "<br>"
				+ "10���Ƿ�:<br>" + StkUtils.createHtmlTable(null, table6) + "<br>"
				+ "ͻ���½�������:<br>" + StkUtils.createHtmlTable(null, table3) + "<br>"
				+ "ͻ��20�վ���:<br>" + StkUtils.createHtmlTable(null, table1) + "<br>"
				+ "��60���¸ߺ�س�20�վ���:<br>" + StkUtils.createHtmlTable(null, table2) + "<br>"
				);
	}
	
	private static List<List<String>> buildIndustryTable(List<Index> results, String date) throws Exception{
		List<List<String>> table = new ArrayList<List<String>>();
		List<String> row = new ArrayList<String>();
		row.add("");row.add("��ҵ");row.add("�Ƿ�");row.add("�ʽ�����");row.add("60����");row.add("120����");row.add("250����");
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
			if(ma60 > yma60){row.add("����");}else{row.add("");}
			if(ma120 > yma120){row.add("����");	}else{row.add("");}
			if(ma250 > yma250){row.add("����");	}else{row.add("");}
			table.add(row);
			i++;
		}
		return table;
	}
	
	/**
	 * 쳲��������� ʱ�䴰�� ��ʱ
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
			
			EmailUtils.sendAndReport("ʱ�䴰�� 쳲��������� ��ʱ,����:"+today, 
					"쳲��������� ��ʱ: result>=2ʱ����ʱ�䴰��<br/>"+StringUtils.join(results, "<br/>"));
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
			return date+",c1="+count1+",d1="+diff1+",c2="+count2+",d2="+diff2+", result="+(result>=2?result+" ��===":result);
		}
	}
	

}

