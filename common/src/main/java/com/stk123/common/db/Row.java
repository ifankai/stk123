package com.stk123.common.db;

import java.io.Serializable;
import java.util.LinkedList;

import com.stk123.common.db.sql.Dialect;
import com.stk123.common.db.sql.SQLDataType;



public class Row implements Serializable {
	
	private Table table;
	private LinkedList<Cell> cells = new LinkedList<Cell>();
	
	public Row(Table table){
		this.table = table;
	}
	
	public static String getSQLWithParentheses(String sql){
		StringBuffer statement = new StringBuffer();
		statement.append("(").append(sql).append(")");
		return statement.toString();
	}

	public void addCell(Cell cell){
		this.cells.add(cell);
	}
	
	public Cell getCell(String columnName){
		for(Cell cell:cells){
			if(cell.getColumn().getName().equalsIgnoreCase(columnName)){
				return cell;
			}
		}
		return null;
	}
	
	public Cell getCell(int columnNumber){
		return cells.get(columnNumber);
	}
	
	public Object getValue(String columnName){
		return getCell(columnName).getValue();
	}
	
	public Object getValue(int columnNumber){
		return cells.get(columnNumber-1).getValue();
	}
	
	public LinkedList<Cell> getCells(){
		return this.cells;
	}
	
	public Row getRow(LinkedList<Column> columns){
		Row columnsRow = new Row(this.table);
		for(Column column:columns){
			Cell cell = this.getCell(column.getName());
			columnsRow.addCell(cell);
		}
		return columnsRow;
	}
	
	public Row getRow(Column column){
		Row columnRow = new Row(this.table);
		Cell cell = this.getCell(column.getName());
		columnRow.addCell(cell);
		return columnRow;
	}
	
	public LinkedList<Column> getColumns(){
		LinkedList<Column> columns = new LinkedList<Column>();
		for(Cell cell:cells){
			columns.add(cell.getColumn());
		}
		return columns;
	}
	
	public String getColumnsAsSQL(){
		return getColumnsAsSQL(getColumns());
	}
	
	public String getColumnsAsSQL(LinkedList<Column> columns){
		StringBuffer sb = new StringBuffer();
		for (Column col:columns){
			sb.append(Dialect.COLUMN_SEPARATOR).append(col.getName());
		}
		return sb.substring(1);
	}
	
	public String getColumnsAsSQLWithParentheses(){
		return Row.getSQLWithParentheses(this.getColumnsAsSQL());
	}
	
	public String getColumnsAsSQLWithParentheses(LinkedList<Column> columns){
		return Row.getSQLWithParentheses(this.getColumnsAsSQL(columns));
	}
	
	public String getRowAsSQL(){
		return this.getCellsAsSQL();
	}
	public String getRowAsSQL(Column column){
		return this.getCellsAsSQL(column);
	}
	
	public String getRowAsSQLWithParentheses(){
		return Row.getSQLWithParentheses(getCellsAsSQL());
	}
	
	public String getRowAsWhereConditionSQL(){
		return getRowAsWhereConditionSQL(getColumns());
	}
	public String getRowAsWhereConditionSQL(LinkedList<Column> columns){
		StringBuffer whereCondition = new StringBuffer();
		for(int i=0;i<columns.size();i++){
			Column column = columns.get(i);
			if(i == 0){
				whereCondition.append(column.getName()).append(Dialect.EQUAL).append(this.getCell(column.getName()).getCellAsSQL());
			}else{
				whereCondition.append(Dialect.AND).append(column.getName()).append(Dialect.EQUAL).append(this.getCell(column.getName()).getCellAsSQL());
			}
		}
		return whereCondition.toString();
	}
	
	public String getPKAsWhereConditionSQL(){
		return this.getRowAsWhereConditionSQL(table.getPK().getColumns());
	}
	
	public String getInsertSQL(){
		String sql = new StringBuffer().append(Dialect.INSERT_INTO).append(this.table.getName()).append("(")
			.append(this.table.getColumnsAsSQL()).append(") VALUES ").append(this.getRowAsSQLWithParentheses()).toString();
		return sql;
	}
	
	public String getUpdateSQL(){
		String sql = new StringBuffer().append(Dialect.UPDATE).append(this.table.getName()).append(" SET (")
			.append(this.table.getColumnsAsSQL()).append(") = (").append(Dialect.SELECT).append(this.getRowAsSQL())
			.append(" FROM DUAL) WHERE ").append(this.getPKAsWhereConditionSQL()).toString();
		return sql;
	}
	
	public String getDeleteSQL(){
		String sql = new StringBuffer().append(Dialect.DELETE_FROM).append(this.table.getName())
			.append(Dialect.WHERE).append(this.getPKAsWhereConditionSQL()).toString();
		return sql;
	}
	
	public String getInsertOrUpdateSQL(){
		String updateSql = new StringBuffer().append(Dialect.UPDATE).append(this.table.getName()).append(" SET (")
			.append(this.table.getColumnsAsSQL()).append(") = (").append(Dialect.SELECT).append(this.getRowAsSQL())
			.append(" FROM DUAL) WHERE ").append(this.getPKAsWhereConditionSQL()).toString();
		String insertSql = new StringBuffer().append(Dialect.INSERT_INTO).append(this.table.getName()).append("(")
			.append(this.table.getColumnsAsSQL()).append(") SELECT ").append(this.getRowAsSQL())
			.append(" FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM ").append(this.table.getName())
			.append(Dialect.WHERE).append(this.getPKAsWhereConditionSQL()).append(")").toString();
		return updateSql+";\n"+insertSql;
	}
	
	private String getCellsAsSQL(){
		StringBuffer sb = new StringBuffer();
		for (Cell cell:cells){
			sb.append(",").append(cell.getColumn().getSQLDataType().getValueAsSQLString(cell.getValue()));
		}
		return sb.substring(1);
	}
	private String getCellsAsSQL(Column column){
		StringBuffer sb = new StringBuffer();
		for (Cell cell:cells){
			SQLDataType sqlDataType = cell.getColumn().getSQLDataType();
			if(cell.getColumn().getName().equals(column.getName())){
				sqlDataType = column.getSQLDataType();
			}
			sb.append(",").append(sqlDataType.getValueAsSQLString(cell.getValue()));
		}
		return sb.substring(1);
	}
	
	public boolean equals(Row row){
		if(this.getCells().size() != row.getCells().size()){
			return false;
		}
		boolean cellEqual = true;
		for(Cell c1:this.getCells()){
			String column1 = c1.getColumn().getName();
			Cell c2 = row.getCell(column1);
			if(c2 == null){
				return false;
			}
			cellEqual = c1.equals(c2);
			if(!cellEqual){
				return false;
			}
		}
		return true;
	}
	
	public String toString(){
		return cells.toString();
	}
	
}
