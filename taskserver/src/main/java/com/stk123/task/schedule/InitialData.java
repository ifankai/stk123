package com.stk123.task.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.common.CommonConstant;
import com.stk123.common.db.TableTools;
import com.stk123.common.db.util.CloseUtil;
import com.stk123.common.db.util.DBUtil;
import com.stk123.common.util.*;
import com.stk123.common.util.collection.Name2Value;
import com.stk123.model.Index;
import com.stk123.model.Industry;
import com.stk123.model.News;
import com.stk123.model.Text;
import com.stk123.model.bo.*;
import com.stk123.model.bo.cust.StkFnDataCust;
import com.stk123.model.dto.HexunIndustryConception;
import com.stk123.model.dto.SinaMeiGu;
import com.stk123.service.DictService;
import com.stk123.service.IndustryServiceDeprecated;
import com.stk123.util.ExceptionUtils;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.htmlparser.Node;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;


/**
 * 初始化行业数据，盈利预期数据，财务数据 等
 * http://wwtnews.windin.com/WUDS/web/F9/Stock/WEST/EPS/EarningsForecast.aspx?WindCode=002275.sz
 */
@Component
@Slf4j
public class InitialData {

    @Autowired
    private IndustryServiceDeprecated industryService;

	private  int fnYearFrom = ServiceUtils.YEAR - 3;
	private  List<String> infos = new ArrayList<String>();
	private  List<Name2Value> awesomefunds = new ArrayList<Name2Value>();

	private  List<Name2Value> awesomePersons = new ArrayList<Name2Value>();

