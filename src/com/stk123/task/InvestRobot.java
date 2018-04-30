package com.stk123.task;

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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.stk123.bo.Stk;
import com.stk123.bo.StkInvestigation;
import com.stk123.model.Index;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.FileType;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.tool.util.WordUtils;

public class InvestRobot {
	
	private static Date InitialDate = null;
	static{
		try {
			//InitialDate = StkUtils.sf_ymd2.parse("20110101");
			InitialDate = StkUtils.addDay(new Date(), -7);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		try{
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
			run(InitialDate);
			
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
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			for(Stk stk : stks){
				System.out.println(stk.getCode());
				Index index =  new Index(conn,stk.getCode(),stk.getName());
				List<Map> news = parse(index, date);
				for(Map map : news){
					List params = new ArrayList();
					params.add(stk.getCode());
					params.add(map.get("sourceUrl"));
					List<StkInvestigation> infos = JdbcUtils.list(conn, "select * from stk_investigation where code=? and source_url=?", params, StkInvestigation.class);
					if(infos.size() == 0){
						params.clear();
						params.add(stk.getCode());
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
			}
			
			List<StkInvestigation> list = JdbcUtils.list(conn, "select * from stk_investigation where invest_date = (select max(invest_date) from stk_investigation) order by investigator_count desc", StkInvestigation.class);
			List<String> s = new ArrayList();
			for(StkInvestigation inv : list){
				Index index =  new Index(conn,inv.getCode());
				s.add(StkUtils.wrapCodeAndNameAsHtml(index)+" "+StkUtils.wrapLink(inv.getTitle(), inv.getSourceUrl()) + " ["+inv.getInvestigatorCount()+"]");
			}
			EmailUtils.send("投资者关系", StringUtils.join(s, "<br>"));
		} finally {
			if (conn != null) conn.close();
		}
	}
	
	public static List<Map> parse(Index index,Date DateBefore) throws Exception {
		List<Map> news = new ArrayList<Map>();
		Date now = new Date();
		String page = HttpUtils.get("http://www.cninfo.com.cn//disclosure/tzzgxxx/stocks/zxxx1y/cninfo/"+index.getCode()+".js?ver="+StkUtils.formatDate(now, StkUtils.sf_ymd12), "gb2312");
		//System.out.println(page);
		if("404".equals(page)){
			return news;
		}
		String json = StringUtils.substringBetween(page, "=", "];");
		List<List> list = JsonUtils.getList4Json(json+"]", List.class);
		for(List item : list){
			//System.out.println(item);
			Date investDate = StkUtils.sf_ymd.parse(String.valueOf(item.get(5)));
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
