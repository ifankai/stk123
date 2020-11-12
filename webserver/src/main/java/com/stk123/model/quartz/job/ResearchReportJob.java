package com.stk123.model.quartz.job;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.model.Text;
import com.stk123.tool.util.StkUtils;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.HtmlUtils;
import com.stk123.tool.util.HttpUtils;

public class ResearchReportJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try{
			System.out.println("ResearchReportJob executing...");
			getReportByType("1",Text.SUB_TYPE_COMPANY_RESEARCH);
			getReportByType("2",Text.SUB_TYPE_INDUSTRY_RESEARCH);
		} catch (Exception e) {
			EmailUtils.send("ResearchReportJob Error", ExceptionUtils.getExceptionAsString(e));
		}
	}

	public static void main(String[] args) throws Exception {
		//getReportByType("1",100);
		getReportByType("2",110);
	}
	
	/**
	 * @param type 1:公司调研  2:行业分析
	 * @param subType select * from stk_dictionary where type=400
	 */
	public static void getReportByType(String type, int subType) throws Exception {
		System.out.println("ResearchReportJob:"+type);
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			
			int pageNum = 1;
			do{
				boolean flag = false;
				String page = HttpUtils.get("http://www.hibor.com.cn/elitelist_"+pageNum+"_"+type+".html", null, "gb2312");
				//System.out.println(page);
				List<Node> divNodes = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "div", "class", "classbaogao_sousuo_list");
				for(Node div : divNodes){
					//System.out.println(span.toHtml());
					Node titleNode = HtmlUtils.getNodeByTagName(div, "a");
					String titleLink = HtmlUtils.getAttribute(titleNode, "href");
					String s = HttpUtils.get("http://www.hibor.com.cn/"+titleLink, null, "gb2312");
					Node contentNode = HtmlUtils.getNodeByAttribute(s, null, "class", "p_main");
					String content = contentNode==null?null:contentNode.toHtml();
					//System.out.println(content);
					String title = StkUtils.wrapLink(HtmlUtils.getAttribute(titleNode, "title"), "http://www.hibor.com.cn/"+titleLink) ;
					
					String code = null;
					if("1".equals(type)){
						code = StringUtils.split(titleNode.toPlainTextString(), "-")[2];
					}
					
					Node timeNode = HtmlUtils.getNodeByAttribute(div, null, "width", "17%");
					
					//System.out.println(pageNum+","+code+","+title+","+timeNode.toPlainTextString());
					long cnt = 0;
					if(code == null){
						cnt = Text.insert(conn, 2, title, content, subType, timeNode.toPlainTextString());
					}else{
						cnt = Text.insert(conn, 2,code, title, content, subType, timeNode.toPlainTextString());
					}
					if(cnt == 0){
						flag = true;
						break;
					}
				}
				if(flag)break;
				pageNum++;
				if(pageNum >= 2){
					break;
				}
			}while(true);
			
		}finally {
			if (conn != null) conn.close();
		}
	}

}
