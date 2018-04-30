package com.stk123.web;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.stk123.bo.StkDictionary;
import com.stk123.tool.db.connection.Pool;
import com.stk123.tool.util.JdbcUtils;


public class StkDict {
	
	public final static Integer MONITOR_K_PARAM1 = new Integer(1);
	public final static Integer MONITOR_K_PARAM2 = new Integer(2);
	
	public final static Integer XUEQIU_COMMENT = new Integer(3);
	public final static Integer XUEQIU_ARTICLE = new Integer(4);
	public final static Integer XUEQIU_ZHUTIE = new Integer(5);//雪球主贴
	
	public final static Integer NIUSAN = new Integer(20);//牛散
	
	public final static Integer INTERNET_SEARCH_TYPE = new Integer(10);//internet search type
	
	public final static Integer STKS_COLUMN_NAMES = new Integer(1000);//多股同列下显示的列名
	public final static Integer INDUSTRY_SOURCE = new Integer(300);//行业分类来源
	public final static Integer TEXT_SUB_TYPE = new Integer(400);//文档子类型
	
	private static Map<Integer, Map<String, StkDictionary>> dict = new HashMap<Integer, Map<String, StkDictionary>>();
	
	static {
		try {
			init();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private final static String SQL_SELECT_DICT = "select * from stk_dictionary order by type";
	public static void init() throws SQLException{
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			List<StkDictionary> dicts = JdbcUtils.list(conn, SQL_SELECT_DICT, StkDictionary.class);
			for(StkDictionary sd : dicts){
				if(dict.get(sd.getType()) == null){
					Map<String, StkDictionary> map = new TreeMap<String, StkDictionary>();
					map.put(sd.getKey(), sd);
					dict.put(sd.getType(), map);
				}else{
					dict.get(sd.getType()).put(sd.getKey(), sd);
				}
			}
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public static List<StkDictionary> getDictionary(Integer type){
		return new ArrayList(dict.get(type).values());
	}
	
	public static List<StkDictionary> getDictionaryOrderByParam(Integer type){
		List<StkDictionary> list = getDictionary(type);
		Collections.sort(list, new Comparator<StkDictionary>(){
			public int compare(StkDictionary arg0, StkDictionary arg1) {
				int d0 = Integer.parseInt(arg0.getParam());
				int d1 = Integer.parseInt(arg1.getParam());
				return (d0-d1);
			}
		});
		return list;
	}
	
	public static Map<String, StkDictionary> getDict(Integer type){
		return dict.get(type);
	}
	
	public static String getDict(Integer type, String key){
		return dict.get(type).get(key).getText();
	}
	
	public static String getDict(Integer type, int key){
		return getDict(type, String.valueOf(key));
	}
	
	public static String htmlOptions(Integer type){
		Map<String, StkDictionary> map = getDict(type);
		StringBuffer sb = new StringBuffer();
		for(Map.Entry<String, StkDictionary> dict : map.entrySet()){
			sb.append("<option value='"+dict.getKey()+"'>").append(dict.getValue().getText()).append("</option>");
		}
		return sb.toString();
	}
	
	public static String htmlOptions(Integer type, String selectValue){
		Map<String, StkDictionary> map = getDict(type);
		StringBuffer sb = new StringBuffer();
		for(Map.Entry<String, StkDictionary> dict : map.entrySet()){
			sb.append("<option value='"+dict.getKey()+"' "+(selectValue.equals(dict.getKey())?"selected":"")+">").append(dict.getValue().getText()).append("</option>");
		}
		return sb.toString();
	}
	
	
}
