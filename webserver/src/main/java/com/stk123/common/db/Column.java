package com.stk123.common.db;

import java.io.Serializable;

import com.stk123.common.db.sql.SQLDataType;


public class Column implements Serializable {
	
	private String name;
	private String type;
	private Table table;
	private SQLDataType sqlDataType;
	//private LinkedList<Cell> cells = new LinkedList<Cell>();
	
	private short dataLength, dataPrecision, dataScale;
	
	public Column(String name,String type,short dataLength,short dataPrecision,short dataScale,Table table) {
		this.name = name;
		this.type = type;
		this.dataLength = dataLength;
		this.dataPrecision = dataPrecision;
		this.dataScale = dataScale;
		this.table = table;
		if(table != null){
			sqlDataType = table.getDialect().getDataType(this);
		}
	}
	
	public Column(String name,Table table,SQLDataType sqlDataType){
		this.name = name;
		this.table = table;
		this.sqlDataType = sqlDataType;
	}
	
	public boolean isPK(){
		PK pk = this.table.getPK();
		if(pk == null){
			return false;
		}
		return pk.getColumn(this.name)!=null;
	}
	
	public boolean isIndexColumn(boolean unique) {
		for(Index index:this.table.getIndexs()){
			for(Column column:index.getColumns()){
				if(column.getName().equalsIgnoreCase(this.getName())){
					if(unique){
						if(index.isUnique())
							return true;
						else
							return false;
					}else{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public SQLDataType getSQLDataType(){
		return sqlDataType;
	}
	
	public void setSQLDataType(SQLDataType sqlDataType){
		this.sqlDataType = sqlDataType;
	}
	
	public String getSQLDataTypeAsString(){
		return type; 
	}
	
	public Class getType(){
		return this.getSQLDataType().getJavaDataType();
	}
	
	public short getDataLength() {
		return dataLength;
	}

	public short getDataPrecision() {
		return dataPrecision;
	}

	public short getDataScale() {
		return dataScale;
	}
	

	
//	public int size(){
//		return cells.size();
//	}

//	public Cell getCell(int i) {
//		return cells.get(i);
//	}
//
//	public LinkedList<Cell> getCells() {
//		return cells;
//	}
	
	
	public String toString(){
		return name;
	}
	
}
