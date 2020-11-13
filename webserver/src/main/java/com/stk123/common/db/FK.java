package com.stk123.common.db;

import java.util.LinkedList;

public class FK extends Contraint {
	
	protected Table parentTable;
	protected Table table;
	
	protected FK(String name, Table table) {
		super(name);
		this.table = table;
	}
	
	public LinkedList<Column> getParentColumns() {
		if(this.getParentTable() == null){
			throw new RuntimeException("FK "+this.getName()+" has not set parent table yet.");
		}
		return this.getParentTable().getPK().getColumns();
	}
	public LinkedList<Column> getColumns() {
		return columns;
	}

	public Table getParentTable() {
		return this.parentTable;
	}
	public PK getParentPK(){
		return this.parentTable.getPK();
	}
	
	public Table getTable(){
		return this.table;
	}

	public void setParentTable(Table parentTable) {
		this.parentTable = parentTable;
	}
	
	public String toString(){
		return "FK="+this.getName()+", columns="+this.getColumns();
	}

}

