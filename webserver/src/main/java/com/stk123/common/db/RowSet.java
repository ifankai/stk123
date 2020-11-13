package com.stk123.common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import com.stk123.common.db.sql.Dialect;
import com.stk123.common.db.util.DBUtil;


public class RowSet {

	private Table table;
	private LinkedList<Row> rows;
	
	private String whereCondition;
	private String orderby;
	
	public RowSet(Table table){
		this.table = table;
	}
	
	public RowSet(Table table,String whereCondition){
		this(table);
		this.whereCondition = whereCondition;
	}
	
	/*public RowSet(String tableName,String whereCondition){
		this(Table.getInstance(tableName),whereCondition);
	}*/
	
	public void setOrderBy(String orderby){
		this.orderby = orderby;
	}
	
	public static String getSQLWithParentheses(String sql){
		StringBuffer statement = new StringBuffer();
		statement.append("(").append(sql).append(")");
		return statement.toString();
	}
	
	public String getRowsAsSQL(LinkedList<Column> columns){
		StringBuffer sb = new StringBuffer();
		LinkedList<Row> rows = this.getRows(columns);
		for (Row row:rows){
			if(columns.size() == 1){
				sb.append(Dialect.COLUMN_SEPARATOR).append(row.getRowAsSQL());
			}else{
				sb.append(Dialect.COLUMN_SEPARATOR).append(row.getRowAsSQLWithParentheses());
			}
		}
		rows.clear();
		rows = null;
		return sb.substring(1);
	}
	
	public String getRowsAsSQL(Column column){
		StringBuffer sb = new StringBuffer();
		LinkedList<Row> rows = this.getRows(column);
		for (Row row:rows){
			sb.append(Dialect.COLUMN_SEPARATOR).append(row.getRowAsSQL(column));
		}
		rows.clear();
		rows = null;
		return sb.substring(1);
	}
	
	public String getRowsAsWhereConditionSQL(LinkedList<Column> columns){
		StringBuffer whereCondition = new StringBuffer();
		LinkedList<Row> rows = this.getRows();
		for(int i=0;i<columns.size();i++){
			Column column = columns.get(i);
			String inOrEqual = null;
			String conditionWithParenthesesOrNot = null;
			if(rows.size() == 1){
				inOrEqual = Dialect.EQUAL;
				conditionWithParenthesesOrNot = this.getRowsAsSQL(column);
			}else{
				inOrEqual = Dialect.IN;
				conditionWithParenthesesOrNot = RowSet.getSQLWithParentheses(this.getRowsAsSQL(column));
			}
			if(i == 0){
				whereCondition.append(column.getName()).append(inOrEqual).append(conditionWithParenthesesOrNot);
			}else{
				whereCondition.append(Dialect.AND).append(column.getName()).append(inOrEqual).append(conditionWithParenthesesOrNot);
			}
		}
		return whereCondition.toString();
	}
	
	public LinkedList<Row> getRows(LinkedList<Column> columns){
		if(rows == null){
			rows = getRows();
		}
		LinkedList<Row> columnsRows = new LinkedList<Row>();
		for(Row row:rows){
			columnsRows.add(row.getRow(columns));
		}
		return columnsRows;
	}
	
	public LinkedList<Row> getRows(Column column){
		if(rows == null){
			rows = getRows();
		}
		LinkedList<Row> columnsRows = new LinkedList<Row>();
		for(Row row:rows){
			columnsRows.add(row.getRow(column));
		}
		return columnsRows;
	}
	
