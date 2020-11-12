package com.stk123.task;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.tool.util.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.stk123.bo.Stk;
import com.stk123.bo.StkMonitor;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.model.News;
import com.stk123.model.Text;
import com.stk123.tool.db.util.DBUtil;

/**
 * 公告 机器人
 */
public class NoticeRobot {

	public static void main(String[] args) {
		try{
			run();
		}catch(Exception e){
			StringWriter aWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(aWriter));
			EmailUtils.send("NoticeRobot Error", aWriter.getBuffer().toString());
			e.printStackTrace();
		}
	}

	public static void run() throws Exception {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			List<Stk> stks = JdbcUtils.list(conn, "select code,name from stk_cn order by code", Stk.class);
			for(Stk stk : stks){
				System.out.println(stk.getCode());
				Index index = new Index(conn,stk.getCode(),stk.getName());
				try{
					updateNotice(conn, index);
					//updateRate(conn, index);
					index.gc();
				}catch(Exception e){
					e.printStackTrace();
					ExceptionUtils.insertLog(conn, index.getCode(), e);
				}
			}
		}finally {
			if (conn != null) conn.close();
		}
	}
	
	public static void updateRate(Connection conn, Index index) throws Exception {
		List params = new ArrayList();
		params.add(index.getCode());
		List<StkMonitor> ms = JdbcUtils.list(conn, "select * from stk_monitor where type=2 and code=?", params, StkMonitor.class);
		for(StkMonitor m : ms){
			params.clear();
			params.add(StkUtils.number2String(index.getK().getClose()/index.getK(m.getParam4()).getClose(), 2));
			params.add(m.getId());
			JdbcUtils.update(conn, "update stk_monitor set param_5=? where id=?", params);
		}
	}

    /**
     *
     * http://www.cninfo.com.cn/new/disclosure/stock?stockCode=000002&orgId=gssz0000002
     * http://www.cninfo.com.cn/new/disclosure/stock?stockCode=600600&orgId=gssh0600600
     *
     * Request:
     * http://www.cninfo.com.cn/new/hisAnnouncement/query
     * stock=600600%2Cgssh0600600&tabName=fulltext&pageSize=30&pageNum=1&column=sse&category=&plate=sh&seDate=&searchkey=&secid=&sortName=&sortType=&isHLtitle=true
     * stock=000002%2Cgssz0000002&tabName=fulltext&pageSize=30&pageNum=1&column=szse&category=&plate=sz&seDate=&searchkey=&secid=&sortName=&sortType=&isHLtitle=true
     *
     * Response:
     * {
     *     "classifiedAnnouncements":null,
     *     "totalSecurities":0,
     *     "totalAnnouncement":761,
     *     "totalRecordNum":761,
     *     "announcements":[
     *         {
     *             "id":null,
     *             "secCode":"600600",
     *             "secName":"青岛啤酒",
     *             "orgId":"gssh0600600",
     *             "announcementId":"1208388950",
     *             "announcementTitle":"关于持股5%以上股东权益变动达1%的提示公告",
     *             "announcementTime":1599148800000,
     *             "adjunctUrl":"finalpage/2020-09-04/1208388950.PDF",
     *             "adjunctSize":102,
     *             "adjunctType":"PDF",
     *             "storageTime":null,
     *             "columnId":"250401||251302",
     *             "pageColumn":"SHZB",
     *             "announcementType":"01010503||010113||01150101",
     *             "associateAnnouncement":null,
     *             "important":null,
     *             "batchNum":null,
     *             "announcementContent":"",
     *             "orgName":null,
     *             "announcementTypeName":null
     *         },
     *         {
     *             "id":null,
     *             "secCode":"600600",
     *             "secName":"青岛啤酒",
     *             "orgId":"gssh0600600",
     *             "announcementId":"1208345383",
     *             "announcementTitle":"2020年半年度报告",
     *             "announcementTime":1598803200000,
     *             "adjunctUrl":"finalpage/2020-08-31/1208345383.PDF",
     *             "adjunctSize":3424,
     *             "adjunctType":"PDF",
     *             "storageTime":null,
     *             "columnId":"250401||250403||251302",
     *             "pageColumn":"SHZB",
     *             "announcementType":"01010503||010113||01030301",
     *             "associateAnnouncement":null,
     *             "important":null,
     *             "batchNum":null,
     *             "announcementContent":"",
     *             "orgName":null,
     *             "announcementTypeName":null
     *         },
     *         ...
     *     ],
     *     "categoryList":null,
     *     "hasMore":true,
     *     "totalpages":25
     * }
     */
    public static void updateNotice(Connection conn, Index index) throws Exception{
        String column = index.getLoc() == Index.SH ? "sse" : "szse";
        String stock = index.getCode() + (index.getLoc() == Index.SH ? "%2Cgssh0": "%2Cgssz0") + index.getCode();
        String body = "stock="+stock+"&tabName=fulltext&pageSize=30&pageNum=1&column="+column+"&category=&plate=sz&seDate=&searchkey=&secid=&sortName=&sortType=&isHLtitle=true";
        String page = HttpUtils.post("http://www.cninfo.com.cn/new/hisAnnouncement/query", body, "UTF-8");
        System.out.println(page);
        if("404".equals(page)){
            return ;
        }
        ObjectMapper mapper = new ObjectMapper();
        NoticeJsonRoot root = mapper.readValue(page, NoticeJsonRoot.class);

        Set<String> prices1 = new HashSet<String>();
        Set<String> prices2 = new HashSet<String>();
        Date DateBefore = StkUtils.addDay(new Date(), -1);
        for(Announcement item : root.getAnnouncements()){
            Date createDate = new Date(item.getAnnouncementTime());
            if(createDate.before(DateBefore)){
                break;
            }

            String filePath = String.valueOf(item.getAdjunctUrl());
            String sourceType = String.valueOf(item.getAdjunctType());
            String title = String.valueOf(item.getAnnouncementTitle());
            //if(!(title.contains("年度报告") || title.contains("季度报告全文")))continue;
            if(title.contains("员工持股")){
                //System.out.println(title);
                //http://static.cninfo.com.cn/finalpage/2020-09-04/1208388950.PDF
                String downloadFilePath = download(filePath,index);
                String fileType = getFileType(downloadFilePath, sourceType);
                if("pdf".equalsIgnoreCase(fileType)){
                    String spdf = PDFUtils.getText(downloadFilePath);
                    Set<String> sets = StkUtils.getMatchStrings(spdf, "(收盘价)( ?)[0-9]*(\\.?)[0-9]+( ?)(元测算)");
                    if(sets != null && sets.size() == 0){
                        sets = StkUtils.getMatchStrings(spdf, "(购买均价)( ?)[0-9]*(\\.?)[0-9]+( ?)(元/股)");
                    }
                    prices1.addAll(sets);
                    if(sets!= null && sets.size() > 0){
                        //System.out.println(item);
                        String p = sets.iterator().next();
                        String s = StkUtils.getMatchStrings(p,"\\d+(\\.?)(\\d+)?( *)(元)").iterator().next();
                        double price = Double.parseDouble(StringUtils.replace(StringUtils.replace(s, " ", ""),"元",""));
                        //System.out.println(price);
                        insertStkMonitor(conn, index, "员工持股", price, item);
                    }
                }
            }
            else if(StringUtils.contains(title, "非公开发行")){
                //System.out.println(filePath);
                String downloadFilePath = download(filePath,index);
                String fileType = getFileType(downloadFilePath, sourceType);
                if("pdf".equalsIgnoreCase(fileType)){
                    String spdf = PDFUtils.getText(downloadFilePath);

                    if(createDate.after(StkUtils.addDay(new Date(), -1))){
						/*if(StkUtils.getMatchString(title, "获得中国(证监会|证券监督).{0,10}(核准|审核通过|批复)") != null){
							EmailUtils.sendAndReport("【非公开发行股票获得中国证监会核准】-"+ index.getName(),
									StkUtils.wrapCodeAndNameAsHtml(index) + " - " + StkUtils.wrapLink(title, "http://www.cninfo.com.cn/information/companyinfo.html?fulltext?"+index.getCode()));
						}*/
                        //System.out.println(downloadFilePath);
                        if(StkUtils.getMatchString(title, "非公开发行.{0,15}(报告|公告)") != null){
                            Set<String> matches = StkUtils.getMatchStrings(spdf,"发行.{2,10}无锁定期");
                            matches.addAll(StkUtils.getMatchStrings(spdf,"发行.{2,10}无限售期"));
                            matches.addAll(StkUtils.getMatchStrings(spdf,"发行.{2,10}不设限售期"));
                            matches.addAll(StkUtils.getMatchStrings(spdf,"发行.{2,10}无限售条件"));
                            matches.addAll(StkUtils.getMatchStrings(spdf,"发行结束.{0,6}可.{0,2}交易"));

                            if(matches.size() > 0){
                                EmailUtils.sendAndReport("★【非公开发行-无限售期】-"+ index.getName(),
                                        StkUtils.wrapCodeAndNameAsHtml(index)+" "+matches + " - " + StkUtils.wrapLink(title, "http://www.cninfo.com.cn/information/companyinfo.html?fulltext?"+index.getCode()));
                            }
                        }
                    }

                    Set<String> sets = StkUtils.getMatchStrings(spdf, "(非公开发行股票价格为)( ?)[0-9]*(\\.?)[0-9]+( *)(元/股)");
                    if(sets != null && sets.size() == 0){
                        sets = StkUtils.getMatchStrings(spdf, "(发行价格为)( ?)[0-9]*(\\.?)[0-9]+( *)(元/股)");
                    }
                    if(sets != null && sets.size() == 0){
                        sets = StkUtils.getMatchStrings(spdf, "(交易日公司股票交易均价，即)( ?)[0-9]*(\\.?)[0-9]+( *)(元/股)");
                    }
                    if(sets != null && sets.size() == 0){
                        sets = StkUtils.getMatchStrings(spdf, "(个交易日股票交易)(.{0,50})(即)( ?)[0-9]*(\\.?)[0-9]+( *)(元/股)");
                    }
                    if(sets != null && sets.size() == 0){
                        sets = StkUtils.getMatchStrings(spdf, "(以)( ?)[0-9]*(\\.?)[0-9]+( *)(元/股为本次发行的发行价格)");
                    }
                    if(sets != null && sets.size() == 0){
                        sets = StkUtils.getMatchStrings(spdf, "(每股发行价为人民币，即)( ?)[0-9]*(\\.?)[0-9]+( *)(元)");
                    }
                    prices2.addAll(sets);
                    if(sets!= null && sets.size() > 0){
                        //System.out.println(item);
                        String p = sets.iterator().next();
                        String s = StkUtils.getMatchStrings(p,"\\d+(\\.?)(\\d+)?( *)(元)").iterator().next();
                        double price = Double.parseDouble(StringUtils.replace(StringUtils.replace(s, " ", ""),"元",""));
                        //System.out.println(price);
                        insertStkMonitor(conn, index, "非公开发行", price, item);

                        break;
                    }
                }
            }
            else if(StringUtils.contains(title, "股权激励") || StringUtils.contains(title, "股票激励")){
                //System.out.println(item);
                String downloadFilePath = download(filePath,index);
                String fileType = getFileType(downloadFilePath, sourceType);
                if("pdf".equalsIgnoreCase(fileType)){
                    String spdf = PDFUtils.getText(downloadFilePath);
                    Set<String> sets = StkUtils.getMatchStrings(spdf, "(净利润增长率不低于)( ?)[0-9]*(\\.?)[0-9]+( *)(%)");
                    if(sets != null && sets.size() == 0){
                        sets = StkUtils.getMatchStrings(spdf, "(营业收入增长率不低于)( ?)[0-9]*(\\.?)[0-9]+( *)(%)");
                    }
                    if(sets!= null && sets.size() > 0){
                        //System.out.println(item);
                        double tmp = 0.0;
                        Iterator it = sets.iterator();
                        while(it.hasNext()){
                            String p = String.valueOf(it.next());
                            String s = StkUtils.getMatchStrings(p,"\\d+(\\.?)(\\d+)?( *)(%)").iterator().next();
                            double price = Double.parseDouble(StringUtils.replace(StringUtils.replace(s, " ", ""),"%",""));
                            if(price > tmp){
                                tmp = price;
                            }
                        }
                        //System.out.println("股权激励="+tmp);
                        if(tmp >= 100){
                            List params = new ArrayList();
                            params.add(index.getCode());
                            params.add(News.TYPE_5);
                            params.add("[股权激励] "+ tmp +"% - "+ title + " - "+ StkUtils.formatDate(new Date(item.getAnnouncementTime()), StkUtils.sf_ymd2));
                            params.add(index.getCode());
                            params.add(News.TYPE_5);
                            params.add("[股权激励] "+ tmp +"% - "+ title + " - "+ StkUtils.formatDate(new Date(item.getAnnouncementTime()), StkUtils.sf_ymd2));
                            JdbcUtils.insert(conn, "insert into stk_import_info select s_import_info_id.nextval,?,?,sysdate,1,?,null,null,null,null from dual "
                                    + "where not exists (select 1 from stk_import_info where code=? and type=? and info=?)", params);
                            //EmailUtils.send("发现股权激励", item.toString());
                        }
                        break;
                    }
                }
            }
            else if(title.contains("年度报告") || title.contains("季度报告全文")){
                String fileName = StringUtils.substringAfterLast(filePath, "/");
                String downloadFilePath = download(filePath,index);
                Text.insert(conn, 2, index.getCode(), title, "<a target='_blank' href='/stock/"+index.getCode()+"/notice/"+fileName+"'>报告链接</a>", Text.SUB_TYPE_STK_REPORT, new Date());
                String fileType = getFileType(downloadFilePath, sourceType);
                if("pdf".equalsIgnoreCase(fileType)){
                    //String spdf = PDFUtils.getText(downloadFilePath);
                    PDFUtils.toTextFile(downloadFilePath, "d:\\report.txt");
                    List<String> ls = FileUtils.readLines(new File("d:\\report.txt"));
                    boolean flag = false;
                    for(int i=0;i<ls.size();i++){
                        String line = ls.get(i);
                        if(StringUtils.contains(line, "已累计投入募集资金总额") ||
                                StringUtils.contains(line, "已累计使用募集资金总额")){
                            //System.out.println(line);
                            String n1 = StkUtils.getNumberFromString(line);
                            //System.out.println(n1);
                            if(n1 == null || "-".equals(n1))break;
                            int j = 1;
                            while(true){
                                line = ls.get(i-j);
                                if(StringUtils.contains(line, "r募集资金总额")){
                                    //System.out.println(line);
                                    String n2 = StkUtils.getNumberFromString(line);
                                    double d1 = Double.parseDouble(n1);
                                    double d2 = Double.parseDouble(n2);
                                    if(d1 < d2 * 0.8 && d1 > d2 * 0.2){
                                        //System.out.println(item + ",  n1="+n1+",n2="+n2);
                                        List params = new ArrayList();
                                        params.add(index.getCode());
                                        params.add(News.TYPE_21);
                                        params.add("[募集资金使用小于80%大于20%] "+ title + ",  n1="+n1+",n2="+n2);
                                        params.add(index.getCode());
                                        params.add(News.TYPE_21);
                                        params.add("[募集资金使用小于80%大于20%] "+ title + ",  n1="+n1+",n2="+n2);
                                        JdbcUtils.insert(conn, "insert into stk_import_info select s_import_info_id.nextval,?,?,sysdate,1,?,null,null,null,null from dual "
                                                + "where not exists (select 1 from stk_import_info where code=? and type=? and info=?)", params);
                                    }
                                    flag = true;
                                    break;
                                }
                                if(j++ > 10)break;
                            }
                            if(flag)break;
                        }
                    }
                    break;
                }
            }
            else if(title.contains("业绩预告修正")){
                String downloadFilePath = download(filePath,index);
                String fileType = getFileType(downloadFilePath, sourceType);
                if("pdf".equalsIgnoreCase(fileType)){
                    String spdf = PDFUtils.getText(downloadFilePath);
                    if(StringUtils.contains(spdf, "■同向上升") || title.contains("√ 同向上升") || title.contains("√同向上升")){
                        EmailUtils.send("业绩上修", StkUtils.wrapCodeAndNameAsHtml(index)+"-"+StkUtils.wrapLink(title, "http://www.cninfo.com.cn/"+filePath));
                    }
                }
            }
        }
    }


	public static void insertStkMonitor(Connection conn, Index index, String type, double price, Announcement item) throws Exception {
		/**
		 * param1 = 员工持股/非公开发行
		 * param2 = 价格
		 * param3 = 公告发布日期
		 * param4 = 不复权时，价格对应的最靠近的日期
		 * result1 = source file url
		 */
		String date = StkUtils.formatDate(new Date(item.getAnnouncementTime()), StkUtils.sf_ymd2);
		List params = new ArrayList();
		params.add(index.getCode());
		params.add(type);
		params.add(price);
		params.add(date);
		index.gc();
		List<K> ks = index.getKs(false);
		K k = index.getK(date,120);
		K k2 = index.getK(date);
		K tmpK = null;
		double tmp = 10000;
		do{
			double d = Math.abs(k2.getClose() - price);
			if(d < tmp){
				tmp = d;
				tmpK = k2;
			}
			k2 = k2.before(1);
			if(k2.getDate().compareTo(k.getDate()) <= 0){
				break;
			}
		}while(true);
		params.add(tmpK.getDate());
		params.add(StkUtils.number2String(index.getK().getClose()/tmpK.getClose(), 2));
		params.add("http://static.cninfo.com.cn/"+item.getAdjunctUrl());
		params.add(index.getCode());
		params.add("http://static.cninfo.com.cn/"+item.getAdjunctUrl());
		JdbcUtils.insert(conn, "insert into stk_monitor select s_monitor_id.nextval,?,2,1,null,sysdate,?,?,?,?,?,? from dual "
				+ "where not exists (select 1 from stk_monitor where code=? and result_1=?)", params);
	}

    //http://static.cninfo.com.cn/finalpage/2020-09-04/1208388950.PDF
	public static String download(String filePath, Index index) throws Exception {
		String downloadUrl = "http://static.cninfo.com.cn/"+filePath;
		String fileName = StringUtils.substringAfterLast(filePath, "/");
		String downloadFilePath = ConfigUtils.getProp("stk_report")+index.getCode()+"\\notice\\";
		File path = new File(downloadFilePath);
		if(!path.exists()){
			FileUtils.forceMkdir(path);
		}
		HttpUtils.download(downloadUrl,null, downloadFilePath, fileName);
		return downloadFilePath+fileName;
	}
	
	public static String getFileType(String downloadFilePath, String sourceType){
		String fileType = FileType.getFileType(downloadFilePath);
		if(!StringUtils.endsWithIgnoreCase(fileType, sourceType)){
			//System.out.println(sourceType+","+fileType);
			if("rtf".equalsIgnoreCase(fileType)){
				sourceType = "RTF";
			}else if("docx".equalsIgnoreCase(fileType) && "doc".equalsIgnoreCase(sourceType)){
				sourceType = "DOCX";
			}
		}
		return sourceType;
	}
}

@Setter
@Getter
class NoticeJsonRoot {
    private String classifiedAnnouncements;
    private int totalSecurities;
    private int totalAnnouncement;
    private int totalRecordNum;
    private List<Announcement> announcements;
    private String categoryList;
    private boolean hasMore;
    private int totalpages;
}

@Setter
@Getter
class Announcement {
    private String id;
    private String secCode;
    private String secName;
    private String orgId;
    private String announcementId;
    private String announcementTitle;
    private long announcementTime;
    private String adjunctUrl;
    private int adjunctSize;
    private String adjunctType;
    private String storageTime;
    private String columnId;
    private String pageColumn;
    private String announcementType;
    private String associateAnnouncement;
    private String important;
    private String batchNum;
    private String announcementContent;
    private String orgName;
    private String announcementTypeName;
}