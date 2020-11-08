package com.stk123.web.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.stk123.tool.tree.Tree;
import com.stk123.tool.util.JsonUtils;
import com.stk123.web.StkConstant;
import com.stk123.web.bs.IndexService;
import com.stk123.web.bs.StkService;
import com.stk123.web.context.StkContext;

public class IndexAction {
	
	public final static StkService stkService = new StkService();
	public final static IndexService indexService = new IndexService();
	
	public String perform() throws Exception {
		return StkConstant.ACTION_SUCC;
	}
	
	public void getUpdownRank() throws Exception {
		StkContext sc = StkContext.getContext();
		List<String> codes = stkService.getUpdownCodes("desc");
		String json = stkService.getStksAsJson(codes);
		sc.setResponseAsJson(json);
	}
	
	public void getIndexTree() throws Exception {
		StkContext sc = StkContext.getContext();
		String json = indexService.getTreeJson();
		sc.setResponseAsJson(json);
	}
	
	public void index() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String sid = request.getParameter(StkConstant.PARAMETER_ID);
		Tree tree = indexService.getTree();
		String template = "undefined";
		if(sid != null && sid.length() > 0){
			int id = Integer.parseInt(sid);
			System.out.println(id);
			List<Map> list = null;
			if(id > 1000 && id < 2000){//自定义指标
				
			}else if(id > 2000 && id < 3000){//宏观经济指标
				
			}else if(id > 3000 && id < 4000){//小智慧指标
				if(id == 3001){
					list = indexService.bias(id);
				}
				
			}else if(id > 4000 && id < 5000){//个股K线指标
				
			}else if(id > 5000 && id < 6000){//个股财务指标
				
			}else if(id > 60000 && id < 70000){//行业平均PE
				template = tree.getNode(IndexService.ID_INDUSTRY_PE).getStkIndexNode().getChartTemplate();
				template = StringUtils.replace(template, "\"", "\\\"");
				list = indexService.industryPE(id - IndexService.ID_INDUSTRY_PE);
			}else if(id > 700000 && id < 800000) {
				list = indexService.getPPI(id);
			}
			
			String json = JsonUtils.getJsonString4JavaPOJO(list, StkConstant.DATE_FORMAT_YYYY_MM_DD);
			StringBuffer sb = new StringBuffer();
			sb.append("{\"template\":\"").append(template).append("\",\"datas\":").append(json).append("}");
			//System.out.println(sb.toString());
			sc.setResponse(sb.toString());
			return;
		}
	}
	
}