	public LinkedList<Row> getRows(int offset, int limit){
		if(rows != null){
			return rows;
		}
		LinkedList<Row> rows = new LinkedList<Row>();
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		String sql = null;
		try {
			con = this.table.getDBConnection();
			sql = new StringBuffer().append(Dialect.SELECT).append(this.table.getColumnsAsSQL())
									.append(Dialect.FROM).append(this.table.getName())
									.append(whereCondition==null?"":(Dialect.WHERE+whereCondition))
									.append(orderby==null?(table.getPK()==null?"":table.getPK().getPKAsOrderBySQL()):orderby).toString();
			pst = con.prepareStatement(sql);
			//System.out.println("sql="+sql);
			rs = pst.executeQuery();
			while(rs.next()){
				Row row = new Row(this.table);
				for(Column col:this.table.getColumns()){
					Cell cell = new Cell(col.getSQLDataType().getSQLValue(rs, col.getName()),col,row);
					row.addCell(cell);
				}
				rows.add(row);
			}
		} catch(Exception e){
			Table.error(sql);
			e.printStackTrace();
		} finally {
			DBUtil.closeAll(rs, pst, con);
		}
		return rows;
	}
	
	public LinkedList<Row> getRows(){
		return this.getRows(0, Integer.MAX_VALUE);
	}
	
	
	public Table getTable(){
		return this.table;
	}
	public PK getTablePK(){
		return this.table.getPK();
	}
	public List<FK> getTableFKs(){
		return this.table.getFKs();
	}
	
	public String getWhereCondition(){
		return this.whereCondition;
	}
	
	//parentTable.getPKRowsAsWhereConditionSQLByChildTableFK
	public String getPKRowsAsWhereConditionSQLByChildTableFK(FK childFK){
		if(!childFK.getParentTable().getName().equals(this.getTable().getName())){
			throw new RuntimeException("PK "+this.table.getPK()+" is not the parent of FK "+childFK);
		}
		
		StringBuffer whereCondition = new StringBuffer();
		LinkedList<Column> columns = childFK.getColumns();
		LinkedList<Row> parentPKRows = this.getRows(this.table.getPK().getColumns());
		for(int i=0;i<columns.size();i++){
			Column column = columns.get(i);
			Column parentPKColumn = childFK.getParentColumns().get(i);
			String inOrEqual = Dialect.EQUAL;
			String conditionWithParenthesesOrNot = this.getRowsAsSQL(parentPKColumn);
			if(parentPKRows.size() > 1 || conditionWithParenthesesOrNot.indexOf(Dialect.COLUMN_SEPARATOR) > 0){
				inOrEqual = Dialect.IN;
				conditionWithParenthesesOrNot = RowSet.getSQLWithParentheses(conditionWithParenthesesOrNot);
			}
			if(i == 0){
				whereCondition.append(column.getName()).append(inOrEqual).append(conditionWithParenthesesOrNot);
			}else{
				whereCondition.append(Dialect.AND).append(column.getName()).append(inOrEqual).append(conditionWithParenthesesOrNot);
			}
		}
		return whereCondition.toString();
	}
	
	public List<String> getInsertOrUpdateSQLByPK(){
		List<String> sqls = new LinkedList<String>();
		LinkedList<Row> rows = this.getRows();
		for(Row row:rows){
			sqls.add(row.getInsertOrUpdateSQL());
		}
		return sqls;
	}
	
	/*
	 * Update SQL
	 */
	public List<String> getUpdateSQLByPK(){
		List<String> sqls = new LinkedList<String>();
		LinkedList<Row> rows = this.getRows();
		for(Row row:rows){
			sqls.add(row.getUpdateSQL());
		}
		return sqls;
	}
	
	
	/*
	 * Delete SQL 
	 */
	public String getDeleteSQLByPK(){
		if(this.getRows().size()==0){
			Table.warning("PK rows is empty.");
			return Dialect.DELETE_FROM+this.table.getName();
		}
		return new StringBuffer().append(Dialect.DELETE_FROM).append(this.table.getName()).append(Dialect.WHERE)
				.append(this.getRowsAsWhereConditionSQL(this.table.getPK().getColumns())).toString();
	}
	
