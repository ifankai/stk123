package com.stk123.task;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

import com.stk123.bo.Stk;
import com.stk123.bo.StkMonitor;
import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.model.News;
import com.stk123.model.Text;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.ConfigUtils;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.FileType;
import com.stk123.tool.util.HttpUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.tool.util.PDFUtils;

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
	
	public static void updateNotice(Connection conn, Index index) throws Exception{
		//http://www.cninfo.com.cn//disclosure/fulltext/stocks/fulltext1y/cninfo/002052.js?ver=20170818000000
		String page = HttpUtils.get("http://www.cninfo.com.cn//disclosure/fulltext/stocks/fulltext1y/cninfo/"+index.getCode()+".js?ver="+StkUtils.formatDate(new Date(), StkUtils.sf_ymd12), "gb2312");
		//System.out.println(page);
		if("404".equals(page)){
			return ;
		}
		String json = StringUtils.substringBetween(page, "=", "];");
		List<List> list = JsonUtils.getList4Json(json+"]", List.class);
		
		Set<String> prices1 = new HashSet<String>();
		Set<String> prices2 = new HashSet<String>();
		Date DateBefore = StkUtils.addDay(new Date(), -1);
		for(List item : list){
			Date createDate = StkUtils.sf_ymd.parse(String.valueOf(item.get(5)));
			if(createDate.before(DateBefore)){
				break;
			}
			
			String title = String.valueOf(item.get(2));
			//if(!(title.contains("��ȱ���") || title.contains("���ȱ���ȫ��")))continue;
			if(title.contains("Ա���ֹ�")){
				//System.out.println(title);
				String filePath = String.valueOf(item.get(1));
				String downloadFilePath = download(filePath,index);
				String sourceType = String.valueOf(item.get(3));
				String fileType = getFileType(downloadFilePath, sourceType);
				if("pdf".equalsIgnoreCase(fileType)){
					String spdf = PDFUtils.getText(downloadFilePath);
					Set<String> sets = StkUtils.getMatchStrings(spdf, "(���̼�)( ?)[0-9]*(\\.?)[0-9]+( ?)(Ԫ����)");
					if(sets != null && sets.size() == 0){
						sets = StkUtils.getMatchStrings(spdf, "(�������)( ?)[0-9]*(\\.?)[0-9]+( ?)(Ԫ/��)");
					}
					prices1.addAll(sets);
					if(sets!= null && sets.size() > 0){
						//System.out.println(item);
						String p = sets.iterator().next();
						String s = StkUtils.getMatchStrings(p,"\\d+(\\.?)(\\d+)?( *)(Ԫ)").iterator().next();
						double price = Double.parseDouble(StringUtils.replace(StringUtils.replace(s, " ", ""),"Ԫ",""));
						//System.out.println(price);							
						insertStkMonitor(conn, index, "Ա���ֹ�", price, item);
						
						break;
					}
				}
			}
			else if(StringUtils.contains(title, "�ǹ�������")){
				String filePath = String.valueOf(item.get(1));
				//System.out.println(filePath);
				String downloadFilePath = download(filePath,index);
				String sourceType = String.valueOf(item.get(3));
				String fileType = getFileType(downloadFilePath, sourceType);
				if("pdf".equalsIgnoreCase(fileType)){
					String spdf = PDFUtils.getText(downloadFilePath);
					
					if(createDate.after(StkUtils.addDay(new Date(), -1))){
						/*if(StkUtils.getMatchString(title, "����й�(֤���|֤ȯ�ල).{0,10}(��׼|���ͨ��|����)") != null){
							EmailUtils.sendAndReport("���ǹ������й�Ʊ����й�֤����׼��-"+ index.getName(), 
									StkUtils.wrapCodeAndNameAsHtml(index) + " - " + StkUtils.wrapLink(title, "http://www.cninfo.com.cn/information/companyinfo.html?fulltext?"+index.getCode()));
						}*/
						//System.out.println(downloadFilePath);
						if(StkUtils.getMatchString(title, "�ǹ�������.{0,15}(����|����)") != null){
							Set<String> matches = StkUtils.getMatchStrings(spdf,"����.{2,10}��������");
							matches.addAll(StkUtils.getMatchStrings(spdf,"����.{2,10}��������"));
							matches.addAll(StkUtils.getMatchStrings(spdf,"����.{2,10}����������"));
							matches.addAll(StkUtils.getMatchStrings(spdf,"����.{2,10}����������"));
							matches.addAll(StkUtils.getMatchStrings(spdf,"���н���.{0,6}��.{0,2}����"));
							
							if(matches.size() > 0){
								EmailUtils.sendAndReport("��ǹ�������-�������ڡ�-"+ index.getName(), 
										StkUtils.wrapCodeAndNameAsHtml(index)+" "+matches + " - " + StkUtils.wrapLink(title, "http://www.cninfo.com.cn/information/companyinfo.html?fulltext?"+index.getCode()));
							}
						}
					}
					
					Set<String> sets = StkUtils.getMatchStrings(spdf, "(�ǹ������й�Ʊ�۸�Ϊ)( ?)[0-9]*(\\.?)[0-9]+( *)(Ԫ/��)");
					if(sets != null && sets.size() == 0){
						sets = StkUtils.getMatchStrings(spdf, "(���м۸�Ϊ)( ?)[0-9]*(\\.?)[0-9]+( *)(Ԫ/��)");
					}
					if(sets != null && sets.size() == 0){
						sets = StkUtils.getMatchStrings(spdf, "(�����չ�˾��Ʊ���׾��ۣ���)( ?)[0-9]*(\\.?)[0-9]+( *)(Ԫ/��)");
					}
					if(sets != null && sets.size() == 0){
						sets = StkUtils.getMatchStrings(spdf, "(�������չ�Ʊ����)(.{0,50})(��)( ?)[0-9]*(\\.?)[0-9]+( *)(Ԫ/��)");
					}
					if(sets != null && sets.size() == 0){
						sets = StkUtils.getMatchStrings(spdf, "(��)( ?)[0-9]*(\\.?)[0-9]+( *)(Ԫ/��Ϊ���η��еķ��м۸�)");
					}
					if(sets != null && sets.size() == 0){
						sets = StkUtils.getMatchStrings(spdf, "(ÿ�ɷ��м�Ϊ����ң���)( ?)[0-9]*(\\.?)[0-9]+( *)(Ԫ)");
					}
					prices2.addAll(sets);
					if(sets!= null && sets.size() > 0){
						//System.out.println(item);
						String p = sets.iterator().next();
						String s = StkUtils.getMatchStrings(p,"\\d+(\\.?)(\\d+)?( *)(Ԫ)").iterator().next();
						double price = Double.parseDouble(StringUtils.replace(StringUtils.replace(s, " ", ""),"Ԫ",""));
						//System.out.println(price);							
						insertStkMonitor(conn, index, "�ǹ�������", price, item);
						
						break;
					}
				}
			}
			else if(StringUtils.contains(title, "��Ȩ����") || StringUtils.contains(title, "��Ʊ����")){
				//System.out.println(item);
				String filePath = String.valueOf(item.get(1));
				String downloadFilePath = download(filePath,index);
				String sourceType = String.valueOf(item.get(3));
				String fileType = getFileType(downloadFilePath, sourceType);
				if("pdf".equalsIgnoreCase(fileType)){
					String spdf = PDFUtils.getText(downloadFilePath);
					Set<String> sets = StkUtils.getMatchStrings(spdf, "(�����������ʲ�����)( ?)[0-9]*(\\.?)[0-9]+( *)(%)");
					if(sets != null && sets.size() == 0){
						sets = StkUtils.getMatchStrings(spdf, "(Ӫҵ���������ʲ�����)( ?)[0-9]*(\\.?)[0-9]+( *)(%)");
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
						//System.out.println("��Ȩ����="+tmp);							
						if(tmp >= 100){
							List params = new ArrayList();
							params.add(index.getCode());
							params.add(News.TYPE_5);
							params.add("[��Ȩ����] "+ tmp +"% - "+ title + " - "+item.get(5));
							params.add(index.getCode());
							params.add(News.TYPE_5);
							params.add("[��Ȩ����] "+ tmp +"% - "+ title + " - "+item.get(5));
							JdbcUtils.insert(conn, "insert into stk_import_info select s_import_info_id.nextval,?,?,sysdate,1,?,null,null,null,null from dual "
									+ "where not exists (select 1 from stk_import_info where code=? and type=? and info=?)", params);
							//EmailUtils.send("���ֹ�Ȩ����", item.toString());
						}
						break;
					}
				}
			}
			else if(title.contains("��ȱ���") || title.contains("���ȱ���ȫ��")){
				String filePath = String.valueOf(item.get(1));
				String fileName = StringUtils.substringAfterLast(filePath, "/");
				String downloadFilePath = download(filePath,index);
				Text.insert(conn, 2, index.getCode(), title, "<a target='_blank' href='/stock/"+index.getCode()+"/notice/"+fileName+"'>��������</a>", Text.SUB_TYPE_STK_REPORT, new Date());
				String sourceType = String.valueOf(item.get(3));
				String fileType = getFileType(downloadFilePath, sourceType);
				if("pdf".equalsIgnoreCase(fileType)){
					//String spdf = PDFUtils.getText(downloadFilePath);
					PDFUtils.toTextFile(downloadFilePath, "d:\\report.txt");
					List<String> ls = FileUtils.readLines(new File("d:\\report.txt"));
					boolean flag = false;
					for(int i=0;i<ls.size();i++){
						String line = ls.get(i);
						if(StringUtils.contains(line, "���ۼ�Ͷ��ļ���ʽ��ܶ�") ||
								StringUtils.contains(line, "���ۼ�ʹ��ļ���ʽ��ܶ�")){
							//System.out.println(line);
							String n1 = StkUtils.getNumberFromString(line);
							//System.out.println(n1);
							if(n1 == null || "-".equals(n1))break;
							int j = 1;
							while(true){
								line = ls.get(i-j);
								if(StringUtils.contains(line, "rļ���ʽ��ܶ�")){
									//System.out.println(line);
									String n2 = StkUtils.getNumberFromString(line);
									double d1 = Double.parseDouble(n1);
									double d2 = Double.parseDouble(n2);
									if(d1 < d2 * 0.8 && d1 > d2 * 0.2){
										//System.out.println(item + ",  n1="+n1+",n2="+n2);
										List params = new ArrayList();
										params.add(index.getCode());
										params.add(News.TYPE_21);
										params.add("[ļ���ʽ�ʹ��С��80%����20%] "+ title + ",  n1="+n1+",n2="+n2);
										params.add(index.getCode());
										params.add(News.TYPE_21);
										params.add("[ļ���ʽ�ʹ��С��80%����20%] "+ title + ",  n1="+n1+",n2="+n2);
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
			else if(title.contains("ҵ��Ԥ������")){
				String filePath = String.valueOf(item.get(1));
				//System.out.println(filePath);
				String downloadFilePath = download(filePath,index);
				String sourceType = String.valueOf(item.get(3));
				String fileType = getFileType(downloadFilePath, sourceType);
				if("pdf".equalsIgnoreCase(fileType)){
					String spdf = PDFUtils.getText(downloadFilePath);
					if(StringUtils.contains(spdf, "��ͬ������") || title.contains("�� ͬ������") || title.contains("��ͬ������")){
						EmailUtils.send("ҵ������", StkUtils.wrapCodeAndNameAsHtml(index)+"-"+StkUtils.wrapLink(title, "http://www.cninfo.com.cn/"+filePath));
					}
				}
			}
		}
	}
	
	public static void insertStkMonitor(Connection conn, Index index, String type, double price, List<String> item) throws Exception {
		/**
		 * param1 = Ա���ֹ�/�ǹ�������
		 * param2 = �۸�
		 * param3 = ���淢������
		 * param4 = ����Ȩʱ���۸��Ӧ�����������
		 * result1 = source file url
		 */
		List params = new ArrayList();
		params.add(index.getCode());
		params.add(type);
		params.add(price);
		params.add(StringUtils.replace(item.get(5), "-", ""));
		index.gc();
		List<K> ks = index.getKs(false);
		K k = index.getK(StringUtils.replace(item.get(5), "-", ""),120);
		K k2 = index.getK(StringUtils.replace(item.get(5), "-", ""));
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
		params.add("http://www.cninfo.com.cn/"+item.get(1));
		params.add(index.getCode());
		params.add("http://www.cninfo.com.cn/"+item.get(1));
		JdbcUtils.insert(conn, "insert into stk_monitor select s_monitor_id.nextval,?,2,1,null,sysdate,?,?,?,?,?,? from dual "
				+ "where not exists (select 1 from stk_monitor where code=? and result_1=?)", params);
	}
	
	public static String download(String filePath, Index index) throws Exception {
		String downloadUrl = "http://www.cninfo.com.cn/"+filePath;
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
