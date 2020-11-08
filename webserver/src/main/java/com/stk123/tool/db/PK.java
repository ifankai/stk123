package com.stk123.tool.db;

import java.util.LinkedList;

import com.stk123.tool.db.sql.Dialect;


public class PK extends Contraint {
	
	private Table table;
	
	protected PK(String name,Table table) {
		super(name);
		this.table = table;
	}
	
	public LinkedList<Column> getColumns() {
		if(super.columns == null){
			table.getPK();
		}
		return super.columns;
	}
	
	public Table getTable(){
		return table;
	}
	
	public String getPKAsOrderBySQL(){
		return new StringBuffer().append(Dialect.ORDER_BY).append(this.getColumnsAsSQL()).toString();
	}
	
	public String toString(){
		return "PK="+this.getName()+", columns="+super.columns;
	}

}
