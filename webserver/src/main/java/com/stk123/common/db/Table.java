package com.stk123.common.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.stk123.common.db.code.ClassCode;
import com.stk123.common.db.sql.Dialect;
import com.stk123.common.db.sql.dialect.OracleDialect;
import com.stk123.common.db.util.DBUtil;


public class Table extends Contraint {
	
	private PK pk;
	private boolean hasPK = true;
	private LinkedList<FK> fks = null;
	private LinkedList<Index> indexs = null;
	private LinkedList<Table> childTables;
	
	private Dialect dialect = null;
	private Database db = null;
	
	Table(String tableName,Database db) {
		super(tableName);
		this.db = db;
		if("Oracle".equalsIgnoreCase(db.getDatabaseName())){
			dialect = new OracleDialect();
		}
		if(db.getSchema() == null){
			throw new RuntimeException("Get current schema failure!"); 
		}
		if(dialect == null){
			throw new RuntimeException("Get current dialect failure!");
		}
	}
	
	public static Table getInstance(String tableName) {
		return Table.getInstance(tableName, Database.getInstance());
	}
	
	public static Table getInstance(String tableName,Database db) {
		if(tableName == null || tableName.trim().length() == 0){
			throw new RuntimeException("Table name["+tableName+"] can not be null or empty.");
		}
		String tableNameUpper = tableName.trim().toUpperCase();
		return db.getTable(tableNameUpper);
	}
	
	public RowSet getRowSet(String whereCondition) {
		return new RowSet(this,whereCondition);
	}
	
	/*public String getSchema(){
		return db.getSchema();
	}*/
	public String getDatabaseName(){
		return db.getDatabaseName();
	}
	public String getDBConnectionName(){
		return db.getDBConnectionName();
	}
	public Dialect getDialect(){
		return this.dialect;
	}
	public Connection getDBConnection() throws Exception {
		return db.getDBConnection();
	}
	
