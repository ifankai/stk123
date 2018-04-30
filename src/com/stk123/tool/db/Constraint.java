package com.stk123.tool.db;

import java.util.LinkedList;

abstract class Contraint {
	
	private String name;
	public LinkedList<Column> columns;
	
	protected Contraint(String name) {
		this.name = name;
	}
	
	public void setColumn(int position,Column column){
		if(columns == null){
			columns = new LinkedList<Column>();
		}
		while(position > columns.size()){
			columns.addLast(null);
		}
		columns.set(position-1, column);
	}

	abstract public LinkedList<Column> getColumns();
	
	public String getColumnsAsSQL(){
		return this.getColumnsAsSQL(this.getColumns());
	}
	
	public String getColumnsAsSQL(LinkedList<Column> columns) {
		StringBuffer statement = new StringBuffer();
		if(columns.size()>0){
			statement.append(columns2SQLInNumber(columns));
		}
		return statement.toString();
	}
	
	private String columns2SQLInNumber(LinkedList<Column> columns) {
		StringBuffer sb = new StringBuffer();
		for (Column col:columns){
			sb.append(",").append(col.getName());
		}
		return sb.substring(1);
	}
	
	public Column getColumn(String columnName){
		for(Column column:getColumns()){
			if(column.getName().equalsIgnoreCase(columnName)){
				return column;
			}
		}
		//error("Column ["+columnName+"] is not exists in "+this.getName());
		return null;
	}
	
	public Column getColumn(int idx) {
		return columns.get(idx);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean equals(Object o) {
		return this.name.equals(((Contraint)o).name);
	}
	
	public static void warning(String msg){
		System.err.println("[WARNING]"+msg);
	}
	
	public static void error(String msg){
		System.err.println("[ERROR]"+msg);
	}
	
	
}
