package com.stk123.tool.util;

import java.io.StringReader;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

public class SqlUtils {
	
	public final static int TYPE_INSERT = 1;
	public final static int TYPE_UPDATE = 2;
	public final static int TYPE_DELETE = 3;
	
	private final static CCJSqlParserManager parserManager = new CCJSqlParserManager();
	
	public static Statement getStatement(String sql) throws JSQLParserException {
		return parserManager.parse(new StringReader(sql));
	}
	
	public static Table getTable(String sql) throws JSQLParserException {
		Statement statement = SqlUtils.getStatement(sql);
		if(statement instanceof Update){
			Update update = (Update)statement;
			return update.getTable();
		}else if(statement instanceof Insert){
			Insert insert = (Insert)statement;
			return insert.getTable();
		}else if(statement instanceof Delete){
			Delete delete = (Delete)statement;
			return delete.getTable();
		}else{
			throw new JSQLParserException("Not suppert sql:"+sql);
		}
	}
	
	public static String getTableName(String sql) throws JSQLParserException {
		return SqlUtils.getTable(sql).getName().toUpperCase();
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String sql = "update stk_pe set bias=?,insert_time=sysdate,name=to_char(?) where report_date=? and id=?";
		Update update = (Update)SqlUtils.getStatement(sql);
		System.out.println(update.getColumns());
		System.out.println(update.getExpressions());
		System.out.println(update.getTable());
		System.out.println(update.getWhere());
	}

}
