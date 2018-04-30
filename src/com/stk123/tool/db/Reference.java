package com.stk123.tool.db;

import java.util.LinkedList;

import com.stk123.tool.db.sql.SQLDataType;


public class Reference extends FK {
	
	private LinkedList<Column> childTableColumns = new LinkedList<Column>();
	private LinkedList<Column> parentTableColumns = new LinkedList<Column>();
	
	public Reference(String name, Table table, String[] childTableColumnsName, Table parentTable, String[] parentTableColumnsName) {
		super(name,table);
		this.parentTable = parentTable;
		for(String columnName:childTableColumnsName){
			Column column = new Column(columnName.toUpperCase(),table,table.getColumn(columnName).getSQLDataType());
			childTableColumns.add(column);
		}
		for(String columnName:parentTableColumnsName){
			Column column = new Column(columnName.toUpperCase(),parentTable,parentTable.getColumn(columnName).getSQLDataType());
			parentTableColumns.add(column);
		}
	}
	
	public LinkedList<Column> getColumns(){
		return childTableColumns;
	}
	
	public Column getColumn(String columnName){
		for(Column column:childTableColumns){
			if(column.getName().equalsIgnoreCase(columnName)){
				return column;
			}
		}
		return null;
	}
	
	public LinkedList<Column> getParentColumns(){
		return parentTableColumns;
	}
	
	public Column getParentPKColumns(String columnName){
		for(Column column:parentTableColumns){
			if(column.getName().equalsIgnoreCase(columnName)){
				return column;
			}
		}
		return null;
	}
	
	
	public void setChildTableColumnSQLDataType(String columnName,SQLDataType sqlDataType) {
		this.getColumn(columnName).setSQLDataType(sqlDataType);
	}
	
	public void setParentTableColumnSQLDataType(String columnName,SQLDataType sqlDataType) {
		this.getParentPKColumns(columnName).setSQLDataType(sqlDataType);
	}

	
}
