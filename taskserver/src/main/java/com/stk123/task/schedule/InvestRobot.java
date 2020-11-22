package com.stk123.task.schedule;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.common.util.*;
import com.stk123.util.ExceptionUtils;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.htmlparser.Node;

import com.stk123.model.bo.StkInvestigation;
import com.stk123.model.Index;
import com.stk123.common.db.util.DBUtil;

public class InvestRobot {

	private static Date InitialDate = null;
	static{
		try {
			//InitialDate = StkUtils.sf_ymd2.parse("20110101");
			InitialDate = ServiceUtils.addDay(new Date(), -7);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		try{
			//docx解析时指定正确的解析类
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
			run(InitialDate);

			//parse3();

		}catch(Exception e){
			StringWriter aWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(aWriter));
			EmailUtils.send("InvestRobot Error", aWriter.getBuffer().toString());
			e.printStackTrace();
		}
	}

	public static void run(Date date) throws Exception {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			//List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			//for(Stk stk : stks){
				//System.out.println(stk.getCode());
				//Index index =  new Index(conn,stk.getCode(),stk.getName());
				List<Map> news = parse3(conn);
				for(Map map : news){
					List params = new ArrayList();
					params.add(map.get("code"));
					params.add(map.get("sourceUrl"));
					List<StkInvestigation> infos = JdbcUtils.list(conn, "select * from stk_investigation where code=? and source_url=?", params, StkInvestigation.class);
					if(infos.size() == 0){
						params.clear();
						params.add(map.get("code"));
						params.add((String)map.get("title"));
						params.add((String)map.get("investigator"));
						params.add(map.get("investigatorCount"));
						params.add(JdbcUtils.createClob((String)map.get("text")));
						params.add(map.get("textCount"));
						params.add(new Timestamp(((Date)map.get("investDate")).getTime()));
						params.add((String)map.get("sourceUrl"));
						params.add((String)map.get("sourceType"));
						JdbcUtils.insert(conn, "insert into stk_investigation(id,code,title,investigator,investigator_count,text,text_count,invest_date,insert_date,source_url,source_type) " +
								"values (s_investigation_id.nextval,?,?,?,?,?,?,?,sysdate,?,?)", params);
					}
				}
				//break;
			//}*/

			List<StkInvestigation> list = JdbcUtils.list(conn, "select * from stk_investigation where invest_date = (select max(invest_date) from stk_investigation) order by investigator_count desc", StkInvestigation.class);
			List<String> s = new ArrayList();
			for(StkInvestigation inv : list){
				Index index =  new Index(conn,inv.getCode());
				s.add(ServiceUtils.wrapCodeAndNameAsHtml(index)+" "+ServiceUtils.wrapLink(inv.getTitle(), inv.getSourceUrl()) + " ["+inv.getInvestigatorCount()+"]");
			}
			EmailUtils.send("投资者关系", StringUtils.join(s, "<br>"));
		} finally {
			if (conn != null) conn.close();
		}
	}

	/**
	 * 投资者关系 PDF
	 * http://irm.cninfo.com.cn
	 */
	public static List<Map> parse3(Connection conn) throws Exception {
		List<Map> list = new ArrayList<Map>();
		String page = HttpUtils.post("http://irm.cninfo.com.cn/ircs/index/search", "pageNo=1&pageSize=50&searchTypes=4%2C&market=&industry=&stockCode=", "utf-8");
		ObjectMapper mapper = new ObjectMapper();
		InvestJson json = mapper.readValue(page, InvestJson.class);
		if(json != null) {
			for (Results result : json.getResults()) {
				String downloadUrl = result.getAttachmentUrl();
				String code = result.getStockCode();
				String title = result.getMainContent();
				String investDate = ServiceUtils.formatDate(new Date(Long.parseLong(result.getPubDate())));
				System.out.println(code+", "+title+", "+downloadUrl);
                Map map = null;
				try {
                    map = download(code, "http://static.cninfo.com.cn/" + downloadUrl, title, investDate);
                }catch (Exception e){
                    ExceptionUtils.insertLogWithSimilarCheck(conn, ExceptionUtils.ERROR_CODE_999997, e);
                    continue;
                }
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * http://irm.cninfo.com.cn/ircs/sse/sseSubIndex.do?condition.type=6
	 */
	public static List<Map> parse2() throws Exception {
		List<Map> news = new ArrayList<Map>();
		String page = HttpUtils.get("http://irm.cninfo.com.cn/ircs/interaction/irmInformationList.do?irmType=251314", "utf-8");
		System.out.println(page);
		if("404".equals(page)){
			return news;
		}
		Node div = HtmlUtils.getNodeByAttribute(page, null, "class", "irmbox");
		Node table = HtmlUtils.getNodeByTagName(div, "table");
		//List<List<String>> list = HtmlUtils.getListFromTable2((TableTag)table,-1);
		List<Node> rows = HtmlUtils.getNodeListByTagName(table, "tr");
		for(Node row : rows){
			Node link = HtmlUtils.getNodeByTagName(row, "a");
			String code = link.toPlainTextString();
			Node a = HtmlUtils.getNodeByTagName(row, "a", 1);
			String downloadUrl = HtmlUtils.getAttribute(a, "href");
			String title = HtmlUtils.getAttribute(a, "title");
			//Node em = HtmlUtils.getNodeByTagName(row, "em");
			Node span = HtmlUtils.getNodeByTagName(row, "span", 1);
			String investDate = span.toPlainTextString();
			Map map = download(code, downloadUrl, title, investDate);
			map.put("code", code);
			System.out.println(map);
			news.add(map);
		}
		return news;
	}

	public static Map download(String code, String downloadUrl, String title, String investDate) throws Exception {
		//String filePath = String.valueOf(item.get(1));
		//String downloadUrl = "http://www.cninfo.com.cn/"+filePath;
		String fileName = StringUtils.substringAfterLast(downloadUrl, "/");
		String downloadFilePath = ConfigUtils.getProp("stk_report")+ code +"\\invest\\";
		File path = new File(downloadFilePath);
		if(!path.exists()){
			FileUtils.forceMkdir(path);
		}
		HttpUtils.download(downloadUrl,null, downloadFilePath, fileName);
		String sourceType = "pdf";
		String fileType = FileType.getFileType(downloadFilePath + fileName);
		if(!StringUtils.endsWithIgnoreCase(fileType, sourceType)){
			System.out.println(sourceType+","+fileType);
			if("rtf".equalsIgnoreCase(fileType)){
				sourceType = "RTF";
			}else if("docx".equalsIgnoreCase(fileType) && "doc".equalsIgnoreCase(sourceType)){
				sourceType = "DOCX";
			}else{
				sourceType = fileType;
			}
		}

		Map map = new HashMap();
		map.put("sourceUrl", downloadUrl);
		map.put("sourceType", sourceType);
		map.put("title", title);
		map.put("investDate", ServiceUtils.sf_ymd.parse(investDate));
		map.put("code", code);

		if("doc".equalsIgnoreCase(sourceType)){
			Table tb = WordUtils.getTable(downloadFilePath + fileName,0);
			if(tb != null){
				String investigator = WordUtils.getCellWhenColumnContain(tb, 0, "参与单位", 1);
				if(investigator != null){
					investigator = StringUtils.replace(investigator, "               ", "");
					if(investigator.length() >= 1500){
						investigator = investigator.substring(0, 1300);
					}
					map.put("investigator", investigator);
					map.put("investigatorCount", investigator.length());
				}
				String text = WordUtils.getCellWhenColumnContain(tb, 0, "主要内容", 1);
				if(text != null){
					map.put("text", text);
					map.put("textCount", text.length());
				}
			}
		}else if("docx".equalsIgnoreCase(sourceType)){
			XWPFTable tb = WordUtils.getTableX(downloadFilePath + fileName,0);
			if(tb != null){
				String investigator = WordUtils.getCellXWhenColumnContain(tb, 0, "参与单位", 1);
				if(investigator != null){
					map.put("investigator", investigator);
					map.put("investigatorCount", investigator.length());
				}
				String text = WordUtils.getCellXWhenColumnContain(tb, 0, "主要内容", 1);
				if(text != null){
					map.put("text", text);
					map.put("textCount", text.length());
				}
			}
		}
		return map;
	}

	public static List<Map> parse(Index index,Date DateBefore) throws Exception {
		List<Map> news = new ArrayList<Map>();
		Date now = new Date();
		String page = HttpUtils.get("http://www.cninfo.com.cn//disclosure/tzzgxxx/stocks/zxxx1y/cninfo/"+index.getCode()+".js?ver="+ServiceUtils.formatDate(now, ServiceUtils.sf_ymd12), "gb2312");
		//System.out.println(page);
		if("404".equals(page)){
			return news;
		}
		String json = StringUtils.substringBetween(page, "=", "];");
		List<List> list = JsonUtils.getList4Json(json+"]", List.class);
		for(List item : list){
			//System.out.println(item);
			Date investDate = ServiceUtils.sf_ymd.parse(String.valueOf(item.get(5)));
			if(investDate.before(DateBefore)){
				break;
			}

			String filePath = String.valueOf(item.get(1));
			String downloadUrl = "http://www.cninfo.com.cn/"+filePath;
			String fileName = StringUtils.substringAfterLast(filePath, "/");
			String downloadFilePath = ConfigUtils.getProp("stk_report")+index.getCode()+"\\invest\\";
			File path = new File(downloadFilePath);
			if(!path.exists()){
				FileUtils.forceMkdir(path);
			}
			HttpUtils.download(downloadUrl,null, downloadFilePath, fileName);
			String sourceType = String.valueOf(item.get(3));
			String fileType = FileType.getFileType(downloadFilePath + fileName);
			if(!StringUtils.endsWithIgnoreCase(fileType, sourceType)){
				System.out.println(sourceType+","+fileType);
				if("rtf".equalsIgnoreCase(fileType)){
					sourceType = "RTF";
				}else if("docx".equalsIgnoreCase(fileType) && "doc".equalsIgnoreCase(sourceType)){
					sourceType = "DOCX";
				}
			}

			Map map = new HashMap();
			map.put("sourceUrl", downloadUrl);
			map.put("sourceType", sourceType);
			map.put("title", item.get(2));
			map.put("investDate", investDate);

			if("doc".equalsIgnoreCase(sourceType)){
				Table tb = WordUtils.getTable(downloadFilePath + fileName,0);
				if(tb != null){
					String investigator = WordUtils.getCellWhenColumnContain(tb, 0, "参与单位", 1);
					if(investigator != null){
						investigator = StringUtils.replace(investigator, "               ", "");
						if(investigator.length() >= 1500){
							investigator = investigator.substring(0, 1300);
						}
						map.put("investigator", investigator);
						map.put("investigatorCount", investigator.length());
					}
					String text = WordUtils.getCellWhenColumnContain(tb, 0, "主要内容", 1);
					if(text != null){
						map.put("text", text);
						map.put("textCount", text.length());
					}
				}
			}else if("docx".equalsIgnoreCase(sourceType)){
				XWPFTable tb = WordUtils.getTableX(downloadFilePath + fileName,0);
				if(tb != null){
					String investigator = WordUtils.getCellXWhenColumnContain(tb, 0, "参与单位", 1);
					if(investigator != null){
						map.put("investigator", investigator);
						map.put("investigatorCount", investigator.length());
					}
					String text = WordUtils.getCellXWhenColumnContain(tb, 0, "主要内容", 1);
					if(text != null){
						map.put("text", text);
						map.put("textCount", text.length());
					}
				}
			}
			news.add(map);
		}
		return news;
	}

}
class InvestJson
{
	private int pageNo;

	private int pageSize;

	private int totalRecord;

	private int totalPage;

	private List<Results> results;

	private boolean count;

	public void setPageNo(int pageNo){
		this.pageNo = pageNo;
	}
	public int getPageNo(){
		return this.pageNo;
	}
	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}
	public int getPageSize(){
		return this.pageSize;
	}
	public void setTotalRecord(int totalRecord){
		this.totalRecord = totalRecord;
	}
	public int getTotalRecord(){
		return this.totalRecord;
	}
	public void setTotalPage(int totalPage){
		this.totalPage = totalPage;
	}
	public int getTotalPage(){
		return this.totalPage;
	}
	public void setResults(List<Results> results){
		this.results = results;
	}
	public List<Results> getResults(){
		return this.results;
	}
	public void setCount(boolean count){
		this.count = count;
	}
	public boolean getCount(){
		return this.count;
	}
}

class Results
{
	private String esId;

	private String indexId;

	private int contentType;

	private List<String> trade;

	private String mainContent;

	private String attachmentUrl;

	private String stockCode;

	private String secid;

	private String companyShortName;

	private String companyLogo;

	private List<String> boardType;

	private String filetype;

	private String pubDate;

	private String updateDate;

	private int score;

	private int topStatus;

	private int praiseCount;

	private boolean praiseStatus;

	private boolean favoriteStatus;

	private boolean attentionCompany;

	private int qaStatus;

	private String packageDate;

	public void setEsId(String esId){
		this.esId = esId;
	}
	public String getEsId(){
		return this.esId;
	}
	public void setIndexId(String indexId){
		this.indexId = indexId;
	}
	public String getIndexId(){
		return this.indexId;
	}
	public void setContentType(int contentType){
		this.contentType = contentType;
	}
	public int getContentType(){
		return this.contentType;
	}
	public void setTrade(List<String> trade){
		this.trade = trade;
	}
	public List<String> getTrade(){
		return this.trade;
	}
	public void setMainContent(String mainContent){
		this.mainContent = mainContent;
	}
	public String getMainContent(){
		return this.mainContent;
	}
	public void setAttachmentUrl(String attachmentUrl){
		this.attachmentUrl = attachmentUrl;
	}
	public String getAttachmentUrl(){
		return this.attachmentUrl;
	}
	public void setStockCode(String stockCode){
		this.stockCode = stockCode;
	}
	public String getStockCode(){
		return this.stockCode;
	}
	public void setSecid(String secid){
		this.secid = secid;
	}
	public String getSecid(){
		return this.secid;
	}
	public void setCompanyShortName(String companyShortName){
		this.companyShortName = companyShortName;
	}
	public String getCompanyShortName(){
		return this.companyShortName;
	}
	public void setCompanyLogo(String companyLogo){
		this.companyLogo = companyLogo;
	}
	public String getCompanyLogo(){
		return this.companyLogo;
	}
	public void setBoardType(List<String> boardType){
		this.boardType = boardType;
	}
	public List<String> getBoardType(){
		return this.boardType;
	}
	public void setFiletype(String filetype){
		this.filetype = filetype;
	}
	public String getFiletype(){
		return this.filetype;
	}
	public void setPubDate(String pubDate){
		this.pubDate = pubDate;
	}
	public String getPubDate(){
		return this.pubDate;
	}
	public void setUpdateDate(String updateDate){
		this.updateDate = updateDate;
	}
	public String getUpdateDate(){
		return this.updateDate;
	}
	public void setScore(int score){
		this.score = score;
	}
	public int getScore(){
		return this.score;
	}
	public void setTopStatus(int topStatus){
		this.topStatus = topStatus;
	}
	public int getTopStatus(){
		return this.topStatus;
	}
	public void setPraiseCount(int praiseCount){
		this.praiseCount = praiseCount;
	}
	public int getPraiseCount(){
		return this.praiseCount;
	}
	public void setPraiseStatus(boolean praiseStatus){
		this.praiseStatus = praiseStatus;
	}
	public boolean getPraiseStatus(){
		return this.praiseStatus;
	}
	public void setFavoriteStatus(boolean favoriteStatus){
		this.favoriteStatus = favoriteStatus;
	}
	public boolean getFavoriteStatus(){
		return this.favoriteStatus;
	}
	public void setAttentionCompany(boolean attentionCompany){
		this.attentionCompany = attentionCompany;
	}
	public boolean getAttentionCompany(){
		return this.attentionCompany;
	}
	public void setQaStatus(int qaStatus){
		this.qaStatus = qaStatus;
	}
	public int getQaStatus(){
		return this.qaStatus;
	}
	public void setPackageDate(String packageDate){
		this.packageDate = packageDate;
	}
	public String getPackageDate(){
		return this.packageDate;
	}
}