	public String getDeleteSQLByWhereCondition(){
		String where = this.getWhereCondition()==null?"":(Dialect.WHERE+this.getWhereCondition());
		return new StringBuffer().append(Dialect.DELETE_FROM).append(this.table.getName()).append(where).toString();
	}
	
	public void getCascadeDeleteSQL(List<String> sqls){	
		if(/*this.getTablePK()!=null && */this.getRows(/*getTablePK().getColumns()*/).size()>0){
			sqls.add(this.getDeleteSQLByWhereCondition());
			for(Table childTable:this.table.getChildTables()){
				List<FK> fks = childTable.getFKsByParentTable(this.table.getName());
				for(FK fk:fks){
					RowSet data = new RowSet(childTable,this.getPKRowsAsWhereConditionSQLByChildTableFK(fk));
					data.getCascadeDeleteSQL(sqls);
				}
			}
		}
	}
	
	
	/*
	 * Insert SQL 
	 */
	public List<String> getInsertSQLByWhereCondition(){
		List<String> sqls = new LinkedList<String>();
		LinkedList<Row> rows = this.getRows(this.table.getColumns());
		for(Row row:rows){
			sqls.add(row.getInsertSQL());
		}
		return sqls;
	}
	
	public void getCascadeInsertSQL(List<String> sqls){
		if(/*this.getTablePK()!=null && */this.getRows(/*getTablePK().getColumns()*/).size()>0){
			sqls.addAll(this.getInsertSQLByWhereCondition());
			for(Table childTable:this.table.getChildTables()){
				List<FK> fks = childTable.getFKsByParentTable(this.table.getName());
				for(FK fk:fks){
					RowSet data = new RowSet(childTable,this.getPKRowsAsWhereConditionSQLByChildTableFK(fk));
					if(/*childTable.getPK()!=null && */data.getRows(fk.getParentColumns()).size()>0){
						data.getCascadeInsertSQL(sqls);
					}
				}
			}
		}
	}
	
	public List<String> export() {
		if(table.getPK() != null){
			return this.getInsertOrUpdateSQLByPK();
		}else{
			List<String> sqls = new LinkedList<String>();
			String sql = this.getDeleteSQLByWhereCondition();
			sqls.add(sql);
			sqls.addAll(this.getInsertSQLByWhereCondition());
			return sqls;
		}
	}
	
	
	/**
	 * this table is src environment, des is target environment. data is sync from src to des.
	 * @param des
	 * @return
	 */
	public List<String> syncTo(RowSet des){
		if(!this.table.getName().equals(des.table.getName())){
			throw new RuntimeException("src table["+this.table.getName()+"] is different from des table["+des.table.getName()+"]"); 
		}
		List<String> sqls = new LinkedList<String>();
		List<Row> srcRows = this.getRows();
		System.out.println("srcRows="+srcRows.size());
		List<Row> srcPKRows = this.getRows(this.table.getPK().getColumns());
		List<Row> desRows = des.getRows();
		System.out.println("desRows="+desRows.size());
		List<Row> desPKRows = des.getRows(des.table.getPK().getColumns());
		for(int i=srcPKRows.size()-1;i>=0;i--){
			Row srcPKRow = srcPKRows.get(i);
			boolean needInsert = true;
			boolean needUpdate = false;
			for(int j=0;j<desPKRows.size();j++){
				Row desPKRow = desPKRows.get(j);
				if(srcPKRow.equals(desPKRow)){
					needInsert = false;
					if(!srcRows.get(i).equals(desRows.get(j))){
						needUpdate = true;
					}
					desPKRows.remove(j);
					desRows.remove(j);
					break;
				}
			}
			if(needInsert){
				sqls.add(srcRows.get(i).getInsertSQL());
			}
			if(needUpdate){
				sqls.add(srcRows.get(i).getUpdateSQL());
			}
		}
		for(Row desRow:desRows){
			sqls.add(desRow.getDeleteSQL());
		}
		return sqls;
	}
	
	
}
