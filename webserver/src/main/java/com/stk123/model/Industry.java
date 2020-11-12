package com.stk123.model;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stk123.tool.util.*;
import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.tags.TableTag;

import com.stk123.bo.StkIndustry;
import com.stk123.bo.StkIndustryType;
import com.stk123.bo.StkKlineRankIndustry;
import com.stk123.tool.db.connection.Pool;


@SuppressWarnings("serial")
public class Industry implements Serializable {
	
	public final static String INDUSTRY_WIND = "wind";
	public final static String INDUSTRY_HEXUN_CONCEPTION = "hexun_conception";
	public final static String INDUSTRY_CNINDEX = "cnindex";
	public final static String INDUSTRY_MY_FNTYPE = "my_industry_fntype";
	public final static String INDUSTRY_EASYMONEY_MEIGU = "easymoney_meigu";
	
	//private static Map<String,StkIndustryType> industryTypes = new HashMap<String,StkIndustryType>();
	private StkIndustryType type;
	public double changePercent;
	
	public Industry(StkIndustryType type){
		this.type = type;
	}
	
	public final static String SQL_SELECT_INDUSTRY_TYPE_ALL = "select * from stk_industry_type";
	
	public static Map<String,StkIndustryType> getIndustryType(Connection conn){
		Map<String,StkIndustryType> industryTypes = (Map<String,StkIndustryType>)CacheUtils.getForever(CacheUtils.KEY_INDUSTRY_TYPE);
		if(industryTypes != null){
			//return industryTypes;
		}
		//if(industryTypes.size() > 0)return industryTypes;
		industryTypes = new HashMap<String,StkIndustryType>();
		List<StkIndustryType> list = JdbcUtils.list(conn, SQL_SELECT_INDUSTRY_TYPE_ALL , StkIndustryType.class);
		for(StkIndustryType type : list){
			industryTypes.put(type.getId().toString(), type);
		}
		CacheUtils.putForever(CacheUtils.KEY_INDUSTRY_TYPE, industryTypes);
		return industryTypes;
	}
	
	@Deprecated
	public static List<Industry> getIndsutriesBySource(Connection conn, String source){
		List params = new ArrayList();
		List<Industry> inds = new ArrayList<Industry>();
		params.add(source);
		List<StkIndustryType> list = JdbcUtils.list(conn, "select * from stk_industry_type where source=? order by name", params, StkIndustryType.class);
		for(StkIndustryType type : list){
			inds.add(new Industry(type));
		}
		return inds;
	}
	
	public static Industry getIndustry(Connection conn, String id){
		StkIndustryType type = Industry.getIndustryType(conn).get(id);
		return new Industry(type);
	}
	