	public  void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource(TableTools.class,"db.properties");
		Connection conn = null;
        InitialData initialData = new InitialData();
		try{
			conn = DBUtil.getConnection();
			int market = 1;//default A stock
			if(args != null && args.length > 0){
				for(String arg : args){
					if("US".equals(arg)){
						market = 2;
					}
				}
			}
			if(market == 1){
                initialData.initialAStock(conn);

                initialData.initialHKStock(conn);
			}else if(market == 2){
                initialData.initialUStock(conn);
			}

		}finally{
			if(conn != null)conn.close();
			CacheUtils.close();
		}
	}

	public void run(int market) {
        Connection conn = null;
        try {
            ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
            conn = DBUtil.getConnection();
            if (market == 1) {
                initialAStock(conn);
                initialHKStock(conn);
            } else if (market == 2) {
                initialUStock(conn);
            }
        }catch (Exception e){
            log.error("InitialData", e);
        }finally{
            CloseUtil.close(conn);
            CacheUtils.close();
        }
    }

	public void initialUStock(Connection conn) throws Exception {
		try{
			infos.clear();
			initUStkFromSina(conn, true);
			initUStkFromSina(conn, false);
			//InitialData.initUStkFromXueQiu(conn);
			//InitialData.initUStkFromEasymoney(conn);//更新中概股
		}catch(Exception e){
			EmailUtils.send("Initial US Stock Data init Error", e);
			e.printStackTrace();
		}
		try{
			//update outstanding share & add new stock
			initUStkFromFinviz(conn);
		}catch(Exception e){
			EmailUtils.send("Initial US Stock Data from Finviz Error", e);
			e.printStackTrace();
		}
		try{
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_us order by code", Stk.class);
			List<StkFnType> fnTypes = JdbcUtils.list(conn, "select * from stk_fn_type where market=2 and status=1", StkFnType.class);
			for(Stk stk : stks){
				Index index = new Index(conn,stk.getCode(),stk.getName());
				System.out.println(stk.getCode());
				try{
					//update stk status
					initialUStkStatus(conn,index.getCode());

				}catch(Exception e){
					ExceptionUtils.insertLog(conn, stk.getCode(), e);
					e.printStackTrace();
				}
				try{
					//update财务数据
					initFnDataTTM(conn,ServiceUtils.now,index,fnTypes);
				}catch(Exception e){
					ExceptionUtils.insertLog(conn, stk.getCode(), e);
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			EmailUtils.send("Initial US Stock Data Error", e);
			e.printStackTrace();
		}
	}


	public void initialAStock(Connection conn) throws Exception {
		try{
			infos.clear();
			//InitialData.initialStk(conn,ServiceUtils.now);
		}catch(Exception e){
			EmailUtils.send("Initial A Stock initial Data Error", e);
			e.printStackTrace();
		}

        try{
            //InitialData.initialIndustryFromCnIndex(conn, 14);
            initialIndustryFromCsindex_zjh(conn, 7);
            initialIndustryFromCsindex_zz1(conn);
        }catch(Exception e){
            EmailUtils.send("Initial A Stock Industry csindex Data Error", e);
            e.printStackTrace();
        }

		try{
			updateIndustryFromHexun(conn);
		}catch(Exception e){
			EmailUtils.send("Initial A Stock Industry Data Error", e);
			e.printStackTrace();
		}

		try{
			initialIndustryFrom10jqka(conn,"gn");
			initialIndustryFrom10jqka(conn,"thshy");
		}catch(Exception e){
			EmailUtils.send("Initial A Stock Industry Data Error", e);
			e.printStackTrace();
		}

		try{
			//牛基列表
			initAwesomeFund();
			//牛散
			List<StkDictionary> aps = DictService.getDictionary(DictService.NIUSAN);
			for(StkDictionary sd : aps){
				awesomePersons.add(new Name2Value(sd.getText(),3));
			}
		}catch(Exception e){
			EmailUtils.send("Initial A Stock Awesome Fund Data Error", e);
			e.printStackTrace();
		}

		try{
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			List<StkFnType> fnTypes = JdbcUtils.list(conn, "select * from stk_fn_type where market=1 and status=1", StkFnType.class);
			for(Stk stk : stks){
				//System.out.println(stk.getCode());
				Index index = new Index(conn,stk.getCode(),stk.getName());
				try{
					//行业，盈利预期
					//updateStkInfoFromWind(conn,index,StkUtils.now,fnTypes);
					//fn
					initFnDataTTM(conn,ServiceUtils.now,index,fnTypes);
				}catch(Exception e){
					ExceptionUtils.insertLog(conn, stk.getCode(), e);
					e.printStackTrace();
				}

				try{
					//earnings forecast
					//index.initEarningsForecast();
					//盈利预测
					index.initEarningsForecast();

					//成长股，潜力股，反转股
					upateStkFnType(conn, index);

					//主力持仓
					//InitialData.initOwnership(conn, index, "mh");
					//十大流通股东
					initOwnership(conn, index);

				}catch(Exception e){
					ExceptionUtils.insertLog(conn, stk.getCode(), e);
					e.printStackTrace();
				}

				try{
					//股东人数
					initHolderFrom10jqka(conn, index);
				}catch(Exception e){
					ExceptionUtils.insertLog(conn, stk.getCode(), e);
					e.printStackTrace();
				}

				try{
					//主营业务
					index.initKeywordOnMainBusiness();
				}catch(Exception e){
					ExceptionUtils.insertLog(conn, stk.getCode(), e);
					e.printStackTrace();
				}

				try{
					updateStkF9(conn, index);
				}catch(Exception e){
					try{
						updateStkF9(conn, index);
					}catch(Exception ee){}
					ExceptionUtils.insertLog(conn, stk.getCode(), e);
					e.printStackTrace();
				}

				/*try{
					//所属板块
					updateIndustryFromQQ(conn, index);
				}catch(Exception e){
					ExceptionUtils.insertLog(conn, stk.getCode(), e);
					e.printStackTrace();
				}*/


				try{
					//限售解禁
					updateRestricted(conn, index);
				}catch(Exception e){
					ExceptionUtils.insertLog(conn, stk.getCode(), e);
					e.printStackTrace();
				}
			}

			String msg = "";
			if(infos.size() > 0){
				msg += StringUtils.join(infos, "<br>");
			}
			if(msg != null && msg.length() > 0){
				EmailUtils.send("Initial A Stock Data Successfully", msg);
			}
		}catch(Exception e){
			EmailUtils.send("Initial A Stock Data Error", e);
			e.printStackTrace();
		}
	}



	/***************************************** HK Stock *****************************************/
	/**
	 *	初始化港股
	 */
	public void initialHKStock(Connection conn) throws Exception {
		int pageCnt = 1;
		while(true){
			//http://quote.eastmoney.com/center/list.html#50_1
			String page = HttpUtils.get("http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=C._HKS&sty=FCOQB&sortType=C&sortRule=-1&page="+pageCnt+"&pageSize=20&js=&token=7bc05d0d4c3c22ef9fca8c2a912d779c&jsName=quote_123&_g=", null, "utf-8");
			String str = StringUtils.substringBetween(page, "([", "])");
			List<String> list = JsonUtils.testJsonArray("["+str+"]");
			for(String s : list){
				String[] item = StringUtils.split(s, ",");
				if("-".equals(item[2])){
					continue;
				}
				List params = new ArrayList();
				params.add(item[0]);
				params.add(item[1]);
				params.add(new Timestamp(ServiceUtils.now.getTime()));
				params.add(item[0]);
				String sql = "insert into stk(code,name,insert_time,market) select ?,?,?,3 from dual where not exists (select 1 from stk where code=?)";
				JdbcUtils.insert(conn, sql, params);
			}
			if(list.size() < 20){
				break;
			}
			pageCnt++;
		}

		int cnt = 1;
		List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_hk order by code", Stk.class);
		for(Stk stk : stks){
			//Index index =  new Index(conn,stk.getCode(),stk.getName());
			System.out.println("code:"+stk.getCode());

			//update company profile
            //http://emweb.securities.eastmoney.com/PC_HKF10/BusinessExpectation/index?type=web&code=01088&color=b#ywzw
			try{
				String page = HttpUtils.get("http://emweb.securities.eastmoney.com/PC_HKF10/BusinessExpectation/PageAjax?code="+stk.getCode(), null);
				//System.out.println(page);
				//Map map = JsonUtils.testJson(page);
                if(StringUtils.isNotEmpty(page)) {
                    List params = new ArrayList();
                    params.add(page);
                    params.add(stk.getCode());
                    JdbcUtils.update(conn, "update stk set company_profile=? where code=?", params);
                }
			}catch(Exception e){
				ExceptionUtils.insertLog(conn, e);
				Thread.sleep(1000 * 60 * 5);
			}

			//update f9
			try{
				String page = HttpUtils.get("http://web.ifzq.gtimg.cn/appstock/hk/HkInfo/getReview?c="+stk.getCode()+"&_callback=jQuery1124010398680807140925_1502022259119&_=", "utf-8");
				//System.out.println(page);
				String s = StringUtils.substringBetween(page, "({", "})");
				if(s != null && s.length() > 0){
					Map map = JsonUtils.testJson("{"+ s +"}");
					//System.out.println(map.get("data"));
					if("{}".equals(String.valueOf(map.get("data")))){
						continue;
					}

					List params = new ArrayList();
					params.add(String.valueOf(map.get("data")));
					params.add(stk.getCode());
					JdbcUtils.update(conn, "update stk set f9=? where code=?", params);
				}
			}catch(Exception e){
				ExceptionUtils.insertLog(conn, e);
			}

			if(cnt++ % 100 == 0){
				Thread.sleep(1000 * 60);
			}
		}
	}

	/***************************************** US Stock *****************************************/

	public  void initialUStkStatus(Connection conn, String code) throws Exception{
		String page = HttpUtils.get("http://qt.gtimg.cn/&q=us"+code, "GBK");
		if(page.contains("pv_none_match")){
			List params = new ArrayList();
			params.add(code);
			JdbcUtils.update(conn, "update stk set status=1 where code=?", params);
		}
	}


	/***************************************** A Stock *****************************************/

	public  void updateIndustryFromQQ(Connection conn, Index index) throws Exception {
		String page = HttpUtils.get("http://stock.finance.qq.com/corp1/profile.php?zqdm="+index.getCode(), null, "GBK");
		Node node = HtmlUtils.getNodeByText(page, null, "所属板块");
		List<Node> nodes = HtmlUtils.getNodeListByTagName(node.getNextSibling().getNextSibling(), "a");
		List params = new ArrayList();
		boolean clear = false;
		for(Node a : nodes){
			String indName = a.toPlainTextString();
			//System.out.println(indName);
			params.clear();
			params.add(indName);
			StkIndustryType type = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='qq_conception'",params, StkIndustryType.class);
			if(type == null){
				params.clear();
				params.add(indName);
				int n = JdbcUtils.insert(conn, "insert into stk_industry_type(id,name,source) values(s_industry_type_id.nextval,?,'qq_conception')", params);
				if(n > 0){
					EmailUtils.send("新行业 - "+ indName, "updateIndustryFromQQ");
				}
				params.clear();
				params.add(indName);
				type = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='qq_conception'",params, StkIndustryType.class);
			}

			if(!clear){
				params.clear();
				params.add(index.getCode());
				JdbcUtils.update(conn, "delete from stk_industry where code=? and industry in (select id from stk_industry_type where source='qq_conception')", params);
				clear = true;
			}

			params.clear();
			params.add(index.getCode());
			params.add(type.getId());
			params.add(index.getCode());
			params.add(type.getId());
			JdbcUtils.insert(conn, "insert into stk_industry(code,industry) select ?,? from dual where not exists (select 1 from stk_industry where code=? and industry=?)", params);
		}
	}

	public  void updateIndustryFromHexun(Connection conn) throws Exception {
		List params = new ArrayList();
		String page = HttpUtils.get("http://quote.hexun.com/js/conception.ashx", null, "GBK");
		List<HexunIndustryConception> inds = JsonUtils.getList4Json(StringUtils.substringBetween(page, "conceptionData=", ";"), HexunIndustryConception.class);
		for(HexunIndustryConception ind:inds){
			params.clear();
			params.add(ind.getType_name());
			StkIndustryType type = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='hexun_conception'",params, StkIndustryType.class);
			if(type == null){
				params.clear();
				params.add(ind.getType_name());
				int n = JdbcUtils.insert(conn, "insert into stk_industry_type(id,name,source) values(s_industry_type_id.nextval,?,'hexun_conception')", params);
				if(n > 0){
					EmailUtils.send("新行业 - "+ ind.getType_name(), "updateIndustryFromHexun");
				}
				params.clear();
				params.add(ind.getType_name());
				type = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='hexun_conception'",params, StkIndustryType.class);
			}

			params.clear();
			params.add(type.getId());
			JdbcUtils.update(conn, "delete from stk_industry where industry=?", params);

			page = HttpUtils.get("http://quote.tool.hexun.com/hqzx/stocktype.aspx?columnid=5522&type_code="+ind.getType_code()+"&sorttype=3&updown=up&page=1&count=5000", null, "GBK");
			List<List> stks = JsonUtils.getList4Json(StringUtils.substringBetween(page, "dataArr = ", ";"), ArrayList.class);

			for(List stk:stks){
				if(stk==null || stk.get(0)==null || StringUtils.startsWith(String.valueOf(stk.get(0)), "200")
						||StringUtils.startsWith(String.valueOf(stk.get(0)), "900")){//B股
					continue;
				}
				params.clear();
				params.add(stk.get(0));
				params.add(type.getId());
				params.add(stk.get(0));
				params.add(type.getId());
				try{//加try{}，处理不了000820
					JdbcUtils.insert(conn, "insert into stk_industry(code,industry) select ?,? from dual where not exists (select 1 from stk_industry where code=? and industry=?)", params);
				}catch(Exception e){
					//ExceptionUtils.insertLog(conn, "999999", e);
				}
			}
		}
	}

	//同花顺概念板块
	public  void initialIndustryFrom10jqka(Connection conn, String category) throws Exception {
		System.out.println("initialIndustryFrom10jqka");
		String source = "10jqka_"+category;
		List params = new ArrayList();
		String page = HttpUtils.get("http://q.10jqka.com.cn/"+category+"/", null, "");
		List<Node> cates = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "div", "class", "cate_items");
		for(Node cate : cates){
			List<Node> items = HtmlUtils.getNodeListByTagName(cate, "a");
			for(Node item : items){
				//System.out.println(item.toHtml());
				params.clear();
				String indName = StringUtils.trim(item.toPlainTextString());
				params.add(indName);
				StkIndustryType type = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='"+source+"'",params, StkIndustryType.class);
				String link = ((LinkTag)item).getAttribute("href");
				//link: http://q.10jqka.com.cn/gn/detail/code/300646/
				page = HttpUtils.get(link, null, "");

				if(type == null){
					Node na = HtmlUtils.getNodeByAttribute(page, null, "class", "board-hq");
					if(na == null)continue;
					Node name = HtmlUtils.getNodeByTagName(na, "h3");
					Node codeNode = HtmlUtils.getNodeByTagName(name, "span");
					String code = codeNode.toPlainTextString();

					params.clear();
					params.add(code);
					type = JdbcUtils.load(conn, "select * from stk_industry_type where id=? and source='"+source+"'",params, StkIndustryType.class);
					if(type != null){
						params.clear();
						params.add(indName);
						params.add(code);
						JdbcUtils.update(conn, "update stk_industry_type set name=? where id=?", params);
					}else{
						System.out.println(indName+"-"+code);
						params.clear();
						params.add(code);
						params.add(indName);
						int n = JdbcUtils.insert(conn, "insert into stk_industry_type(id,name,source) values(?,?,'"+source+"')", params);

						params.clear();
						params.add(indName);
						type = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='"+source+"'",params, StkIndustryType.class);

						params.clear();
						params.add(code);
						params.add(indName);
						JdbcUtils.insert(conn, "insert into stk(code,name,insert_time,market,cate,address) values(?,?,sysdate,1,4,'"+source+"')", params);
					}
				}

				params.clear();
				params.add(type.getId());
				System.out.println(type.getId()+","+type.getName());
				JdbcUtils.update(conn, "delete from stk_industry where industry=?", params);

				int p = 1;
				while(true){
					page = HttpUtils.get(link + "order/desc/page/"+p+"/ajax/1", null);
					if(page.contains("暂无成份股数据"))break;
					Node table = HtmlUtils.getNodeByAttribute(page, null, "class", "m-table m-pager-table");
					if(table == null)break;
					List<List<String>> lists = HtmlUtils.getListFromTable((TableTag)table,0);
					if(lists == null || lists.size() == 0)break;
					for(List<String> stk : lists){
						params.clear();
						params.add(stk.get(1));
						params.add(type.getId());
						params.add(stk.get(1));
						params.add(type.getId());
						JdbcUtils.insert(conn, "insert into stk_industry(code,industry) select ?,? from dual where not exists (select 1 from stk_industry where code=? and industry=?)", params);
					}
					p++;
				}
			}
		}
	}

	//证监会行业-个股，市盈率
	//http://www.csindex.com.cn/zh-CN/downloads/industry-price-earnings-ratio?type=zjh1&date=2020-08-07
	//http://www.csindex.com.cn/zh-CN/downloads/industry-price-earnings-ratio?type=zz1&date=2020-08-05
	public void initialIndustryFromCsindex_zjh(Connection conn, int n) throws Exception {
		String page = HttpUtils.get("http://www.csindex.com.cn/zh-CN/downloads/industry-price-earnings-ratio?type=zjh1&date="+ServiceUtils.formatDate(ServiceUtils.addDayOfWorking(ServiceUtils.now, -1), ServiceUtils.sf_ymd), "utf-8");
		//System.out.println(page);
		List<Node> tables = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "table", "class", "list-div-table");
		String parentCode = null;
		List params = new ArrayList();

		//行业-个股
		/*for(Node table : tables){
			//System.out.println(table.toHtml());
			//List<List<String>> list = HtmlUtils.getListFromTable((TableTag)table);
			//System.out.println(list);
			List<Node> tds = HtmlUtils.getNodeListByTagName(table, "td");
			//for(Node td : tds){
				//System.out.println(td.toHtml());
			//}
			if(tds != null && tds.size() > 4) {
				String code = StringUtils.trim(tds.get(0).toPlainTextString());
				if(StringUtils.length(code) == 1) parentCode = code;
				String parent = StringUtils.length(code)==1?"":parentCode;
				String name = StringUtils.trim(tds.get(1).toPlainTextString());
				Node a = HtmlUtils.getNodeByTagName(tds.get(3), "a");

				//===============
				System.out.println("parentCode:"+parent+",code:"+code+",name:"+name);
				StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, name, code, parent, "csindex_zjh");
				//TODO stk_data_industry_pe

				String href = HtmlUtils.getAttribute(a, "href");
				//System.out.println(StringEscapeUtils.unescapeHtml(href));
				page = HttpUtils.get(StringEscapeUtils.unescapeHtml(href),"utf-8");
				Node tab = HtmlUtils.getNodeByAttributeContain(page, null, "class", "p_table");
				List<List<String>> list = HtmlUtils.getListFromTable((TableTag) tab, 0);
				System.out.println(list);
				for(List<String> row : list){
					if(row != null && row.size() > 2) {
						String scode = row.get(1);
						params.clear();
						params.add(scode);
						params.add(indType.getId());
						params.add(scode);
						params.add(indType.getId());
						JdbcUtils.insert(conn, "insert into stk_industry(code,industry) select ?,? from dual where not exists (select 1 from stk_industry where code=? and industry=?)", params);
					}
				}
			}
		}*/


		//市盈率-pe
		int day = n;
		Map<String,Integer> indIdMap = new HashMap<String,Integer>();
		do{
			if(day <= 0) break;

			Date date = ServiceUtils.addDayOfWorking(ServiceUtils.now, (day--)-n);
			String d1 = ServiceUtils.formatDate(date, ServiceUtils.sf_ymd);
			String url = "http://www.csindex.com.cn/zh-CN/downloads/industry-price-earnings-ratio?type=zjh1&date="+d1;
            log.info(url);
			page = HttpUtils.get(url, "utf-8");
			tables = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "table", "class", "list-div-table");

			for(Node table : tables){
				List<Node> tds = HtmlUtils.getNodeListByTagName(table, "td");
				if(tds != null && tds.size() > 4) {
					String code = StringUtils.trim(tds.get(0).toPlainTextString());
					if(StringUtils.isNotEmpty(code)) {
						if(indIdMap.get(code) == null){
							StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, null,  code, null, "csindex_zjh");
							indIdMap.put(indType.getCode(), indType.getId());
						}
						String sdate = ServiceUtils.formatDate(date, ServiceUtils.sf_ymd2);
						String pe = StringUtils.trim(tds.get(2).toPlainTextString());
						if(NumberUtils.isNumber(pe)) {
                            industryService.updatePe(indIdMap.get(code), sdate, Double.parseDouble(pe));
						}
					}
				}
			}
		}while(true);

		//Pe-TTM
		day = n;
		do{
			if(day <= 0) break;

			Date date = ServiceUtils.addDayOfWorking(ServiceUtils.now, (day--)-n);
			String d1 = ServiceUtils.formatDate(date, ServiceUtils.sf_ymd);
			String url = "http://www.csindex.com.cn/zh-CN/downloads/industry-price-earnings-ratio?type=zjh2&date="+d1;
			log.info(url);
			page = HttpUtils.get(url, "utf-8");
			tables = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "table", "class", "list-div-table");

			for(Node table : tables){
				List<Node> tds = HtmlUtils.getNodeListByTagName(table, "td");
				if(tds != null && tds.size() > 4) {
					String code = StringUtils.trim(tds.get(0).toPlainTextString());
					if(StringUtils.isNotEmpty(code)) {
						if(indIdMap.get(code) == null){
							StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, null,  code, null, "csindex_zjh");
							indIdMap.put(indType.getCode(), indType.getId());
						}
						String sdate = ServiceUtils.formatDate(date, ServiceUtils.sf_ymd2);
						String pe = StringUtils.trim(tds.get(2).toPlainTextString());
						if(NumberUtils.isNumber(pe)) {
                            industryService.updatePettm(indIdMap.get(code), sdate, Double.parseDouble(pe));

						}
					}
				}
			}
		}while(true);

		//Pb
		day = n;
		do{
			if(day <= 0) break;

			Date date = ServiceUtils.addDayOfWorking(ServiceUtils.now, (day--)-n);
			String d1 = ServiceUtils.formatDate(date, ServiceUtils.sf_ymd);
			String url = "http://www.csindex.com.cn/zh-CN/downloads/industry-price-earnings-ratio?type=zjh3&date="+d1;
			log.info(url);
			page = HttpUtils.get(url, "utf-8");
			tables = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "table", "class", "list-div-table");

			for(Node table : tables){
				List<Node> tds = HtmlUtils.getNodeListByTagName(table, "td");
				if(tds != null && tds.size() > 4) {
					String code = StringUtils.trim(tds.get(0).toPlainTextString());
					if(StringUtils.isNotEmpty(code)) {
						if(indIdMap.get(code) == null){
							StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, null,  code, null, "csindex_zjh");
							indIdMap.put(indType.getCode(), indType.getId());
						}
						String sdate = ServiceUtils.formatDate(date, ServiceUtils.sf_ymd2);
						String pe = StringUtils.trim(tds.get(2).toPlainTextString());
						if(NumberUtils.isNumber(pe)) {
                            industryService.updatePb(indIdMap.get(code), sdate, Double.parseDouble(pe));

						}
					}
				}
			}
		}while(true);

		//adr
		day = n;
		do{
			if(day <= 0) break;

			Date date = ServiceUtils.addDayOfWorking(ServiceUtils.now, (day--)-n);
			String d1 = ServiceUtils.formatDate(date, ServiceUtils.sf_ymd);
			String url = "http://www.csindex.com.cn/zh-CN/downloads/industry-price-earnings-ratio?type=zjh4&date="+d1;
			log.info(url);
			page = HttpUtils.get(url, "utf-8");
			tables = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "table", "class", "list-div-table");

			for(Node table : tables){
				List<Node> tds = HtmlUtils.getNodeListByTagName(table, "td");
				if(tds != null && tds.size() > 4) {
					String code = StringUtils.trim(tds.get(0).toPlainTextString());
					if(StringUtils.isNotEmpty(code)) {
						if(indIdMap.get(code) == null){
							StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, null,  code, null, "csindex_zjh");
							indIdMap.put(indType.getCode(), indType.getId());
						}
						String sdate = ServiceUtils.formatDate(date, ServiceUtils.sf_ymd2);
						String pe = StringUtils.trim(tds.get(2).toPlainTextString());
						if(NumberUtils.isNumber(pe)) {
							industryService.updateAdr(indIdMap.get(code), sdate, Double.parseDouble(pe));
						}
					}
				}
			}
		}while(true);
	}

	//中证行业-个股，市盈率
	public  void initialIndustryFromCsindex_zz1(Connection conn) throws Exception {
		String page = HttpUtils.get("http://www.csindex.com.cn/zh-CN/downloads/industry-price-earnings-ratio?type=zz1&date="+ServiceUtils.formatDate(ServiceUtils.addDayOfWorking(ServiceUtils.now, -1), ServiceUtils.sf_ymd), "utf-8");
		//System.out.println(page);
		List<Node> tables = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "table", "class", "list-div-table");
		String parentCode = null;
		List params = new ArrayList();
		for(Node table : tables){
		    try {
                //System.out.println(table.toHtml());
                //List<List<String>> list = HtmlUtils.getListFromTable((TableTag)table);
                //System.out.println(list);
                List<Node> tds = HtmlUtils.getNodeListByTagName(table, "td");
                for (Node td : tds) {
                    System.out.println(StringUtils.trim(td.toPlainTextString()));
                }
                if (tds != null && tds.size() > 4) {
                    String code = StringUtils.trim(tds.get(0).toPlainTextString());
                    if (parentCode == null || StringUtils.length(code) == 2) {
                        parentCode = "";
                    } else {
                        parentCode = StringUtils.substring(code, 0, StringUtils.length(code) - 2);
                    }
                    String name = StringUtils.trim(tds.get(1).toPlainTextString());
                    Node a = HtmlUtils.getNodeByTagName(tds.get(3), "a");

                    //===============
                    System.out.println("parentCode:" + parentCode + ",code:" + code + ",name:" + name);
                    StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, name, code, parentCode, "csindex_zz");
                    //TODO stk_data_industry_pe

                    String href = HtmlUtils.getAttribute(a, "href");
                    System.out.println(StringEscapeUtils.unescapeHtml(href));
                    page = HttpUtils.get(StringEscapeUtils.unescapeHtml(href), "utf-8");
                    Node tab = HtmlUtils.getNodeByAttributeContain(page, null, "class", "p_table");
                    List<List<String>> list = HtmlUtils.getListFromTable((TableTag) tab, 0);
                    //System.out.println(list);
                    for (List<String> row : list) {
                        if (row != null && row.size() > 2) {
                            String scode = row.get(1);
                            params.clear();
                            params.add(scode);
                            params.add(indType.getId());
                            params.add(scode);
                            params.add(indType.getId());
                            JdbcUtils.insert(conn, "insert into stk_industry(code,industry) select ?,? from dual where not exists (select 1 from stk_industry where code=? and industry=?)", params);
                        }
                    }

                }
            }catch (Exception e){
		        ExceptionUtils.insertLog(conn, "000000", e);
            }
		}
	}

	//巨潮 pe
	public  void initialIndustryFromCnIndex(Connection conn, int n) throws Exception {
		List params = new ArrayList();
		Date date = ServiceUtils.now;
		String page = null;
		while(true){
			String sdate = ServiceUtils.formatDate(date, ServiceUtils.sf_ymd);
			page = HttpUtils.get("http://www.cnindex.com.cn/syl/"+sdate+"/cninfo_hsls.html", "utf-8");
			System.out.println("http://www.cnindex.com.cn/syl/"+sdate+"/cninfo_hsls.html");
			if(!"404".equals(page))break;
			date = ServiceUtils.addDay(date, -1);
		}
		List<Node> nodes = HtmlUtils.getNodesByText(page, null, "查看");
		for(Node node : nodes){
			String str = StringUtils.substringBetween(((LinkTag)node).getAttribute("onclick"), "showStockPE(", ")");
			String[] ss = str.split(",");
			String url = "http://www.cnindex.com.cn/stockPEs.do?query.category="+StringUtils.substringBetween(ss[1], "'", "'")+"&query.industry="+StringUtils.substringBetween(ss[2], "'", "'")+"&query.date="+StringUtils.substringBetween(ss[3], "'", "'")+"&pageNo=1&pageSize=1000";
			System.out.println(url);
			String tmpPage = HttpUtils.get(url, "utf-8");
			//System.out.println(tmpPage);
			Node tab = HtmlUtils.getTableNodeByText(tmpPage, null, "A股代码");
			if(tab != null){
				List<List<String>> list = HtmlUtils.getListFromTable((TableTag)tab, 0);
				//System.out.println(list);
				for(List<String> stk : list){
					String indName = StringUtils.replace(stk.get(10), "&nbsp;", "");
					String code = StringUtils.replace(stk.get(1), "&nbsp;", "");
					if("000693".equals(code))continue;
					if(code != null && code.length() > 0){
						StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, indName, null, "cnindex");
						params.clear();
						params.add(code);
						params.add(indType.getId());
						params.add(code);
						params.add(indType.getId());
						JdbcUtils.insert(conn, "insert into stk_industry(code,industry) select ?,? from dual where not exists (select 1 from stk_industry where code=? and industry=?)", params);
					}
				}
			}
		}
		Map<String,Integer> indIdMap = new HashMap<String,Integer>();
		while(true){
			String sdate = ServiceUtils.formatDate(date, ServiceUtils.sf_ymd);
			//System.out.println(sdate);
			//沪深
			page = HttpUtils.get("http://www.cnindex.com.cn/syl/"+sdate+"/cninfo_hsls.html", "utf-8");
			date = ServiceUtils.addDay(date, -1);
			n --;
			if(n <= 0)break;
			if("404".equals(page))continue;
			//System.out.println(page);
			TableTag tab = (TableTag)HtmlUtils.getNodeByAttribute(page, null, "class", "table_01_box window0");
			//System.out.println(tab.toHtml());
			List<List<String>> datas = HtmlUtils.getListFromTable(tab);
			//System.out.println(datas);
			for(List<String> data : datas){
				if(data.size() >= 9){
					if("NA".equals(data.get(3)) || "NA".equals(data.get(5)))continue;
					params.clear();
					String indName = data.get(1);
					if(indIdMap.get(indName) == null){
						StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, indName, null, "cnindex");
						indIdMap.put(indType.getName(), indType.getId());
					}
					params.add(indIdMap.get(indName));
					params.add(StringUtils.replace(sdate, "-", ""));
					params.add(data.get(3));
					params.add(data.get(5));
					params.add(indIdMap.get(indName));
					params.add(StringUtils.replace(sdate, "-", ""));
					JdbcUtils.insert(conn, "insert into stk_data_industry_pe (industry_id,pe_date,type,pe,pe_ttm,insert_time) select ?,?,3,?,?,sysdate from dual where not exists (select 1 from stk_data_industry_pe where industry_id=? and pe_date=? and type=3)", params);
				}
			}
			//创业板
			page = HttpUtils.get("http://www.cnindex.com.cn/syl/"+sdate+"/cninfo_cyb.html", "utf-8");
			if(!"404".equals(page)){
				tab = (TableTag)HtmlUtils.getNodeByAttribute(page, null, "class", "table_01_box window0");
				//System.out.println(tab.toHtml());
				datas = HtmlUtils.getListFromTable(tab);
				//System.out.println(datas);
				for(List<String> data : datas){
					if(data.size() >= 9){
						if("NA".equals(data.get(3)) || "NA".equals(data.get(5)))continue;
						params.clear();
						String indName = data.get(1);
						if(indIdMap.get(indName) == null){
							StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, indName, null, "cnindex");
							indIdMap.put(indType.getName(), indType.getId());
						}
						params.add(indIdMap.get(indName));
						params.add(StringUtils.replace(sdate, "-", ""));
						params.add(data.get(3));
						params.add(data.get(5));
						params.add(indIdMap.get(indName));
						params.add(StringUtils.replace(sdate, "-", ""));
						JdbcUtils.insert(conn, "insert into stk_data_industry_pe (industry_id,pe_date,type,pe,pe_ttm,insert_time) select ?,?,2,?,?,sysdate from dual where not exists (select 1 from stk_data_industry_pe where industry_id=? and pe_date=? and type=2)", params);
					}
				}
			}

			//中小板
			page = HttpUtils.get("http://www.cnindex.com.cn/syl/"+sdate+"/cninfo_zxb.html", "utf-8");
			if(!"404".equals(page)){
				tab = (TableTag)HtmlUtils.getNodeByAttribute(page, null, "class", "table_01_box window0");
				//System.out.println(tab.toHtml());
				datas = HtmlUtils.getListFromTable(tab);
				//System.out.println(datas);
				for(List<String> data : datas){
					if(data.size() >= 9){
						if("NA".equals(data.get(3)) || "NA".equals(data.get(5)))continue;
						params.clear();
						String indName = data.get(1);
						if(indIdMap.get(indName) == null){
							StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, indName, null, "cnindex");
							indIdMap.put(indType.getName(), indType.getId());
						}
						params.add(indIdMap.get(indName));
						params.add(StringUtils.replace(sdate, "-", ""));
						params.add(data.get(3));
						params.add(data.get(5));
						params.add(indIdMap.get(indName));
						params.add(StringUtils.replace(sdate, "-", ""));
						JdbcUtils.insert(conn, "insert into stk_data_industry_pe (industry_id,pe_date,type,pe,pe_ttm,insert_time) select ?,?,1,?,?,sysdate from dual where not exists (select 1 from stk_data_industry_pe where industry_id=? and pe_date=? and type=1)", params);
					}
				}
			}
		}
	}

	private  Map<Integer,Integer> industryTypeMap = new HashMap<Integer,Integer>();
	/**
	 * 归类：成长，潜力，反转
	 */
	public  void upateStkFnType(Connection conn, Index index) throws Exception{
		List params = new ArrayList();
		params.add(index.getCode());
		StkIndustry si = JdbcUtils.load(conn, "select * from stk_industry where code=? and industry in (select id from stk_industry_type where source='my_industry_fntype')",params, StkIndustry.class);
		/*params.clear();
		params.add(index.getCode());*/
		JdbcUtils.delete(conn, "delete from stk_industry where code=? and industry in (select id from stk_industry_type where source='my_industry_fntype')", params);
		int type = index.isGrowthOrPotentialOrReversion();
		if(type == Index.FN_Growth){
			if(industryTypeMap.get(Index.FN_Growth) == null){
				StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, "成长股", null, "my_industry_fntype");
				industryTypeMap.put(Index.FN_Growth, indType.getId());
			}
			params.clear();
			params.add(index.getCode());
			params.add(industryTypeMap.get(Index.FN_Growth));
			JdbcUtils.insert(conn, "insert into stk_industry select ?,? from dual", params);
			if(si == null){
				//EmailUtils.send("发现成长股 - "+index.toString(), StkUtils.wrapCodeLink(index.getCode()));
				Text.insert(conn, index.getCode(), "[发现成长股]", Text.SUB_TYPE_FIND_GROWTH);
			}
			return;
		}
		if(type == Index.FN_Potential){
			if(industryTypeMap.get(Index.FN_Potential) == null){
				StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, "潜力股", null, "my_industry_fntype");
				industryTypeMap.put(Index.FN_Potential, indType.getId());
			}
			params.clear();
			params.add(index.getCode());
			params.add(industryTypeMap.get(Index.FN_Potential));
			JdbcUtils.insert(conn, "insert into stk_industry select ?,? from dual", params);
			return;
		}
		if(type == Index.FN_Reversion){
			if(industryTypeMap.get(Index.FN_Reversion) == null){
				StkIndustryType indType = Industry.insertOrLoadIndustryType(conn, "反转股", null, "my_industry_fntype");
				industryTypeMap.put(Index.FN_Reversion, indType.getId());
			}
			params.clear();
			params.add(index.getCode());
			params.add(industryTypeMap.get(Index.FN_Reversion));
			JdbcUtils.insert(conn, "insert into stk_industry select ?,? from dual", params);
			if(si == null){
				//EmailUtils.send("发现反转股 - "+index.toString(), StkUtils.wrapCodeLink(index.getCode()));
				Text.insert(conn, index.getCode(), "[发现反转股]", Text.SUB_TYPE_FIND_REVERSION);
			}
		}
	}


	public  void updateStkF9(Connection conn, Index index) throws Exception {
		String page = HttpUtils.get("http://emweb.securities.eastmoney.com/PC_HSF10/CoreConception/CoreConceptionAjax?code="+(index.getLoc()==1?Index.SH_LOWER:Index.SZ_LOWER+index.getCode()), null, "utf-8");
		//System.out.println(page);
		Map map = JsonUtils.testJson(page);
		if(map != null){
			List<Map> list = (List<Map>)map.get("hxtc");
			StringBuffer sb = new StringBuffer(1024);
			int i = 1;
			if(list != null){
				for(Map m : list){
					sb.append("要点"+ i++ + ": ");
					sb.append(m.get("gjc"));
					sb.append(" ");
					sb.append(m.get("ydnr"));
					sb.append("\r");
				}
			}
			if(sb != null && sb.length() > 0){
				List params = new ArrayList();
				params.add(JdbcUtils.createClob(sb.toString()));
				params.add(index.getCode());
				JdbcUtils.update(conn, "update stk set f9=? where code=?", params);
			}
		}
	}

	//http://www.windin.com/home/stock/html_wind/000002.SH.shtml
	public  void updateStkInfoFromWind(Connection conn,Index index,Date now,List<StkFnType> fnTypes) throws Exception {
		String code = index.getCode();
		String name = index.getName();
		String loc = ServiceUtils.getStkLocation(code);
		String page = HttpUtils.get("http://www.windin.com/home/stock/html_wind/"+code+"."+loc+".shtml", null, "utf8");
		List params = new ArrayList();

		//行业
		Node industryCompare = HtmlUtils.getNodeByAttribute(page, "", "id", "industryCompare");
		if(industryCompare != null && industryCompare.getChildren() != null && industryCompare.getChildren().elementAt(2) != null){
			String industry = StringUtils.substringBetween(HtmlUtils.getNodeByText(industryCompare.toHtml(), null, "所属行业：").toHtml(), "所属行业：", "(");
			params.add(industry);
			com.stk123.model.bo.StkIndustryType type = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='wind'",params, StkIndustryType.class);
			if(type == null){
				params.clear();
				params.add(industry);
				int n = JdbcUtils.insert(conn, "insert into stk_industry_type(id,name,source) values(s_industry_type_id.nextval,?,'wind')", params);
				if(n > 0){
					EmailUtils.send("新行业 - "+ industry, index.toString());
				}
				params.clear();
				params.add(industry);
				type = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='wind'",params, StkIndustryType.class);
			}
			/*params.clear();
			params.add(code);
			JdbcUtils.update(conn, "delete from stk_industry where code=? and industry in (select id from stk_industry_type where source='wind')", params);
*/
			params.clear();
			params.add(code);
			params.add(type.getId());
			params.add(code);
			params.add(type.getId());
			JdbcUtils.insert(conn, "insert into stk_industry(code,industry) select ?,? from dual where not exists (select 1 from stk_industry where code=? and industry=?)", params);
		}

		//盈利预期
		List<Node> redstars = HtmlUtils.getNodesByAttribute(page, "", "src", "http://i1.windin.com/imgserver/common/redstar.gif");
		if(redstars != null && redstars.size() > 0){
			String text = "";
			for(Node redstar : redstars){
				text = text +" "+ StringUtils.trim(redstar.getParent().getNextSibling().toPlainTextString())+":"+StringUtils.substringBetween(((TagNode)redstar.getParent().getNextSibling()).getAttribute("onmouseover"), "event,'", "')");
			}
			params.clear();
			text = ChineseUtils.lengthForOracle(text)>4000?StringUtils.substring(text, 0, 1000):text;
			params.add(text);
			params.add(new Timestamp(now.getTime()));
			params.add(code);
			params.add(text);
			JdbcUtils.update(conn, "update stk set earning_expect=?,earning_expect_date=? where code=? and (earning_expect!=? or earning_expect is null)", params);
		}

		//限售股份解禁
		Node saleLimit = HtmlUtils.getNodeByText(page, "", "限售股份解禁时间表");
		if(saleLimit != null){
			List<List<String>> rows = HtmlUtils.getListFromTable((TableTag)saleLimit.getParent().getParent(),1);
			List<List<String>> sales = new ArrayList<List<String>>();
			for(List<String> row:rows){
				if(row.size() >= 2){
					sales.add(row);
				}
			}
			String json = JsonUtils.getJsonString4JavaPOJO(sales);
			params.clear();
			String text = StringUtils.replace(json, "\\r", "");
			params.add(JdbcUtils.createClob(text));
			params.add(code);
			JdbcUtils.update(conn, "update stk set sale_limit=? where code=?", params);
		}

		//公司简介
		Node profile = HtmlUtils.getNodeByText(page, "", "公司简介");
		if(profile != null){
			String text = profile.getParent().getNextSibling().getFirstChild().toPlainTextString();
			params.clear();
			params.add(JdbcUtils.createClob(text));
			params.add(code);
			JdbcUtils.update(conn, "update stk set company_profile=? where code=?", params);
		}

		//股东人数
		Node nodeHoder = HtmlUtils.getNodeByText(page, "", "A股股东户数");
		if(nodeHoder != null){
			Map<String,Map<String, String>> map = HtmlUtils.getListFromTable((TableTag)nodeHoder.getParent().getParent(), 0, 0);
			for(Map.Entry<String, Map<String, String>> kv: map.entrySet()){
				String fnDate = "20"+StringUtils.replace(kv.getKey(), "-", "");
				String holderNum = StringUtils.replace(kv.getValue().get("A股股东户数"), "--", "0.0");
				params.clear();
				params.add(holderNum);
				params.add(code);
				params.add(fnDate);
				int n = JdbcUtils.update(conn, "update stk_holder set holder=? where code=? and fn_date=?", params);
				if(n == 0){
					params.clear();
					params.add(code);
					params.add(fnDate);
					params.add(holderNum);
					JdbcUtils.insert(conn, "insert into stk_holder(code,fn_date,holder) select ?,?,? from dual", params);

					params.clear();
					params.add(code);
					List<StkHolder> holders = JdbcUtils.list(conn, "select * from stk_holder where code=? order by fn_date desc", params, StkHolder.class);
					if(holders.size() >= 2){
						StkHolder holder1 = holders.get(0);
						StkHolder holder2 = holders.get(1);
						double percentige = (holder1.getHolder()-holder2.getHolder())/holder2.getHolder();
						if( (percentige <= -0.15 && (holder1.getHolder() > 0 && holder1.getHolder() <= 8000)) ||
							(percentige <= -0.20 && (holder1.getHolder() > 8000 && holder1.getHolder() <= 16000))	){
							String info = holder1.getFnDate()+"["+holder1.getHolder()+"]"+"比"+holder2.getFnDate()+"["+holder2.getHolder()+"]"+"减少"+ServiceUtils.number2String(percentige*100,2)+"%";
							/*params.clear();
							params.add(code);
							params.add(info);
							JdbcUtils.insert(conn, "insert into stk_import_info(id,code,type,insert_time,care_flag,info) select s_import_info_id.nextval,?,1,sysdate,1,? from dual", params);
							infos.add("[股东人数减少]"+code+","+name+","+info);*/

							params.clear();
							//params.add(SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_TEXT_ID));
							params.add(index.getCode());
							params.add(JdbcUtils.createClob(info));
							params.add(Text.SUB_TYPE_STK_HOLDER_REDUCE);
							JdbcUtils.insert(conn, "insert into stk_text(id,type,code,code_type,title,text,insert_time,update_time,sub_type) values (s_text_id.nextval,1,?,1,null,?,sysdate,null,?)", params);
						}
					}
				}
			}
		}

		//wind财务数据
		Node fn = HtmlUtils.getNodeByAttribute(page, null, "id", "browseByReport");
		if(fn != null){
			Map<String,Map<String, String>> map = HtmlUtils.getListFromTable((TableTag)fn.getFirstChild(), 0, 0);
			for(Map.Entry<String,Map<String, String>> kv:map.entrySet()){
				for(StkFnType fnType:fnTypes){
					if(fnType.getSource().intValue()==2){
						String fnDate = kv.getKey();
						fnDate = "20"+StringUtils.replace(StringUtils.replace(fnDate, "-", ""), "*", "");
						String fnValue = ServiceUtils.getValueFromMap(kv.getValue(), fnType.getName());
						if("--".equals(fnValue)){
							fnValue = "0.0";
						}

						params.clear();
						params.add(fnValue);
						params.add(code);
						params.add(fnType.getType());
						params.add(fnDate);
						int n = index.updateFnData(params);

						if(n == 0){
							params.clear();
							params.add(code);
							params.add(fnType.getType());
							params.add(fnDate);
							params.add(fnValue);
							params.add(code);
							params.add(fnType.getType());
							params.add(fnDate);
							index.insertFnData(params);

							//对于成长股jlr>=45,要注意
							if(index.FN_JLR.equals(String.valueOf(fnType.getType()))){
								if(index.getNetProfitGrowthAverageValue(12, 5) >= 5
										&& index.getNetProfitGrowthAsNumber(fnDate) >= 45){
									infos.add("[成长股季度净利润增长]"+code+","+index.getName()+","+"季度"+fnDate+"净利润增长"+ServiceUtils.number2String(index.getNetProfitGrowthAsNumber(fnDate), 2));
								}
							}

						}
					}
				}
			}
		}

		//相关地域板块
		Node address = HtmlUtils.getNodeByText(page, "", "相关地域板块");
		if(address != null){
			address = address.getNextSibling();
			if(address != null){
				params.clear();
				params.add(ServiceUtils.getMatchString(address.toPlainTextString(), "[\u4e00-\u9fa5]+"));
				params.add(code);
				JdbcUtils.update(conn, "update stk set address=? where code=?", params);
			}
		}
	}

	//牛基
	public  void initAwesomeFund() throws Exception {
		//一年期牛基排行前20
		//http://vip.stock.finance.sina.com.cn/fund_center/index.html#hbphgpx
		//http://vip.stock.finance.sina.com.cn/fund_center/data/jsonp.php/IO.XSRV2.CallbackList['hLfu5s99aaIUp7D4']/NetValueReturn_Service.NetValueReturnOpen?page=1&num=40&sort=form_year&asc=0&ccode=&type2=2&type3=
		String page = HttpUtils.get("http://vip.stock.finance.sina.com.cn/fund_center/api/jsonp.php/IO.XSRV2.CallbackList"+URLEncoder.encode("['DcBGhAVnTMWhR1GU']","utf-8")+"/NetValueReturn_Service.NetValueReturnOpen?page=1&num=40&sort=one_year&asc=0&ccode=&type2=2&type3=&%5Bobject%20HTMLDivElement%5D=nkyl2","gb2312");
		//System.out.println(page);
		String json = StringUtils.substringBetween(page, "(", ");");
		Map<String, Class> m = new HashMap<String, Class>();
        m.put("data", Map.class);
		Object obj = JsonUtils.getObject4Json(json, Map.class, m);
		Map datas = (Map)obj;
		int i = 1;
		for(Map data : (List<Map>)datas.get("data")){
			awesomefunds.add(new Name2Value(data.get("sname"),1));
			if(++i > 20) break;
		}

		//成立以来牛基排行前20
		page = HttpUtils.get("http://vip.stock.finance.sina.com.cn/fund_center/api/jsonp.php/IO.XSRV2.CallbackList"+URLEncoder.encode("['MH8C82zCRTRcBOjM']","utf-8")+"/NetValueReturn_Service.NetValueReturnOpen?page=1&num=40&sort=form_start&asc=0&ccode=&type2=2&type3=&%5Bobject%20HTMLDivElement%5D=yywui","gb2312");
		//System.out.println(page);
		json = StringUtils.substringBetween(page, "(", ");");
		m = new HashMap<String, Class>();
        m.put("data", Map.class);
		obj = JsonUtils.getObject4Json(json, Map.class, m);
		datas = (Map)obj;
		i = 1;
		for(Map data : (List<Map>)datas.get("data")){
			awesomefunds.add(new Name2Value(data.get("sname"),2));
			if(++i > 20) break;
		}
	}

	public  void initHolderFrom10jqka(Connection conn, Index index)  throws Exception {
		String code = index.getCode();
		List params = new ArrayList();
		String page = HttpUtils.get("http://basic.10jqka.com.cn/"+index.getCode()+"/holder.html", null, "gbk");
		//股东人数
		Node gdrsFlashData = HtmlUtils.getNodeByAttribute(page, null, "id", "gdrsFlashData");
		if(gdrsFlashData != null){
			String holderStr = gdrsFlashData.toPlainTextString();
			//System.out.println(holderStr);
			List<List> holderList = JsonUtils.testJsonArray(holderStr);

			for(List holder : holderList){
				//System.out.println(holder);
				String holderNum = String.valueOf(holder.get(1));
				if("{}".equals(holderNum))continue;
				String fnDate = StringUtils.replace(String.valueOf(holder.get(0)), "-", "");
				params.clear();
				params.add(holderNum);
				params.add(code);
				params.add(fnDate);
				int n = JdbcUtils.update(conn, "update stk_holder set holder=? where code=? and fn_date=?", params);
				if(n == 0){
					params.clear();
					params.add(code);
					params.add(fnDate);
					params.add(holderNum);
					JdbcUtils.insert(conn, "insert into stk_holder(code,fn_date,holder) select ?,?,? from dual", params);

					params.clear();
					params.add(code);
					List<StkHolder> holders = JdbcUtils.list(conn, "select * from stk_holder where code=? order by fn_date desc", params, StkHolder.class);
					if(holders.size() >= 2){
						StkHolder holder1 = holders.get(0);
						StkHolder holder2 = holders.get(1);
						double percentige = (holder1.getHolder()-holder2.getHolder())/holder2.getHolder();
						if( (percentige <= -0.15 && (holder1.getHolder() > 0 && holder1.getHolder() <= 8000)) ||
							(percentige <= -0.20 && (holder1.getHolder() > 8000 && holder1.getHolder() <= 16000))	){
							String info = holder1.getFnDate()+"["+holder1.getHolder()+"]"+"比"+holder2.getFnDate()+"["+holder2.getHolder()+"]"+"减少"+ServiceUtils.number2String(percentige*100,2)+"%";
							/*params.clear();
							params.add(code);
							params.add(info);
							JdbcUtils.insert(conn, "insert into stk_import_info(id,code,type,insert_time,care_flag,info) select s_import_info_id.nextval,?,1,sysdate,1,? from dual", params);
							infos.add("[股东人数减少]"+code+","+name+","+info);*/

							params.clear();
							//params.add(SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_TEXT_ID));
							params.add(index.getCode());
							params.add(JdbcUtils.createClob(info));
							params.add(Text.SUB_TYPE_STK_HOLDER_REDUCE);
							JdbcUtils.insert(conn, "insert into stk_text(id,type,code,code_type,title,text,insert_time,update_time,sub_type) values (s_text_id.nextval,1,?,1,null,?,sysdate,null,?)", params);
						}
					}
				}
			}
		}

	}

	/**
	 * http://basic.10jqka.com.cn/000001/holder.html
	 */
	//流通股东
	public  void initOwnership(Connection conn, Index index)  throws Exception {
		String page = HttpUtils.get("http://basic.10jqka.com.cn/"+index.getCode()+"/holder.html", null, "gbk");
		//System.out.println("initOwnership="+page);
		Node div = HtmlUtils.getNodeByAttribute(page, null, "id", "bd_list1");
		if(div == null)return;
		//System.out.println(div.toHtml());
		List params = new ArrayList();
		for(int i=1;i<=5;i++){
			Node n1 = HtmlUtils.getNodeByAttribute(div, null, "targ", "fher_"+i);
			if(n1 == null)return;
			int newAdd = 0;
			//System.out.println(n1.toHtml());
			Node n2 = HtmlUtils.getNodeByAttribute(div, null, "id", "fher_"+i);
			Node tab = HtmlUtils.getNodeByTagName(n2, "table");
			//System.out.println(tab.toHtml());
			int 质押占其直接持股比 = 0; //有没有‘质押占其直接持股比’这列，有的话为1，没有为0
			if(StringUtils.contains(tab.toHtml(), "质押占其直接持股比")){
				质押占其直接持股比 = 1;
			}
			int 机构成本估算 = 0;
			if(StringUtils.contains(tab.toHtml(), "机构成本估算")){
				机构成本估算 = 1;
			}

			List<List<String>> list = HtmlUtils.getListFromTable2((TableTag)tab, 0);
			//System.out.println(list);
			String date = StringUtils.replace(n1.toPlainTextString(), "-", "");

			for(List<String> data : list){
				if(data.size() < 6)continue;
				//System.out.println(data);
				String orgName = StringUtils.trim(data.get(0));
				params.clear();
				params.add(orgName);
				StkOrganization org = JdbcUtils.load( "select * from stk_organization where name=?",params, StkOrganization.class);
				if(org == null){
					long seq = JdbcUtils.getSequence(conn, "s_organization_id");
					params.clear();
					params.add(seq);
					params.add(orgName);
					JdbcUtils.insert(conn, "insert into stk_organization(id,name) select ?,? from dual", params);

					params.clear();
					params.add(seq);
					org = JdbcUtils.load(conn, "select * from stk_organization where id=?",params, StkOrganization.class);
				}

				if(org != null){
					double holdcount = ServiceUtils.getAmount万(StringUtils.replace(data.get(1), "股", ""));
					params.clear();
					params.add(index.getCode());
					params.add(date);
					params.add(org.getId());
					params.add(holdcount);

					params.add(StringUtils.replace(data.get(3), "%", ""));
					String change = data.get(2);
					if("退出".equals(change)) continue;
					double dChange = 0;
					if("不变".equals(change)){
						dChange = 0;
					}else if("新进".equals(change)){
						newAdd ++;
						dChange = holdcount;
					}else{
						int n = 1;
						String dd = StringUtils.replace((String)data.get(2), "万", "");
						if(StringUtils.indexOf(dd, "亿") > 0){
							n = 10000;
							dd = StringUtils.replace(dd, "亿", "");
						}
						dChange = Double.parseDouble(dd) * n;
					}
					params.add(dChange);
					String rate = (String)data.get(4+质押占其直接持股比+机构成本估算);
					double rateChange = 0;
					if("不变".equals(rate)){
						rateChange = 0;
					}else if("新进".equals(rate)){
						rateChange = 100;
					}else if("限售股流通".equals(rate)){
						rateChange = 100;
					}else{
						rateChange = Double.parseDouble(StringUtils.replace(rate, "%", ""));
					}
					params.add(rateChange);
					params.add(index.getCode());
					params.add(date);
					params.add(org.getId());
					int cnt = JdbcUtils.insert(conn, "insert into stk_ownership(code,fn_date,org_id,stk_num,rate,num_change,num_change_rate) select ?,?,?,?,?,?,? from dual where not exists (select 1 from stk_ownership where code=? and fn_date=? and org_id=?)", params);
					if(cnt > 0){
						List<Name2Value> pairs = Name2Value.containName(awesomefunds, org.getName());
						if(pairs.size() > 0){
							params.clear();
							//params.add(SequenceUtils.getSequenceNextValue(SequenceUtils.SEQ_TEXT_ID));
							params.add(index.getCode());
							String content = org.getName() + " - " + date + " - 买入";
							params.add(JdbcUtils.createClob(content));
							params.add(pairs.get(0).getValue().equals(1)?Text.SUB_TYPE_NIU_FUND_ONE_YEAR:Text.SUB_TYPE_NIU_FUND_ALL_TIME);
							JdbcUtils.insert(conn, "insert into stk_text(id,type,code,code_type,title,text,insert_time,update_time,sub_type) values (s_text_id.nextval,1,?,1,null,?,sysdate,null,?)", params);
						}

						List<Name2Value> ap = Name2Value.containName(awesomePersons, org.getName());
						if(ap.size() > 0){
							params.clear();
							params.add(index.getCode());
							params.add(News.TYPE_3);
							params.add(org.getName());
							JdbcUtils.insert(conn, "insert into stk_import_info(id,code,type,insert_time,info) values (s_import_info_id.nextval,?,?,sysdate,?)", params);
						}
					}
				}
			}
			if(i==1 && newAdd >= 5){
				//EmailUtils.send("发现新增股东数大于5个 - "+index.toString(), StkUtils.wrapCodeLink(index.getCode()));
			}
		}
	}


	//初始化fn data,包括2005年后所有数据
	public  void initFnData(Connection conn,Date now,Index index,List<StkFnType> fnTypes) throws Exception {
		List params = new ArrayList();
		String code = index.getCode();
		if(index.getMarket() == 1){//A Stock
			if(true){
				Map<String, List<StkFnType>> map = new HashMap<String, List<StkFnType>>();
				for(StkFnType fnType : fnTypes){
					if(fnType.getMarket() == 1){
						int isource = fnType.getSource().intValue();
						if(isource == 1 || isource == 2 || isource == 3 || isource == 4){
							String source = fnType.getSource().toString();
							if(map.get(source) == null){
								List<StkFnType> list = new ArrayList<StkFnType>();
								list.add(fnType);
								map.put(source, list);
							}else{
								List<StkFnType> list = map.get(source);
								list.add(fnType);
							}
						}
					}
				}

				for(Map.Entry<String, List<StkFnType>> fnEntry: map.entrySet()){
					int year = ServiceUtils.YEAR;
					String id = null;
					if("1".equals(fnEntry.getKey()) || "2".equals(fnEntry.getKey())){
						id = "BalanceSheetNewTable0";
					}else if("3".equals(fnEntry.getKey()) || "4".equals(fnEntry.getKey())){
						id = "ProfitStatementNewTable0";
					}
					while(true){
						//System.out.println(year);
						String url = null;
						if("1".equals(fnEntry.getKey())){
							url = "http://money.finance.sina.com.cn/corp/go.php/vFD_FinancialGuideLine/stockid/"+index.getCode()+"/ctrl/"+year+"/displaytype/4.phtml";
						}else if("2".equals(fnEntry.getKey())){
							url = "http://money.finance.sina.com.cn/corp/go.php/vFD_BalanceSheet/stockid/"+index.getCode()+"/ctrl/"+year+"/displaytype/4.phtml";
						}else if("3".equals(fnEntry.getKey())){
							url = "http://money.finance.sina.com.cn/corp/go.php/vFD_ProfitStatement/stockid/"+index.getCode()+"/ctrl/"+year+"/displaytype/4.phtml";
						}else if("4".equals(fnEntry.getKey())){
							url = "http://money.finance.sina.com.cn/corp/go.php/vFD_CashFlow/stockid/"+index.getCode()+"/ctrl/"+year+"/displaytype/4.phtml";
						}
						String page = HttpUtils.get(url, "GBK");
						Node table = HtmlUtils.getNodeByAttribute(page, null, "id", id);
						if(table == null)break;
						//System.out.println(table.toHtml());
						Map<String,Map<String, String>> datas = HtmlUtils.getListFromTable((TableTag)table, 1, 0);
						//System.out.println(datas);
						for(StkFnType fnType : fnEntry.getValue()){
							for(Map.Entry<String, Map<String, String>> fn: datas.entrySet()){
								String fnDate = StringUtils.replace(fn.getKey(),"-","");
								Object obj = fn.getValue().get(fnType.getName());
								if(obj == null){
									obj = fn.getValue().get(fnType.getNameAlias());
								}
								String fnValue = null;
								if(obj != null){
									fnValue = StringUtils.replace(StringUtils.replace(String.valueOf(obj),",",""),"--","");
								}
								if(fnValue == null || fnValue.length() == 0)continue;
								if(!"1".equals(fnEntry.getKey())){
									fnValue = String.valueOf(Double.parseDouble(fnValue) * 10000);
								}
								params.clear();
								params.add(fnValue);
								params.add(code);
								params.add(fnType.getType());
								params.add(fnDate);
								int n = index.updateFnData(params);

								if(n == 0){
									params.clear();
									params.add(code);
									params.add(fnType.getType());
									params.add(fnDate);
									params.add(fnValue);
									params.add(code);
									params.add(fnType.getType());
									params.add(fnDate);
									index.insertFnData(params);
								}
							}
						}
						year --;
						if(year < 2013)break;
					}
				}
			}
		}
	}

	public static void initFnDataTTM(Connection conn,Date now,Index index,List<StkFnType> fnTypes) throws Exception {
	    log.info("initFnDataTTM, code: {}", index.getCode());
		initFnDataTTM(conn, now, index, fnTypes, "quarter");
	}

	//update最近4个季度fn data
	public static void initFnDataTTM(Connection conn,Date now,Index index,List<StkFnType> fnTypes, String type) throws Exception {
		List params = new ArrayList();
		String code = index.getCode();
		if(index.getMarket() == 1){//A Stock
			if(true){
				Map<String, List<StkFnType>> map = new HashMap<String, List<StkFnType>>();
				for(StkFnType fnType : fnTypes){
					if(fnType.getMarket() == 1){
						int isource = fnType.getSource().intValue();
						if(isource == 1 || isource == 2 || isource == 3 || isource == 4){
							String source = fnType.getSource().toString();
							if(map.get(source) == null){
								List<StkFnType> list = new ArrayList<StkFnType>();
								list.add(fnType);
								map.put(source, list);
							}else{
								List<StkFnType> list = map.get(source);
								list.add(fnType);
							}
						}
					}
				}

				for(Map.Entry<String, List<StkFnType>> fnEntry: map.entrySet()){
					String id = null;
					if("1".equals(fnEntry.getKey()) || "2".equals(fnEntry.getKey())){
						id = "BalanceSheetNewTable0";
					}else if("3".equals(fnEntry.getKey()) || "4".equals(fnEntry.getKey())){
						id = "ProfitStatementNewTable0";
					}
					String url = null;
					if("1".equals(fnEntry.getKey())){
						url = "http://money.finance.sina.com.cn/corp/go.php/vFD_FinancialGuideLine/stockid/"+index.getCode()+"/displaytype/4.phtml";
					}else if("2".equals(fnEntry.getKey())){
						url = "http://money.finance.sina.com.cn/corp/go.php/vFD_BalanceSheet/stockid/"+index.getCode()+"/ctrl/part/displaytype/4.phtml";
					}else if("3".equals(fnEntry.getKey())){
						url = "http://money.finance.sina.com.cn/corp/go.php/vFD_ProfitStatement/stockid/"+index.getCode()+"/ctrl/part/displaytype/4.phtml";
					}else if("4".equals(fnEntry.getKey())){
						url = "http://money.finance.sina.com.cn/corp/go.php/vFD_CashFlow/stockid/"+index.getCode()+"/ctrl/part/displaytype/4.phtml";
					}
					String page = HttpUtils.get(url, "GBK");
					Node table = HtmlUtils.getNodeByAttribute(page, null, "id", id);
					//System.out.println(table.toHtml());
					if(table == null)break;
					Map<String,Map<String, String>> datas = HtmlUtils.getListFromTable((TableTag)table, 1, 0);
					for(StkFnType fnType : fnEntry.getValue()){
						for(Map.Entry<String, Map<String, String>> fn: datas.entrySet()){
							String fnDate = StringUtils.replace(fn.getKey(),"-","");
							Object obj = fn.getValue().get(fnType.getNameAlias());
							if(obj == null){
								obj = fn.getValue().get(fnType.getName());
							}
							String fnValue = null;
							if(obj != null){
								fnValue = StringUtils.replace(StringUtils.replace(String.valueOf(obj),",",""),"--","");
							}
							if(fnValue == null || fnValue.length() == 0)continue;
							if(!"1".equals(fnEntry.getKey())){
								fnValue = String.valueOf(Double.parseDouble(fnValue) * 10000);
							}
							params.clear();
							params.add(fnValue);
							params.add(code);
							params.add(fnType.getType());
							params.add(fnDate);
							int n = index.updateFnData(params);

							if(n == 0){
								params.clear();
								params.add(code);
								params.add(fnType.getType());
								params.add(fnDate);
								params.add(fnValue);
								params.add(code);
								params.add(fnType.getType());
								params.add(fnDate);
								index.insertFnData(params);

								//主营收入增长率连续3个季度大于净利润增加率40个点
								if(CommonConstant.FN_TYPE_CN_ZYSRZZL.equals(fnType.getType().toString())){
									StkFnDataCust fn1 = index.getFnDataLastestByType(CommonConstant.FN_TYPE_CN_ZYSRZZL);
									StkFnDataCust fn2 = index.getFnDataLastestByType(CommonConstant.FN_TYPE_CN_JLRZZL);
									if(fn1 != null && fn2 != null && fn1.getFnValue() != null && fn2.getFnValue() != null
											&&  fn1.getFnValue() > 40
											&& fn1.getFnValue() > 0 && fn1.getFnValue() - fn2.getFnValue() >= 40){
										fn1 = fn1.getBefore();
										fn2 = fn2.getBefore();
										String fd = fn1.getFnDate();
										if(fn1 != null && fn2 != null && fn1.getFnValue() != null && fn2.getFnValue() != null
												&&  fn1.getFnValue() > 40
												&& fn1.getFnValue() > 0 && fn1.getFnValue() - fn2.getFnValue() >= 30){
											fn1 = fn1.getBefore();
											fn2 = fn2.getBefore();
											if(fn1 != null && fn2 != null && fn1.getFnValue() != null && fn2.getFnValue() != null
													&&  fn1.getFnValue() > 40
													&& fn1.getFnValue() > 0 && fn1.getFnValue() - fn2.getFnValue() >= 20){
												params.clear();
												params.add(index.getCode());
												params.add(News.TYPE_20);
												params.add("[主营远大于利润]["+fd+"] 主营收入增长率连续3个季度大于净利润增加率40个点");
												JdbcUtils.insert(conn, "insert into stk_import_info(id,code,type,insert_time,info) values (s_import_info_id.nextval,?,?,sysdate,?)", params);
												//EmailUtils.send("发现 - 主营远大于利润", index.getCode()+","+index.getName());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}else if(index.getMarket() == 2){//US Stock
			Map<String, Map<String,StkFnType>> map = new HashMap<String, Map<String,StkFnType>>();
			for(StkFnType fnType : fnTypes){
				if(fnType.getMarket() == 2){
					int isource = fnType.getSource().intValue();
					if(isource == 10 || isource == 11 || isource == 12){
						String source = fnType.getSource().toString();
						if(map.get(source) == null){
							Map<String,StkFnType> list = new HashMap<String,StkFnType>();
							list.put(fnType.getName(),fnType);
							map.put(source, list);
						}else{
							Map<String,StkFnType> list = map.get(source);
							list.put(fnType.getName(),fnType);
						}
					}
				}
			}
			for(Map.Entry<String, Map<String,StkFnType>> fnEntry: map.entrySet()){
				String url = null;
				if("10".equals(fnEntry.getKey())){
					url = "http://f10.eastmoney.com/usf10/zhsyb_4.aspx?type=&code="+index.getCode()+"&s=";
				}else if("11".equals(fnEntry.getKey())){
					url = "http://f10.eastmoney.com/usf10/zcfzb_4.aspx?type=&code="+index.getCode()+"&s=";
				}else if("12".equals(fnEntry.getKey())){
					url = "http://f10.eastmoney.com/usf10/xjllb_4.aspx?type=&code="+index.getCode()+"&s=";
				}
				String page = HttpUtils.get(url, "gb2312");
				Node tab = HtmlUtils.getNodeByAttribute(page, null, "class", "c_table txtr");
				if(tab == null)continue;
				List<List<String>> list = HtmlUtils.getListFromTable2((TableTag)tab, -1);

				List<String> fnDates = null;
				for(List<String> row : list){
					if(row.size() > 0){
						String key = row.get(0);
						if("截止日期".equals(key)){
							fnDates = row;
							break;
						}
					}
				}
				for(List<String> row : list){
					if(row.size() > 0){
						String key = row.get(0);
						if("原始币种".equals(key)){
							if(row.size() > 1){
								params.clear();
								String currency = row.get(1);
								if("美元".equals(row.get(1))){
									currency = "USD";
								}else if("人民币".equals(row.get(1))){
									currency = "CNY";
								}else if("欧元".equals(row.get(1))){
									currency = "EUR";
								}
								params.add(currency);
								params.add(code);
								JdbcUtils.update(conn, "update stk set fn_currency=? where code=?", params);
								break;
							}
						}
					}
				}
				if(fnDates == null)continue;

				for(List<String> row : list){
					if(row.size() > 0){
						String key = row.get(0);
						key = StringUtils.replace(key, "&nbsp;", "");
						if(fnEntry.getValue().get(key) != null){
							for(int j=1;j<row.size();j++){
								Integer fnType = fnEntry.getValue().get(key).getType();
								String fnDate = StringUtils.replace(fnDates.get(j), "-", "");
								String fnValue = StringUtils.replace(row.get(j), ",", "");
								if("-".equals(fnValue)){
									fnValue = null;
								}
								params.clear();
								params.add(fnValue);
								params.add(code);
								params.add(fnType);
								params.add(fnDate);
								int n = index.updateFnData(params);

								if(n == 0){
									params.clear();
									params.add(code);
									params.add(fnType);
									params.add(fnDate);
									params.add(fnValue);
									params.add(code);
									params.add(fnType);
									params.add(fnDate);
									index.insertFnData(params);
								}
							}
						}
					}
				}


				/*
				for(StkFnType fnType : fnEntry.getValue()){
					for(Map.Entry<String, Map<String, String>> fn: datas.entrySet()){
						String fnDate = StringUtils.replace(StringUtils.replace(fn.getKey(), "至", ""),"-","");
						String fnValue = fn.getValue().get(fnType.getName());
						params.clear();
						params.add(fnValue);
						params.add(code);
						params.add(fnType.getType());
						params.add(fnDate);
						int n = index.updateFnData(params);

						if(n == 0){
							params.clear();
							params.add(code);
							params.add(fnType.getType());
							params.add(fnDate);
							params.add(fnValue);
							params.add(code);
							params.add(fnType.getType());
							params.add(fnDate);
							index.insertFnData(params);
						}
					}
				}*/
			}
			if("annual".equals(type))return;

			//https://query1.finance.yahoo.com/v10/finance/quoteSummary/IRBT?formatted=true&lang=en-US&region=US&modules=defaultKeyStatistics%2CfinancialData%2CsummaryDetail%2CcalendarEvents&corsDomain=finance.yahoo.com
			String page = HttpUtils.get("https://query1.finance.yahoo.com/v10/finance/quoteSummary/"+index.getCode()+"?formatted=true&lang=en-US&region=US&modules=defaultKeyStatistics%2CfinancialData%2CsummaryDetail%2CcalendarEvents&corsDomain=finance.yahoo.com", null, "utf-8");
			//{"quoteSummary":{"result":[{"summaryDetail":{"maxAge":1,"priceHint":{"raw":2,"fmt":"2","longFmt":"2"},"previousClose":{"raw":99.23,"fmt":"99.23"},"open":{"raw":98.98,"fmt":"98.98"},"dayLow":{"raw":97.8201,"fmt":"97.82"},"dayHigh":{"raw":100.48,"fmt":"100.48"},"regularMarketPreviousClose":{"raw":99.23,"fmt":"99.23"},"regularMarketOpen":{"raw":98.98,"fmt":"98.98"},"regularMarketDayLow":{"raw":97.8201,"fmt":"97.82"},"regularMarketDayHigh":{"raw":100.48,"fmt":"100.48"},"dividendRate":{},"dividendYield":{},"exDividendDate":{},"payoutRatio":{},"fiveYearAvgDividendYield":{},"beta":{"raw":0.999804,"fmt":"1.00"},"trailingPE":{"raw":51.37789,"fmt":"51.38"},"forwardPE":{"raw":39.035156,"fmt":"39.04"},"volume":{"raw":598443,"fmt":"598.44k","longFmt":"598,443"},"regularMarketVolume":{"raw":598443,"fmt":"598.44k","longFmt":"598,443"},"averageVolume":{"raw":629137,"fmt":"629.14k","longFmt":"629,137"},"averageVolume10days":{"raw":834914,"fmt":"834.91k","longFmt":"834,914"},"averageDailyVolume10Day":{"raw":834914,"fmt":"834.91k","longFmt":"834,914"},"bid":{"raw":96.81,"fmt":"96.81"},"ask":{"raw":0.0,"fmt":"0.00"},"bidSize":{"raw":200,"fmt":"200","longFmt":"200"},"askSize":{"raw":0,"fmt":null,"longFmt":"0"},"marketCap":{"raw":2739790848,"fmt":"2.74B","longFmt":"2,739,790,848"},"yield":{},"ytdReturn":{},"totalAssets":{},"expireDate":{},"strikePrice":{},"openInterest":{},"fiftyTwoWeekLow":{"raw":33.9,"fmt":"33.90"},"fiftyTwoWeekHigh":{"raw":100.48,"fmt":"100.48"},"priceToSalesTrailing12Months":{"raw":3.9237008,"fmt":"3.92"},"fiftyDayAverage":{"raw":87.47857,"fmt":"87.48"},"twoHundredDayAverage":{"raw":66.70993,"fmt":"66.71"},"trailingAnnualDividendRate":{},"trailingAnnualDividendYield":{},"navPrice":{}},"defaultKeyStatistics":{"maxAge":1,"enterpriseValue":{},"forwardPE":{"raw":39.035156,"fmt":"39.04"},"profitMargins":{"raw":0.07786,"fmt":"7.79%"},"floatShares":{"raw":26641453,"fmt":"26.64M","longFmt":"26,641,453"},"sharesOutstanding":{"raw":27417100,"fmt":"27.42M","longFmt":"27,417,100"},"sharesShort":{"raw":4101930,"fmt":"4.1M","longFmt":"4,101,930"},"sharesShortPriorMonth":{"raw":3965860,"fmt":"3.97M","longFmt":"3,965,860"},"heldPercentInsiders":{"raw":0.0322,"fmt":"3.22%"},"heldPercentInstitutions":{"raw":0.71199995,"fmt":"71.20%"},"shortRatio":{"raw":10.44,"fmt":"10.44"},"shortPercentOfFloat":{"raw":0.141524,"fmt":"14.15%"},"beta":{"raw":0.999804,"fmt":"1"},"morningStarOverallRating":{},"morningStarRiskRating":{},"category":null,"bookValue":{"raw":14.902,"fmt":"14.90"},"priceToBook":{"raw":6.705811,"fmt":"6.71"},"annualReportExpenseRatio":{},"ytdReturn":{},"beta3Year":{},"totalAssets":{},"yield":{},"fundFamily":null,"fundInceptionDate":{},"legalType":null,"threeYearAverageReturn":{},"fiveYearAverageReturn":{},"priceToSalesTrailing12Months":{},"lastFiscalYearEnd":{"raw":1483142400,"fmt":"2016-12-31"},"nextFiscalYearEnd":{"raw":1546214400,"fmt":"2018-12-31"},"mostRecentQuarter":{"raw":1491004800,"fmt":"2017-04-01"},"earningsQuarterlyGrowth":{"raw":3.16,"fmt":"316.00%"},"revenueQuarterlyGrowth":{},"netIncomeToCommon":{"raw":54366000,"fmt":"54.37M","longFmt":"54,366,000"},"trailingEps":{"raw":1.945,"fmt":"1.95"},"forwardEps":{"raw":2.56,"fmt":"2.56"},"pegRatio":{"raw":3.19,"fmt":"3.19"},"lastSplitFactor":null,"lastSplitDate":{},"enterpriseToRevenue":{},"enterpriseToEbitda":{},"52WeekChange":{"raw":1.6376929,"fmt":"163.77%"},"SandP52WeekChange":{"raw":0.16081035,"fmt":"16.08%"},"lastDividendValue":{},"lastCapGain":{},"annualHoldingsTurnover":{}},"calendarEvents":{"maxAge":1,"earnings":{"earningsDate":[{"raw":1500940800,"fmt":"2017-07-25"}],"earningsAverage":{"raw":-0.28,"fmt":"-0.28"},"earningsLow":{"raw":-0.35,"fmt":"-0.35"},"earningsHigh":{"raw":0.0,"fmt":"0.00"},"revenueAverage":{"raw":174660000,"fmt":"174.66M","longFmt":"174,660,000"},"revenueLow":{"raw":165480000,"fmt":"165.48M","longFmt":"165,480,000"},"revenueHigh":{"raw":185000000,"fmt":"185M","longFmt":"185,000,000"}},"exDividendDate":{},"dividendDate":{}},"financialData":{"maxAge":86400,"currentPrice":{"raw":99.93,"fmt":"99.93"},"targetHighPrice":{"raw":90.0,"fmt":"90.00"},"targetLowPrice":{"raw":57.0,"fmt":"57.00"},"targetMeanPrice":{"raw":74.4,"fmt":"74.40"},"targetMedianPrice":{"raw":72.0,"fmt":"72.00"},"recommendationMean":{"raw":2.8,"fmt":"2.80"},"recommendationKey":"hold","numberOfAnalystOpinions":{"raw":5,"fmt":"5","longFmt":"5"},"totalCash":{"raw":275670016,"fmt":"275.67M","longFmt":"275,670,016"},"totalCashPerShare":{"raw":10.055,"fmt":"10.06"},"ebitda":{"raw":86783000,"fmt":"86.78M","longFmt":"86,783,000"},"totalDebt":{"raw":0,"fmt":null,"longFmt":"0"},"quickRatio":{"raw":3.233,"fmt":"3.23"},"currentRatio":{"raw":3.877,"fmt":"3.88"},"totalRevenue":{"raw":698267008,"fmt":"698.27M","longFmt":"698,267,008"},"debtToEquity":{},"revenuePerShare":{"raw":25.603,"fmt":"25.60"},"returnOnAssets":{"raw":0.09137,"fmt":"9.14%"},"returnOnEquity":{"raw":0.13234,"fmt":"13.23%"},"grossProfits":{"raw":319315000,"fmt":"319.31M","longFmt":"319,315,000"},"freeCashflow":{"raw":94818752,"fmt":"94.82M","longFmt":"94,818,752"},"operatingCashflow":{"raw":94560000,"fmt":"94.56M","longFmt":"94,560,000"},"earningsGrowth":{"raw":3.462,"fmt":"346.20%"},"revenueGrowth":{"raw":0.288,"fmt":"28.80%"},"grossMargins":{"raw":0.49365002,"fmt":"49.37%"},"ebitdaMargins":{"raw":0.124280006,"fmt":"12.43%"},"operatingMargins":{"raw":0.10521,"fmt":"10.52%"},"profitMargins":{"raw":0.07786,"fmt":"7.79%"}}}],"error":null}}
			//System.out.println(page);
			if(page == null || !StringUtils.startsWith(page, "{"))return;
			Map summary = JsonUtils.testJson(page);
			Map quote = (Map)summary.get("quoteSummary");
			Map result = (Map)((List)quote.get("result")).get(0);

			Map defaultKeyStatistics = (Map)result.get("defaultKeyStatistics");
			if(defaultKeyStatistics != null && defaultKeyStatistics.get("lastFiscalYearEnd") != null){
				String lastFiscalYearEnd = String.valueOf(((Map)defaultKeyStatistics.get("lastFiscalYearEnd")).get("fmt"));
				String mostRecentQuarter = String.valueOf(((Map)defaultKeyStatistics.get("mostRecentQuarter")).get("fmt"));
				String yearEnd = StringUtils.replace(lastFiscalYearEnd, "-", "");
				params.clear();
				params.add(index.getCode());
				params.add(yearEnd);
				JdbcUtils.update(conn, "update stk_fn_data_us set fiscal_year_ends=1 where code=? and fn_date=?", params);

				//跟新总股本
				/*String sharesOutstanding = String.valueOf(((Map)defaultKeyStatistics.get("sharesOutstanding")).get("raw"));
				params.clear();
				params.add(Double.parseDouble(sharesOutstanding)/10000);
				params.add(index.getCode());
				JdbcUtils.update(conn, "update stk set total_capital=? where code=?", params);*/

				String fnDate = StringUtils.replace(mostRecentQuarter, "-", "");

				for(StkFnType fnType : fnTypes){
					if(fnType.getMarket() == 2){
						int isource = fnType.getSource().intValue();
						if(isource == 100){
							Map category = (Map)result.get(fnType.getNameAlias());

							Object fnValueObj = ((Map)category.get(fnType.getName())).get("fmt");
							if(fnValueObj == null)continue;
							String fnValue = String.valueOf(fnValueObj);
							fnValue = StringUtils.replace(fnValue, "%", "");
							fnValue = StringUtils.replace(fnValue, "M", "");
							fnValue = StringUtils.replace(fnValue, ",", "");
							if(fnValue.indexOf("B") > 0){
								fnValue = StringUtils.replace(fnValue, "B", "");
								fnValue = String.valueOf(Double.parseDouble(fnValue)*1000);
							}else if(StringUtils.indexOfIgnoreCase(fnValue, "K") > 0){
								fnValue = StringUtils.replace(fnValue, "K", "");
								fnValue = StringUtils.replace(fnValue, "k", "");
								fnValue = String.valueOf(Double.parseDouble(fnValue)/1000);
							}

							params.clear();
							params.add(fnValue);
							params.add(code);
							params.add(fnType.getType());
							params.add(fnDate);
							int n = index.updateFnData(params);

							if(n == 0){
								params.clear();
								params.add(code);
								params.add(fnType.getType());
								params.add(fnDate);
								params.add(fnValue);
								params.add(code);
								params.add(fnType.getType());
								params.add(fnDate);
								index.insertFnData(params);
							}
						}
					}
				}
			}



		}
	}

	private  String getAllStocksCode(){
		StringBuffer sb = new StringBuffer(1024);
		for(int i=0;i<=999;i++){
			sb.append("sh600"+StringUtils.leftPad(""+i, 3, '0')+",");
		}
		for(int i=0;i<=999;i++){
			sb.append("sh601"+StringUtils.leftPad(""+i, 3, '0')+",");
		}
		for(int i=0;i<=999;i++){
			sb.append("sh603"+StringUtils.leftPad(""+i, 3, '0')+",");
		}
        for(int i=0;i<=999;i++){
            sb.append("sh605"+StringUtils.leftPad(""+i, 3, '0')+",");
        }
		for(int i=0;i<=999;i++){
			sb.append("sh688"+StringUtils.leftPad(""+i, 3, '0')+",");
		}
		for(int i=0;i<=999;i++){
			sb.append("sz000"+StringUtils.leftPad(""+i, 3, '0')+",");
		}
		for(int i=0;i<=999;i++){
			sb.append("sz001"+StringUtils.leftPad(""+i, 3, '0')+",");
		}
		for(int i=0;i<=999;i++){
			sb.append("sz002"+StringUtils.leftPad(""+i, 3, '0')+",");
		}
		for(int i=0;i<=999;i++){
			sb.append("sz300"+StringUtils.leftPad(""+i, 3, '0')+",");
		}
		return sb.toString();
	}


	public  void initUStkFromSina(Connection conn, boolean flag) throws Exception {
		int pageNum = 1;
		List params = new ArrayList();
		if(flag){
			String page = HttpUtils.get("http://stock.finance.sina.com.cn/usstock/api/jsonp.php/var%20category=/US_CategoryService.getCategory","GBK");
			Map<String, Class> m = new HashMap<String, Class>();
	        m.put("child", Map.class);
			List<Map> usList = (List)JsonUtils.getList4Json(StringUtils.substringBetween(page, "(", ");"), Map.class, m);
			//System.out.println(usList);
			for(Map map2 : usList){
				//System.out.println("category_cn="+map2.get("category_cn"));
				String indType = String.valueOf(map2.get("category_cn"));
				if(indType == null || indType.length() == 0)continue;
				params.clear();
				params.add(indType);
				StkIndustryType parentType = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='sina_meigu'",params, StkIndustryType.class);
				if(parentType == null){
					params.clear();
					params.add(indType);
					params.add(map2.get("category"));
					JdbcUtils.insert(conn, "insert into stk_industry_type(id,name,source,us_name) values(s_industry_type_id.nextval,?,'sina_meigu',?)", params);
					params.clear();
					params.add(indType);
					parentType = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='sina_meigu'",params, StkIndustryType.class);
				}

				List<Map> children = (List)map2.get("child");
				for(Map child : children){
					//System.out.println(child.get("id")+","+child.get("category_cn")+","+child.get("category"));
					indType = String.valueOf(child.get("category_cn"));
					params.clear();
					params.add(indType);
					if(indType == null || indType.length() == 0)continue;
					StkIndustryType childType = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='sina_meigu'",params, StkIndustryType.class);
					if(childType == null){
						params.clear();
						params.add(indType);
						params.add(parentType.getId());
						params.add(child.get("category"));
						JdbcUtils.insert(conn, "insert into stk_industry_type(id,name,source,parent_id,us_name) values(s_industry_type_id.nextval,?,'sina_meigu',?,?)", params);
						params.clear();
						params.add(indType);
						childType = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='sina_meigu'",params, StkIndustryType.class);
					}
					//http://stock.finance.sina.com.cn/usstock/api/jsonp.php/IO.XSRV2.CallbackList['nNKQkWkifBt9Rqml']/US_CategoryService.getList?page=1&num=60&sort=&asc=0&market=&id=2
					pageNum = 0;
					while(true){
						//System.out.println("page num:"+pageNum);
						String url = "http://stock.finance.sina.com.cn/usstock/api/jsonp.php/IO.XSRV2.CallbackList"+URLEncoder.encode("['f0j3ltzVzdo2Fo4p']","utf-8")+"/US_CategoryService.getList?page="+pageNum+"&num=60&sort=&asc=0&market=&id="+child.get("id");
						page = HttpUtils.get(url, null, "GBK");
						m = new HashMap<String, Class>();
				        m.put("data", Map.class);
						SinaMeiGu meiGu = (SinaMeiGu)JsonUtils.getObject4Json(StringUtils.substringBetween(page, "((", "));"), SinaMeiGu.class, m);
						if(meiGu == null || meiGu.getData() == null){
							break;
						}
						for(Map map : meiGu.getData()){
							String code = (String)map.get("symbol");
							params.clear();
							params.add(code);
							String sql = "select code from stk where code=?";
							Stk stk = JdbcUtils.load(conn, sql,params, Stk.class);
							if(stk == null){
								params.clear();
								params.add(code);
								params.add(map.get("cname"));
								sql = "insert into stk(code,name,insert_time,market) values(?,?,sysdate,2)";
								JdbcUtils.insert(conn, sql, params);
								//Index index = new Index(conn, code);
								//index.initKLines();
							}else{
								params.clear();
								params.add(code);
								JdbcUtils.update(conn, "update stk set status=0 where code=?", params);
							}

							params.clear();
							params.add(code);
							params.add(childType.getId());
							params.add(code);
							params.add(childType.getId());
							JdbcUtils.insert(conn, "insert into stk_industry(code,industry) select ?,? from dual where not exists (select 1 from stk_industry where code=? and industry=?)", params);
						}
						pageNum ++;
					}
				}
			}


		}else{

			while(true){
				//System.out.println("page num:"+pageNum);
				String page = HttpUtils.get("http://stock.finance.sina.com.cn/usstock/api/jsonp.php/IO.XSRV2.CallbackList"+URLEncoder.encode("['f0j3ltzVzdo2Fo4p']","utf-8")+"/US_CategoryService.getList?page="+pageNum+"&num=60&sort=&asc=0&market=&id=", null, "GBK");
				//System.out.println(page);
				Map<String, Class> m = new HashMap<String, Class>();
		        m.put("data", Map.class);
				SinaMeiGu meiGu = (SinaMeiGu)JsonUtils.getObject4Json(StringUtils.substringBetween(page, "(", ");"), SinaMeiGu.class, m);
				if(meiGu.getData() == null){
					break;
				}
				for(Map map : meiGu.getData()){
					String code = (String)map.get("symbol");
					params.clear();
					params.add(code);
					String sql = "select code from stk where code=?";
					Stk stk = JdbcUtils.load(conn, sql,params, Stk.class);
					if(stk == null){
						params.clear();
						params.add(code);
						params.add(map.get("cname"));
						sql = "insert into stk(code,name,insert_time,market) values(?,?,sysdate,2)";
						JdbcUtils.insert(conn, sql, params);
						System.out.println("new stk:"+code);
					}
					String indType = String.valueOf(map.get("category"));
					if(indType != null && !"null".equals(indType) && indType.length() != 0){
						params.clear();
						params.add(indType);
						StkIndustryType type = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='sina_meigu'",params, StkIndustryType.class);
						if(type == null){
							params.clear();
							params.add(indType);
							JdbcUtils.insert(conn, "insert into stk_industry_type(id,name,source) values(s_industry_type_id.nextval,?,'sina_meigu')", params);
							params.clear();
							params.add(indType);
							type = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source='sina_meigu'",params, StkIndustryType.class);
						}

						if(type != null){
							params.clear();
							params.add(code);
							params.add(type.getId());
							params.add(code);
							params.add(type.getId());
							JdbcUtils.insert(conn, "insert into stk_industry(code,industry) select ?,? from dual where not exists (select 1 from stk_industry where code=? and industry=?)", params);
						}
					}
				}
				pageNum ++;
			}
		}
	}

	private  void initUStkFromXueQiu(Connection conn) throws Exception {
		String page = HttpUtils.get("http://xueqiu.com/hq/US", "GBK");
		String text = "{"+StringUtils.substringBetween(page, "stockList.searchResult={", "};")+"}";
		//System.out.println(text);
		Map<String, Class> m = new HashMap<String, Class>();
        m.put("industries", Map.class);
		Map map = (Map)JsonUtils.getObject4Json(text, Map.class, m);
		List<Map> list = (List)map.get("industries");
		List params = new ArrayList();
		int flag = 0;
		for(Map industry : list){
			System.out.println(industry);

			StkIndustryType parentIndustry = null;
			String parentIndustryName = String.valueOf(industry.get("plate"));
			if(parentIndustryName != null && parentIndustryName.length() > 0){
				parentIndustry = Industry.insertOrLoadIndustryType(conn,parentIndustryName, null, "xueqiu_meigu");
			}
			/*if("纺织品/成衣".equals(industry.get("industry"))){
				flag = 1;
			}
			if(flag == 0){
				continue;
			}*/

			String industryName = String.valueOf(industry.get("industry"));
			String industryParentName = parentIndustry==null?null:parentIndustry.getName();
			StkIndustryType type = Industry.insertOrLoadIndustryType(conn, industryName, industryParentName, "xueqiu_meigu");

			page = HttpUtils.get("http://xueqiu.com/hq/US/"+URLEncoder.encode(industryName,"utf-8"), "GBK");
			//System.out.println(page);
			text = "{"+StringUtils.substringBetween(page, "stockList.searchResult={", "};")+"}";
			Map<String, Class> m2 = new HashMap<String, Class>();
	        m2.put("stocks", Map.class);
			Map map2 = (Map)JsonUtils.getObject4Json(text, Map.class, m2);
			Map stks = (Map)map2.get("stocks");
			for(Map<String, String> stk :(List<Map>)stks.get("stocks")){
				//System.out.println(stk);
				String code = stk.get("code");
				/*if(code.equals("MFC") || code.equals("MFI") || code.equals("MFLR") || code.equals("MFNC")
						|| code.equals("MFG") || code.equals("MFE") || code.equals("MFA")
						|| code.equals("MFRI") || code.equals("MFLX")||code.equals("MFB")){
					continue;
				}*/
				String name = stk.get("name");
				page = HttpUtils.get("http://xueqiu.com/S/"+code, null, "utf-8");
				Node profileNode = HtmlUtils.getNodeByAttribute(page, null, "class", "companyInfo detailContent");
				String profile = "";
				if(profileNode != null){
					profile = StringUtils.replace(profileNode.toPlainTextString(), "收起", "");
				}
				//System.out.println(profile.toHtml());
				Node node = HtmlUtils.getNodeByAttribute(page, "", "class", "stockQuote");
				String str = node.toHtml();
				//System.out.println(str);
				Node nodeCap = HtmlUtils.getNodeByText(str, "", "总股本：");
				String totalCap = nodeCap!=null?nodeCap.getLastChild().toPlainTextString():"0";
				totalCap = StringUtils.replace(totalCap, "万", "").replaceAll(",", "");
				int n = 1;
				if(StringUtils.indexOf(totalCap, "亿") > 0){
					n = 10000;
					totalCap = StringUtils.replace(totalCap, "亿", "");
				}
				double totalCapital = Double.parseDouble(totalCap)*n;
				System.out.println(code+","+name+","+totalCapital+","+profile);
				createStk(conn, code, name, String.valueOf(totalCapital), profile);

				params.clear();
				params.add(code);
				params.add(type.getId());
				StkIndustry stkInd = JdbcUtils.load(conn, "select * from stk_industry where code=? and industry=?",params, StkIndustry.class);
				if(stkInd == null){
					params.clear();
					params.add(code);
					params.add(type.getId());
					JdbcUtils.insert(conn, "insert into stk_industry(code,industry) select ?,? from dual", params);
				}

				//break;
			}
		}
	}

	public  void initUStkFromEasymoney(Connection conn) throws Exception {
		String page = HttpUtils.get("http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=C.__28CHINA&sty=MPICTTA&sortType=C&sortRule=-1&page=1&pageSize=200&js=var%20quote_123%3d"+URLEncoder.encode("{rank:[(x)],pages:(pc)}","utf-8")+"&token=44c9d251add88e27b65ed86506f6e5da&jsName=quote_123&_g=0.051958891308406585", null);
		String strStks = StringUtils.substringBetween(page, "{rank:", ",pages:");
		List<String> stks = JsonUtils.testJsonArray(strStks);

		StkIndustryType type = Industry.insertOrLoadIndustryType(conn,"中国概念股", null, Industry.INDUSTRY_EASYMONEY_MEIGU);
		Industry.deleteAllStks(conn, type);
		for(String stk : stks){
			//System.out.println(stk);
			String stkCode = StringUtils.split(stk, ",")[1];
			Industry.addStk(conn, type, stkCode);
		}
	}


	public  void createStk(Connection conn,String code,String name,String totalCapital,String companyProfile) {
		List params = new ArrayList();
		params.clear();
		params.add(code);
		String sql = "select code from stk where code=?";
		Stk stk = JdbcUtils.load(conn, sql,params, Stk.class);
		if(stk == null){
			params.clear();
			params.add(code);
			params.add(name);
			params.add(totalCapital);
			if(companyProfile == null){
				params.add(null);
			}else{
				params.add(JdbcUtils.createClob(companyProfile));
			}
			params.add(ServiceUtils.isAllNumeric(code)?1:2);
			sql = "insert into stk(code,name,total_capital,company_profile,insert_time,market) values(?,?,?,?,sysdate,?)";
			JdbcUtils.insert(conn, sql, params);
			infos.add("[新股]"+code+","+name);
		}else{
			params.clear();
			params.add(name);
			params.add(totalCapital);
			if(companyProfile == null){
				params.add(null);
			}else{
				params.add(JdbcUtils.createClob(companyProfile));
			}
			params.add(code);
			sql = "update stk set name=?,total_capital=?,company_profile=? where code=?";
			JdbcUtils.update(conn, sql, params);
		}
	}

	public  void updateRestricted(Connection conn, Index index) throws Exception{
		String page = HttpUtils.get("http://vip.stock.finance.sina.com.cn/q/go.php/vInvestConsult/kind/xsjj/index.phtml?symbol="+(index.getLoc()==1?Index.SH_LOWER:Index.SZ_LOWER+index.getCode()), "gbk");
		//System.out.println(page);
		Node table = HtmlUtils.getNodeByAttribute(page, null, "id", "dataTable");
		//System.out.println(table.toHtml());
		List<List<String>> tab = HtmlUtils.getListFromTable((TableTag)table, 0);
		for(List<String> row : tab){
			//System.out.println(row);
			List params = new ArrayList();
			params.add(index.getCode());
			params.add(row.get(6));
			params.add(row.get(2));
			String banAmount = StringUtils.replace(row.get(3), ",", "");
			params.add(banAmount);
			String banMarketValue = StringUtils.replace(row.get(4), ",", "");
			params.add(banMarketValue);
			params.add(index.getCode());
			params.add(row.get(6));
			params.add(row.get(2));
			JdbcUtils.insert(conn, "insert into stk_restricted(code,report_date,listing_date,ban_amount,ban_market_value) select ?,?,?,?,? from dual where not exists (select 1 from stk_restricted where code=? and report_date=? and listing_date=?)", params);
		}
	}


	public  void initUStkFromFinviz(Connection conn) throws Exception {
		int r = 1;
		List params = new ArrayList();
		while(true){
			String page = HttpUtils.get("http://www.finviz.com/screener.ashx?v=131&r="+r, "utf-8");
			List<Node> trs = HtmlUtils.getNodeListByTagNameAndAttribute(page,null, "tr", "class", "table-light-row-cp");
			List<Node> trs2 = HtmlUtils.getNodeListByTagNameAndAttribute(page,null, "tr", "class", "table-dark-row-cp");
			trs.addAll(trs2);
			for(Node tr : trs){
				//System.out.println(tr.toHtml());
				List<Node> tds = HtmlUtils.getNodeListByTagName(tr, "td");
				String outstanding = tds.get(3).toPlainTextString();
				if(!"-".equals(outstanding)){
					String code = tds.get(1).toPlainTextString();
					System.out.println(code);
					if(outstanding.indexOf("B") > 0){
						outstanding = StringUtils.replace(outstanding, "B", "");
						outstanding = String.valueOf(Double.parseDouble(outstanding)*100000);
					}else if(outstanding.indexOf("M") > 0){
						outstanding = StringUtils.replace(outstanding, "M", "");
						outstanding = String.valueOf(Double.parseDouble(outstanding)*100);
					}
					params.clear();
					params.add(outstanding);
					params.add(code);
					String sql = "update stk set total_capital=? where code=?";
					int cnt = JdbcUtils.update(conn, sql, params);
					if(cnt == 0){
						params.clear();
						params.add(code);
						params.add(outstanding);
						sql = "insert into stk(code,insert_time,market,total_capital) values(?,sysdate,2,?)";
						JdbcUtils.insert(conn, sql, params);
					}
				}
			}

			if(trs.size() < 20) break;
			r += 20;
			//break;
		}
	}

}
