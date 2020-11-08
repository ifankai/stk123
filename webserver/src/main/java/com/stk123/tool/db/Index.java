package com.stk123.tool.db;

import java.util.LinkedList;

public class Index extends Contraint {
	
	private boolean unique;
	private Table table;
	
	public Index(String name,boolean unique,Table table){
		super(name);
		this.unique = unique;
		this.table = table;
	}

	@Override
	public LinkedList<Column> getColumns() {
		if(super.columns == null){
			table.getIndexs();
		}
		return super.columns;
	}
	
	public boolean isUnique(){
		return unique;
	}
	
	public Table getTable(){
		return this.table;
	}
	
	public String toString(){
		return "Index="+this.getName()+", unique="+this.unique+", columns="+this.getColumns();
	}

}
