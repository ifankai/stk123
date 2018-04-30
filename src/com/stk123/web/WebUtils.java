package com.stk123.web;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.stk123.bo.Stk;
import com.stk123.bo.StkFnData;
import com.stk123.bo.StkImportInfo;
import com.stk123.bo.StkMonitor;
import com.stk123.bo.StkUser;
import com.stk123.bo.cust.StkFnDataCust;
import com.stk123.model.Index;
import com.stk123.model.User;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.MyCacheUtils;
import com.stk123.tool.html.HtmlTable;
import com.stk123.tool.html.HtmlTd;
import com.stk123.tool.html.HtmlTr;
import com.stk123.tool.util.ChineseUtils;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.HtmlUtils;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.web.context.StkContext;


public class WebUtils {
	
	public static User getUser(HttpSession session){
		return (User)session.getAttribute(StkConstant.SESSION_CURRENT_USER);
	}
	
	public static StkUser getUserInfo(HttpSession session){
		User user = getUser(session);
		if(user != null){
			return user.getStkUser();
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(display("公司是专业的无线通信终端天线生产企业。主营业务为无线通信终端天线的研发、生产和销售。公司致力于运用tester新一代移动通信、无线互联网和物联网等技术，开发高品质、多品种的无线通信终端天线产品，为手机、笔记本电脑、AP、移动电视终端、卫星定位终端等多种无线通信终端厂商提供一揽子的天线解决方案。公司“多制式高性能手机天线项目”列入国家发改委和工信部“电子信息产业振兴与技术改造项目2011年第一批中央预算内投资计划”。公司是国家级高新技术企业、国家火炬计划重点高新技术企业、广东省创新型试点企业。",320));
	}
	
	public static String REPORT_DATE = WebUtils.class+"REPORT_DATE";
	
	public static String getFnDate(List<StkFnDataCust> list){
		StkFnDataCust fnData = (StkFnDataCust)StkUtils.getFirstNotNull(list);
		return fnData.getFnDate();
	}
	
	public static void setCareStk(StkContext sc) throws Exception {
		Connection conn = sc.getConnection();
		List<Index> indexs = new ArrayList<Index>();
		List<Stk> careStks = JdbcUtils.list(conn, "select code,name from stk where status=1", Stk.class);
		for(Stk stk : careStks){
			indexs.add(new Index(sc.getConnection(), stk.getCode()));
		}
		sc.put("care_indexs", indexs);
		String reportDate = null;
		if(MyCacheUtils.get(REPORT_DATE) == null){
			//reportDate = JdbcUtils.load(conn, "select report_date from (select report_date from stk_report_daily order by report_date desc) a limit 0,1", String.class);
			reportDate = JdbcUtils.load(conn, "select report_date from (select report_date from stk_report_daily order by report_date desc) where rownum <= 1", String.class);
			MyCacheUtils.put(REPORT_DATE, reportDate, 3600 * 1000);
		}
		List params = new ArrayList();
		params.add(reportDate);
		Integer no_of_newhigh600 = JdbcUtils.load(conn, "select count(1) from stk_report_daily where type=1 and report_date=?", params, Integer.class);
		sc.put("no_of_newhigh600", String.valueOf(no_of_newhigh600));
		Integer no_of_newhigh250 = JdbcUtils.load(conn, "select count(1) from stk_report_daily where type=4 and report_date=?", params, Integer.class);
		sc.put("no_of_newhigh250", String.valueOf(no_of_newhigh250));
	}
	
	public static String getBaiduNewsUrl(String searchWords, boolean inTitle){
		return "http://news.baidu.com/ns?ct=0&rn=20&ie=utf-8&bs="+searchWords+"&rsv_bp=1&sr=0&cl=2&f=8&prevct=0&word="+searchWords+"&tn="+(inTitle?"newstitle":"news")+"&inputT=0";
	}
	
	public static String getBaiduNewsSearch(String name, String id, String searchWord, String dispWord, boolean inTitle, int fontSize){
		return "<a target=\"_blank\" name=\""+name+"\" id=\""+id+"\" keyword=\""+searchWord+"\" intitle=\""+inTitle+"\" href=\""+getBaiduNewsUrl(searchWord,inTitle)+"\" style=\"font-size: "+fontSize+"\">"+dispWord+"</a>";
	}
	public static String getBaiduNewsSearch(String name, String id, String searchWord, String despWord, boolean inTitle){
		return getBaiduNewsSearch(name, id, searchWord, despWord, inTitle, 12);
	}
	
	public static String display2(String html,int len){
		if(html == null)return html;
		String rhtml = HtmlUtils.removeHTML(html);
		String summary = ChineseUtils.substring(rhtml, len, StkConstant.MARK_EMPTY);
		if(summary.length() == rhtml.length())return "<div class=\"content-detail-inner\">"+html+"</div>";
		StringBuffer sb = new StringBuffer();
		sb.append("<div class=\"content-summary\">");
		sb.append(summary);
		sb.append("<span style=\"cursor:pointer; color:#0055a2;\" onclick=\"expand(this)\">...[展开]</span>");
		sb.append("</div>");
		sb.append("<div class=\"content-detail\" style=\"display: none;\"><div class=\"content-detail-inner\">");
		sb.append(html);
		sb.append("</div><span style=\"cursor:pointer; color:#0055a2;\" onclick=\"expand(this)\">[收起]</span>");
		sb.append("</div>");
		return sb.toString();
	}
	
	public static String display(String html,int len){
		return display(html,len,true,true);
	}
	
	private final static String DIV_END = "</div>";
	private final static String CONTENT_DETAIL = "<div class=\"content-detail\">";
	private final static String CONTENT_DETAIL_INNER = "<div class=\"content-detail-inner\">";
	private final static String CONTENT_SUMMARY = "<div class=\"content-summary\">";
	private final static String SHOW_ALL = "<div class=\"show-all\" onclick=\"expand(this)\">";
	private final static String SHOW_TEXT_1 = "<span class=\"show-text\" onclick=\"expand(this)\">[展开]</span>";
	private final static String SHOW_TEXT_2 = "<span class=\"show-text\" onclick=\"expand(this)\">[收起]</span>";
	
	public static String display(String html,int len,boolean br){
		return display(html,len,br,true);
	}
	public static String display(String html,int len,boolean br,boolean showImg){
		return WebUtils.display(html, len, br, showImg, true);
	}
			
	public static String display(String html,int len,boolean br,boolean showImg,boolean showThreeDot){
		if(html == null)return StkConstant.MARK_DOUBLE_HYPHEN;
		String img = StkUtils.getMatchString(html, HtmlUtils.RegxpForImgTag);
		String rhtml = HtmlUtils.removeHTML(html);
		String summary = ChineseUtils.substring(rhtml, len, StkConstant.MARK_EMPTY);
		if(summary.length() == rhtml.length() && img == null){
			return CONTENT_DETAIL_INNER + html + DIV_END;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(CONTENT_SUMMARY);
		sb.append(summary);
		if(showThreeDot)
			sb.append(StkConstant.MARK_THREE_DOT);
		if(br)sb.append(StkConstant.HTML_TAG_BR);
		sb.append(SHOW_TEXT_1);
		//StringUtils.replace(text, searchString, replacement)
		if(img != null && showImg){
			sb.append(SHOW_ALL).append(img).append(DIV_END);
		}
		sb.append(DIV_END);
		sb.append(CONTENT_DETAIL);
		sb.append(CONTENT_DETAIL_INNER);
		sb.append(html);
		sb.append(DIV_END);
		sb.append(SHOW_TEXT_2);
		sb.append(DIV_END);
		
		return sb.toString();
	}
	public static String display(String html,String summary,int len){
		if(html == null)return html;
		String rhtml = HtmlUtils.removeHTML(html);
		String tmp = ChineseUtils.substring(rhtml, len, StkConstant.MARK_EMPTY);
		if(summary == null){
			return display(html,len);
		}
		if(tmp.length() == rhtml.length()){
			return CONTENT_DETAIL_INNER + summary + DIV_END;
		}
		String img = StkUtils.getMatchString(html, HtmlUtils.RegxpForImgTag);
		StringBuffer sb = new StringBuffer();
		sb.append(CONTENT_SUMMARY);
		sb.append(summary);
		sb.append(StkConstant.MARK_THREE_DOT);
		sb.append(StkConstant.HTML_TAG_BR);
		sb.append(SHOW_TEXT_1);
		//StringUtils.replace(text, searchString, replacement)
		if(img != null){
			sb.append(SHOW_ALL).append(img).append(DIV_END);
		}
		sb.append(DIV_END);
		sb.append(CONTENT_DETAIL);
		sb.append(CONTENT_DETAIL_INNER);
		sb.append(html);
		sb.append(DIV_END);
		sb.append(SHOW_TEXT_2);
		sb.append(DIV_END);
		
		return sb.toString();
	}
	
	public static String substring(String origin, int len,String start, String end) {   
        if (origin == null || origin.equals("") || len < 1)   
            return "";   
        byte[] strByte = new byte[len];   
        if (len >= ChineseUtils.length(origin)) {   
            return origin;   
        } 
        
        try {   
        	byte[] orignByte = origin.getBytes("GBK");
            System.arraycopy(orignByte, 0, strByte, 0, len);   
            int count = 0;   
            for (int i = 0; i < len; i++) {   
                int value = (int) strByte[i];   
                if (value < 0) {   
                    count++;   
                }   
            }  
            if (count % 2 != 0) {   
                len = (len == 1) ? ++len : --len;   
            }   
            len = len - 2;
            return new String(strByte, 0, len, "GBK")+start+new String(orignByte, len, orignByte.length-len, "GBK")+end;   
        } catch (Exception e) {  
        	e.printStackTrace();
            throw new RuntimeException(e);   
        }   
    } 
	
	public final static String WEB_CLASS_PATH = WebUtils.class.getResource("/").getPath();
	public final static String WEB_PATH = WEB_CLASS_PATH + "../../";
	public final static String WEB_IMAGE_PATH = WEB_PATH + "images/";
	
	// file///D:/xxx.png
	public static String download(String url) throws MalformedURLException, IOException{
		String file = StringUtils.substringAfter(url, "//");
		file = StringUtils.replace(file, ":", "");
		String target = WEB_CLASS_PATH + "../../images/download/" + file;
		//System.out.println(target);
		File targetFile = new File(target);
		if(!targetFile.exists()){
			FileUtils.copyURLToFile(new URL(url), targetFile, 10*1000, 10*1000);
			FileUtils.copyFile(targetFile, new File(StkConstant.PATH_TEXT_IMG_DOWNLOAD/*"D:/share/workspace/stock/web/images/download/"*/+file));
		}
		return "/images/download/"+file;
	}
	
	public static String createTable(List<HtmlTd> titles, List<List<HtmlTd>> datas){
		HtmlTable tab = new HtmlTable();
		tab.attributes.put("border", "1");
		tab.attributes.put("cellspacing", "0");
		tab.attributes.put("cellpadding", "2");
		HtmlTr tr = new HtmlTr();
		if(titles != null){
			for(HtmlTd title : titles){
				tr.columns.add(title);
			}
			tab.rows.add(tr);
		}
		for(List<HtmlTd> row : datas){
			tr = new HtmlTr();
			for(HtmlTd td : row){
				tr.columns.add(td);
			}
			tab.rows.add(tr);
		}
		return tab.toHtml();
	}
	
	public static String createTableOfStkMonitor(Index index, List<StkMonitor> ms, String item) throws Exception {
		List<HtmlTd> titles = new ArrayList<HtmlTd>();
		titles.add(HtmlTd.getInstanceAlignMiddle("员工持股价"));
		titles.add(HtmlTd.getInstanceAlignMiddle("前复权价"));
		titles.add(HtmlTd.getInstanceAlignMiddle("公告日"));
		titles.add(HtmlTd.getInstanceAlignMiddle("折价率"));
		titles.add(HtmlTd.getInstanceAlignMiddle("来源"));
		
		List<List<HtmlTd>> datas = new ArrayList<List<HtmlTd>>();
		for(StkMonitor m : ms){
			if(item.equals(m.getParam1())){
				List<HtmlTd> row = new ArrayList<HtmlTd>();
				row.add(HtmlTd.getInstanceAlignRight(m.getParam2()));
				row.add(HtmlTd.getInstanceAlignRight(index.getK(m.getParam4()).getClose()));
				row.add(HtmlTd.getInstanceAlignRight(StkUtils.formatDate(m.getParam3())));
				HtmlTd td = HtmlTd.getInstanceAlignRight(m.getParam5());
				if(Double.parseDouble(m.getParam5()) <= 1){
					td.attributes.put("style", "color:red");
				}
				row.add(td);
				row.add(HtmlTd.getInstanceAlignRight("<a target='_blank' href='"+m.getResult1()+"'>来源</a>"));
				datas.add(row);
			}
		}
		return datas.size()>0 ? createTable(titles, datas) : StkConstant.MARK_EMPTY;
	}
	
	public static String createTableOfStkImportInfo(Connection conn, Index index, int type) throws Exception {
		List<StkImportInfo> infos = index.getImportInfo(3);
		List<HtmlTd> titles = new ArrayList<HtmlTd>();
		titles.add(HtmlTd.getInstanceAlignMiddle("名称"));
		titles.add(HtmlTd.getInstanceAlignMiddle("公告日"));
		
		List<List<HtmlTd>> datas = new ArrayList<List<HtmlTd>>();
		for(StkImportInfo m : infos){
			List<HtmlTd> row = new ArrayList<HtmlTd>();
			row.add(HtmlTd.getInstanceAlignRight(m.getInfo()));
			row.add(HtmlTd.getInstanceAlignRight(StkUtils.formatDate(m.getInsertTime())));
			datas.add(row);
			//out.println("<span style='color:red'>"+sii.getInfo()+" ["+StkUtils.formatDate(sii.getInsertTime())+"]"+"</span>");
		}
		return datas.size()>0 ? createTable(titles, datas) : StkConstant.MARK_EMPTY;
	}

}
