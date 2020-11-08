package com.stk123.web.action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stk123.bo.StkDictionary;
import com.stk123.bo.StkIndustryType;
import com.stk123.bo.StkKlineRankIndustry;
import com.stk123.bo.StkKlineRankIndustryStock;
import com.stk123.model.Index;
import com.stk123.model.Industry;
import com.stk123.task.StkUtils;
import com.stk123.tool.util.JdbcUtils;
import com.stk123.tool.util.JsonUtils;
import com.stk123.web.StkConstant;
import com.stk123.web.StkDict;
import com.stk123.web.WebUtils;
import com.stk123.web.bs.StkService;
import com.stk123.web.context.StkContext;
import com.stk123.web.pojo.GridBox;
import com.stk123.web.pojo.MetaData;
import com.stk123.web.pojo.Node;


public class IndustryAction {
	
	public final static StkService stkService = new StkService();
	
	public String perform() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		String id = request.getParameter(StkConstant.PARAMETER_ID);
		if(id != null && id.length() > 0){
			Connection conn = StkContext.getConnection();
			Industry industry = Industry.getIndustry(conn, id);
			sc.put(StkConstant.ATTRIBUTE_INDUSTRY_SELECT, industry);
		}
		return StkConstant.ACTION_SUCC;
	}
	
	public String dailyRank() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = StkContext.getConnection();
		Index sh = new Index(conn, Index.SH999999);
		List params = new ArrayList();
		params.add(sh.getK().getDate());
		List<StkKlineRankIndustry> ranks = JdbcUtils.list(conn, "select * from stk_kline_rank_industry where rank_date=? and rank <= 5 order by rank_days asc,rank asc", params, StkKlineRankIndustry.class);
		for(StkKlineRankIndustry rank : ranks){
			params.clear();
			params.add(rank.getRankId());
			List<StkKlineRankIndustryStock> stks = JdbcUtils.list(conn, "select * from stk_kline_rank_industry_stock where rank_id=?", params, StkKlineRankIndustryStock.class);
			rank.setStkKlineRankIndustryStock(stks);
			Industry industry = Industry.getIndustry(conn, rank.getIndustryId().toString());
			rank.setStkIndustryType(industry.getType());
		}
		sc.put("industry_rank", ranks);
		return StkConstant.ACTION_SUCC;
	}
	
	public void list() throws Exception {
		StkContext sc = StkContext.getContext();
		Connection conn = StkContext.getConnection();
		List<StkIndustryType> indTypes = JdbcUtils.list(conn, "select * from stk_industry_type order by name asc", StkIndustryType.class);
		List<StkDictionary> sources = StkDict.getDictionaryOrderByParam(StkDict.INDUSTRY_SOURCE);
		
		List list = new ArrayList();
		for(StkDictionary source : sources){
			Map map = new HashMap();
			map.put("data", source.getText());
			if(source.getParam2().equals(StkConstant.NUMBER_ONE)){
				map.put("state", "open");
			}
			List<Node> nodes = new ArrayList<Node>();
			for(StkIndustryType indType : indTypes){
				if(indType.getSource().equals(source.getKey())){
					Node node = new Node();
					node.data = indType.getName();
					MetaData md = new MetaData();
					md.id = indType.getId().toString();
					//md.title = indType.getSource();
					node.attr = md;
					nodes.add(node);
				}
			}
			map.put("children", nodes);
			list.add(map);
		}
		String json = JsonUtils.getJsonString4JavaPOJO(list);
		//System.out.println(json);
		sc.setResponse(json);
	}
	
	public void select() throws Exception {
		StkContext sc = StkContext.getContext();
		HttpServletRequest request = sc.getRequest();
		Connection conn = StkContext.getConnection();
		String id = request.getParameter(StkConstant.PARAMETER_ID);
		Industry industry = Industry.getIndustry(conn, id);
		List<Index> stks = industry.getIndexs();
		String json = "{\"data\":"+JsonUtils.getJsonString4JavaPOJO(stkService.getStkList(stks, 2))+"}";
		//System.out.println(json);
		sc.setResponse(json);
	}
	
	
}