	public final static String SQL_SELECT_STK_BY_INDUSTRY = "select * from stk_industry where industry=?";
	public List<Index> getIndexs() throws Exception {
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List<String> codes = (List<String>)CacheUtils.getForever(CacheUtils.KEY_INDUSTRY_STK + type.getId());
			if(codes == null){
				codes = new ArrayList<String>();
				List params = new ArrayList();
				params.add(type.getId());
				List<StkIndustry> list = JdbcUtils.list(conn, SQL_SELECT_STK_BY_INDUSTRY, params, StkIndustry.class);
				for(StkIndustry stk : list){
					codes.add(stk.getCode());
				}
				CacheUtils.putForever(CacheUtils.KEY_INDUSTRY_STK + type.getId(), codes);
			}
			List<Index> indexs = new ArrayList<Index>();
			for(String code : codes){
				indexs.add(new Index(conn, code));
			}
			return indexs;
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public static StkKlineRankIndustry getIndustryRank(Connection conn, Integer industryId, String rankDate, int days){
		List params = new ArrayList();
		params.add(industryId);
		params.add(rankDate);
		params.add(days);
		StkKlineRankIndustry indRank = JdbcUtils.load(conn, "select * from stk_kline_rank_industry where industry_id=? and rank_date=? and rank_days=?", params, StkKlineRankIndustry.class);
		return indRank;
	}
	
	public static StkKlineRankIndustry getIndustryRank(Connection conn, String rankDate,int days, int rank){
		List params = new ArrayList();
		params.add(rankDate);
		params.add(days);
		params.add(rank);
		StkKlineRankIndustry indRank = JdbcUtils.load(conn, "select * from stk_kline_rank_industry where rank_date=? and rank_days=? and rank=?", params, StkKlineRankIndustry.class);
		indRank.setStkIndustryType(Industry.getIndustry(conn, indRank.getIndustryId().toString()).getType());
		return indRank;
	}
	
	
	public static StkIndustryType insertOrLoadIndustryType(Connection conn, String industryName, String parentName, String source) throws Exception{
		List params = new ArrayList();
		StkIndustryType parentType = null;
		if(parentName != null && parentName.length() > 0){
			params.clear();
			params.add(parentName);
			params.add(source);
			parentType = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source=?",params, StkIndustryType.class);
			if(parentType == null){
				params.clear();
				params.add(parentName);
				params.add(source);
				int n = JdbcUtils.insert(conn, "insert into stk_industry_type(id,name,source) values(s_industry_type_id.nextval,?,?)", params);
				if(n > 0){
					EmailUtils.send("新行业 - "+ parentName, "insertOrLoadIndustryType - 1");
				}
				params.clear();
				params.add(parentName);
				params.add(source);
				parentType = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source=?",params, StkIndustryType.class);
			}
		}
		
		params.clear();
		params.add(industryName);
		params.add(source);
		StkIndustryType type = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source=?",params, StkIndustryType.class);
		if(type == null){
			params.clear();
			params.add(industryName);
			params.add(source);
			if(parentType != null){
				params.add(parentType.getId());
			}else{
				params.add(null);
			}
			int n = JdbcUtils.insert(conn, "insert into stk_industry_type(id,name,source,parent_id) values(s_industry_type_id.nextval,?,?,?)", params);
			if(n > 0){
				EmailUtils.send("新行业 - "+ parentName, "insertOrLoadIndustryType - 2");
			}
			params.clear();
			params.add(industryName);
			params.add(source);
			type = JdbcUtils.load(conn, "select * from stk_industry_type where name=? and source=?",params, StkIndustryType.class);
		}
		return type;
	}

    public static StkIndustryType insertOrLoadIndustryType(Connection conn, String industryName, String code, String parentCode, String source) throws Exception{
        List params = new ArrayList();
        StkIndustryType parentType = null;
        if(parentCode != null && parentCode.length() > 0){
            params.clear();
            params.add(parentCode);
            params.add(source);
            parentType = JdbcUtils.load(conn, "select * from stk_industry_type where code=? and source=?",params, StkIndustryType.class);
            if(parentType == null){
                //throw new Exception("Please insert parent code/name firstly:"+parentCode);
                ExceptionUtils.insertLog(conn, "000000", new Exception("Please insert parent code/name firstly:"+parentCode));
                return null;
            }
        }

        params.clear();
        params.add(code);
        params.add(source);
        StkIndustryType type = JdbcUtils.load(conn, "select * from stk_industry_type where code=? and source=?",params, StkIndustryType.class);
        if(type == null){
            params.clear();
            params.add(industryName);
            params.add(source);
            params.add(code);
            if(parentType != null){
                params.add(parentType.getCode());
            }else{
                params.add(null);
            }
            int n = JdbcUtils.insert(conn, "insert into stk_industry_type(id,name,source,code,parent_code) values(s_industry_type_id.nextval,?,?,?,?)", params);
            if(n > 0){
                //EmailUtils.send("新行业 - "+ industryName, "insertOrLoadIndustryType - 2");
            }
            params.clear();
            params.add(code);
            params.add(source);
            type = JdbcUtils.load(conn, "select * from stk_industry_type where code=? and source=?",params, StkIndustryType.class);
        }
        return type;
    }
	
	public static int addStk(Connection conn, StkIndustryType type, String stkCode){
		List params = new ArrayList();
		params.add(stkCode);
		params.add(type.getId());
		params.add(stkCode);
		params.add(type.getId());
		return JdbcUtils.insert(conn, "insert into stk_industry(code,industry) select ?,? from dual where not exists (select 1 from stk_industry where code=? and industry=?)", params);
	}
	
	public static void deleteAllStks(Connection conn, StkIndustryType type){
		List params = new ArrayList();
		params.add(type.getId());
		JdbcUtils.delete(conn, "delete from stk_industry where industry=?", params);
	}
	
	public static void updateCapitalFlow(Connection conn, String date, String category) throws Exception {
		int totalPage = 1;
		int curPage = 1;
		do{
			//http://data.10jqka.com.cn/funds/hyzjl/field/tradezdf/order/desc/page/2/ajax/1/
			String page = HttpUtils.get("http://data.10jqka.com.cn/funds/"+category+"/field/tradezdf/order/desc/page/"+curPage+"/ajax/1/", "gbk");
			//System.out.println(page);
			Node table = HtmlUtils.getNodeByAttribute(page, null, "class", "m-table J-ajax-table");
			List<List<String>> lists = HtmlUtils.getListFromTable((TableTag)table, 0);
			for(List<String> list : lists){
				//System.out.println(list);
				String code = JdbcUtils.load(conn, "select code from stk where name=?",list.get(1), String.class);
				List params = new ArrayList();
				params.add(code);
				params.add(date);
				params.add(list.get(6));
				params.add(code);
				params.add(date);
				//System.out.println(params);
				JdbcUtils.insert(conn, "insert into stk_capital_flow select ?,?,?,null,null,null,null,null,null,null,null,null,sysdate from dual where not exists (select 1 from stk_capital_flow where code=? and flow_date=?)", params);

			}
			Node nodePage = HtmlUtils.getNodeByAttribute(page, null, "class", "m-page J-ajax-page");
			Node nodeSpan = HtmlUtils.getNodeByTagName(nodePage, "span");
			totalPage = Integer.parseInt(StringUtils.substringAfter(nodeSpan.toPlainTextString(), "/"));
			curPage++;
			if(curPage > totalPage)break;
		}while(true);
	}

	public StkIndustryType getType() {
		return type;
	}

	public void setType(StkIndustryType type) {
		this.type = type;
	}
	
	
}
