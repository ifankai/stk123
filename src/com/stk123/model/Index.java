package com.stk123.model;

import java.awt.Point;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.htmlparser.Node;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableHeader;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.jfree.data.category.DefaultCategoryDataset;

import com.stk123.bo.Stk;
import com.stk123.bo.StkCapitalFlow;
import com.stk123.bo.StkEarningsForecast;
import com.stk123.bo.StkEarningsNotice;
import com.stk123.bo.StkFnData;
import com.stk123.bo.StkFnType;
import com.stk123.bo.StkHolder;
import com.stk123.bo.StkImportInfo;
import com.stk123.bo.StkIndustryType;
import com.stk123.bo.StkKline;
import com.stk123.bo.StkMonitor;
import com.stk123.bo.StkRestricted;
import com.stk123.bo.cust.StkFnDataCust;
import com.stk123.json.SinaQianFuQuan;
import com.stk123.json.XueQiuQianFuQuan;
import com.stk123.model.K.Condition;
import com.stk123.task.InitialData;
import com.stk123.task.StkUtils;
import com.stk123.task.XueqiuUtils;
import com.stk123.tool.db.util.DateUtil;
import com.stk123.tool.util.AlgorithmUtils;
import com.stk123.tool.util.CacheUtils;
import com.stk123.tool.util.ChartUtils;
import com.stk123.tool.util.ChineseUtils;
import com.stk123.tool.util.DrawLineUtils;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.HtmlUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.ImageUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.tool.util.collection.Name2Value;
import com.stk123.tool.util.collection.Table;
import com.stk123.tool.util.collection.TableCell;
import com.stk123.web.StkConstant;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class Index {
	
	public final static int SH = 1;
	public final static int SZ = 2;
	
	public final static String SH_UPPER = "SH";
	public final static String SZ_UPPER = "SZ";
	public final static String SH_LOWER = "sh";
	public final static String SZ_LOWER = "sz";
	
	public String FN_JLR = StkConstant.FN_TYPE_CN_JLR;//净利润
	public String FN_JLRZZL = StkConstant.FN_TYPE_CN_JLRZZL;//净利润增长率
	public String FN_MGJZC = StkConstant.FN_TYPE_CN_MGJZC; //每股净资产_调整后(元)
	public String FN_ZYSR = StkConstant.FN_TYPE_CN_ZYSR; //主营收入
	public String FN_YFFY = StkConstant.FN_TYPE_CN_YFFY; //研发费用
	public String FN_GDQY = StkConstant.FN_TYPE_CN_GDQY; //归属于母公司股东权益合计
	public String FN_ZYSRZZL = StkConstant.FN_TYPE_CN_ZYSRZZL;//主营收入增长率
	public String FN_ROE = StkConstant.FN_TYPE_CN_ROE;//ROE
	public String FN_GROSS_MARGIN = StkConstant.FN_TYPE_CN_GROSS_MARGIN;//毛利率
	
	public final static int FN_Growth = 1;
	public final static int FN_Potential = 2;
	public final static int FN_Reversion = 3;
	public final static int FN_Common = 0;
	
	public final static String KLINE_20110101 = "and kline_date>='20110101'";
	public final static String KLINE_20060101 = "and kline_date>='20060101'";
	public final static String KLINE_20130101 = "and kline_date>='20130101'";
	public final static String KLINE_20140101 = "and kline_date>='20140101'";
	public final static String KLINE_20150101 = "and kline_date>='20150101'";
	public final static String KLINE_20160101 = "and kline_date>='20160101'";
	public final static String KLINE_20170101 = "and kline_date>='20170101'";
	
	public static String getKlineDateAsWhereClause(int days) {
		Date date = StkUtils.addDay(new Date(), days);
		String s = StkUtils.formatDate(date, StkUtils.sf_ymd2);
		return "and kline_date>='"+s+"'";
	}
	
	// "and kline_date>='" + StkUtils.formatDate(StkUtils.addDay(StkUtils.now, -900),StkUtils.sf_ymd2)+"'";
	public static String KLineWhereClause = KLINE_20130101; 
	
	public final static String FNDate_1 = "and fn_date>='20120101'";
	public final static String FNDate_20130101 = "and fn_date>='20130101'";
	public final static String FNDate_20140101 = "and fn_date>='20140101'";
	public static String FNDateWhereClause = "";
	
	private Connection conn = null;
	private static final String EMPTY = "_empty";

	
	private String code;
	private String name;
	public double changePercent;
	public boolean isNew = false; //看是不是今天新入选的股
	public boolean isDeleted = false; //昨天入选，而今天没有入选
	public int order;
	
	private int loc;// 1:sh, 2:sz
	private int market;// 1:A股, 2:美股, 3:港股
	public final static int CN = 1;
	public final static int US = 2;
	public final static int HK = 3;
	
	private String tab_stkkline;
	public String tab_stkfndata;
	
	//private Stk stk;
	private Stk stock;
	private Map<String,List<String>> industryNames = new HashMap<String,List<String>>();
	//private List<Industry> industries = new ArrayList<Industry>();
	private Canslim canslim = new Canslim(); //欧奈尔
	private PE pe;

	private LinkedList<K> ks = null; 
	private LinkedList<K> kws = null;
	private LinkedList<K> kms = null;
	private List<StkFnDataCust> fnData = null;
	private List<StkHolder> holders = new ArrayList<StkHolder>();
	private List<Name2Value> f9 = null;
	
	//---------common SQL-------------//
	public final static String SQL_SELECT_STK_CODE_NAME_ALL = "select code,name from stk";
	public final static String SQL_SELECT_STK_CODE_NAME_BY_CODE = "select code,name from stk where code=?";
	public final static String SQL_SELECT_STOCK_BY_CODE = "select * from stk where code=?";
	//---------SQL-------------//	

	/** use when in multiple thread */
	public Index(String code) {
		this((Connection)null, code);
	}
	
	public Index(Connection conn, String code) {
		this.conn = conn;
		this.code = code;
		boolean isAllNumber = StkUtils.isAllNumeric(code);
		
		if(code.length() == 5 && isAllNumber){
			this.market = HK;
		}else{
			this.market = isAllNumber?CN:US;
		}
		
		if(this.market == 1){
			if(code.length() == 8){//01000010 : sh 000010
				if(code.startsWith("01")){
					this.loc = Index.SH;
				}else{
					this.loc = Index.SZ;
				}
			}else{
				this.loc = Index.getLocation(code);
			}
		}
		setMarketTable();
		setMarketFnType();
	}
	
	public Index(Connection conn, String code, int loc) {
		this(conn, code);
		this.loc = loc;
	}
	
	public static int getLocation(String code){
		if(code.startsWith(StkConstant.NUMBER_SIX) || code.startsWith("99")){
			return Index.SH;
		}else{
			return Index.SZ;
		}
	}
	
	public Index(Connection conn, String code, String name) {
		this(conn, code);
		this.name = name;
	}
	
	public String getLocationAsString(){
		if(this.loc == Index.SH)return Index.SH_UPPER;
		return Index.SZ_UPPER;
	}
	
	//----table name----//
	private final static String STK_KLINE = "stk_kline";
	private final static String STK_FN_DATA = "stk_fn_data";
	private final static String STK_KLINE_US = "stk_kline_us";
	private final static String STK_FN_DATA_US = "stk_fn_data_us";
	private final static String STK_KLINE_HK = "stk_kline_hk";
	private final static String STK_FN_DATA_HK = "stk_fn_data_hk";
	
	public void setMarketTable(){
		if(this.market == 1){
			this.tab_stkkline = STK_KLINE;
			this.tab_stkfndata = STK_FN_DATA;
		}else if(this.market == 2){
			this.tab_stkkline = STK_KLINE_US;
			this.tab_stkfndata = STK_FN_DATA_US;
		}else if(this.market == 3){
			this.tab_stkkline = STK_KLINE_HK;
			this.tab_stkfndata = STK_FN_DATA_HK;
		}
		
	}
	
	public void setMarketFnType(){
		if(this.market == 2){
			FN_JLR = "1008";//净利润
			FN_ROE = "4004";
			FN_MGJZC = "4013";
		}
	}
	
	public boolean isStop(String date) throws Exception {
		K k = this.getK(date);
		if(k != null && !date.equals(k.getDate())){
			return true;
		}
		return false;
	}
	
	public void gc(){
		CacheUtils.removeByCode(this.getCode(), CacheUtils.KEY_STK_STOCK);
		if(ks != null){
			ks.clear();
			ks = null;
		}
		if(kws != null){
			kws.clear();
			kws = null;
		}
		if(kms != null){
			kms.clear();
			kms = null;
		}
		//ks = new LinkedList<K>();
		CacheUtils.removeByCode(this.getCode(), CacheUtils.KEY_STK_K);
		if(fnData != null){
			fnData.clear();
			fnData = null;
		}
		//fnData = new ArrayList<StkFnDataCust>();
		CacheUtils.removeByCode(this.getCode(), CacheUtils.KEY_STK_FN);
		this.stock = null;
	}
	
	public Exception exception(Exception e){
		return ExceptionUtils.exception(this.code+","+this.name, e);
	}
	
	/********************************* 初始化信息 ******************************************/
	private final static String SQL_INIT = "select * from stk_fn_type where status=1 and market=";
	//init stk info and fn data
	public void init() throws Exception {
		if(this.getName() == null){
			throw new Exception("stk name is null.");
		}
		List<StkFnType> fnTypes = JdbcUtils.list(this.getConnection(), SQL_INIT + this.market, StkFnType.class);
		if(this.market == 1){
			//行业，盈利预期
			InitialData.updateStkInfoFromWind(this.getConnection(),this,StkUtils.now,fnTypes);
		}
		//fn
		InitialData.initFnDataTTM(this.getConnection(),StkUtils.now,this,fnTypes);
	}
	
	//from finance.qq.com
	public void initKLine() throws Exception {
		if(market == 1){
			int cate = this.getStk().getCate().intValue();
			String tmpCode = null;
			String page = null;
			if(cate == 1){
				tmpCode = (this.loc==1?Index.SH_LOWER:Index.SZ_LOWER)+this.code;
			}else if(cate == 2){
				if(SH999999.equals(this.code)){
					tmpCode = "sh000001";
				}else{
					if(this.code.length() == 8){
						tmpCode = (this.loc==1?Index.SH_LOWER:Index.SZ_LOWER)+StringUtils.substring(this.code, 2, 8);
					}else{
						tmpCode = (this.loc==1?Index.SH_LOWER:Index.SZ_LOWER)+this.code;
					}
				}
			}else if(cate == 4){
				//http://q.10jqka.com.cn/stock/gn/
				page = HttpUtils.get("http://d.10jqka.com.cn/v2/line/bk_"+this.code+"/01/last.js", null, "gbk");
				if("404".equals(page))return;
				String data = StringUtils.substringBetween(page, "data\":\"", "\"})");
				String[] ks = StringUtils.split(data, ";");
				if(ks != null){
					for(String kk : ks){
						String[] k = kk.split(",");
						List params = new ArrayList();
						params.add(code);
						params.add(k[0]);//date
						params.add(k[1]);//open
						params.add(k[4]);//close
						//params.add(lastClose);
						params.add(k[2]);//high
						params.add(k[3]);//low
						params.add(k[5]);//volume
						params.add(Double.parseDouble(k[6])/100);//amount
						params.add(code);
						params.add(k[0]);
						
						JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+" (code,kline_date,open,close,high,low,volumn,amount,close_change) select ?,?,?,?,?,?,?,?,null from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
					}
				}
				
				page = HttpUtils.get("http://d.10jqka.com.cn/v2/line/bk_"+this.code+"/01/today.js", null, "gbk");
				data = "{"+StringUtils.substringBetween(page, ":{", "}}")+"}";
				Map map = (Map)JsonUtils.getObject4Json(data, Map.class, null);
				
				String date = null;
				if(map != null){
					List params = new ArrayList();
					params.add(code);
					date = String.valueOf(map.get("1"));
					params.add(date);//date
					params.add(map.get("7"));//open
					params.add(map.get("11"));//close
					params.add(map.get("8"));//high
					params.add(map.get("9"));//low
					params.add(map.get("13"));//volume
					params.add(Double.parseDouble(String.valueOf(map.get("19")))/100);//amount
					params.add(code);
					params.add(date);

					JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+" (code,kline_date,open,close,high,low,volumn,amount,close_change) select ?,?,?,?,?,?,?,?,null from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
				}
				
				return;
			}
			//腾讯股票接口 http://qt.gtimg.cn/&q=
			page = HttpUtils.get("http://qt.gtimg.cn/&q="+tmpCode, null, "");
			if(page != null && page.length() > 0){
				String str = StringUtils.substringBetween(page, "\"", "\"");
				//System.out.println(str);
				String[] ss = str.split("~");
				
				if(ss.length > 40){
					String date = ss[30].substring(0, 8);
					double close = Double.parseDouble(ss[3]);
					double open = Double.parseDouble(ss[5]);
					if(close == 0.0 || open == 0.0){//停牌
						//System.out.println("停牌");
						return;
					}
					
					double lastClose = Double.parseDouble(ss[4]);
					double volume = Double.parseDouble(ss[6]) * 100;
					double percentage = Double.parseDouble(ss[32]);
					double high = Double.parseDouble(ss[33]);
					double low = Double.parseDouble(ss[34]);
					double amount = Double.parseDouble(ss[37]) * 10000;
					double hsl = 0;
					double pettm = 0;
					double pbttm = 0;
					double psttm = 0;
					if(cate == 1){
						hsl = Double.parseDouble(ss[38]);
						if(ss[39].length()>0){
							pettm = Double.parseDouble(ss[39]);
						}
						if(ss[46].length()>0){
							pbttm = Double.parseDouble(ss[46]); //sina的pb不对
							StkKline kTmp = this.getK().getKline();
							if(kTmp != null){
								Double pbLast = kTmp.getPbTtm();
								if(pbLast != null && Math.abs((pbLast-pbttm)/pbLast)>0.2 ){
									pbttm = this.getPB();
								}
							}
						}
						psttm = this.getPSTTM();
					}
					//System.out.println("date="+date+",lastClose="+lastClose+",open="+open+",close="+close+",high="+high+",low="+low+",volume="+volume+",amount="+amount+",percentage="+percentage+",hsl="+hsl+",pettm="+pettm+",pbttm="+pbttm);
					
					List params = new ArrayList();
					params.add(code);
					params.add(date);
					params.add(open);
					params.add(close);
					params.add(lastClose);
					params.add(high);
					params.add(low);
					params.add(volume);
					params.add(amount);
					params.add(hsl);
					params.add(pettm);
					params.add(pbttm);
					params.add(percentage);
					params.add(psttm);
					params.add(code);
					params.add(date);
					
					JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+" (code,kline_date,open,close,last_close,high,low,volumn,amount,close_change,hsl,pe_ttm,pb_ttm,percentage,ps_ttm) select ?,?,?,?,?,?,?,?,?,null,?,?,?,?,? from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
					this.setCloseChange();
					
					/*try{
						initCapitalFlow(date);
					}catch(Exception e){
						ExceptionUtils.insertLog(this.getConnection(),code, e);
						try{
							initCapitalFlow(date);
						}catch(Exception ee){}
					}*/
				}
				
			}
		}else if(market == 2){
			String tmpCode = this.code.replace(".", "");
			String page = HttpUtils.get("http://qt.gtimg.cn/&q=us"+tmpCode, null, "GBK");
			
			if(page != null && page.length() > 0){
				String str = StringUtils.substringBetween(page, "\"", "\"");
				//System.out.println(str);
				if(str == null)return;
				String[] ss = str.split("~");
				
				if(ss.length > 40){
					double close = Double.parseDouble(ss[3]);
					double open = Double.parseDouble(ss[5]);
					if(close == 0.0 || open == 0.0){//停牌
						//System.out.println("停牌");
						return;
					}
					String date = StringUtils.replace(ss[30].substring(0, 10), "-", "");
					double lastClose = Double.parseDouble(ss[4]);
					double volume = Double.parseDouble(ss[6]);
					double percentage = Double.parseDouble(ss[32]);
					double high = Double.parseDouble(ss[33]);
					double low = Double.parseDouble(ss[34]);
					double amount = Double.parseDouble(ss[37]) * 10000;
					double pettm = 0;
					if(ss[39].length()>0 && StkUtils.isAllNumericOrDot(ss[39])){
						pettm = Double.parseDouble(ss[39]);
					}
					//System.out.println("date="+date+",lastClose="+lastClose+",open="+open+",close="+close+",high="+high+",low="+low+",volume="+volume+",amount="+amount+",percentage="+percentage+",pettm="+pettm);
					
					List params = new ArrayList();
					//计算PB
					params.add(code);
					Double bv = JdbcUtils.first(this.getConnection(), "select fn_value from stk_fn_data_us where type=4013 and code=? order by fn_date desc", params, Double.class);
					Double pbttm = (bv == null || bv == 0)?null:close/bv;
					
					Double salePerShare = JdbcUtils.first(this.getConnection(), "select fn_value from stk_fn_data_us where type=4005 and code=? order by fn_date desc", params, Double.class);
					Double psttm = (salePerShare == null || salePerShare == 0)?null:close/salePerShare;
					
					//params.add(code);
					params.add(date);
					params.add(open);
					params.add(close);
					params.add(lastClose);
					params.add(high);
					params.add(low);
					params.add(volume);
					params.add(amount);
					params.add(pettm);
					params.add(percentage);
					params.add(pbttm);
					params.add(psttm);
					params.add(code);
					params.add(date);
					
					JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+" (code,kline_date,open,close,last_close,high,low,volumn,amount,close_change,pe_ttm,percentage,pb_ttm,ps_ttm) select ?,?,?,?,?,?,?,?,?,null,?,?,?,? from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
				}
			}
			
		}else if(market == 3){
			String page = HttpUtils.get("http://qt.gtimg.cn/&q=hk"+this.code, null, "GBK");
			
			if(page != null && page.length() > 0){
				String str = StringUtils.substringBetween(page, "\"", "\"");
				//System.out.println(str);
				if(str == null)return;
				String[] ss = str.split("~");
				
				if(ss.length > 40){
					double close = Double.parseDouble(ss[3]);
					double open = Double.parseDouble(ss[5]);
					if(close == 0.0 || open == 0.0){//停牌
						//System.out.println("停牌");
						return;
					}
					String date = StringUtils.replace(ss[30].substring(0, 10), "/", "");
					double lastClose = Double.parseDouble(ss[4]);
					double volume = Double.parseDouble(ss[6]);
					double percentage = Double.parseDouble(ss[32]);
					double high = Double.parseDouble(ss[33]);
					double low = Double.parseDouble(ss[34]);
					double amount = Double.parseDouble(ss[37]);
					double pettm = 0;
					if(ss[39].length()>0 && StkUtils.isAllNumericOrDot(ss[39])){
						pettm = Double.parseDouble(ss[39]);
					}
					//System.out.println("date="+date+",lastClose="+lastClose+",open="+open+",close="+close+",high="+high+",low="+low+",volume="+volume+",amount="+amount+",percentage="+percentage+",pettm="+pettm);
					
					List params = new ArrayList();
					params.add(code);
					params.add(date);
					params.add(open);
					params.add(close);
					params.add(lastClose);
					params.add(high);
					params.add(low);
					params.add(volume);
					params.add(amount);
					params.add(pettm);
					params.add(percentage);
					params.add(null);//pbttm
					params.add(null);//psttm
					params.add(code);
					params.add(date);
					
					JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+" (code,kline_date,open,close,last_close,high,low,volumn,amount,close_change,pe_ttm,percentage,pb_ttm,ps_ttm) select ?,?,?,?,?,?,?,?,?,null,?,?,?,? from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
				}
			}
		}
	}
	

	//http://hqdigi2.eastmoney.com/EM_Quote2010NumericApplication/CompatiblePage.aspx?Type=f&jsName=js&stk=0000011&Reference=xml
	//http://q.stock.sohu.com/cn/002353/index_kp.shtml
	//http://xueqiu.com/S/SZ002353
	//http://xueqiu.com/S/SH000001
	public final static String SH999999 = "999999";
	public final static String SH000001 = "SH000001";
	@Deprecated
	public void initKLineToday() throws Exception {
		if(market == 1){
			if(true){//from xueqiu
				String tmp = null;
				if(SH999999.equals(this.code)){
					tmp = SH000001;
				}else{
					if(this.code.length() == 8){
						tmp = (this.loc==1?Index.SH_UPPER:Index.SZ_UPPER)+StringUtils.substring(this.code, 2, 8);
					}else{
						tmp = (this.loc==1?Index.SH_UPPER:Index.SZ_UPPER)+this.code;
					}
				}
				String page = HttpUtils.get("http://xueqiu.com/S/"+tmp, null, XueqiuUtils.getCookies(), "utf-8");
				Node node = HtmlUtils.getNodeByAttribute(page, "", "class", "stockQuote");
				if(node == null)return;
				String str = node.toHtml();
				node = HtmlUtils.getNodeByAttribute(str, "", "id", "currentQuote");
				if("停牌".equals(node.toPlainTextString())){
					return;
				}
				
				node = HtmlUtils.getNodeByAttribute(str, "", "id", "timeInfo");
				if(node == null)return;//停牌
				if("-&nbsp;&nbsp;".equals(node.toPlainTextString()))return;//新股
				String date = StkUtils.formatDate(StkUtils.sf_ymd.parse(StkUtils.YEAR + "-" + StringUtils.substring(node.toPlainTextString(), 0, 5)),StkUtils.sf_ymd2);
				node = HtmlUtils.getNodeByText(str, "", "今开：");
				if(node == null){//停牌了
					return;
				}
				String open = node.getLastChild().toPlainTextString();
				if("-".equals(open) || (StkUtils.isAllNumericOrDot(open) && Double.parseDouble(open) == 0.0)){//停牌了
					return;
				}
				node = HtmlUtils.getNodeByAttribute(str, "", "class", "quote-percentage");
				String percentage = StringUtils.substringBetween(node.toPlainTextString(), "(", "%)");
				
			    node = HtmlUtils.getNodeByText(str, "", "昨收");
				String close = StringUtils.substringBetween(str, "data-current=\"", "\"");
				node = HtmlUtils.getNodeByText(str, "", "昨收");
				String lastClose = node.getLastChild().toPlainTextString();
				node = HtmlUtils.getNodeByText(str, "", "最高");
				String high = node.getLastChild().toPlainTextString();
				node = HtmlUtils.getNodeByText(str, "", "最低");
				String low = node.getLastChild().toPlainTextString();
				node = HtmlUtils.getNodeByText(str, "", "成交量");
				String svolumn = node.getChildren().elementAt(1).toPlainTextString();
				svolumn = StringUtils.replace(svolumn, "股", "");
				int n = 1;
				if(StringUtils.indexOf(svolumn, "万") > 0){
					n = 10000;
					svolumn = StringUtils.replace(svolumn, "万", "");
				}else if(StringUtils.indexOf(svolumn, "亿") > 0){
					n = 100000000;
					svolumn = StringUtils.replace(svolumn, "亿", "");
				}else if("-".equals(svolumn)){
					svolumn = "0";
				}
				double volumn = Double.parseDouble(svolumn)*n;
				node = HtmlUtils.getNodeByText(str, "", "成交额");
				String samount= node.getLastChild().toPlainTextString();
				if("-".equals(samount)){
					samount = "0";
				}else if(StringUtils.indexOf(samount, "万") > 0){
					n = 10000;
					samount = StringUtils.replace(samount, "万", "");
				}else if(StringUtils.indexOf(samount, "亿") > 0){
					n = 100000000;
					samount = StringUtils.replace(samount, "亿", "");
				}
				double amount = Double.parseDouble(samount)*n;
				
				//hsl
				String hsl = null;
				if(this.getStk().getCate().intValue() == 1 || "999999".equals(this.code)){
					if("999999".equals(this.code)){
						tmp = "0000011";
					}else{
						tmp = this.code+this.loc;
					}
					page = HttpUtils.get("http://hqdigi2.eastmoney.com/EM_Quote2010NumericApplication/CompatiblePage.aspx?Type=f&jsName=js&stk="+tmp+"&Reference=xml", null, "GBK");
					String sData = StringUtils.substringBetween(page, "js={skif:\"", "\",");
					String[] datas = sData.split(",");
					if(datas.length > 10){
						hsl = StringUtils.replace(datas[12], "%", "");
					}
				}
				
				List params = new ArrayList();
				params.add(code);
				params.add(date);
				params.add(open);
				params.add(close);
				params.add(lastClose);
				params.add(high);
				params.add(low);
				params.add(volumn);
				params.add(amount);
				params.add(hsl);
				
				String peTTM = null;
				String lyr = null;
				String psTTM = null;
				String pbTTM = null;
				if(this.getStk().getCate().intValue() == 1){
					//this.initPEFromXueQiu(str);
					//Node nodeLYR = HtmlUtils.getNodeByText(str, "", "市盈率LYR：");
					Node nodePE = HtmlUtils.getNodeByText(str, "", "市盈率(静)/(动)：");
					if(nodePE == null){
						EmailUtils.send("[Error]雪球取PE/PB出错!", code);
					}
					if(nodePE != null){
						lyr = StringUtils.substringBefore(nodePE.getLastChild().toPlainTextString(), "/");
						peTTM = StringUtils.substringAfter(nodePE.getLastChild().toPlainTextString(), "/");
					}
					//System.out.println("lyr="+lyr+",ttm="+peTTM);
					params.add(NumberUtils.isNumber(peTTM)?peTTM:null);
					params.add(NumberUtils.isNumber(lyr)?lyr:null);
					Node nodePS = HtmlUtils.getNodeByText(str, "", "市销率(动)：");
					if(nodePS != null){
						psTTM = nodePS.getLastChild().toPlainTextString();
						params.add(NumberUtils.isNumber(psTTM)?psTTM:null);
					}
					Node nodePB = HtmlUtils.getNodeByText(str, "", "市净率(动)：");
					if(nodePB != null){
						pbTTM = nodePB.getLastChild().toPlainTextString();
						params.add(NumberUtils.isNumber(pbTTM)?pbTTM:null);
					}
				}else{
					params.add(null);
					params.add(null);
					params.add(null);
					params.add(null);
				}
				params.add(percentage);
				params.add(code);
				params.add(date);
				int cnt = JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+" (code,kline_date,open,close,last_close,high,low,volumn,amount,hsl,pe_ttm,pe_lyr,ps_ttm,pb_ttm,percentage) select ?,?,?,?,?,?,?,?,?,?,?,?,?,?,? from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
				if(cnt == 0){
					params.clear();
					params.add(hsl);
					params.add(NumberUtils.isNumber(peTTM)?peTTM:null);
					params.add(NumberUtils.isNumber(lyr)?lyr:null);
					params.add(NumberUtils.isNumber(psTTM)?psTTM:null);
					params.add(NumberUtils.isNumber(pbTTM)?pbTTM:null);
					params.add(code);
					params.add(date);
					JdbcUtils.update(this.getConnection(), "update "+tab_stkkline+" set hsl=?,pe_ttm=?,pe_lyr=?,ps_ttm=?,pb_ttm=? where code=? and kline_date=?", params);
				}
				this.setCloseChange();
				
				if(peTTM == null || "0".equals(peTTM) || "-".equals(peTTM)){
					peTTM = StkUtils.number2String(this.getPETTMByCalculation(StkUtils.getToday()).getPe(),2);
					params.clear();
					params.add(peTTM);
					params.add(code);
					params.add(date);
					JdbcUtils.update(this.getConnection(), "update "+tab_stkkline+" set pe_ttm=? where code=? and kline_date=?", params);
				}
				try{
					initCapitalFlow(date);
				}catch(Exception e){
					ExceptionUtils.insertLog(this.getConnection(),code, e);
					try{
						initCapitalFlow(date);
					}catch(Exception ee){}
				}
			}else{//from eastmoney
				String tmp = null;
				if("999999".equals(this.code)){
					tmp = "0000011";
				}else{
					tmp = this.code+this.loc;
				}
				String page = HttpUtils.get("http://hqdigi2.eastmoney.com/EM_Quote2010NumericApplication/CompatiblePage.aspx?Type=f&jsName=js&stk="+tmp+"&Reference=xml", null, "GBK");
				String str = StringUtils.substringBetween(page, "js={skif:\"", "\",");
				String[] datas = str.split(",");
				if(datas.length > 10){
					if("0.00".equals(datas[8])){
						return;
					}
					List params = new ArrayList();
					params.add(code);
					String date = StringUtils.replace(StringUtils.substring(datas[27], 0, 10), "-", "");
					params.add(date);
					params.add(datas[8]);
					params.add(datas[3]);
					params.add(datas[9]);
					params.add(datas[10]);
					params.add(datas[11]);
					params.add(Double.parseDouble(datas[17])*100);
					params.add(Double.parseDouble(datas[19])*10000);
					params.add(code);
					params.add(date);
					JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+" (code,kline_date,open,close,last_close,high,low,volumn,amount,close_change,hsl) select ?,?,?,?,?,?,?,?,?,null,null from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
					this.setCloseChange();
				}
				/*if(!"999999".equals(this.code)){
					this.initPEFromXueQiu(str);
				}*/
			}
		}else if(market == 2){
			Date tmp = StkUtils.addDay(StkUtils.now, -60);
			String url = "http://finance.yahoo.com/q/hp?s="+StringUtils.replace(code, ".", "%5E")+"&a="+StkUtils.get(tmp,Calendar.MONTH)+"&b="+StkUtils.get(tmp,Calendar.DATE)+"&c="+StkUtils.get(tmp,Calendar.YEAR)+"&d="+StkUtils.get(StkUtils.now,Calendar.MONTH)+"&e="+StkUtils.get(StkUtils.now,Calendar.DATE)+"&f="+StkUtils.get(StkUtils.now,Calendar.YEAR)+"&g=d";
			//System.out.println(url);
			String page = HttpUtils.get(url, "utf8");
			Node node = HtmlUtils.getNodeByAttribute(page, null, "class", "yfnc_datamodoutline1");
			if(node == null)return;
			List<Node> list = HtmlUtils.getNodeListByTagName(node, "table");
			List<List<String>> datas = HtmlUtils.getListFromTable((TableTag)list.get(1), 0);
			//delete item: [Feb 28, 2014, 0.078 Dividend]
			for(int i=datas.size()-1;i>=0;i--){
				List k = (List)datas.get(i);
				if(k.size() < 7){
					datas.remove(i);
				}
			}
			List params = new ArrayList();
			for(int i=0;i<datas.size();i++){
				if(i >= 30)break;
				List k = (List)datas.get(i);
				if(k.size() >= 7){
					params.clear();
					params.add(code);
					String date = StkUtils.formatDate(new SimpleDateFormat("MMM d, yyyy",Locale.US).parse(String.valueOf(k.get(0))), new SimpleDateFormat("yyyyMMdd"));
					params.add(date);
					params.add(StringUtils.replace(String.valueOf(k.get(1)), ",", ""));
					params.add(StringUtils.replace(String.valueOf(k.get(4)), ",", ""));
					if(i>=1 && i<datas.size()-2){
						List before = datas.get(i+1);
						if(before.size() < 7){
							before = datas.get(i+2);
						}
						params.add(StringUtils.replace(String.valueOf(before.get(4)), ",", ""));
					}else{
						params.add(StringUtils.replace(String.valueOf(k.get(1)), ",", ""));
					}
					params.add(StringUtils.replace(String.valueOf(k.get(2)), ",", ""));
					params.add(StringUtils.replace(String.valueOf(k.get(3)), ",", ""));
					params.add(StringUtils.replace(String.valueOf(k.get(5)), ",", ""));
					params.add(StringUtils.replace(String.valueOf(k.get(4)), ",", ""));
					params.add(code);
					params.add(date);
					JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+" (code,kline_date,open,close,last_close,high,low,volumn,close_change) select ?,?,?,?,?,?,?,?,? from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
				}
			}
		}
	}
	
	public void initKLines() throws Exception {
		initKLines(100000);
	}
	
	public void initKLines(int n) throws Exception {
		List params = new ArrayList();
		Date now = new Date();
		String startDate = StkUtils.formatDate(StkUtils.addDay(now, -n),StkUtils.sf_ymd2);
		
		if(market == 1){
			if(this.getStk().getCate() == 2 || this.getStk().getCate() == 1){
				String tmp = null;
				if(this.code.length() == 6){
					tmp = (this.loc==1?"sse":"szse") + (this.code.equals("999999")?"000001":this.code);
					/*String url = "http://flashquote.stock.hexun.com/Quotejs/DA/"+(this.loc)+"_"+(this.code.equals("999999")?"000001":this.code)+"_DA.html";
					System.out.println(url);
					String page = HttpUtils.get("http://flashquote.stock.hexun.com/Quotejs/DA/"+(this.loc)+"_"+(this.code.equals("999999")?"000001":this.code)+"_DA.html", null, "gbk");
					//System.out.println(page);
					List<List> datas = JsonUtils.getList4Json("[["+StringUtils.substringBetween(page, "[[", "]]")+"]]", ArrayList.class );
					for(List data:datas){
						String date = String.valueOf(data.get(0));
						date = date.replaceAll("000000", "");
						if(date.compareTo(StkUtils.getToday()) > 0)continue;
						if(date.compareTo(startDate) < 0)continue;
						params.clear();
						params.add(this.code);
						params.add(data.get(0));
						params.add(data.get(2));
						params.add(data.get(5));
						params.add(data.get(1));
						params.add(data.get(3));
						params.add(data.get(4));
						params.add(data.get(6));
						params.add(data.get(7));
						params.add(this.code);
						params.add(data.get(0));
						JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+"(code,kline_date,open,close,last_close,high,low,volumn,amount,close_change,hsl) select ?,?,?,?,?,?,?,?,?,null,null from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
					}*/
				}else if(this.code.length() == 8){
					tmp = (this.loc==1?"sse":"ssz")+StringUtils.substring(this.code, 2, 8);
				}
				
				String page = HttpUtils.get("http://webstock.quote.hermes.hexun.com/a/kline?code="+tmp+"&start="+StkUtils.getToday()+"150000&number=-1000&type=5&callback=callback", null, "gb2312");
				//System.out.println(page);
				List<List> datas = JsonUtils.getList4Json("["+StringUtils.substringBetween(page, "[[", "]]")+"]]", ArrayList.class );
				for(List data:datas){
					if(data == null)return;
					String date = String.valueOf(data.get(0));
					date = date.replaceAll("000000", "");
					if(date.compareTo(StkUtils.getToday()) > 0)continue;
					if(date.compareTo(startDate) < 0)continue;
					params.clear();
					params.add(this.code);
					params.add(StringUtils.substring(String.valueOf(data.get(0)), 0, 8));
					params.add(Double.parseDouble(String.valueOf(data.get(2)))/100.0);
					params.add(Double.parseDouble(String.valueOf(data.get(3)))/100.0);
					params.add(Double.parseDouble(String.valueOf(data.get(1)))/100.0);
					params.add(Double.parseDouble(String.valueOf(data.get(4)))/100.0);
					params.add(Double.parseDouble(String.valueOf(data.get(5)))/100.0);
					params.add(Double.parseDouble(String.valueOf(data.get(6)))/100.0);
					params.add(Double.parseDouble(String.valueOf(data.get(7)))/100.0);
					params.add(this.code);
					params.add(StringUtils.substring(String.valueOf(data.get(0)), 0, 8));
					JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+"(code,kline_date,open,close,last_close,high,low,volumn,amount,close_change,hsl) select ?,?,?,?,?,?,?,?,?,null,null from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
				}
				
			}
			/*else{
				*//**
				 * TODO
				 * http://webstock.quote.hermes.hexun.com/a/kline?code=szse000001&start=20180601150000&number=-600&type=5&callback=callback
				 *//*
				String page = HttpUtils.get("http://flashquote.stock.hexun.com/Quotejs/DA/" + this.loc + "_" + this.code + "_DA.html?", null, "gbk");
				List<List> datas = JsonUtils.getList4Json("[[" + StringUtils.substringBetween(page, "[[", "]]") + "]]", ArrayList.class);
				page = HttpUtils.get("http://finance.sina.com.cn/realstock/company/"+ (loc==1?"sh":"sz") + this.code + "/qianfuquan.js", null, "gbk");
				SinaQianFuQuan sina = (SinaQianFuQuan) JsonUtils.getObject4Json(StringUtils.substringBetween(page, "[", "]"), SinaQianFuQuan.class, null);
				for (List data : datas) {
					if (data == null || data.size() < 8)
						continue;
					String date = String.valueOf(data.get(0));
					if(date.compareTo(startDate) < 0)continue;
					params.clear();
					String closeChange = (String)sina.getData().get(StkUtils.sf_ymd3.format(StkUtils.sf_ymd2.parse(date)));
					params.add(code);
					params.add(date);
					params.add(data.get(2));
					params.add(data.get(5));
					params.add(data.get(1));
					params.add(data.get(3));
					params.add(data.get(4));
					params.add(data.get(6));
					params.add(data.get(7));
					params.add(closeChange);
					params.add(code);
					params.add(date);
					JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+" (code,kline_date,open,close,last_close,high,low,volumn,amount,close_change,hsl) select ?,?,?,?,?,?,?,?,?,?,null from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
					
					params.clear();
					params.add(data.get(6));
					params.add(data.get(7));
					params.add(closeChange);
					params.add(code);
					params.add(date);
					JdbcUtils.insert(this.getConnection(), "update "+tab_stkkline+" set volumn=?,amount=?,close_change=? where code=? and kline_date=?", params);
				}
				//this.initPEFromXueQiu();
			}*/
			setCloseChange();
			//initHsl(1);
			//initCapitalFlow();
		}else if(market == 2){//美股
			String page = HttpUtils.get("http://stock.finance.sina.com.cn/usstock/api/json.php/US_MinKService.getDailyK?symbol="+this.code.toLowerCase()+"&___qn=3", null, "GBK");
			//System.out.println(page);
			if(page == null || "null".equals(page))return;
			List<Map> ks = JsonUtils.getList4Json(page, Map.class);
			for(int i=0; i<ks.size(); i++){
				Map k = ks.get(i);
				String date = StringUtils.replace(String.valueOf(k.get("d")), "-", "");
				if(date.compareTo(startDate) < 0)continue;
				if("20120101".compareTo(date) <= 0){
					if("0".equals(k.get("v")))continue;
					params.clear();
					params.add(code);
					params.add(date);
					params.add(k.get("o"));
					params.add(k.get("c"));
					if(i>=1){
						params.add(ks.get(i-1).get("c"));
					}else{
						params.add(k.get("o"));
					}
					params.add(k.get("h"));
					params.add(k.get("l"));
					params.add(k.get("v"));
					params.add(k.get("c"));
					params.add(code);
					params.add(date);
					JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+" (code,kline_date,open,close,last_close,high,low,volumn,close_change) select ?,?,?,?,?,?,?,?,? from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
					
				}
			}
			page = null;
			ks = null;
		}else{
			//throw new Exception("market not defined.");
		}
	}
	
	public void updateNtile(){
		List params = new ArrayList();
		params.add(code);
		Map map = JdbcUtils.load2Map(this.getConnection(), "select kline_date, ntile(100) over(order by pe_ttm) pe_ntile, ntile(100) over(order by pb_ttm) pb_ntile, ntile(100) over(order by ps_ttm) ps_ntile from stk_kline where kline_date>=to_char(sysdate-1825,'yyyymmdd') and code=? order by kline_date desc", params);
		if(map != null){
			params.clear();
			params.add(map.get("pe_ntile"));
			params.add(map.get("pb_ntile"));
			params.add(map.get("ps_ntile"));
			params.add(code);
			params.add(map.get("kline_date"));
			JdbcUtils.update(getConnection(), "update stk_kline set pe_ntile=?,pb_ntile=?,ps_ntile=? where code=? and kline_date=?", params);
		}
	}
	
	public K getKRealTime() throws Exception{
		String sb = (this.getLoc()==1?"sh":"sz")+StringUtils.substring(this.getCode(), this.getCode().length()-6);
		String page = HttpUtils.get("http://hq.sinajs.cn/list="+sb, null, "GBK");
		//System.out.println(page);
		String[] str = page.split(";");
		for(int j=0;j<str.length;j++){
			String s = str[j];
			if(s.length() > 40){
				String code = StringUtils.substringBefore(s, "=");
				code = StringUtils.substring(code, code.length()-8);
				s = StringUtils.substringBetween(s, "\"", "\"");
				String[] ss = s.split(",");
				K k = new K();
				k.setOpen(Double.parseDouble(ss[1]));
				k.setLastClose(Double.parseDouble(ss[2]));
				k.setClose(Double.parseDouble(ss[3]));
				k.setHigh(Double.parseDouble(ss[4]));
				k.setLow(Double.parseDouble(ss[5]));
				k.setVolumn(Double.parseDouble(ss[8]));
				k.setAmount(Double.parseDouble(ss[9]));
				k.setDate(StringUtils.replace(ss[30], "-", ""));
				
				K k2 = this.getK(0);
				k.setBefore(k2);
				k2.setAfter(k);
				return k;
			}
		}
		return null;
	}
	
	public List<K> getKsRealTime(int type) throws Exception {
		String tmp = this.code;
		if(tmp.length() == 6){
			tmp = (this.loc==1?"sse":"szse") + this.code;
		}
		//http://quote.hexun.com/default.html
		//http://webstock.quote.hermes.hexun.com/a/kline?code=szse399006&start=20151120150000&number=-1000&type=3&callback=callback
		//http://webstock.quote.hermes.hexun.com/a/kline?code=szse002026&start=20151204150000&number=-1000&type=2&callback=callback
		String time = StkUtils.sf_ymd12.format(new Date());
		String page = HttpUtils.get("http://webstock.quote.hermes.hexun.com/a/kline?code="+tmp+"&start="+time+"&number=-1000&type="+type+"&callback=callback", null, "utf-8");
		List<List> datas = JsonUtils.getList4Json("["+StringUtils.substringBetween(page, "[[", "]]")+"]]", ArrayList.class );
		Collections.reverse(datas);
		LinkedList<K> ks = new LinkedList<K>();
		for(List data : datas){
			K k = new K();
			k.setDate(String.valueOf(data.get(0)));
			k.setOpen(((Integer)data.get(2))/100.0);
			k.setClose(((Integer)data.get(3))/100.0);
			k.setHigh(((Integer)data.get(4))/100.0);
			k.setLow(((Integer)data.get(5))/100.0);
			k.setVolumn(new Double(String.valueOf(data.get(6))));
			k.setAmount(new Double(String.valueOf(data.get(7))));
			ks.add(k);
		}
		int i = 0;
		for(K k:ks){
			if(i < ks.size()-1){
				k.setBefore(ks.get(i+1));
			}
			if(i > 0){
				k.setAfter(ks.get(i-1));
			}
			i++;
			//System.out.println(k);
		}
		return this.ks = ks;
	}
	
	public List<K> getKsRealTimeOnQuarterHour() throws Exception {
		return this.getKsRealTime(2);
	}
	
	public List<K> getKsRealTimeOnHalfHour() throws Exception {
		return this.getKsRealTime(3);
	}
	
	public List<K> getKsRealTimeOnHour() throws Exception {
		return this.getKsRealTime(4);
	}
	
	public List<K> getKsRealTimeOnDay() throws Exception {
		return this.getKsRealTime(5);
	}
	
	public void setCloseChange() throws Exception {
		Index.KLineWhereClause = Index.KLINE_20140101;
		List params = new ArrayList();
		params.add(this.code);
		//List<StkKline> klines = JdbcUtils.list(this.getConnection(), "select * from "+tab_stkkline+" where code=? "+Index.KLineWhereClause+" and kline_date <= date_format(sysdate(),'%Y%m%d') order by kline_date desc",params, StkKline.class);
		List<StkKline> klines = JdbcUtils.list(this.getConnection(), "select * from "+tab_stkkline+" where code=? "+Index.KLineWhereClause+" and kline_date<=to_char(sysdate,'yyyymmdd') order by kline_date desc",params, StkKline.class);
		for(int i=0;i<klines.size();i++){
			StkKline kline = klines.get(i);
			if(kline.getCloseChange() == null && i != 0 && klines.get(i-1).getCloseChange() != null){
				StkKline lastkline = klines.get(i-1);
				double change = (lastkline.getClose()-lastkline.getLastClose())/lastkline.getLastClose();
				params.clear();
				Double closeChange = lastkline.getCloseChange()/(1+change);
				kline.setCloseChange(closeChange);
				params.add(closeChange);
				params.add(code);
				params.add(kline.getKlineDate());
				JdbcUtils.update(this.getConnection(), "update "+tab_stkkline+" set close_change=? where code=? and kline_date=?", params);
			}
		}
		for(int i=klines.size()-1;i>=0;i--){
			StkKline kline = klines.get(i);
			if(kline.getCloseChange() == null && (i+1)<klines.size() && klines.get(i+1).getCloseChange() != null){
				StkKline lastkline = klines.get(i+1);
				double change = (kline.getClose()-kline.getLastClose())/kline.getLastClose();
				params.clear();
				Double closeChange = lastkline.getCloseChange()*(1+change);
				kline.setCloseChange(closeChange);
				params.add(closeChange);
				params.add(code);
				params.add(kline.getKlineDate());
				JdbcUtils.update(this.getConnection(), "update "+tab_stkkline+" set close_change=? where code=? and kline_date=?", params);
			}
		}
		if(klines.size() > 0 && klines.get(klines.size()-1).getCloseChange() == null){
			params.clear();
			params.add(code);
			params.add(klines.get(klines.size()-1).getKlineDate());
			JdbcUtils.update(this.getConnection(), "update "+tab_stkkline+" set close_change=1 where code=? and kline_date=?", params);
			this.setCloseChange();
		}
	}
	
	public void initCapitalFlow(String today) throws Exception {
		int cate = this.getStk().getCate().intValue();
		if(cate == 4){
			String page = HttpUtils.get("http://q.10jqka.com.cn/stock/gn/" + this.getNameforCate4(), "gb2312");
			Node node = HtmlUtils.getNodeByText(page, null, "资金净流入");
			if(node == null)return;
			List params = new ArrayList();
			params.add(code);
			params.add(today);
			params.add(StkUtils.getNumberFromString(node.toPlainTextString()));
			params.add(code);
			params.add(today);
			//System.out.println(params);
			JdbcUtils.insert(this.getConnection(), "insert into stk_capital_flow select ?,?,?,null,null,null,null,null,null,null,null,null,sysdate from dual where not exists (select 1 from stk_capital_flow where code=? and flow_date=?)", params);
		}else{
			String page = HttpUtils.get("http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=FF&sty=FFHD&code="+this.code+"&mkt="+(this.loc==1?"SH":"SZ"), "gb2312");
			//System.out.println(page);
			String content = StringUtils.substringBetween(page, "[\"", "\"]");
			if(content == null)return;
			String[] datas = content.split("\",\"");
			
			List params = new ArrayList();
			for(String flow : datas){
				String[] dd = flow.split(",");
				if(dd.length >= 12){
					params.clear();
					params.add(code);
					String date = StringUtils.replace(dd[0], "-", "");
					params.add(date);
					double amount = replaceAmount(dd[3]);
					String percent = StringUtils.replace(dd[4], "%", "");
					params.add(amount);
					params.add(percent);
					amount = replaceAmount(dd[5]);
					percent = StringUtils.replace(dd[6], "%", "");
					params.add(amount);
					params.add(percent);
					amount = replaceAmount(dd[7]);
					percent = StringUtils.replace(dd[8], "%", "");
					params.add(amount);
					params.add(percent);
					amount = replaceAmount(dd[9]);
					percent = StringUtils.replace(dd[10], "%", "");
					params.add(amount);
					params.add(percent);
					amount = replaceAmount(dd[11]);
					percent = StringUtils.replace(dd[12], "%", "");
					params.add(amount);
					params.add(percent);
					params.add(code);
					params.add(date);
					//JdbcUtils.insert(this.getConnection(), "insert into stk_capital_flow select ?,?,?,?,?,?,?,?,?,?,?,?,sysdate() from dual where not exists (select 1 from stk_capital_flow where code=? and flow_date=?)", params);
					JdbcUtils.insert(this.getConnection(), "insert into stk_capital_flow select ?,?,?,?,?,?,?,?,?,?,?,?,sysdate from dual where not exists (select 1 from stk_capital_flow where code=? and flow_date=?)", params);
				}
			}
		}
	}
	
	public String getCapitalFlowImageOnMain(String startDate) throws Exception {
		List<StkCapitalFlow> flows = this.getCapitalFlowAfterDate(startDate);
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(StkCapitalFlow flow : flows) {
			dataset.addValue(flow.getMainAmount(), "", flow.getFlowDate());
		}
		return StkUtils.getImgBase64(ImageUtils.getImageStr(ChartUtils.createBarChart(dataset,300, 40)), 300, 30);
	}

	
	public int NumberOfCapitalFlowPostive = 0;
	
	public String getCapitalFlowImageOnMainAndSuper(int days, int width, int high) throws Exception {
		NumberOfCapitalFlowPostive = 0;
		K k = this.getK(days-1);
		if(k == null) return "";
		List<StkCapitalFlow> flows = this.getCapitalFlowAfterDate(k.getDate());
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(StkCapitalFlow flow : flows) {
			if(flow.getLargePercent() != null && flow.getSuperLargePercent() != null){
				double d = (flow.getLargePercent()+flow.getSuperLargePercent())/2;
				if(d > 0)NumberOfCapitalFlowPostive++;
				dataset.addValue(StkUtils.numberFormat(d, 2), "", flow.getFlowDate());
			}else{
				dataset.addValue(flow.getSuperLargePercent(), "", flow.getFlowDate());
			}
		}
		return StkUtils.getImgBase64(ImageUtils.getImageStr(ChartUtils.createBarChart(dataset,width, high+10)), width, high);
	}
	
	public String getCapitalFlowImageOnMainAndSuper(int days) throws Exception {
		NumberOfCapitalFlowPostive = 0;
		K k = this.getK(days-1);
		if(k == null) return "";
		List<StkCapitalFlow> flows = this.getCapitalFlowAfterDate(k.getDate());
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(StkCapitalFlow flow : flows) {
			if(flow.getLargePercent() != null && flow.getSuperLargePercent() != null){
				double d = (flow.getLargePercent()+flow.getSuperLargePercent())/2;
				if(d > 0)NumberOfCapitalFlowPostive++;
				dataset.addValue(StkUtils.numberFormat(d, 2), "", flow.getFlowDate());
			}else{
				dataset.addValue(flow.getSuperLargePercent(), "", flow.getFlowDate());
			}
		}
		return StkUtils.getImgBase64(ImageUtils.getImageStr(ChartUtils.createBarChart(dataset,260, 40)), 260, 24);
	}
	
	private double replaceAmount(String str){
		if(StringUtils.isEmpty(str))return 0.0;
		return Double.parseDouble(str)/10000;
	}
	
	public void initKLinesFromXueQiu(String yyyyMMdd) throws Exception {
		List<String> yyyyMMdds = new ArrayList<String>();
		this.initKLinesFromXueQiu(yyyyMMdds);
	}
	public void initKLinesFromXueQiu(List<String> yyyyMMdds) throws Exception {
		String tmp = (this.loc==1?Index.SH_UPPER:Index.SZ_UPPER)+this.code;
		String page = HttpUtils.get("http://xueqiu.com/stock/forchartk/stocklist.json?period=1day&symbol="+tmp+"&type=before&access_token=QCzVzljgmYpfAIdUbcnkO4&_="+new Date().getTime(), null, "GBK");
		Map<String, Class> m = new HashMap<String, Class>();
        m.put("chartlist", Map.class);
		XueQiuQianFuQuan xq = (XueQiuQianFuQuan) JsonUtils.getObject4Json(page, XueQiuQianFuQuan.class, m);
		for(Map map : xq.getChartlist()){
			String time = (String)map.get("time");
			time = StkUtils.sf_ymd2.format(new Date(time));
			map.put("time", time);
		}
		for(String yyyyMMdd : yyyyMMdds){
			int n = xq.indexOf(yyyyMMdd);
			if(n >= 0){
				Map<String,String> data = xq.get(n);
				//System.out.println(data);
				List params = new ArrayList();
				params.add(code);
				params.add(yyyyMMdd);
				String open = String.valueOf(data.get("open"));
				String close = String.valueOf(data.get("close"));
				String lastClose = String.valueOf(xq.get(n-1).get("close"));
				String high = String.valueOf(data.get("high"));
				String low = String.valueOf(data.get("low"));
				String volumn = String.valueOf(data.get("volume"));
				String hsl = String.valueOf(data.get("turnrate"));
				params.add(open);
				params.add(close);
				params.add(lastClose);
				params.add(high);
				params.add(low);
				params.add(volumn);
				//params.add(data.get(7));
				params.add(hsl);
				params.add(code);
				params.add(yyyyMMdd);
				JdbcUtils.insert(this.getConnection(), "insert into "+tab_stkkline+" (code,kline_date,open,close,last_close,high,low,volumn,amount,close_change,hsl) select ?,?,?,?,?,?,?,?,null,null,? from dual where not exists (select 1 from "+tab_stkkline+" where code=? and kline_date=?)", params);
			}
		}
		setCloseChange();
	}
	
	/**
	 */
	public void updatePEFromXueQiu() throws Exception {
		String page = HttpUtils.get("http://xueqiu.com/S/"+StkUtils.getStkLocation(code)+code, null, "utf-8");
		this.updatePEFromXueQiu(page);
	}
	/**
	 */
	private void updatePEFromXueQiu(String page) throws Exception {
		Node nodeLYR = HtmlUtils.getNodeByText(page, "", "市盈率LYR：");
		Node nodeTTM = HtmlUtils.getNodeByText(page, "", "市盈率TTM：");
		List params = new ArrayList();
		params.add(nodeLYR!=null?nodeLYR.getLastChild().toPlainTextString():"0");
		String peTTM = nodeTTM!=null?nodeTTM.getLastChild().toPlainTextString():"0";
		params.add(peTTM);
		params.add(code);
		params.add(StkUtils.getToday());
		JdbcUtils.update(this.getConnection(), "update "+this.tab_stkkline+" set pe_ttm=?,pe_lyr=? where code=? and kline_date=?", params);
	}
	
	public void initHsl() throws Exception {
		KLineWhereClause = " and hsl is null ";
		this.initHsl(1000);
	}
	
	public void initHsl(int n) throws Exception {
		List<K> ks = this.getKs();
		for(int i=0; i<ks.size(); i=i+50){
			String ed = ks.get(i).getDate();
			String sd = ks.get(i+50>=ks.size()?ks.size()-1:i+50).getDate();
			ed = StkUtils.sf_ymd.format(StkUtils.sf_ymd2.parse(ed));
			sd = StkUtils.sf_ymd.format(StkUtils.sf_ymd2.parse(sd));
			String page = HttpUtils.get("http://q.stock.sohu.com/app2/history.up?method=history&code=cn_"+this.code+"&sd="+sd+"&ed="+ed+"&t=d&res=js&r="+Math.random()+"&"+Math.random(), null, "utf-8");
			page = StringUtils.replace(page, "\n", "");
			List<List> list = JsonUtils.getList4Json("[["+StringUtils.substringBetween(page, "[[", "]]")+"]]", ArrayList.class);
			for(List hsl:list){
				if(hsl.size() > 9){
					List params = new ArrayList();
					params.add(StringUtils.replace(StringUtils.replace(String.valueOf(hsl.get(9)), "%", ""),"-", ""));
					params.add(code);
					params.add(StkUtils.sf_ymd2.format(StkUtils.sf_ymd.parse(String.valueOf(hsl.get(0)))));
					JdbcUtils.update(this.getConnection(), "update "+tab_stkkline+" set hsl=? where code=? and kline_date=?", params);
				}
			}
			if(--n<=0)
				break;
		}
	}
	
	
	/******************************** 基本信息 **********************************************/
	//private final static String SQL_SELECT_STK_BASIC_INFO = "select name,code,status,total_capital,earning_expect,market,year_end,cate,address from stk where code=?";
	public Stk getStk() {
		/*if(stk != null)return stk;
		List params = new ArrayList();
		params.add(this.code);
		this.stk = JdbcUtils.load(this.getConnection(), SQL_SELECT_STK_BASIC_INFO, params, Stk.class);
		return stk;*/
		return this.getStock();
	}
	
	public Stk getStock(){
		if(this.stock != null){
			return this.stock;
		}
		this.stock = (Stk)CacheUtils.getByCode(this.getCode(), CacheUtils.KEY_STK_STOCK);
		if(this.stock != null){
			return this.stock;
		}
		//if(stock != null)return stock;
		List params = new ArrayList();
		params.add(this.code);
		this.stock = JdbcUtils.load(this.getConnection(), SQL_SELECT_STOCK_BY_CODE, params, Stk.class);
		CacheUtils.putByCode(this.getCode(), CacheUtils.KEY_STK_STOCK, this.stock);
		return this.stock;
	}
	
	private final static String SQL_SELECT_INDUSTRY_TYPE_BY_CODE = "select t.* from stk_industry s,stk_industry_type t where s.industry=t.id and s.code=? order by s.industry,t.source";
	private final static String SQL_SELECT_STK_BY_INDUSTRY = "select * from stk_industry where industry=?";
	
	public List<Industry> getIndustry(){
		//if(industries.size() > 0) return this.industries;
		List<Industry> industries = null;
		if((industries = (List<Industry>)CacheUtils.getByCode(this.getCode(), CacheUtils.KEY_STK_INDUSTRY)) != null){
			return industries;
		}
		
		industries = new ArrayList<Industry>();
		List params = new ArrayList();
		params.add(code);
		List<StkIndustryType> types = JdbcUtils.list(this.getConnection(), SQL_SELECT_INDUSTRY_TYPE_BY_CODE, params, StkIndustryType.class);
		for(StkIndustryType type : types){
			Industry ind = new Industry(type);
			/*params.clear();
			params.add(type.getId());
			List<StkIndustry> list = JdbcUtils.list(this.getConnection(), SQL_SELECT_STK_BY_INDUSTRY, params, StkIndustry.class);
			for(StkIndustry tmp : list){
				ind.addStkCode(tmp.getCode());
			}*/
			industries.add(ind);
		}
		CacheUtils.putByCode(this.getCode(), CacheUtils.KEY_STK_INDUSTRY, industries);
		return industries;
	}
	
	public Industry getIndustryDefault(){
		Industry ind = this.getIndustryBySource(Industry.INDUSTRY_CNINDEX);
		if(ind == null){
			ind = this.getIndustryBySource(Industry.INDUSTRY_WIND);
		}
		return ind;
	}
	
	public Industry getIndustryBySource(String source){
		List<Industry> industries = this.getIndustry();
		for(Industry ind : industries){
			if(source.equals(ind.getType().getSource())){
				return ind;
			}
		}
		return null;
	}
	
	public Industry getIndustryByMyFn(){
		Industry ind = this.getIndustryBySource(Industry.INDUSTRY_MY_FNTYPE);
		if(ind != null){
			return ind;
		}
		return null;
	}
	
	public List<Industry> getIndustriesBySource(String source){
		List<Industry> result = new ArrayList<Industry>();
		List<Industry> industries = this.getIndustry();
		for(Industry ind : industries){
			if(source.equals(ind.getType().getSource())){
				result.add(ind);
			}
		}
		return result;
	}
	
	/**
	 * can not be used by web
	 */
	@Deprecated
	public List<String> getIndustryName(){
		return this.getIndustryName(null);
	}
	
	@Deprecated
	private List<String> getIndustryName(String source){
		if(source == null){
			if(industryNames.get(EMPTY) != null){
				return industryNames.get(EMPTY);
			}
		}else{
			if(industryNames.get(source) != null){
				return industryNames.get(source);
			}
		}
		List<String> tmpIndustry = new ArrayList<String>();
		List params = new ArrayList();
		params.add(code);
		if(source == null){
			tmpIndustry = JdbcUtils.list(this.getConnection(), "select t.name from stk_industry s,stk_industry_type t where s.industry=t.id and s.code=? order by s.industry,t.source", params, String.class);
			industryNames.put(EMPTY, tmpIndustry);
		}else{
			params.add(source);
			tmpIndustry = JdbcUtils.list(this.getConnection(), "select t.name from stk_industry s,stk_industry_type t where s.industry=t.id and s.code=? and t.source=? order by s.industry,t.source", params, String.class);
			industryNames.put(source, tmpIndustry);
		}
		return tmpIndustry;
	}
	
	private List<StkRestricted> Restricted = null;
	
	public List<StkRestricted> getRestricted(){
		if(Restricted != null){
			return Restricted;
		}else{
			List params = new ArrayList();
			params.add(code);
			Restricted = JdbcUtils.list(this.getConnection(), "select * from stk_restricted where code=? order by listing_date desc", params, StkRestricted.class);
			return Restricted;
		}
	}
	
	public StkRestricted isRestrictedDateBetween(Date start, Date end) throws Exception {
		if(end == null){
			end = new Date();
		}
		List<StkRestricted> Restricted = this.getRestricted();
		StkRestricted ret = null;
		for(StkRestricted sr : Restricted){
			Date listingDate = StkUtils.sf_ymd.parse(sr.getListingDate());
			if(listingDate.after(start) && listingDate.before(end)){
				if(ret == null){
					ret = sr;
				}else{
					ret.setBanAmount(ret.getBanAmount() + sr.getBanAmount());
					ret.setBanMarketValue(ret.getBanMarketValue() + sr.getBanMarketValue());
				}
			}
			if(listingDate.before(start)){
				break;
			}
		}
		return ret;
	}
	
	public List<StkImportInfo> getImportInfo(int type) {
		List params = new ArrayList();
		params.add(code);
		params.add(type);
		return JdbcUtils.list(this.getConnection(), "select * from Stk_Import_Info where code=? and type=? order by insert_time desc", params, StkImportInfo.class);
	}
	
	public List<StkImportInfo> getImportInfoAfterDate(int type, Date date) {
		List params = new ArrayList();
		params.add(code);
		params.add(type);
		params.add(date);
		return JdbcUtils.list(this.getConnection(), "select * from Stk_Import_Info where code=? and type=? and info_create_time>? order by insert_time desc", params, StkImportInfo.class);
	}
	
	
	private final static String SQL_GETKS_1 = " where code=? ";
	//private final static String SQL_GETKS_2 = " and kline_date<=date_format(sysdate(),'%Y%m%d') order by kline_date desc";
	private final static String SQL_GETKS_2 = " and kline_date<=to_char(sysdate,'yyyymmdd') order by kline_date desc";
	
	public static String KLINE_BEFORE_DATE = null;
	
	/**
	 * 前复权
	 */
	public LinkedList<K> getKs() throws Exception {
		if(code.startsWith("88")){//同花顺概念板块k线数据
			return getKs(false);
		}
		return getKs(true);
	}
	
	public K addK(K k) throws Exception {
		K k0 = this.getK(0);
		this.getKs().add(0, k);
		k.setBefore(k0);
		k0.setAfter(k);
		return k;
	}
	
	public void removeK() throws Exception {
		this.getKs().remove(0);
		this.getK(0).setAfter(null);
	}
	
	/**
	 * kline_date>='20100101'
	 * @param flag true:前复权 false:不复权
	 * @param loc sh:1, sz:2
	 * @throws Exception
	 */
	public LinkedList<K> getKs(boolean flag) throws Exception {
		if(this.ks != null && this.ks.size() > 0){
			return this.ks;
		}
		this.ks = (LinkedList<K>)CacheUtils.getByCode(this.getCode(), CacheUtils.KEY_STK_K);
		if(this.ks != null){
			return this.ks;
		}
		/*if(this.ks.size() > 0){
			return this.ks;
		}*/
		ks = new LinkedList<K>();
		List params = new ArrayList();
		params.add(this.code);
		List<StkKline> kLines = JdbcUtils.list(this.getConnection(), JdbcUtils.SELECT_STAR_FROM + tab_stkkline + SQL_GETKS_1 + (KLINE_BEFORE_DATE == null?"":"and kline_date<='"+KLINE_BEFORE_DATE+"'") + KLineWhereClause + SQL_GETKS_2,params, StkKline.class);
		int i = 0;
		K lastKLine = null;
		for(StkKline kLine:kLines){
			if(flag && kLine.getCloseChange() == null && market == 1)continue;
			K k = new K(i,market,kLine,lastKLine,flag);
			lastKLine = k;
			ks.add(k);
			i ++;
		}
		i = 0;
		for(K k:ks){
			if(i < ks.size()-1){
				k.setBefore(ks.get(i+1));
			}
			if(i > 0){
				k.setAfter(ks.get(i-1));
			}
			i++;
		}
		CacheUtils.putByCode(this.getCode(), CacheUtils.KEY_STK_K, ks);
		return this.ks;
	}
	
	
	public LinkedList<K> getKsWeekly() throws Exception {
		return getKsWeekly(false);
	}
	
	/**
	 * @param flag true: 把kws直接赋值给 ks
	 */
	public LinkedList<K> getKsWeekly(boolean flag) throws Exception {
		if(kws != null)return kws;
		LinkedList<K> ks = getKs();
		Date a = null;
		K kw = null;
		kws = new LinkedList<K>();
		for(K k : ks){
			Date kd = StkUtils.sf_ymd2.parse(k.getDate());
			Date monday = ((Calendar)DateUtils.iterator(kd, DateUtils.RANGE_WEEK_MONDAY).next()).getTime();
			if(a == null || monday.compareTo(a) != 0){
				if(kw != null){
					kws.add(kw);
				}
				kw = new K();
				kw.setDate(k.getDate());
				kw.setClose(k.getClose());
				kw.setHigh(k.getHigh());
				kw.setLow(k.getLow());
				kw.setVolumn(k.getVolumn());
			}else{
				kw.setOpen(k.getOpen());
				kw.setHigh(Math.max(k.getHigh(), kw.getHigh()));
				kw.setLow(Math.min(k.getLow(), kw.getLow()));
				kw.setVolumn(kw.getVolumn()+k.getVolumn());
			}
			a = monday;
		}
		int i = 0;
		for(K k:kws){
			if(i < kws.size()-1){
				k.setBefore(kws.get(i+1));
			}
			if(i > 0){
				k.setAfter(kws.get(i-1));
			}
			i++;
		}
		if(flag){
			this.ks = kws;
		}
		return kws;
	}
	
	public LinkedList<K> getKsMonthly(boolean flag) throws Exception {
		if(kms != null)return kms;
		LinkedList<K> ks = getKs();
		int a = -1;
		K kw = null;
		kms = new LinkedList<K>();
		for(K k : ks){
			Date kd = StkUtils.sf_ymd2.parse(k.getDate());
			int month = kd.getMonth();
			if(a == -1 || month != a){
				if(kw != null){
					kms.add(kw);
				}
				kw = new K();
				kw.setDate(k.getDate());
				kw.setClose(k.getClose());
				kw.setHigh(k.getHigh());
				kw.setLow(k.getLow());
				kw.setVolumn(k.getVolumn());
			}else{
				kw.setOpen(k.getOpen());
				kw.setHigh(Math.max(k.getHigh(), kw.getHigh()));
				kw.setLow(Math.min(k.getLow(), kw.getLow()));
				kw.setVolumn(kw.getVolumn()+k.getVolumn());
			}
			a = month;
		}
		int i = 0;
		for(K k:kms){
			if(i < kms.size()-1){
				k.setBefore(kms.get(i+1));
			}
			if(i > 0){
				k.setAfter(kms.get(i-1));
			}
			i++;
		}
		if(flag){
			this.ks = kms;
		}
		return kms;
	}
	
	public int indexOf(String date) throws Exception {
		getKs();
		return this.indexOfBefore(date);
	}
	public int indexOfAfter(String date) throws Exception {
		getKs();
		int pos = 0;
		for(K k:this.getKs()){
			if(k.getDate().compareTo(date) == 0 ||
					(k.before(1) != null && k.before(1).getDate().compareTo(date) < 0)){
				break;
			}
			pos ++;
		}
		return pos==this.getKs().size()?(pos-1):pos;
	}
	public int indexOfBefore(String date) throws Exception {
		getKs();
		int pos = 0;
		for(K k:this.getKs()){
			if(k.getDate().compareTo(date) == 0 || k.getDate().compareTo(date) < 0){
				break;
			}
			pos ++;
		}
		return pos==this.getKs().size()?(pos-1):pos;
	}
	
	/**
	 * @param date  format:'yyyyMMdd'
	 * @param days  
	 * @return
	 * @throws Exception
	 */
	public K getK(String date, int days) throws Exception {
		this.getKs();
		if(this.getKs().size() == 0)return null;
		if(days < 0){
			return this.getKs().get(this.getKs().size()-1);
		}
		return this.getKs().get(this.indexOf(date)).before(days);
	}
	public K getK(String date) throws Exception {
		return getK(date, 0);
	}
	
	public K getK() throws Exception {
		if(this.ks != null && this.ks.size() > 0){
			return this.ks.get(0);
		}else{
			List params = new ArrayList();
			params.add(this.code);
			StkKline kLine = JdbcUtils.load(conn, "select * from (select * from " + tab_stkkline + " where code=? order by kline_date desc) where rownum<=1", params, StkKline.class);
			K k = new K(0,market,kLine,null,true);
			return k;
		}
		//return getK(0);
	}
	public K getK(int n) throws Exception {
		if(getKs().size() < n+1 || n < 0){
			return null;
		}
		return getKs().get(n);
	}
	
	public K getKFirstOfDate() throws Exception {
		if(getKs() == null || getKs().size() == 0){
			return null;
		}
		return getKs().get(getKs().size()-1);
	}
	
	public double getKValue(String date,int days, int type) throws Exception {
		K k = getK(date, days);
		if(k == null){
			return 0.0;
		}
		return k.getValue(type);
	}
	
	public double getKValue(String date, int type) throws Exception {
		return this.getKValue(date, 0, type);
	}
	
	public double getKValue(String date, int type, int type2, int days2) throws Exception {
		return this.getKValue(date, 0, type, type2, days2);
	}
	
	/**
	 * @param date
	 * @param days 取date前多少days的K线
	 * @param type open/close/high/low/amount/volumn/hsl
	 * @param type2 ma/sum
	 * @param days2 type2多少days2的ma/sum
	 * @return
	 * @throws Exception
	 */
	public double getKValue(String date,int days, int type, int type2, int days2) throws Exception {
		K k = getK(date, days);
		if(k == null){
			return 0.0;
		}
		return k.getValue(type, type2, days2);
	}
	
	public double getKValueByHHV(String startDate, String endDate) throws Exception {
		int start = this.indexOfAfter(startDate);
		int end = this.indexOfBefore(endDate);
		double value = 0.0;
		for(int i=end;i<=start;i++){
			K k = this.getKs().get(i);
			double tmp = k.getHigh();
			if(value < tmp || value == 0.0){
				value = tmp;
			}
		}
		return value;
	}
	public double getKValueByHCV(String startDate, String endDate) throws Exception {
		int start = this.indexOfAfter(startDate);
		int end = this.indexOfBefore(endDate);
		double value = 0.0;
		for(int i=end;i<=start;i++){
			K k = this.getKs().get(i);
			double tmp = k.getClose();
			if(value < tmp || value == 0.0){
				value = tmp;
			}
		}
		return value;
	}
	public double getKValueByHHV(String rangeDate) throws Exception {
		String startDate = StringUtils.rightPad(rangeDate, 8, StkConstant.NUMBER_ZERO);
		String endDate = StringUtils.rightPad(rangeDate, 8, StkConstant.NUMBER_NINE);
		return this.getKValueByHHV(startDate, endDate);
	}
	public double getKValueByLLV(String startDate, String endDate) throws Exception {
		int start = this.indexOfAfter(startDate);
		int end = this.indexOfBefore(endDate);
		double value = 0.0;
		for(int i=end;i<start;i++){
			K k = this.getKs().get(i);
			double tmp = k.getLow();
			if(value > tmp || value == 0.0){
				value = tmp;
			}
		}
		return value;
	}
	public double getKValueByLCV(String startDate, String endDate) throws Exception {
		int start = this.indexOfAfter(startDate);
		int end = this.indexOfBefore(endDate);
		double value = 0.0;
		for(int i=end;i<start;i++){
			K k = this.getKs().get(i);
			double tmp = k.getClose();
			if(value > tmp || value == 0.0){
				value = tmp;
			}
		}
		return value;
	}
	
	public double getKValueByLLV(String rangeDate) throws Exception {
		String startDate = StringUtils.rightPad(rangeDate, 8, StkConstant.NUMBER_ZERO);
		String endDate = StringUtils.rightPad(rangeDate, 8, StkConstant.NUMBER_NINE);
		return this.getKValueByLLV(startDate, endDate);
	}
	
	/**
	 * high的高点
	 */
	public K getKByHHV(String startDate, String endDate) throws Exception {
		int start = this.indexOfAfter(startDate);
		int end = this.indexOfBefore(endDate);
		double value = 0.0;
		K ret = null;
		for(int i=end;i<=start;i++){
			K k = this.getKs().get(i);
			double tmp = k.getHigh();
			if(value < tmp || value == 0.0){
				value = tmp;
				ret = k;
			}
		}
		return ret;
	}
	/**
	 * close的高点
	 */
	public K getKByHCV(String startDate, String endDate) throws Exception {
		int start = this.indexOfAfter(startDate);
		int end = this.indexOfBefore(endDate);
		double value = 0.0;
		K ret = null;
		for(int i=end;i<=start;i++){
			K k = this.getKs().get(i);
			double tmp = k.getClose();
			if(value < tmp || value == 0.0){
				value = tmp;
				ret = k;
			}
		}
		return ret;
	}
	/**
	 * low的低点
	 */
	public K getKByLLV(String startDate, String endDate) throws Exception {
		int start = this.indexOfAfter(startDate);
		int end = this.indexOfBefore(endDate);
		double value = 0.0;
		K ret = null;
		for(int i=end;i<start;i++){
			K k = this.getKs().get(i);
			double tmp = k.getLow();
			if(value > tmp || value == 0.0){
				value = tmp;
				ret = k;
			}
		}
		return ret;
	}
	/**
	 * close的低点
	 */
	public K getKByLCV(String startDate, String endDate) throws Exception {
		int start = this.indexOfAfter(startDate);
		int end = this.indexOfBefore(endDate);
		double value = 0.0;
		K ret = null;
		for(int i=end;i<start;i++){
			K k = this.getKs().get(i);
			double tmp = k.getClose();
			if(value > tmp || value == 0.0){
				value = tmp;
				ret = k;
			}
		}
		return ret;
	}
	
	/**
	 * volumn的高点
	 */
	public K getKByHVV(String startDate, String endDate) throws Exception {
		int start = this.indexOfAfter(startDate);
		int end = this.indexOfBefore(endDate);
		double value = 0.0;
		K ret = null;
		for(int i=end;i<=start;i++){
			K k = this.getKs().get(i);
			double tmp = k.getVolumn();
			if(value < tmp || value == 0.0){
				value = tmp;
				ret = k;
			}
		}
		return ret;
	}
	
	public K getKByHVV(String endDate, int days) throws Exception {
		K startK = this.getK(endDate, days);
		if(startK == null)return null;
		return this.getKByHVV(startK.getDate(), endDate);
	}
	
	public int getKUpNumber(String startDate, String endDate){
		return 0;
	}
	
	public boolean isMACross(String endDate, int days, int maDays1, int maDays2, int type) throws Exception {
		int flag = 0;
		for(int i=0;i<days;i++){
			K k = this.getK(endDate, i);
			double ma1 = k.getMA(type, maDays1);
			double ma2 = k.getMA(type, maDays2);
			if(flag == 0){
				if(ma1 >= ma2)flag = 1;
				else flag = 2;
			}
			int tmp = (ma1>=ma2?1:2);
			if(tmp != flag){
				return true;
			}
		}
		return false;
	}
	
	private static final String SQL_SELECT_SKT_HOLDER_BY_CODE = "select * from stk_holder where code=? order by fn_date desc";
	public List<StkHolder> getHolder(){
		if(this.holders.size() > 0) return this.holders;
		List params = new ArrayList();
		params.add(this.getCode());
		this.holders = JdbcUtils.list(this.getConnection(), SQL_SELECT_SKT_HOLDER_BY_CODE, params, StkHolder.class);
		return this.holders;
	}
	
	public StkHolder getHolder(String yyyyMMdd){
		List<StkHolder> holders = this.getHolder();
		for(StkHolder holder : holders){
			if(holder.getFnDate().equals(yyyyMMdd)){
				return holder;
			}
		}
		return null;
	}
	
	private SimpleDateFormat sf_ymd2 = new SimpleDateFormat("yyyyMMdd"); // 多线程下SimpleDateFormat不安全，所以这里给每个index new自己的instance
	private List<StkFnDataCust> getFnData() throws Exception{
		if(fnData != null){
			return fnData;
		}
		/*this.fnData = (List<StkFnDataCust>)CacheUtils.getByCode(this.getCode(), CacheUtils.KEY_STK_FN);
		if(fnData != null){
			return fnData;
		}*/
		
		//if(this.fnData.size() > 0) return fnData;
		List params = new ArrayList();
		params.add(this.getCode());
		this.fnData = JdbcUtils.list(this.getConnection(),"select code,type,fn_date,fn_value from "+this.tab_stkfndata+" where code=? "+ FNDateWhereClause +" and fn_value is not null order by fn_date desc",params,StkFnDataCust.class);
		if(this.fnData != null && this.fnData.size() > 0){
			int MMEnd = 12;//A stock default value
			/*if(this.market == 2){
				String yearEnd = this.getStk().getYearEnd();
				if(yearEnd == null)yearEnd = StkUtils.MMDD_Q4;
				MMEnd = StkUtils.get(StkUtils.sf_MMdd.parse(yearEnd), Calendar.MONTH)+1;
			}*/
			for(StkFnDataCust data : this.fnData){
				data.setFnData(this.fnData);
				data.setStkFnType(Fn.getFnTypes().get(data.getType().toString()));
				int mm = StkUtils.get(sf_ymd2.parse(data.getFnDate()), Calendar.MONTH)+1;
				data.setNumber(StkUtils.getQuarterNumber(MMEnd, mm));
			}
		}
		//CacheUtils.putByCode(this.getCode(), CacheUtils.KEY_STK_FN, fnData);
		return this.fnData;
	}
	
	
	public Map<String,StkFnDataCust> getFnDataByDate(String yyyyMMdd) throws Exception{
		this.getFnData();
		Map<String,StkFnDataCust> result = new HashMap<String,StkFnDataCust>();
		for(StkFnDataCust data:this.fnData){
			if(data.getFnDate().indexOf(yyyyMMdd) >= 0){
				result.put(data.getType().toString(), data);
			}
		}
		return result;
	}
	
	public Map<String,StkFnDataCust> getFnDataByType(String type) throws Exception{
		this.getFnData();
		Map<String,StkFnDataCust> result = new HashMap<String,StkFnDataCust>();
		for(StkFnDataCust data:this.fnData){
			if(type.equals(data.getType().toString())){
				result.put(data.getFnDate(), data);
			}
		}
		return result;
	}
	
	public StkFnDataCust getFnDataLastestByType(String type) throws Exception{
		this.getFnData();
		for(StkFnDataCust data:this.fnData){
			if(type.equals(data.getType().toString())){
				return data;
			}
		}
		return null;
	}
	
	public StkFnDataCust getFnData(String type,String yyyyMMdd) throws Exception{
		this.getFnData();
		for(StkFnDataCust data:this.fnData){
			if(type.equals(data.getType().toString()) && yyyyMMdd.equals(data.getFnDate())){
				return data;
			}
		}
		return null;
	}
	
	public List<Map> getFnTableForUS(){
		String sql = "select * from stk_us_query_view where p_view_param.set_param('code',?)=0";
		List params = new ArrayList();
		params.add(this.getCode());
		return JdbcUtils.list2Map(this.getConnection(), sql, params);
	}
	
	public Table getFnTable(List<StkFnType> fnTypes) throws Exception {
		List<String> topHeads = new ArrayList<String>();
		for(StkFnType fnType : fnTypes){
			topHeads.add(fnType.getType().toString());
		}
		
		List params = new ArrayList();
		params.add(this.getCode());
		List<String> leftHeads = JdbcUtils.list(this.getConnection(),"select distinct fn_date from "+this.tab_stkfndata+" where code=? "+ FNDateWhereClause +" order by fn_date desc",params,String.class);
		List<TableCell> list = new ArrayList<TableCell>();
		for(StkFnDataCust fn : this.getFnData()){
			list.add(fn);
		}
		return new Table(topHeads, leftHeads, list);
	}

	private List<List<StkFnDataCust>> fnTable = new ArrayList<List<StkFnDataCust>>();
	/**
	 * @param n表示显示n年的季度数据，其他只显示年度数据
	 */
	public List<List<StkFnDataCust>> getFnTable(List<StkFnType> fnTypes,int n) throws Exception{
		if(fnTable.size() > 0)return this.fnTable;
		this.getFnData();
		if(this.getFnData().size() <= 0)return this.fnTable;
		//List<List<StkFnDataCust>> result = new ArrayList<List<StkFnDataCust>>();
		String MMEnd = StkConstant.NUMBER_TWELVE;//A stock default value
		if(this.market == 2 && this.getStk().getYearEnd() != null){
			MMEnd = String.valueOf(StkUtils.get(StkUtils.sf_MMdd.parse(this.getStk().getYearEnd()), Calendar.MONTH)+1);
			MMEnd = StringUtils.leftPad(MMEnd, 2, StkConstant.NUMBER_ZERO);
		}
		int year = this.getFnMaxYear();
		if(year == 0) return null;
		boolean flag = false;
		while(true){
			List<String> quarters = StkUtils.getQuarters(year+MMEnd);
			int i = 0;
			for(String yearMM : quarters){
				if(n <= 0 && i > 0)break;
				Map<String,StkFnDataCust> fnTypeDatas = this.getFnDataByDate(yearMM);
				List<StkFnDataCust> fnDatas = new ArrayList<StkFnDataCust>();
				boolean isAllNull = true;
				for(StkFnType fnType : fnTypes){
					String type = fnType.getType().toString();
					StkFnDataCust tmp = fnTypeDatas.get(type);
					if(tmp != null){
						isAllNull = false;
					}else{
						if(fnType.getReCalc() != null && fnTypeDatas.get(fnType.getReCalc()) != null){
							tmp = new StkFnDataCust();
							tmp.setCode(this.code);
							tmp.setType(fnType.getType());
							tmp.setFnDate(fnTypeDatas.get(fnType.getReCalc()).getFnDate());
							tmp.setStkFnType(fnType);
							tmp.setFnData(this.getFnData());
						}
					}
					fnDatas.add(tmp);
				}
				if(n <=0 && isAllNull){
					flag = true;
					break;
				}
				if(!isAllNull)
					fnTable.add(fnDatas);
				i ++;
			}
			if(flag)break;
			n --;
			year --;
		}
		//设置fndata前面一个季度的fndata
		for(int i=0;i<fnTable.size()-1;i++){
			List<StkFnDataCust> list = fnTable.get(i);
			for(int j=0;j<list.size();j++){
				StkFnDataCust fn = list.get(j);
				if(fn != null && fnTable.get(i+1) != null){
					fn.setBefore(fnTable.get(i+1).get(j));
				}
			}
		}
		return fnTable;
	}
	
	private int fnMaxYear = 0;//最近财务数据的年份
	public int getFnMaxYear() throws Exception{
		if(fnMaxYear != 0){
			return fnMaxYear;
		}
		List<StkFnDataCust> list = this.getFnData();
		String maxYear = null;
		for(StkFnData fnData : list){
			if(maxYear == null){
				maxYear = fnData.getFnDate();
			}else{
				if(fnData.getFnDate().compareTo(maxYear) > 0){
					maxYear = fnData.getFnDate();
				}
			}
		}
		if(maxYear != null){
			fnMaxYear = StkUtils.get(StkUtils.sf_ymd2.parse(maxYear), Calendar.YEAR);
		}
		return fnMaxYear;
	}
	
	public double getCloseChange(String startDate, String endDate) throws Exception {
		String key = startDate+'_'+endDate;
		if(closeChange.get(key) != null){
			return closeChange.get(key);
		}
		int start = this.indexOfAfter(startDate);
		int end = this.indexOfBefore(endDate);
		K startK = this.getK(start);
		if(startK == null){
			closeChange.put(key, new Double(0));
			return 0;
		}
		K endK = this.getK(end);
		double change = StkUtils.numberFormat((endK.getCloseChange() - startK.getCloseChange())/startK.getCloseChange(),2);
		closeChange.put(key, change);
		return change;
	}
	/**
	 * @param rangeDate  yyyy or yyyyMM
	 * @return
	 * @throws Exception
	 */
	public double getCloseChange(String rangeDate) throws Exception {
		String startDate = StringUtils.rightPad(rangeDate, 8, StkConstant.NUMBER_ZERO);
		String endDate = StringUtils.rightPad(rangeDate, 8, StkConstant.NUMBER_NINE);
		return this.getCloseChange(startDate, endDate);
	}
	
	private Map<String, Double> closeChange = new HashMap<String, Double>();
	
	public double getCloseChange(String endDate, int days) throws Exception {
		String key = endDate+'_'+days;
		if(closeChange.get(key) != null){
			return closeChange.get(key);
		}
		
		K k = this.getK(endDate, days);
		if(k == null){
			closeChange.put(key, new Double(0));
			return 0;
		}
		double change = this.getCloseChange(k.getDate(), endDate);
		closeChange.put(key, change);
		return change;
	}
	
	public List<K> findKByCloseChange(String date, int days, double change, double change2) throws Exception {
		K k = this.getK(date);
		List<K> result = new ArrayList<K>();
		int n = 0;
		while(k.getBefore() != null){
			if(n++ > days){
				break;
			}
			k = k.getBefore();
			double closeChange = (k.getClose()-k.getBefore().getClose())/k.getBefore().getClose();
			if(k.getBefore() != null && 
				closeChange >= change && closeChange <= change2){
				result.add(k);
			}
		}
		return result;
	}
	
	public double getMA(String date, int days, int type) throws Exception {
		double total = 0.0;
		int tmp = days;
		while(--days >= 0){
			total += this.getKValue(date, days, type);
		}
		return StkUtils.numberFormat(total/tmp,2);
	}
	
	/********************************** 总市值  (亿)****************************************/
	private double marketValue = 0.0;
	public double getTotalMarketValue() throws Exception{
		if(marketValue > 0)return marketValue;
		if(this.getK() == null || this.getStk().getTotalCapital() == null)return 0;
		return marketValue = this.getK().getClose() * this.getStk().getTotalCapital()/10000;
	}
	public double getMarketValue() throws Exception{
		return this.getTotalMarketValue();
	}
	
	/********************************** ps 市销率  ****************************************/
	
	public double getPSTTM() throws Exception{
		StkFnDataCust data = this.getFnDataLastestByType(FN_ZYSR);
		if(data != null && data.getFnDate() != null){
			Double d = data.getFnDataByTTM();
			if(d != null)
				return this.getTotalMarketValue()/d;
		}
		return 0;
	}
	
	public double getPS() throws Exception{
		K k = this.getK();
		if(k != null && k.getKline() != null){
			return k.getKline().getPsTtm();
		}
		return 0;
	}
	
	public String getPSAsString() throws Exception{
		Double ps = null;
		K k = this.getK();
		if(k != null && k.getKline() != null){
			ps = k.getKline().getPsTtm();
		}
		if(ps == null){
			ps = StkUtils.numberFormat(this.getPSTTM(),2);
		}
		if(ps != null){
			return ps.toString();
		}
		return StkConstant.MARK_DOUBLE_HYPHEN;
	}
	
	
	/********************************** pb 市净率  ****************************************/
	
	public double getPB() throws Exception{
		StkFnDataCust data = this.getFnDataLastestByType(FN_GDQY);
		if(data != null && data.getFnDate() != null && this.getK() != null && data.getFnValue() > 0){
			return this.getTotalMarketValue()/data.getFnValue();
		}
		return 0;
	}
	
	public Double getPBTTM(String date) throws Exception{
		if(date == null){
			date = StkUtils.getToday();
		}
		List<K> list = this.getKs();
		for(K k : list){
			if(k.getDate().compareTo(date) <= 0){
				return k.getKline().getPbTtm();
			}
		}
		return null;
	}
	
	/********************************** pr 市研率  ****************************************/
	
	public double getPR() throws Exception{
		StkFnDataCust data = this.getFnDataLastestByType(FN_YFFY);
		if(data != null && data.getFnDate() != null){
			Double d = data.getFnDataByTTM(true);
			if(d != null)
				return this.getTotalMarketValue()/d;
		}
		return 0;
	}
	
	public double getROE() throws Exception {
		StkFnDataCust fnData = this.getFnDataLastestByType(FN_ROE);
		if(fnData == null || fnData.getFnValue() == null)return 0;
		return fnData.getFnValue();
	}
	
	public double getPE() throws Exception {
		int year = StkUtils.YEAR - 1;
		String yyyyMMdd = year+StkUtils.MMDD_Q4;
		Map<String,StkFnDataCust> fnData = this.getFnDataByDate(yyyyMMdd);
		while(fnData.get(FN_JLR) == null){
			yyyyMMdd = (year --)+StkUtils.MMDD_Q4;
			//System.out.println("yyyyMMdd="+yyyyMMdd);
			fnData = this.getFnDataByDate(yyyyMMdd);
			if(year <= 2005)return 0.0;
		}
		return getTotalMarketValue()/fnData.get(FN_JLR).getFnValue();
	}
	
	
	/********************************** pe 市盈率 ****************************************/
	public Double getPETTM(String date) throws Exception{
		if(date == null){
			date = StkUtils.getToday();
		}
		List<K> list = this.getKs();
		for(K k : list){
			if(k.getDate().compareTo(date) <= 0){
				return k.getKline().getPeTtm();
			}
		}
		return null;
	}
	
	public double getPETTM() throws Exception{
		Double pe = this.getPETTM(null);
		return pe!=null?pe.doubleValue():0;
	}
	
	public PE getPETTMByCalculation() throws Exception {
		return this.getPETTMByCalculation(StkUtils.getToday());
	}
	
	public double getPEG() throws Exception{
		double pe = this.getPETTM();
		if(pe <=0)return 0;
		double jlrzz = getCAGRByEarningsForecast();
		if(jlrzz <=0)return 0;
		return pe/jlrzz;
	}
	
	/**
	 * PE, 统计上4个季度的jlr
	 */
	public PE getPETTMByCalculation(String yyyyMMdd) throws Exception {
		if(this.pe != null){
			return pe;
		}
		this.pe = new PE();
		String lastyyyyMMdd = yyyyMMdd;
		double jlr = 0.0;
		for(int i=1; i<=4; i++){
			lastyyyyMMdd = StkUtils.getQuarter(lastyyyyMMdd, 1);
			if((jlr = this.getNetProfitByOneQuarter(lastyyyyMMdd)) > 0){
				break;
			}
		}
		pe.setEndQuarter(lastyyyyMMdd);
		//System.out.println("lastyyyyMMdd="+lastyyyyMMdd+",jlr="+jlr);
		for(int i=1; i<=3; i++){
			lastyyyyMMdd = StkUtils.getQuarter(lastyyyyMMdd, 1);
			double one = this.getNetProfitByOneQuarter(lastyyyyMMdd);
			if(one == 0){
				return pe;
			}
			jlr += one;
		}
		pe.setStartQuarter(lastyyyyMMdd);
		pe.setJlr(jlr);
		double value = StkUtils.numberFormat(this.getStk().getTotalCapital()/10000 * this.getK(yyyyMMdd).getClose() / pe.getJlr(), 2);
		pe.setPe(value);
		return pe;
	}
	
	/**
	 * 得到财务数据最近是哪个季度
	 */
	public String getLastestQuarter() throws Exception {
		if(this.getFnData().size() == 0)return null;
		return this.getFnData().get(0).getFnDate();
	}
	
	/********************************** 主营收入增长率 *************************************/
	public String getOperatingIncomeGrowthRateAsString() throws Exception {
		StkFnDataCust fn = getFnDataLastestByType(FN_ZYSRZZL);
		return fn!=null?fn.getFnValueToString():StkConstant.MARK_DOUBLE_HYPHEN;
	}
	
	
	/********************************** 季度净利润 ****************************************/
	
	public int isGrowthOrPotentialOrReversion() throws Exception {
		if(this.isGrowth()){
			return Index.FN_Growth;
		}
		if(this.isPotential()){
			return Index.FN_Potential;
		}
		if(isReversion()){
			return Index.FN_Reversion;
		}
		return Index.FN_Common;
	}

	private final static String STK_TYPE_CZG = "成长股";
	private final static String STK_TYPE_QLG = "潜力股";
	private final static String STK_TYPE_FZG = "反转股";
	private final static String STK_TYPE_PTG = "普通股";
	
	public String isGrowthOrPotentialOrReversionName() throws Exception {
		if(market == 1){
			int type = this.isGrowthOrPotentialOrReversion();
			if(type == Index.FN_Growth)return STK_TYPE_CZG;
			if(type == Index.FN_Potential)return STK_TYPE_QLG;
			if(type == Index.FN_Reversion)return STK_TYPE_FZG;
		}
		return STK_TYPE_PTG;
	}
	
	public boolean isPotential() throws Exception {
		return this.getNetProfitGrowthAverageValue(10, 0) > 0;
	}
	public boolean isGrowth() throws Exception {
		return this.getNetProfitGrowthAverageValue(6, 15) > 0 || this.getNetProfitGrowthAverageValue(8, 10) > 0;
	}
	public boolean isReversion() throws Exception {
		if(this.getFnData().size() == 0)return false;
		String curyyyyMMdd = this.getFnData().get(0).getFnDate();
		String lastyyyyMMdd = StkUtils.getPrevQuarter(curyyyyMMdd);
		String last2yyyyMMdd = StkUtils.getPrevQuarter(lastyyyyMMdd);
		//净利率增长率
		double jlrzzl = this.getNetProfitGrowthAsNumber(curyyyyMMdd);
		double lastjlrzzl = this.getNetProfitGrowthAsNumber(lastyyyyMMdd);
		double last2jlrzzl = this.getNetProfitGrowthAsNumber(last2yyyyMMdd);
		if(jlrzzl > 0 && (jlrzzl - lastjlrzzl > 20) && lastjlrzzl < 10 && jlrzzl > last2jlrzzl){
			return true;
		}
		//增加主营收入增长率反转股
		StkFnDataCust fnData = this.getFnData(FN_ZYSRZZL, curyyyyMMdd);
		double zyzzl = fnData != null?(fnData.getFnValue()==null?0.0:fnData.getFnValue()):0.0;
		fnData = this.getFnData(FN_ZYSRZZL, lastyyyyMMdd);
		double lastzyzzl = fnData != null?(fnData.getFnValue()==null?0.0:fnData.getFnValue()):0.0;
		fnData = this.getFnData(FN_ZYSRZZL, last2yyyyMMdd);
		double last2zyzzl = fnData != null?(fnData.getFnValue()==null?0.0:fnData.getFnValue()):0.0;
		if(zyzzl > 0 && (zyzzl - lastzyzzl > 20) && lastzyzzl < 10 && zyzzl > last2zyzzl){
			return true;
		}
		return false;
	}
	
	/**
	 * 计算单季度净利润
	 * @param yyyyMMdd必须是0331,0630,0930,1231
	 * @return 数据不全返回0
	 */
	public double getNetProfitByOneQuarter(String yyyyMMdd) throws Exception {
		String MMdd = StkUtils.formatDate(StkUtils.sf_ymd2.parse(yyyyMMdd), StkUtils.sf_MMdd);
		if(!StkUtils.MMDD_Q1.equals(MMdd)){
			String lastyyyyMMdd = StkUtils.getPrevQuarter(yyyyMMdd);
			Map<String,StkFnDataCust> fnData = this.getFnDataByDate(yyyyMMdd);
			Map<String,StkFnDataCust> lastfnData = this.getFnDataByDate(lastyyyyMMdd);
			if(fnData != null && fnData.get(FN_JLR) != null && fnData.get(FN_JLR).getFnValue() != null
				&& lastfnData != null && lastfnData.get(FN_JLR) != null && lastfnData.get(FN_JLR).getFnValue() != null){
				return fnData.get(FN_JLR).getFnValue().doubleValue() - lastfnData.get(FN_JLR).getFnValue().doubleValue();
			}
		}else{
			Map<String,StkFnDataCust> fnData = this.getFnDataByDate(yyyyMMdd);
			if(fnData != null && fnData.get(FN_JLR) != null && fnData.get(FN_JLR).getFnValue() != null){
				return fnData.get(FN_JLR).getFnValue().doubleValue();
			}
		}
		return 0;
	}
	
	
	private Map<String,Double> netProfitGrowth = new HashMap<String,Double>();
	
	/**
	 * jlr同比增长率
	 * @param MMdd必须是 0331,0630,0930,1231
	 */
	public Double getNetProfitGrowth(String yyyyMMdd) throws Exception{
		if(netProfitGrowth.get(yyyyMMdd) != null){
			return netProfitGrowth.get(yyyyMMdd);
		}
		Double jlrzzl = new Double(0);
		StkFnDataCust fnData = this.getFnData(FN_JLR, yyyyMMdd);
		if(fnData != null){
			jlrzzl = fnData.getRateOfYear(false);
		}
		netProfitGrowth.put(yyyyMMdd, jlrzzl);
		return jlrzzl;
	}
	
	public double getNetProfitGrowthAsNumber(String yyyyMMdd) throws Exception{
		Double d = this.getNetProfitGrowth(yyyyMMdd);
		return d==null?0:d;
	}
	
	public double getNetProfitGrowthLastestQuarter() throws Exception {
		if(this.getFnData().size() == 0)return 0;
		String curyyyyMMdd = this.getFnDataLastestByType(FN_JLR).getFnDate();
		return this.getNetProfitGrowthAsNumber(curyyyyMMdd);
	}
	
	public String getNetProfitGrowthLastestQuarterAsString() throws Exception {
		if(this.getFnData().size() == 0)return StkConstant.MARK_DOUBLE_HYPHEN;
		String curyyyyMMdd = this.getFnData().get(0).getFnDate();
		Double d = this.getNetProfitGrowth(curyyyyMMdd);
		if(d == null)return StkConstant.MARK_DOUBLE_HYPHEN;
		return StkUtils.number2String(d,2);
	}
	
	private Map<String,Double> netProfitGrowthAverageValue = new HashMap<String,Double>();
	/**
	 * 前quarters个季度，每季net profit growth要>=x，则返回这些季度jlr平均值，否则返回0
	 */
	public double getNetProfitGrowthAverageValue(int quarters, int x) throws Exception {
		String key = quarters+"_"+x;
		if(netProfitGrowthAverageValue.get(key) != null){
			return netProfitGrowthAverageValue.get(key);
		}
		if(this.getFnData().size() == 0)return 0;
		double total = 0.0;
		double growth = 0.0;
		int i = quarters;
		StkFnDataCust fn = this.getFnDataLastestByType(FN_JLR);
		if(fn == null){
			return 0;
		}
		String curyyyyMMdd = fn.getFnDate();
		while(true){
			growth = this.getNetProfitGrowthAsNumber(curyyyyMMdd);
			//System.out.println(i+"=growth="+curyyyyMMdd+"="+growth);
			if(growth < x){
				netProfitGrowthAverageValue.put(key, new Double(0));
				return 0;
			}
			total += growth;
			curyyyyMMdd = StkUtils.getPrevQuarter(curyyyyMMdd);
			i --;
			if(i <= 0)break;
		}
		Double result = new Double(total/quarters);
		netProfitGrowthAverageValue.put(key, result);
		return result;
	}
	
	/**
	 * @deprecated
	 * 成长性：最近三年净利润增长为正，当年季度净利润增长也是正
	 */
	public double valuationByGrowing(String fnType) throws Exception {
		int curYear = StkUtils.YEAR;
		String yyyyMMdd = (--curYear)+StkUtils.MMDD_Q4;
		int i = 3;
		int j = 0;
		double total = 0.0;
		while(true){
			if(i-- == 0){
				break;
			}
			Map<String,StkFnDataCust> d = this.getFnDataByDate(yyyyMMdd);
			if( (d.get(fnType) != null && d.get(fnType).getFnValue() != null && d.get(fnType).getFnValue().doubleValue() <= 0)
					|| (d.get(FN_JLR) != null && d.get(FN_JLR).getFnValue() != null && d.get(FN_JLR).getFnValue().doubleValue() <= 0)){
				return 0;
			}
			if(d.get(fnType) != null && d.get(fnType).getFnValue() != null){
				total += d.get(fnType).getFnValue().doubleValue();
				j++;
			}
			yyyyMMdd = (--curYear)+StkUtils.MMDD_Q4;
		}
		Map<String,StkFnDataCust> d = null;
		
		yyyyMMdd = curYear+StkUtils.MMDD_Q4;
		d = this.getFnDataByDate(yyyyMMdd);
		if( d.get(FN_JLR) != null && d.get(FN_JLR).getFnValue() != null && d.get(FN_JLR).getFnValue().doubleValue() <= 0){
			return 0;
		}
		
		yyyyMMdd = StkUtils.YEAR+StkUtils.MMDD_Q3;
		d = this.getFnDataByDate(yyyyMMdd);
		if(d.get(fnType) != null && d.get(fnType).getFnValue() != null && d.get(fnType).getFnValue().doubleValue() <= 0){
			return 0;
		}
		yyyyMMdd = StkUtils.YEAR+StkUtils.MMDD_Q2;
		d = this.getFnDataByDate(yyyyMMdd);
		if(d.get(fnType) != null && d.get(fnType).getFnValue() != null && d.get(fnType).getFnValue().doubleValue() <= 0){
			return 0;
		}
		yyyyMMdd = StkUtils.YEAR+StkUtils.MMDD_Q1;
		d = this.getFnDataByDate(yyyyMMdd);
		if(d.get(fnType) != null && d.get(fnType).getFnValue() != null && d.get(fnType).getFnValue().doubleValue() <= 0){
			return 0;
		}
		if(j == 0){
			return 0;
		}
		return total/j;
	}
	
	/**
	 * Not be used by webapp
	 * 复合增长率(CAGR)
	 * 公式： 末年2008的数额（19,500）除以首年2005的数额（10,000），得1.95，再取1/(2008-2005)次幂，也就是开年数次方根，最后减去1。
	 */
	public void valuationByCAGR(String yyyyMMdd) throws Exception {
		List params = new ArrayList();
		String year = StkUtils.formatDate(StkUtils.sf_ymd2.parse(yyyyMMdd), StkUtils.sf_yyyy);
		List<String> list = new ArrayList<String>();
		for(int i=0;i<=4;i++){
			list.add("'"+(Integer.parseInt(year)-i)+"1231'");
		}
		params.add(this.code);
		List<StkFnData> fnDatas = JdbcUtils.list(this.getConnection(),"select * from "+this.tab_stkfndata+" where code=? and type=100 and fn_date in ("+StringUtils.join(list.iterator(), ",")+") order by fn_date desc",params,StkFnData.class);
		if(fnDatas.size()==5){
			if(fnDatas.get(0).getFnValue() > 0 && fnDatas.get(1).getFnValue() > 0 && fnDatas.get(2).getFnValue() > 0 && fnDatas.get(3).getFnValue() > 0 && fnDatas.get(4).getFnValue() > 0){
				int i = 0;
				double d4 =     Math.pow(fnDatas.get(i).getFnValue()/fnDatas.get(fnDatas.size()-1).getFnValue(), 1.0/(fnDatas.size()-1))-1;
				double d3 =     Math.pow(fnDatas.get(i).getFnValue()/fnDatas.get(fnDatas.size()-2).getFnValue(), 1.0/(fnDatas.size()-2))-1;
				double d3Last = Math.pow(fnDatas.get(i+1).getFnValue()/fnDatas.get(fnDatas.size()-2+1).getFnValue(), 1.0/(fnDatas.size()-2))-1;
				double d2 =     Math.pow(fnDatas.get(i).getFnValue()/fnDatas.get(fnDatas.size()-3).getFnValue(), 1.0/(fnDatas.size()-3))-1;
				double d2Last = Math.pow(fnDatas.get(i+1).getFnValue()/fnDatas.get(fnDatas.size()-3+1).getFnValue(), 1.0/(fnDatas.size()-3))-1;
				double d2LastLast = Math.pow(fnDatas.get(i+2).getFnValue()/fnDatas.get(fnDatas.size()-3+2).getFnValue(), 1.0/(fnDatas.size()-3))-1;
				if(d2 >= d3 && d3 >= d4 && d4 > 0 && d3 >= d3Last && d3Last > 0 && d2 >= d2Last && d2Last >= d2LastLast && d2LastLast > 0){
					System.out.print(this.finance());
					//复合增长率A
					System.out.println("复合增长率A,2年,"+StkUtils.number2String(d2,2)+",3年,"+StkUtils.number2String(d3,2)+",4年,"+StkUtils.number2String(d4,2));
					System.out.println();
				}else if(d2 >= d3 && d2 >= d2Last && d2Last > 0 && d3 >= d3Last && d3Last > 0){//2年复合大于3年复合，今年去年复合大于去年前年复合
					System.out.print(this.finance());
					//复合增长率B
					System.out.println("复合增长率B,2年,"+StkUtils.number2String(d2,2)+",3年,"+StkUtils.number2String(d3,2)+",4年,"+StkUtils.number2String(d4,2));
					System.out.println();
				}
			}
		}
	}
	
	/********************************** 盈利预期 ****************************************/

	private List<StkEarningsForecast> earningsForecastList = null;
	
	public List<StkEarningsForecast> getEarningsForecast() throws Exception {
		if(earningsForecastList == null){
			List params = new ArrayList();
			params.add(code);
			this.earningsForecastList = JdbcUtils.list(this.getConnection(), "select * from stk_earnings_forecast where code=? order by forecast_year asc", params, StkEarningsForecast.class);
		}
		return this.earningsForecastList;
	}
	
	public double getPECanBuy() throws Exception {
		double cagr = getCAGRByEarningsForecast(2);
		if(cagr == 0)return 0;
		return StkUtils.numberFormat(Math.pow(1+cagr, 3)*10, 2);
	}
	
	public double getCAGRByEarningsForecast() throws Exception {
		List<StkEarningsForecast> list = this.getEarningsForecast();
		Double startValue = null;
		int i = 0 ;
		for(StkEarningsForecast ef : list){
			if(startValue != null)i++;
			if(ef.getForecastYear().equals(String.valueOf(StkUtils.YEAR-1))){
				startValue = ef.getForecastNetProfit();
			}
		}
		if(i > 1){
			return StkUtils.calcCAGR(startValue, list.get(list.size()-1).getForecastNetProfit(), i);
		}
		return 0;
	}
	
	@Deprecated
	public double getCAGRByEarningsForecast(int years) throws Exception {
		List<StkEarningsForecast> list = this.getEarningsForecast();
		if(list.size() <= years){
			return 0;
		}else{
			return StkUtils.calcCAGR(list.get(0).getForecastNetProfit(), list.get(years).getForecastNetProfit(), years);
		}
	}
	
	public void initEarningsForecast() throws Exception {
		String page = HttpUtils.get("http://f9.eastmoney.com/"+(this.loc==1?"sh":"sz")+this.code+".html", "gbk");
		Node node = HtmlUtils.getNodeByAttribute(page, "", "id", "F9_ylyc");
		if(node == null)return;
		Node table = node.getParent().getParent();
		
		Map<String,Map<String, String>> datas = HtmlUtils.getListFromTable((TableTag)table, 1, 0);
		//System.out.println(datas);
		for(Map.Entry<String, Map<String, String>> data : datas.entrySet()){
			if(data.getKey().indexOf("E") >= 0){
				Map<String, String> map = data.getValue();
				String netProfit = map.get("净利润(元)");
				if("--".equals(netProfit)){
					continue;
				}
				double np = Double.parseDouble(parseAmount(netProfit));
				if(np == 0){
					continue;
				}
				List params = new ArrayList();
				params.add(np);
				params.add(code);
				params.add("20"+StringUtils.replace(data.getKey(), "E", ""));
				int n = JdbcUtils.update(conn, "update stk_earnings_forecast set forecast_net_profit=? where code=? and forecast_year=?", params);
				if(n == 0){
					params.clear();
					params.add(code);
					params.add("20"+StringUtils.replace(data.getKey(), "E", ""));
					netProfit = map.get("净利润(元)");
					params.add(np);
					JdbcUtils.insert(this.getConnection(), "insert into stk_earnings_forecast select ?,?,?,sysdate,null from dual", params);
				}
			}
		}
	}
	
	public void updateEarningsForecastPE() throws Exception {
		List<StkEarningsForecast> efs = getEarningsForecast();
		double mv = this.getTotalMarketValue();
		for(StkEarningsForecast ef : efs){
			if(ef.getForecastNetProfit() != null && ef.getForecastNetProfit().doubleValue() != 0){
				List params = new ArrayList();
				params.add(mv/ef.getForecastNetProfit());
				params.add(code);
				params.add(ef.getForecastYear());
				JdbcUtils.update(conn, "update stk_earnings_forecast set pe=? where code=? and forecast_year=?", params);
			}
		}
	}
	
	/********************************** 业绩预告 ****************************************/
	
	private List<StkEarningsNotice> performanceNoticesList = null;
	
	public List<StkEarningsNotice> getPerformanceNotices(){
		if(performanceNoticesList == null){
			List params = new ArrayList();
			params.add(code);
			this.performanceNoticesList = JdbcUtils.list(this.getConnection(), "select * from stk_earnings_notice where code=? order by fn_date desc", params, StkEarningsNotice.class);
		}
		return this.performanceNoticesList;
	}
	
	public StkEarningsNotice getPerformanceNoticeLatest(String date){
		this.performanceNoticesList = getPerformanceNotices();
		if(performanceNoticesList != null && performanceNoticesList.size() > 0){
			StkEarningsNotice en = this.performanceNoticesList.get(0);
			if(StkUtils.dateBefore(date, en.getFnDate())){
				return en;
			}
		}
		return null;
	}
	
	public String parseAmount(String s){
		if(StringUtils.indexOf(s, "万") > 0){
			s = StringUtils.replace(s, "万", "");
			return String.valueOf(Double.parseDouble(s)/10000);
		}else if(StringUtils.indexOf(s, "亿") > 0){
			s = StringUtils.replace(s, "亿", "");
		}
		return s;
	}
	
	public List<String> getEarningsForecastAsList(){
		String ee = this.getStock().getNextQuarterEarning();
		if(ee != null && ee.length() > 0){
			return JsonUtils.testJsonArray(ee);
		}
		return null;
	}
	
	public List<String> getEarningsForecastAsString(){
		String ee = this.getStock().getNextQuarterEarning();
		if(ee != null && ee.length() > 0){
			ee = StringUtils.substringBetween(ee, "[","]");
			String[] array = ee.split(", ");
			return Arrays.asList(array);
		}
		return null;
	}
	
	/********************************** 主营业务  ****************************************/
	
	public void initKeywordOnMainBusiness() throws Exception{
		String page = HttpUtils.get("http://stock.jrj.com.cn/share,"+this.code+",zyyw.shtml","gbk");
        List<Node> nodes = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "table", "class", "tab1");
        Node table = nodes.get(1);
        TableTag tab = (TableTag)table;
        TableRow[] rows = tab.getRows();
        List<TableColumn> columns = new ArrayList<TableColumn>();
        for(TableRow row : rows){
        	TableHeader[] headers = row.getHeaders();
        	if(headers.length > 0){
        		TableHeader header = headers[0];
        		if(header.toPlainTextString().contains("地域"))break;
        	}
        	TableColumn[] cols = row.getColumns();
        	if(cols.length > 0){
        		columns.add(cols[0]);
        	}
        }
        for(TableColumn column : columns){
        	String kw = column.toPlainTextString();
        	Keyword keyword = new Keyword(this.getConnection(), kw); 
			keyword.addLink(code, 1, 1);
        }
	}
	
	private final static String SQL_SELECT_MAIN_BUSINESS_BY_CODE = "select a.keyword_id keywordid,a.id linkid,b.name name from stk_keyword_link a, stk_keyword b where b.status=0 and a.link_type=1 and a.keyword_id=b.id and a.code=? and a.code_type=1 order by a.insert_time";
	public List<Map> getMainBusiness(){
		List params = new ArrayList();
		params.add(code);
		List<Map> list = JdbcUtils.list2Map(this.getConnection(), SQL_SELECT_MAIN_BUSINESS_BY_CODE, params);
		return list;
	}
	
	private final static String HTML_BR_BR = "<br><br>";
	
	public List<Name2Value> getF9(){
		if(this.f9 != null) return f9;
		this.f9 = new ArrayList<Name2Value>();
		String s = this.getStock().getF9();
		if(s != null && s.length() > 0){
			String[] ss = s.split(HTML_BR_BR);
			for(String str : ss){
				String name = StringUtils.substringBetween(str, StkConstant.HTML_TAG_B, StkConstant.HTML_TAG_B_END);
				this.f9.add(new Name2Value(name,str));
			}
		}
		return this.f9;
	}
	
	public List<String> getKeywordOnMainBusiness(){
		return Keyword.listKeywords(this.getConnection(), code, 1, 1);
	}
	
	public List<String> getKeywordAll(){
		return Keyword.listKeywords(this.getConnection(), code, 1, -1);
	}
	
	
	/********************************** 综合财务 ****************************************/
	
	private String finance = null;
	
	/**
	 * Not be used by webapp
	 * 再加“经营现金流”，“应收款“，”货存”,"三项费用比重"
	 * @return
	 * @throws Exception
	 */
	public String finance() throws Exception {
		if(finance != null){
			return finance;
		}
		String yyyyMMdd = StkUtils.YEAR+StkUtils.MMDD_Q4;
		StringBuffer sb = new StringBuffer();
		double totalCapital = this.getStk().getTotalCapital()/10000;
		String expect = StringUtils.replace(StringUtils.replace(StringUtils.replace(this.getStk().getEarningExpect(), ",", "，"), "?", ""), "<br>", "；");
		sb.append(this.code+","+this.getStk().getName()+",总股本,"+StkUtils.number2String(totalCapital,2)+",雪球PE："+this.getPETTM()+",可投资PE："+this.getPECanBuy()+",行业,"+StringUtils.join(this.getIndustryName(), "/")).append(",,,").append(expect).append("\n");
		sb.append("年份,最低价,最高价,净利润,ROE,毛利率,负债率,主营收入增长,应收账款,预收账款,三项费用比重,经营现金流,净利润增长率,股东人数,最低市值,最高市值,PE低,PE高,PEG低,PEG高").append("\n");
		String MMdd = StkUtils.formatDate(StkUtils.sf_ymd2.parse(yyyyMMdd), StkUtils.sf_MMdd);
		boolean flag = false;
		while(true){
			Map<String,StkFnDataCust> d = this.getFnDataByDate(yyyyMMdd);
			if(d.size() != 0){
				flag = true;
			}else{
				if(flag)break;
			}
			String year = StkUtils.formatDate(StkUtils.sf_ymd2.parse(yyyyMMdd), StkUtils.sf_yyyy);
			yyyyMMdd = (Integer.parseInt(year)-1)+MMdd;
			Map<String,StkFnDataCust> dLast = this.getFnDataByDate(yyyyMMdd);
			double jlr = d.get(FN_JLR)==null?0.0:d.get(FN_JLR).getFnValue();
			double jlrLast = dLast.get(FN_JLR)==null?0.0:dLast.get(FN_JLR).getFnValue();
			double jlrAdd = 0.0;
			if(jlrLast != 0 && jlr != 0){
				jlrAdd = (jlr - jlrLast)/jlrLast*100;
			}
			
			double lowValue = this.getKValueByLLV(year);
			double highValue = this.getKValueByHHV(year);
			double lowPE = jlr==0.0?0.0:(lowValue*totalCapital/jlr);
			double highPE = jlr==0.0?0.0:(highValue*totalCapital/jlr);
			
			sb.append(year
					+","+lowValue
					+","+highValue
					+","+jlr
					+","+(d.get("10")==null?"":d.get("10").getFnValue())
					+","+(d.get("30")==null?"":d.get("30").getFnValue())
					+","+(d.get("80")==null?"":d.get("80").getFnValue())
					+","+(d.get(FN_ZYSRZZL)==null?"":d.get(FN_ZYSRZZL).getFnValue())
					+","+(d.get("210")==null?"":StkUtils.number2String(d.get("210").getFnValue()/10000,2))
					+","+(d.get("200")==null?"":StkUtils.number2String(d.get("200").getFnValue()/10000,2))
					+","+(d.get("45")==null?"":d.get("45").getFnValue())
					+","+(d.get("300")==null?"":StkUtils.number2String(d.get("300").getFnValue()/10000,2))
					+","+StkUtils.number2String(jlrAdd, 2)
					+","+(this.getHolder(year+"1231")==null?"0":this.getHolder(year+"1231").getHolder())
					+","+StkUtils.number2String(lowValue*totalCapital, 2)
					+","+StkUtils.number2String(highValue*totalCapital, 2)
					+","+StkUtils.number2String(lowPE, 2)
					+","+StkUtils.number2String(highPE, 2)
					+","+StkUtils.number2String(jlrAdd==0?0:lowPE/jlrAdd, 2)
					+","+StkUtils.number2String(jlrAdd==0?0:highPE/jlrAdd, 2)
					).append("\n");
			
			if(Integer.parseInt(year) == StkUtils.YEAR || Integer.parseInt(year) == StkUtils.YEAR-1){
				for(String MMDD:StkUtils.MMDD_Q321){
					Map<String,StkFnDataCust> fnQ = this.getFnDataByDate(year+MMDD);
					if(fnQ.size() > 0){
						jlr = fnQ.get(FN_JLR)==null?0.0:fnQ.get(FN_JLR).getFnValue();
						dLast = this.getFnDataByDate((Integer.parseInt(year)-1)+MMDD);
						jlrLast = dLast.get(FN_JLR)==null?0.0:dLast.get(FN_JLR).getFnValue();
						jlrAdd = 0.0;
						if(jlrLast > 0){
							jlrAdd = (jlr - jlrLast)/jlrLast*100;
						}
						
						sb.append(StkUtils.mmddToQuarter(MMDD)
								+",,,"+jlr
								+","+(fnQ.get("10")==null?"":fnQ.get("10").getFnValue())
								+","+(fnQ.get("30")==null?"":fnQ.get("30").getFnValue())
								+","+(fnQ.get("80")==null?"":fnQ.get("80").getFnValue())
								+","+(fnQ.get(FN_ZYSRZZL)==null?"":fnQ.get(FN_ZYSRZZL).getFnValue())
								+","+(fnQ.get("210")==null?"":StkUtils.number2String(fnQ.get("210").getFnValue()/10000,2))
								+","+(fnQ.get("200")==null?"":StkUtils.number2String(fnQ.get("200").getFnValue()/10000,2))
								+","+(fnQ.get("45")==null?"":fnQ.get("45").getFnValue())
								+","+(fnQ.get("300")==null?"":StkUtils.number2String(fnQ.get("300").getFnValue()/10000,2))
								+","+StkUtils.number2String(jlrAdd, 2)
								+","+(this.getHolder(year+MMDD)==null?"0":this.getHolder(year+MMDD).getHolder())
								+",,,,,,"
								).append("\n");
					}
				}
			}
		}
		this.finance = sb.toString();
		return this.finance;
	}
	
	public List<List> getValueHistoryList() throws Exception {
		boolean flag = false;
		String yyyyMMdd = StkUtils.YEAR+StkUtils.MMDD_Q4;
		String MMdd = StkUtils.formatDate(StkUtils.sf_ymd2.parse(yyyyMMdd), StkUtils.sf_MMdd);
		double totalCapital = this.getStk().getTotalCapital()/10000;
		List rows = new ArrayList();
		while(true){
			Map<String,StkFnDataCust> d = this.getFnDataByDate(yyyyMMdd);
			if(d.size() != 0){
				flag = true;
			}else{
				if(flag)break;
			}
			String year = StkUtils.formatDate(StkUtils.sf_ymd2.parse(yyyyMMdd), StkUtils.sf_yyyy);
			double lowValue = this.getKValueByLLV(year);
			if(lowValue == 0){
				break;
			}
			double highValue = this.getKValueByHHV(year);
			
			yyyyMMdd = (Integer.parseInt(year)-1)+MMdd;
			Map<String,StkFnDataCust> dLast = this.getFnDataByDate(yyyyMMdd);
			//净利润
			//double jlr = d.get(FN_JLR)==null?0.0:d.get(FN_JLR).getFnValue();			
			double jlrLast = dLast.get(FN_JLR)==null?0.0:dLast.get(FN_JLR).getFnValue();
			
			//股东权益
			//double gdqy = d.get(FN_GDQY)==null?0.0:d.get(FN_GDQY).getFnValue();			
			double gdqyLast = dLast.get(FN_GDQY)==null?0.0:dLast.get(FN_GDQY).getFnValue();
			//double jlrAdd = d.get(FN_JLRZZL)==null?0.0:(d.get(FN_JLRZZL).getFnValue()==null?0.0:d.get(FN_JLRZZL).getFnValue());
			double jlrLastAdd = dLast.get(FN_JLRZZL)==null?0.0:(dLast.get(FN_JLRZZL).getFnValue()==null?0.0:dLast.get(FN_JLRZZL).getFnValue());
			
			double lowCapital = lowValue*totalCapital;
			double highCapital = highValue*totalCapital;
			double lowPE = jlrLast==0.0?0.0:(lowCapital/jlrLast);
			double highPE = jlrLast==0.0?0.0:(highCapital/jlrLast);
			double lowPB = gdqyLast==0.0?0.0:(lowCapital/gdqyLast);
			double highPB = gdqyLast==0.0?0.0:(highCapital/gdqyLast);
			
			List row = new ArrayList();
			row.add(year);
			row.add(String.valueOf(lowValue));
			row.add(String.valueOf(highValue));
			row.add(StkUtils.number2String(lowCapital, 2));
			row.add(StkUtils.number2String(highCapital, 2));
			row.add(StkUtils.number2String(lowPE, 2));
			row.add(StkUtils.number2String(highPE, 2));
			row.add(StkUtils.number2String(lowPB, 2));
			row.add(StkUtils.number2String(highPB, 2));
			row.add(StkUtils.number2String(jlrLastAdd==0?0:lowPE/jlrLastAdd, 2));
			row.add(StkUtils.number2String(jlrLastAdd==0?0:highPE/jlrLastAdd, 2));
			rows.add(row);
		}
		return rows;
	}
	
	
	/********************************** K线形态分析，创新高 ，另见 IndexUtils ****************************************/
	
	public List<Gap> getGaps(String endDate, int days) throws Exception {
		List<Gap> tmpGaps = new ArrayList<Gap>();
		K start = this.getK(endDate, days);
		K end = this.getK(endDate);
		K k = start;
		while(true){
			if(k.getDate().compareTo(end.getDate()) >= 0){
				break;
			}
			K after = k.after(1);
			if(after == null || after.getDate().equals(endDate))break;
			Gap gap = null;
			if(k.getHigh() < after.getLow()){
				gap = new Gap();
				gap.setStartK(k);
				gap.setEndK(after);
				gap.setHigh(after.getLow());
				gap.setLow(k.getHigh());
				gap.setUp(true);
			}else if(k.getLow() > after.getHigh()){
				gap = new Gap();
				gap.setStartK(k);
				gap.setEndK(after);
				gap.setHigh(k.getLow());
				gap.setLow(after.getHigh());
				gap.setUp(false);
			}
			if(gap != null){
				tmpGaps.add(gap);
			}
			List delList = new ArrayList();
			for(Gap tmpGap : tmpGaps){
				if(tmpGap.getHigh() <= tmpGap.getLow() || (tmpGap.getHigh() < k.getHigh() && tmpGap.getLow() > k.getLow())){
					delList.add(tmpGap);
					continue;
				}
				if(tmpGap.isUp() && tmpGap.getHigh() > after.getLow()){
					tmpGap.setHigh(after.getLow());
				}
				if(!tmpGap.isUp() && tmpGap.getLow() < after.getHigh()){
					tmpGap.setLow(after.getHigh());
				}
				if(tmpGap.getHigh() <= tmpGap.getLow())delList.add(tmpGap);
			}
			tmpGaps.removeAll(delList);
			
			k = after;
		}
		//System.out.println(tmpGaps);
		return tmpGaps;
	}
	
	/**
	 * 
	 * @param endDate
	 * @param days 高于前后 各n天才算高点
	 * @param n
	 * @return
	 * @throws Exception
	 */
	public List<K> getKsHistoryHighPoint(String endDate, int days, int n) throws Exception{
		List<K> highPoints = new ArrayList<K>();
		K start = this.getK(endDate, days);
		K end = this.getK(endDate, n);
		K k = start;
		while(k != null && (k = k.after(1)) != null){
			K high = this.getKByHHV(k.before(n*2/3).getDate(),k.after(n).getDate());
			if(k.getDate().equals(high.getDate())){
				highPoints.add(k);
			}else{
				high = this.getKByHHV(k.before(n).getDate(),k.after(n*2/3).getDate());
				if(k.getDate().equals(high.getDate())){
					highPoints.add(k);
				}
			}
			if(k.dateAfterOrEquals(end)){
				break;
			}
		}
		return highPoints;
	}
	/**
	 * 一般取的参数为 (days,n) = (10,2) or (60,5/6)
	 */
	public List<K> getKsHistoryHighPoint2(String endDate, int days, int n) throws Exception{
		List<K> highPoints = new ArrayList<K>();
		K start = this.getK(endDate, days);
		K end = this.getK(endDate, n);
		K k = start;
		while(k != null && (k = k.after(1)) != null){
			if(k.isHHK(n)){
				highPoints.add(k);
			}
			if(k.dateAfterOrEquals(end)){
				break;
			}
		}
		return highPoints;
	}
	
	public List<K> getKsHistoryLowPoint(String endDate, int days, int n) throws Exception{
		List<K> lowPoints = new ArrayList<K>();
		K start = this.getK(endDate, days);
		K end = this.getK(endDate, n);
		K k = start;
		while(k != null && (k = k.after(1)) != null){
			if(k.getDate().compareTo(end.getDate()) >= 0){
				break;
			}
			K low = this.getKByLLV(k.before(n).getDate(),k.after(n).getDate());
			if(k.getDate().equals(low.getDate())){
				lowPoints.add(k);
			}
		}
		return lowPoints;
	}
	
	/**
	 * 返回历史上的高低点，高低点前后n天都比高点低 或 比低点高
	 * 返回的list里的分布是一个低点一个高点...，最好的分布是：不出现两个高/低点在一起的现象(实际是可能出现这种情况)
	 * 一般n取days的十分之一
	 */
	public List<K> getKsHistoryHighAndLow(String endDate, int days, int n) throws Exception {
		List<K> result = new ArrayList<K>();
		if(this.getKs().size() < days) return result;
		K start = this.getK(endDate, days);
		K end = this.getK(endDate);
		K k = end;
		while((k = k.before(1)) != null){
			if(k.getDate().compareTo(start.getDate()) <= 0){
				break;
			}
			//K low = this.getKByLLV(k.before(n).getDate(),k.after(n).getDate());
			if(k.isLLK(n)){
				result.add(k);
			}
			//K high = this.getKByHHV(k.before(n).getDate(),k.after(n).getDate());
			if(k.isHHK(n)){
				result.add(k);
			}
		}
		return result;
	}
	
	/** 判断close/high/open/low的值的趋势是不是上升的，ident表示取ident个值的平均价格 */
	public boolean isUp(List<K> ks, int ident, int type) throws Exception {
		if (ks.size() <= ident) {
			return false;
		}
		double maValue = 0;
		int x = ident;
		boolean isUp = true;
		for (int i = 0; i < ks.size() - ident + 1; i++) {
			double tmp = 0;
			while (--ident >= 0) {
				tmp += ks.get(i + ident).getValue(type);
			}
			tmp = tmp / x;
			if (tmp < maValue) {
				isUp = false;
				break;
			}
			maValue = tmp;
			ident = x;
		}
		return isUp;
	}
	
	public int getDaysBetween(String startDate, String endDate) throws Exception {
		int days = 0;
		if(startDate.compareTo(endDate) > 0){
			String tmp = startDate;
			startDate = endDate;
			endDate = tmp;
		}
		K tmpK = this.getK(startDate);
		do{
			if(tmpK.getDate().compareTo(endDate) >= 0){
				break;
			}
			days ++;
			K tmpK2 = tmpK.after(1);
			if(tmpK2.getDate().equals(tmpK.getDate())){
				break;
			}
			tmpK = tmpK2;	
		}while(true);
		return days;
	}
	
	//k线是不是缠绕
	public K isKIntersect(String date,int days) throws Exception {
		K k = this.getK(date);
		while(k != null){
			if(this.isKIntersect1(k.getDate(), 0) 
					|| this.isKIntersect2(k.getDate())
					|| this.isKIntersect3(k.getDate())
					|| this.isKIntersect4(k.getDate())
					|| this.isKIntersect5(k.getDate())) {
				return k;
			}
			if(--days <= 0)
				break;
			k = k.before(1);
		}
		return null;
	}
	
	//10,20,30,60,120 K线缠绕
	public boolean isKIntersect2(String date) throws Exception {
		double flag = 0.035;
		double[] d = new double[5];
		K k = this.getK(date);
		if(k != null && this.getKs().size() > 120){
			double ma10 = k.getMA(K.Close, 10);
			d[0] = ma10;
			double ma120 = k.getMA(K.Close, 120);
			d[1] = ma120;
			double abs = Math.abs(ma10 - ma120);
			double min = Math.min(ma10, ma120);
			if(abs/min <= flag){
				double ma20 = k.getMA(K.Close, 20);
				d[2] = ma20;
				abs = Math.abs(ma10 - ma20);
				min = Math.min(ma10, ma20);
				if(abs/min <= flag){
					double ma30 = k.getMA(K.Close, 30);
					d[3] = ma30;
					abs = Math.abs(ma30 - ma20);
					min = Math.min(ma30, ma20);
					if(abs/min <= flag){
						double ma60 = k.getMA(K.Close, 60);
						d[4] = ma60;
						abs = Math.abs(ma30 - ma60);
						min = Math.min(ma30, ma60);
						if(abs/min <= flag){
							abs = Math.abs(ma120 - ma60);
							min = Math.min(ma120, ma60);
							if(abs/min <= flag){
								StkUtils.Pair p = StkUtils.minmax(d);
								double dd = (p.getMax()-p.getMin())/p.getMin();
								if(dd <= 0.040){
									this.changePercent = dd;
								}
								if(market == 2){
									if(k.getClose() >= (p.getMax()+p.getMin())/2){
										return true;
									}
									return false;
								}
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	//10,20,30,60,250 K线缠绕
	public boolean isKIntersect3(String date) throws Exception {
		double flag = 0.04;
		double[] d = new double[5];
		K k = this.getK(date);
		if(k != null && this.getKs().size() > 250){
			double ma10 = k.getMA(K.Close, 10);
			d[0] = ma10;
			double ma250 = k.getMA(K.Close, 250);
			d[1] = ma250;
			double abs = Math.abs(ma10 - ma250);
			double min = Math.min(ma10, ma250);
			if(abs/min <= flag){
				double ma20 = k.getMA(K.Close, 20);
				d[2] = ma20;
				abs = Math.abs(ma10 - ma20);
				min = Math.min(ma10, ma20);
				if(abs/min <= flag){
					double ma30 = k.getMA(K.Close, 30);
					d[3] = ma30;
					abs = Math.abs(ma30 - ma20);
					min = Math.min(ma30, ma20);
					if(abs/min <= flag){
						double ma60 = k.getMA(K.Close, 60);
						d[4] = ma60;
						abs = Math.abs(ma30 - ma60);
						min = Math.min(ma30, ma60);
						if(abs/min <= flag){
							abs = Math.abs(ma250 - ma60);
							min = Math.min(ma250, ma60);
							if(abs/min <= flag){
								StkUtils.Pair p = StkUtils.minmax(d);
								double dd = (p.getMax()-p.getMin())/p.getMin();
								if(dd <= 0.040){
									this.changePercent = dd;
								}
								if(market == 2){
									if(k.getClose() >= (p.getMax()+p.getMin())/2){
										return true;
									}
									return false;
								}
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	//10,20,30,60 K线缠绕
	public boolean isKIntersect4(String date) throws Exception {
		double flag = 0.025;
		double[] d = new double[4];
		K k = this.getK(date);
		if(k != null && this.getKs().size() > 60){
			double ma10 = k.getMA(K.Close, 10);
			d[0] = ma10;
			double ma60 = k.getMA(K.Close, 60);
			d[1] = ma60;
			double abs = Math.abs(ma10 - ma60);
			double min = Math.min(ma10, ma60);
			if(abs/min <= flag){
				double ma20 = k.getMA(K.Close, 20);
				d[2] = ma20;
				abs = Math.abs(ma10 - ma20);
				min = Math.min(ma10, ma20);
				if(abs/min <= flag){
					double ma30 = k.getMA(K.Close, 30);
					d[3] = ma30;
					abs = Math.abs(ma30 - ma20);
					min = Math.min(ma30, ma20);
					if(abs/min <= flag){
						abs = Math.abs(ma30 - ma60);
						min = Math.min(ma30, ma60);
						if(abs/min <= flag){
							StkUtils.Pair p = StkUtils.minmax(d);
							double dd = (p.getMax()-p.getMin())/p.getMin();
							if(dd <= 0.03){
								this.changePercent = dd;
							}
							if(market == 2){
								if(k.getClose() >= (p.getMax()+p.getMin())/2){
									return true;
								}
								return false;
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	//10,20,30 K线缠绕
	public boolean isKIntersect5(String date) throws Exception {
		double flag = 0.015;
		double[] d = new double[3];
		K k = this.getK(date);
		if(k != null && this.getKs().size() > 30){
			double ma10 = k.getMA(K.Close, 10);
			d[0] = ma10;
			double ma30 = k.getMA(K.Close, 30);
			d[1] = ma30;
			double abs = Math.abs(ma10 - ma30);
			double min = Math.min(ma10, ma30);
			//System.out.println("abs/min="+abs/min);
			if(abs/min <= flag){
				double ma20 = k.getMA(K.Close, 20);
				d[2] = ma20;
				abs = Math.abs(ma10 - ma20);
				min = Math.min(ma10, ma20);
				//System.out.println("abs/min="+abs/min);
				if(abs/min <= flag){
					StkUtils.Pair p = StkUtils.minmax(d);
					double dd = (p.getMax()-p.getMin())/p.getMin();
					if(dd <= 0.015){
						this.changePercent = dd;
					}
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isKIntersect6(String date) throws Exception {
		double flag = 0.015;
		double[] d = new double[3];
		K k = this.getK(date);
		if(k != null && this.getKs().size() > 30){
			double ma10 = k.getMA(K.Close, 10);
			d[0] = ma10;
			double ma30 = k.getMA(K.Close, 30);
			d[1] = ma30;
			double abs = Math.abs(ma10 - ma30);
			double min = Math.min(ma10, ma30);
			if(abs/min <= flag){
				double ma20 = k.getMA(K.Close, 20);
				d[2] = ma20;
				abs = Math.abs(ma10 - ma20);
				min = Math.min(ma10, ma20);
				if(abs/min <= flag){
					StkUtils.Pair p = StkUtils.minmax(d);
					double dd = (p.getMax()-p.getMin())/p.getMin();
					if(dd < 0.02){
						this.changePercent = dd;
					}
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isKIntersect1(String date,int days) throws Exception {
		int x = 15;
		K k = this.getK(date);
		while(k != null){
			double ma120 = k.getMA(K.Close, 120);
			double ma120_2 = k.before(x).getMA(K.Close, 120);
			double abs120 = Math.abs(ma120 - ma120_2);
			double ma120_min = Math.min(ma120, ma120_2);

			if(abs120/ma120_min <= 0.02){
				double ma250 = k.getMA(K.Close, 250);
				double abs120_250 = Math.abs(ma120 - ma250);
				double ma120_250_min = Math.min(ma120, ma250);
				
				
				if(abs120_250/ma120_250_min <= 0.20 && (this.isMACross(k.getDate(), x, 30, 60, K.Close) || this.isMACross(k.getDate(), x, 20, 30, K.Close))){
					double ma60 = k.getMA(K.Close, 60);
					double ma60_2 = k.before(x).getMA(K.Close, 60);
					double abs60 = Math.abs(ma60 - ma60_2);
					double ma60_min = Math.min(ma60, ma60_2);
					if(abs60/ma60_min <= 0.05){
						K lowK = this.getKByLLV(k.before(x).getDate(),k.getDate());
						K highK = this.getKByHHV(k.before(x).getDate(),k.getDate());
						
						double tmpMA60 = k.getMA(K.Close, 60);
						double tmpMA120 = k.getMA(K.Close, 120);
						double lowMA = Math.min(tmpMA60, tmpMA120);
						double highMA = Math.max(tmpMA60, tmpMA120);
						for(int j=1;j<x;j++){
							K before = k.before(j);
							tmpMA60 = before.getMA(K.Close, 60);
							tmpMA120 = before.getMA(K.Close, 120);
							if(tmpMA60 > tmpMA120){
								lowMA = Math.min(lowMA, tmpMA120);
								highMA = Math.max(highMA, tmpMA60);
							}else{
								lowMA = Math.min(lowMA, tmpMA60);
								highMA = Math.max(highMA, tmpMA120);
							}
						}
						
						double lowKV = lowK.getLow();
						double highKV = highK.getHigh();
						if(lowKV <= lowMA && highKV >=highMA && (highKV-lowKV)/lowKV <= 0.12){
							//System.out.println("lowMA="+lowMA+",highMA="+highMA);
							//System.out.println(this.getName()+","+this.getCode()+",date="+k.getDate());
							this.changePercent = (highKV-lowKV)/lowKV;
							//indexs.add(index);
							if(market == 2){
								if(k.getClose() >= (highKV+lowKV)/2){
									return true;
								}
								return false;
							}
							return true;
						}
					}
				}
			}
			//index.gc();
			if(days-- == 0)
				break;
			k = k.before(1);
		}
		/*Collections.sort(indexs, new Comparator<Index>(){
			@Override
			public int compare(Index o1, Index o2) {
				int i = (int)((o1.changePercent - o2.changePercent)*100);
				return i;
			}});*/
		return false;
	}
	
	//是否突破短期（60,6）下降趋势线
	public boolean isBreakOutShortTrendLine(String date) throws Exception {
		return this.isBreakOutTrendLine(date, 60, 6);
	}
	
	//是否突破长期（120,12）下降趋势线
	public boolean isBreakOutLongTrendLine(String date) throws Exception {
		return this.isBreakOutTrendLine(date, 120, 12);
	}
	
	public boolean isBreakOutTrendLine(String date, int m, int n) throws Exception {
		if(this.getKs().size() < m)return false;
		List<K> ks = this.getKsHistoryHighPoint(date, m, n);
		/*for(K k : ks){
			System.out.println("=="+k.getDate());
		}*/
		K hk = null;
		K lk = null; 
		if(ks != null && ks.size() >= 2){
			hk = ks.get(ks.size()-2);
			lk = ks.get(ks.size()-1);
			if(hk.getHigh() > lk.getHigh()){
				int days = this.getDaysBetween(hk.getDate(), lk.getDate());
				double d = hk.getClose() - lk.getClose();
				if(d < 0)return false;
				double decreasePerDay = d/days;
				int days2 = this.getDaysBetween(lk.getDate(), date);
				double trendLineValue = lk.getClose() - decreasePerDay * (days2 + 3);
				double ytrendLineValue = lk.getClose() - decreasePerDay * (days2-1 + 2);
				K curK = this.getK(date);
				//System.out.println(curK.getDate()+","+curK.getClose()+">="+trendLineValue+",ytrendLineValue="+ytrendLineValue);
				if(curK.getClose() >= trendLineValue && curK.before(1).getClose() <= ytrendLineValue){
					this.changePercent = (hk.getClose()-curK.getClose())/hk.getClose();
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isBreakOutTrendLine2(String date, int m, int n, double percent) throws Exception {
		if(this.getKs().size() < m)return false;
		List<K> ks = this.getKsHistoryHighPoint(date, m, n);
		/*for(K k : ks){
			System.out.println("=="+k.getDate());
		}*/
		K hk = null;
		K lk = null; 
		if(ks != null && ks.size() >= 2){
			List list = zhuhe(ks);
			for(int i = list.size()-1;i>=0;i--){
				List<K> ksTmp = (List)list.get(i);
				hk = ksTmp.get(0);
				lk = ksTmp.get(1);
				//System.out.println("i="+i+",hk="+hk.getDate()+",lk="+lk.getDate());
				if(hk.getHigh() > lk.getHigh() * (1+percent)){
					int days = this.getDaysBetween(hk.getDate(), lk.getDate());
					double d = hk.getHigh() - lk.getHigh();
					//System.out.println("d="+d+",days="+days);
					if(d < 0) continue;
					double decreasePerDay = d/hk.getHigh()/days;
					//System.out.println("decreasePerDay="+decreasePerDay);
					int days2 = this.getDaysBetween(lk.getDate(), date);
					//System.out.println("day2=="+days2+",hk="+hk.getDate()+",lk="+lk.getDate()+",date="+date);
					double trendLineValue = lk.getHigh()*Math.pow(1-decreasePerDay, days2)*0.97;
					double ytrendLineValue = lk.getHigh()*Math.pow(1-decreasePerDay, days2-1)*0.97;
					K curK = this.getK(date);
					//System.out.println(curK.getDate()+","+curK.getHigh()+">="+trendLineValue+","+curK.before(1).getHigh()+"<"+ytrendLineValue);
					if(curK.getHigh() >= trendLineValue && curK.before(1).getHigh() < ytrendLineValue){
						this.changePercent = (hk.getClose()-curK.getClose())/hk.getClose();
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private List zhuhe(List<K> ks){
		int[] a = new int[ks.size()];
		for(int i=0;i<ks.size();i++){
			a[i] = i;
		}
		List result = new ArrayList();
		List list = AlgorithmUtils.zuhe(a, 2);
		for (int i = 0; i < list.size(); i++) {
			List tmp = new ArrayList();
			int[] temp = (int[]) list.get(i);
			//System.out.println();
			for (int j = 0; j < temp.length; j++) {
				tmp.add(ks.get(temp[j]));
				//System.out.print(temp[j]);
			}
			result.add(tmp);
		}
		return result;
	}
	
	/**
	 * @param date
	 * @param m
	 * @param n
	 * @param days = 1,表示算计的是明天，2表示计算后天
	 * @return
	 * @throws Exception
	 */
	public List<List<K>> isBreakOutTrendLine3(String date, int m, int n, int days) throws Exception {
		List<List<K>> results = new ArrayList<List<K>>();
		if(this.getKs().size() < m)return results;
		List<K> ks = this.getKsHistoryHighPoint2(date, m, n);
		/*for(K k : ks){
			System.out.println(k);
		}*/
		K hk = null;
		K lk = null; 
		K k = this.getK(date);
		K yk = k.before(1);
		
		if(ks != null && ks.size() >= 2){
			List list = zhuhe(ks);
			for(int i = list.size()-1;i>=0;i--){
				List<K> ksTmp = (List)list.get(i);
				hk = ksTmp.get(0);
				lk = ksTmp.get(1);
				//System.out.println("i="+i+",hk="+hk.getDate()+",lk="+lk.getDate());
				if(hk.getHigh() > lk.getHigh()){
					int day = this.getDaysBetween(hk.getDate(), date) + days;
					int day2 = this.getDaysBetween(hk.getDate(), lk.getDate());
					List<Point> ps = DrawLineUtils.getPointsByX(0, (int)(hk.getHigh()*100), day, (int)(k.getClose()*100), day2);
					//System.out.println(date+",ddd="+DrawLineUtils.compare(ps, (int)(lk.getHigh()*100)));
					if(ps.size() > 0){
						if(DrawLineUtils.compare(ps, (int)(lk.getHigh()*100)) == DrawLineUtils.POINT_POSITION.HIGHER_THAN_LINE){
							List<Point> psYesterday = DrawLineUtils.getPointsByX(0, (int)(hk.getHigh()*100), day-1, (int)(yk.getClose()*100), day2);
							if(psYesterday.size() > 0){
								//System.out.println(yk.getDate()+","+DrawLineUtils.compare(ps, (int)(lk.getHigh()*100)));
								if(DrawLineUtils.compare(psYesterday, (int)(lk.getHigh()*100)) != DrawLineUtils.POINT_POSITION.HIGHER_THAN_LINE){
									results.add(ksTmp);
									/*for(K ka : ksTmp){
										System.out.println(ka);
									}*/
								}
							}
						}
					}
				}
			}
		}
		return results;
	}
	
	public K getKWithCondition(String date, int days, Condition cdn) throws Exception {
		K k = this.getK(date);
		while(k != null){
			//System.out.println("k="+k.getDate());
			if(cdn.pass(k)){
				return k;
			}
			k = k.before(1);
			if(days -- <= 1){
				break;
			}
		}
		return null;
	}
	
	public K getKWithCondition(String endDate, String startDate, Condition cdn) throws Exception {
		K k = this.getK(endDate);
		while(k != null){
			//System.out.println("k="+k.getDate());
			if(cdn.pass(k)){
				return k;
			}
			k = k.before(1);
			if(k.getDate().equals(startDate)){
				break;
			}
		}
		return null;
	}
	
	public int getKCountWithCondition(String startDate, String endDate, Condition cdn) throws Exception {
		K k = this.getK(endDate);
		int cnt = 0;
		while(k != null){
			if(cdn.pass(k)){
				cnt ++;
			}
			if(k.getDate().compareTo(startDate) <= 0){
				break;
			}
			k = k.before(1);
		}
		return cnt;
	}
	
	/***************************** update,insert ************************************/
	
	public int updatePE(List params){
		String sql = "update "+this.tab_stkkline+" set pe_ttm=? where code=? and kline_date=?";
		return JdbcUtils.update(this.getConnection(), sql, params);
	}

	public int insertFnData(List params){
		//String sql = "insert into "+this.tab_stkfndata+"(code,type,fn_date,fn_value,insert_time) select ?,?,?,?,sysdate() from dual where not exists (select 1 from "+this.tab_stkfndata+" where code=? and type=? and fn_date=?)";
		String sql = "insert into "+this.tab_stkfndata+"(code,type,fn_date,fn_value,insert_time) select ?,?,?,?,sysdate from dual where not exists (select 1 from "+this.tab_stkfndata+" where code=? and type=? and fn_date=?)";
		return JdbcUtils.insert(this.getConnection(), sql, params);
	}
	public int updateFnData(List params){
		//String sql = "update "+this.tab_stkfndata+" set fn_value=?,update_time=sysdate() where code=? and type=? and fn_date=?";
		String sql = "update "+this.tab_stkfndata+" set fn_value=?,update_time=sysdate where code=? and type=? and fn_date=?";
		return JdbcUtils.update(this.getConnection(), sql, params);
	}
	
	private final static String SQL_GETCAPITALFLOW = "select round((main_percent+super_large_percent)/2,2) from stk_capital_flow where code=? and flow_date=?";
	public Double getCapitalFlowPercent(String date){
		List params = new ArrayList();
		params.add(code);
		params.add(date);
		return JdbcUtils.load(this.getConnection(), SQL_GETCAPITALFLOW, params, Double.class);
	}
	
	public List<StkCapitalFlow> getCapitalFlowAfterDate(String date) {
		List params = new ArrayList();
		params.add(code);
		params.add(date);
		return JdbcUtils.list(this.getConnection(), "select * from stk_capital_flow where code=? and flow_date>=? order by flow_date asc", params, StkCapitalFlow.class);
	}
	
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 1:sh, 2:sz
	 */
	public int getLoc() {
		return loc;
	}

	public void setLoc(int loc) {
		this.loc = loc;
	}

	public Canslim getCanslim() {
		return canslim;
	}

	public void setCanslim(Canslim canslim) {
		this.canslim = canslim;
	}

	public String getName() {
		/*try{
			return this.getStock().getName();
		}catch(Exception e){
			return null;
		}*/
		return this.getStock().getName();
	}
	
	public String getShortName(){
		String name = this.getStock().getName();
		if(name != null && name.length() > 15){
			name = name.substring(0, 15)+"...";
		}
		return name;
	}
	
	public String getNameByTrim(){
		return StringUtils.replace(this.getName(), StkConstant.MARK_BLANK_SPACE, StkConstant.MARK_EMPTY);
	}
	
	public String getNameforCate4(){
		return StringUtils.replace(ChineseUtils.cn2py(this.getName()).toLowerCase(), ".", "");
	}

	public void setName(String name) {
		this.name = name;
	}

	public PE getPe() {
		return pe;
	}

	public void setPe(PE pe) {
		this.pe = pe;
	}
	
	public int getMarket() {
		return market;
	}

	public void setMarket(int market) {
		this.market = market;
	}
	
	public void setConnection(Connection conn){
		this.conn = conn;
	}
	public Connection getConnection(){
		return this.conn;
	}
	
	public List<StkMonitor> getMonitor(int type){
		List params = new ArrayList();
		params.add(code);
		params.add(type);
		return JdbcUtils.list(getConnection(), "select * from stk_monitor where code=? and type=? order by insert_date desc", params, StkMonitor.class);
	}

	public boolean equals(Object o){
		return this.code.equals(((Index)o).code);
	}
	
	public int hashCode(){
		return this.code.hashCode();
	}
	
	public String toHtml(){
		try {
			return this.getName() + "["+StkUtils.wrapCodeLink(this.code)+"]["+StkUtils.number2String(this.getTotalMarketValue(),2)+"亿]";
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public String toString(){
		try {
			return this.getName() + "["+this.code+"]["+StkUtils.number2String(this.getTotalMarketValue(),2)+"亿]";
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public static void main(String[] args) throws Exception {

	}
}




