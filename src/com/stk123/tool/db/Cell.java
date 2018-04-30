package com.stk123.tool.db;

import java.io.Serializable;

public class Cell implements Serializable {
	private Column column;
	private Row row;
	
	private Object value;
	
	public Cell(Object value,Column col,Row row){
		this.value = value;
		this.column = col;
		this.row = row;
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public Row getRow() {
		return row;
	}

	public void setRow(Row row) {
		this.row = row;
	}

	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String getCellAsString() {
		return column.getSQLDataType().getValueAsString(value);
	}

	public String getCellAsSQL(){
		return column.getSQLDataType().getValueAsSQLString(value);
	}
	
	public boolean equals(Cell cell) {
		Object o1 = this.getValue();
		Object o2 = cell.getValue();
		if(o1 == null && o2 == null){
			return true;
		}else if(o1 != null && o2 != null){
			String s1 = this.getCellAsString();
			String s2 = cell.getCellAsString();
			return s1.equals(s2);
		}else{
			return false;
		}
	}
	
	public String toString(){
		return String.valueOf(value);
	}
	
}