	//private final static String get_pk_columns = "select a.constraint_name,a.table_name, a.position, a.column_name, c.coltype from user_cons_columns a,user_constraints b,sys.col c where a.constraint_name=b.constraint_name and b.table_name=? and b.constraint_type = 'P' and a.table_name=c.tname and a.column_name=c.cname";
	private PK pk() {
		Connection con = null;
		ResultSet rs = null;
		//PreparedStatement pst = null;
		try {
			con = this.getDBConnection();
			/*pst = con.prepareStatement(get_pk_columns);
			pst.setString(1, this.getName());
			rs = pst.executeQuery();*/
			DatabaseMetaData dm = con.getMetaData();
			rs = dm.getPrimaryKeys(null, null, this.getName());
			while(rs.next()){
				if(pk == null){
					pk = new PK(rs.getString("PK_NAME"),this);
				}
				pk.setColumn(rs.getInt("KEY_SEQ"), this.getColumn(rs.getString("COLUMN_NAME")));
			}
			if(pk == null){
				hasPK = false;
				warning("PK is not existing in table "+this.getName());
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			DBUtil.closeAll(rs, null, con);
		}
		return pk;
	}
	
	public PK getPK() {
		return (pk==null && hasPK)?pk():pk;
	}
	
	//private final static String get_fk_columns = "select a.constraint_name child_fk_name,a.table_name child_table_name,a.column_name child_column_name,d.coltype child_column_type,a.position,b.r_constraint_name parent_pk_name,c.table_name parent_table_name,c.column_name parent_column_name,e.coltype parent_column_type from user_cons_columns a,user_constraints b,user_cons_columns c,sys.col d,sys.col e where a.constraint_name=b.constraint_name and b.r_constraint_name=c.constraint_name and a.position=c.position and b.table_name=? and b.constraint_type ='R' and a.table_name=d.tname and a.column_name=d.cname and c.table_name=e.tname and c.column_name=e.cname";
	private LinkedList<FK> fk() {
		if(fks == null){
			fks = new LinkedList<FK>();
		}
		Connection con = null;
		ResultSet rs = null;
		//PreparedStatement pst = null;
		try {
			con = this.getDBConnection();
			/*pst = con.prepareStatement(get_fk_columns);
			pst.setString(1, this.getName());
			rs = pst.executeQuery();*/
			DatabaseMetaData dm = con.getMetaData();
			rs = dm.getCrossReference(null, null, null, null, null, this.getName());
			while(rs.next()){
				String name = rs.getString("FK_NAME");
				boolean exists = false;
				FK fk = null;
				for(FK f:fks){
					if(f.getName().equals(name)){
						exists = true;
						fk = f;
					}
				}
				if(!exists){
					fk = new FK(name,this);
					fk.setParentTable(Table.getInstance(rs.getString("PKTABLE_NAME"),db));
				}
				fk.setColumn(rs.getInt("KEY_SEQ"),this.getColumn(rs.getString("FKCOLUMN_NAME")));
				if(!exists){
					fks.add(fk);
				}
			}
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			DBUtil.closeAll(rs, null, con);
		}
		return fks;
	}
	
	public List<FK> getFKs() {
		return fks==null?fk():fks;
	}
	
	//private final static String get_index_columns = "select a.index_name,a.uniqueness,b.column_name,b.column_position from user_indexes a,user_ind_columns b where a.index_name=b.index_name and a.status='VALID' and a.funcidx_status is null and a.table_name=?";
	private LinkedList<Index> index() {
		if(indexs == null){
			indexs = new LinkedList<Index>();
		}
		Connection con = null;
		ResultSet rs = null;
		//PreparedStatement pst = null;
		try {
			con = this.getDBConnection();
			/*pst = con.prepareStatement(get_index_columns);
			pst.setString(1, this.getName());
			rs = pst.executeQuery();*/
			DatabaseMetaData dm = con.getMetaData();
			rs = dm.getIndexInfo(null, null, this.getName(), false, true);
			while(rs.next()){
				String name = rs.getString("INDEX_NAME");
				if(name == null){
					continue;
				}
				boolean exists = false;
				Index index = null;
				for(Index idx:indexs){
					if(idx.getName().equals(name)){
						exists = true;
						index = idx;
					}
				}
				if(!exists){
					boolean unique = "0".equals(rs.getString("NON_UNIQUE"));
					index = new Index(name,unique,this);
				}
				Column column = this.getColumn(rs.getString("COLUMN_NAME"));
				if(column != null){
					index.setColumn(rs.getInt("ORDINAL_POSITION"),column);
				}
				if(!exists){
					indexs.add(index);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			DBUtil.closeAll(rs, null, con);
		}
		return indexs;
	}
	
	/**
	 * Not include index which type is FUNCTION-BASED NORMAL
	 * @return
	 */
	public List<Index> getIndexs() {
		return indexs==null?index():indexs;
	}
	
	/**
	 * Can not get a FK by parentTable, maybe return two FKs
	 * T_GRNW_AUTO_UW_RESULT
	 * FK_GRNW_RESULT__OLD_POLICY_ID Foreign OLD_POLICY_ID	Y	T_POLICY_GENERAL	POLICY_ID	No action	N	N	3/4/2011 9:00:46 AM
	 * FK_GRNW_RESULT__POLICY_ID	 Foreign POLICY_ID	    Y   T_POLICY_GENERAL	POLICY_ID	No action	N	N	3/4/2011 9:00:47 AM
	 * @param fkName
	 * @return
	 */
	public FK getFK(String fkName){
		List<FK> fks = getFKs();
		for(FK fk : fks){
			if(fk.getName().equalsIgnoreCase(fkName)){
				return fk;
			}
		}
		return null;
	}
	
	public List<FK> getFKsByParentTable(String parentTable){
		List<FK> fks = getFKs();
		LinkedList<FK> returnFKs = new LinkedList<FK>();;
		for(FK fk : fks){
			if(fk.getParentTable().getName().equalsIgnoreCase(parentTable)){
				returnFKs.add(fk);
			}
		}
		return returnFKs;
	}
	
	public Table addFK(String fkName,String[] columnsName, Table parentTable){
		FK fk = new FK(fkName,this);
		fk.setParentTable(parentTable);
		for(int i=0;i<columnsName.length;i++){
			Column column = this.getColumn(columnsName[i]);
			if(column == null){
				throw new RuntimeException("Column "+column+" is not exists in "+this.getName());
			}
			fk.setColumn(i+1,column);
		}
		this.getFKs().add(fk);
		return this;
	}
	
	public List<Table> getChildTables() {
		if(childTables != null){
			return this.childTables;
		}
		childTables = new LinkedList<Table>();
		Connection con = null;
		ResultSet rs = null;
		try {
			con = this.getDBConnection();
			DatabaseMetaData dm = con.getMetaData();
			rs = dm.getCrossReference(null, null, this.getName(), null, null, null);
			while(rs.next()){
				String childTableName = rs.getString("FKTABLE_NAME");
				if(this.getChildTable(childTableName) != null){
					continue;
				}
				Table childTable = Table.getInstance(childTableName,db);
				FK fk = childTable.getFK(rs.getString("FK_NAME"));
				fk.setParentTable(this);
				childTables.add(childTable);
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			DBUtil.closeAll(rs, null, con);
		}
		return childTables;
	}
	
	public Table getChildTable(String childTableName) {
		Iterator<Table> it = this.getChildTables().iterator();
		while(it.hasNext()){
			Table table  = it.next();
			if(table.getName().equalsIgnoreCase(childTableName)){
				return table;
			}
		}
		return null;
		//throw new RuntimeException("Table "+this.getName()+" has not this child table "+childTableName);
	}
	
	public Table addChildTable(String childTableName,String[] childTableColumnsName){
		if(this.getPK().getColumns().size() != childTableColumnsName.length){
			throw new RuntimeException("Child table columns size="+childTableColumnsName.length+" not equals to this PK's columns size="+this.getPK().getColumns().size());
		}
		Table childTable = Table.getInstance(childTableName,db);
		for(int i=0;i<this.getPK().getColumns().size();i++){
			Column column = this.getColumns().get(i);
			Column childColumn =  childTable.getColumn(childTableColumnsName[i]);
			if(childColumn == null){
				throw new RuntimeException("Column "+childColumn+" is not exists in "+this.getName());
			}
			if(!column.getSQLDataTypeAsString().equals(childColumn.getSQLDataTypeAsString())){
				throw new RuntimeException("Column "+column.getName()+"'s type "+column.getSQLDataTypeAsString()+" is not equals to child table column "+childColumn.getName()+"'s type "+childColumn.getSQLDataTypeAsString());
			}
		}
		childTable.addFK("FK_"+childTableName+"_"+childTableColumnsName.toString(), childTableColumnsName, this);
		this.getChildTables().add(childTable);
		return childTable;
	}
	
	public Table addChildTable(FK childTableFK){
		Table childTable = childTableFK.getTable();
		childTable.getFKs().add(childTableFK);
		this.getChildTables().add(childTable);
		return childTable;
	}
	
	//private final static String get_tab_columns = "select column_name,data_type,data_length,nvl(data_precision,0),nvl(data_scale,0) from user_tab_cols where table_name=? and virtual_column='NO' order by column_id";
	public LinkedList<Column> getColumns() {
		if(super.columns != null){
			return columns;
		}else{
			super.columns = new LinkedList<Column>();
		}
		Connection con = null;
		ResultSet rs = null;
		//PreparedStatement pst = null;
		try {
			con = this.getDBConnection();
			/*pst = con.prepareStatement(get_tab_columns);
			pst.setString(1, this.getName());
			rs = pst.executeQuery();*/
			DatabaseMetaData dm = con.getMetaData();
			rs = dm.getColumns(null, null, this.getName(), null);
			while(rs.next()){
				if(super.getColumn(rs.getString("COLUMN_NAME")) != null){
					continue;
				}
				Column col = new Column(rs.getString("COLUMN_NAME"),rs.getString("TYPE_NAME"),rs.getShort("CHAR_OCTET_LENGTH"),
						rs.getShort("COLUMN_SIZE"),rs.getShort("DECIMAL_DIGITS"),this);
				
				super.columns.add(col);
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			DBUtil.closeAll(rs, null, con);
		}
		if(super.columns.size()==0){
			throw new RuntimeException("Table "+this.getName()+" is not exists in database.");
		}
		return super.columns;
	}
	
	public boolean equals(Object o){
		if(o instanceof Table){
			return ((Table)o).getName().equals(this.getName());
		}else{
			return false;
		}
	}
	public int hashCode(){
		return this.getName().hashCode();
	}
	public String toString(){
		return "Table="+this.getName()+", pk="+pk+", fks="+fks+", indexs="+indexs;
	}
	
	
	
	/*****************************
	 * Tools Begin
	 ****************************/
	
	/*
	 * generate source code 
	 */
	public File codeEmptyClassFile(String srcDir,String packageName,String fileName) throws IOException {
		FileUtils.forceMkdir(new File(srcDir));
		String packPath = packageName;
		if(packageName.indexOf(".") > 0){
			packPath = packageName.replace(".", File.separator);
		}
		String path = srcDir+File.separator+packPath;
		FileUtils.forceMkdir(new File(path));
		File file = new File(path+File.separator+fileName+".java");
		return file;
	}
	
	private static Set<String> clazzSet = new HashSet<String>();
	
	public void codePOJOBean(String srcDir,String packageName,boolean needAnnotation,boolean codeChildTables,boolean codeParentTables) throws IOException {
		if(clazzSet.contains(this.getName())){
			return;
		}else{
			clazzSet.add(this.getName());
		}
		ClassCode classCode = new ClassCode(packageName,null,this,needAnnotation,codeChildTables,codeParentTables);
		File file = this.codeEmptyClassFile(srcDir, packageName, classCode.getClassName());
		FileUtils.writeLines(file, classCode.writeClass());
		
		if(codeChildTables){
			PK pk = this.getPK();
			if(pk != null){
				for(Table tab:this.getChildTables()){
					if(!tab.getName().equals(this.getName())){
						tab.codePOJOBean(srcDir, packageName, needAnnotation, codeChildTables,codeParentTables);
					}
				}
			}
		}
		if(codeParentTables){
			for(FK fk:this.getFKs()){
				Table tab = fk.getParentTable();
				if(!tab.getName().equals(this.getName())){
					tab.codePOJOBean(srcDir, packageName, needAnnotation, false,false);
				}
			}
		}
	}
	
	public void codePOJOBean(String srcDir,String packageName,String className,boolean needAnnotation) throws IOException {
		ClassCode classCode = new ClassCode(packageName,className,this,needAnnotation,false,false);
		File file = this.codeEmptyClassFile(srcDir, packageName, classCode.getClassName());
		FileUtils.writeLines(file, classCode.writeClass());
	}
	
	
	
	
	
}
