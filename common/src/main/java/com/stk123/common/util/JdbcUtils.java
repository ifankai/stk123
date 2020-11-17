package com.stk123.common.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Reader;
import java.io.StringReader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleConnection;
import org.apache.commons.lang.StringUtils;

import com.stk123.common.db.connection.Pool;
import com.stk123.common.db.sql.Dialect;
import com.stk123.common.db.sql.dialect.OracleDialect;
import com.stk123.common.db.util.sequence.SequenceUtils;
import com.stk123.common.CommonConstant;

//import net.sf.jsqlparser.statement.delete.Delete;
//import net.sf.jsqlparser.statement.insert.Insert;
//import net.sf.jsqlparser.statement.update.Update;
import oracle.sql.CLOB;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author fankai
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public final class JdbcUtils {
	
	public static Dialect DIALECT;
	static{
		String db = ConfigUtils.getProp("database");
		if("Oracle".equalsIgnoreCase(db)){
			DIALECT = new OracleDialect();
		}
	}
	
	public static final String DELETE_FROM = DIALECT.DELETE_FROM;
	public static final String INSERT_INTO = "INSERT INTO ";
	public static final String SELECT_STAR_FROM = "SELECT * FROM ";
	public static final String SELECT = "SELECT ";
	public static final String UPDATE = "UPDATE ";
	public static final String FROM = " FROM ";
	public static final String WHERE = " WHERE ";
	public static final String IN = " IN ";
	public static final String AND = " AND ";
	public static final String EQUAL = " = ";
	
	public static final String TABLE_SEPARATOR = ",";
    public static final String COLUMN_SEPARATOR = ",";
    public static final String TABLE_COLUMN_SEPARATOR = ".";
    public static final String PARAMETER = "?";
    public static final String ON = " ON ";
    public static final String LEFT_JOIN = " LEFT OUTER JOIN ";
    public static final String INNER_JOIN = " INNER JOIN ";
    public static final String DISTINCT = " DISTINCT ";
    public static final String ORDER_BY = " ORDER BY ";
    public static final String LIMIT = " LIMIT ";
    public static final String OFFSET = " OFFSET ";
	
	public static Map<String,IDataTypeHandler> COMMON_DATA_TYPE = new HashMap<String,IDataTypeHandler>(30) ;
	
	public static int execute(Connection conn, String sql) {
		Statement stsm = null;
	    try {
	    	stsm = conn.createStatement();
	        return stsm.executeUpdate(sql);
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(e);
	    } finally {
	    	close(null, stsm);
	    }
	}
	
	public static boolean call(Connection conn, String sql) {
		CallableStatement proc = null;
	    try {
	    	proc = conn.prepareCall(sql);
	        return proc.execute();
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException(e);
	    } finally {
	    	try {
				proc.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    }
	}
	
	public static <T> List<T> list(Connection conn,String sql,Class resultClass) {
		return JdbcUtils.list(conn,sql,null,resultClass,null);
	}
	
	public static <T> List<T> list(Connection conn,String sql,ResultSetLoader<T> loader) {
		return JdbcUtils.list(conn,sql,null,null,loader);
	}
	
	public static <T> List<T> list(Connection conn,String sql,String param1,Class resultClass) {
		List params = new ArrayList();
		params.add(param1);
		return JdbcUtils.list(conn,sql,params,resultClass,null);
	}
	
	public static <T> List<T> list(Connection conn,String sql,List params,Class resultClass) {
		return JdbcUtils.list(conn,sql,params,resultClass,null);
	}
	
	public static <T> List<T> list(Connection conn,String sql,List params,ResultSetLoader<T> loader) {
		return JdbcUtils.list(conn,sql,params,null,loader);
	}
	
	public static List<Map> list2UpperKeyMap(Connection conn,String sql,List params) {
		return JdbcUtils.<Map>list(conn,sql,params,IngoreCaseHashMap.class,null);
	}
	
	public static List<Map> list2UpperKeyMap(Connection conn,String sql) {
		return JdbcUtils.list2UpperKeyMap(conn, sql, (List)null);
	}
	public static List<Map> list2UpperKeyMap(String sql,List params) {
		return JdbcUtils.<Map>list(sql,params,IngoreCaseHashMap.class,null);
	}
	
	public static List<Map> list2UpperKeyMap(String sql) {
		return JdbcUtils.list2UpperKeyMap(sql, (List)null);
	}

	/**
	 * key键小写
	 */
	public static List<Map> list2Map(Connection conn,String sql,List params) {
		return JdbcUtils.<Map>list(conn,sql,params,LowerHashMap.class,null);
	}
	public static List<Map> list2Map(Connection conn,String sql) {
		return JdbcUtils.list2Map(conn, sql, (List)null);
	}
	public static List<Map> list2Map(String sql,List params) {
		return JdbcUtils.<Map>list(sql,params,LowerHashMap.class,null);
	}
	
	public static List<Map> list2Map(String sql) {
		return JdbcUtils.list2Map(sql, (List)null);
	}
	
	
	public static <T> List<T> list(String sql,Class resultClass) {
		return JdbcUtils.list(sql,null,resultClass,null);
	}
	
	public static <T> List<T> list(String sql,ResultSetLoader<T> loader) {
		return JdbcUtils.list(sql,null,null,loader);
	}
	
	public static <T> List<T> list(String sql,List params,Class resultClass) {
		return JdbcUtils.list(sql,params,resultClass,null);
	}
	
	public static <T> List<T> list(String sql,List params,ResultSetLoader<T> loader) {
		return JdbcUtils.list(sql,params,null,loader);
	}
	
	
	/**
	 * Load first row value
	 * @param <T>
	 * @param conn
	 * @param sql
	 * @return type of resultClass
	 */
	public static <T> T load(Connection conn,String sql,Class resultClass) {
		return load(conn,sql,null,resultClass,null);
	}
	
	public static <T> T load(String sql,Class resultClass) {
		return load(sql,null,resultClass,null);
	}
	
	//对只有一个参数
	public static <T> T load(Connection conn,String sql,String param1,Class resultClass) {
		List params = new ArrayList();
		params.add(param1);
		return load(conn,sql,params,resultClass,null);
	}
	public static <T> T load(Connection conn,String sql,int param1,Class resultClass) {
		List params = new ArrayList();
		params.add(param1);
		return load(conn,sql,params,resultClass,null);
	}

	public static <T> T load(Connection conn,String sql,Class resultClass, Object... params) {
		return load(conn, sql, Arrays.asList(params), resultClass,null);
	}
	
	public static <T> T load(Connection conn,int cnt, String sql, List params, Class resultClass) {
		PreparedStatement pstm = null ;
		ResultSet rs = null ;
		T result = null;
		try{
			pstm = conn.prepareStatement(sql) ;
			JdbcUtils.prepareParams(pstm,params) ;
			rs = pstm.executeQuery() ;
			while(rs.next()){
				if(cnt -- <= 0)break;
				result = ((T)JdbcUtils.rs2Object(rs,resultClass, null)) ;
			}
			return result;
		}catch(SQLException e){
			throw new RuntimeException(sql+";\n"+params,e) ;
		}finally{
			close(rs,pstm) ;
		}
	}
	
	public static <T> T load(Connection conn,String sql,List params,Class resultClass) {
		return load(conn,sql,params,resultClass,null);
	}
	
	public static <T> T load(String sql,List params,Class resultClass) {
		return load(sql,params,resultClass,null);
	}
	
	public static <T> T load(Connection conn,String sql,ResultSetLoader loader) {
		return load(conn,sql,null,null,loader);
	}
	
	public static <T> T load(String sql,ResultSetLoader loader) {
		return load(sql,null,null,loader);
	}
	
	public static <T> T load(Connection conn,String sql,List params,ResultSetLoader loader) {
		return load(conn,sql,params,null,loader);
	}
	
	public static <T> T load(String sql,List params,ResultSetLoader loader) {
		return load(sql,params,null,loader);
	}
	
	public static <T> T first(Connection conn,String sql,Class resultClass) {
		return load(conn,sql,null,resultClass,null);
	}
	
	public static <T> T first(Connection conn,String sql,List params,Class resultClass) {
		return load(conn,sql,params,resultClass,null);
	}
	
	public static Map load2Map(Connection conn,String sql,List params) {
		return load(conn,sql,params,IngoreCaseHashMap.class,null);
	}
	
	public static Map load2Map(String sql,List params) {
		return load(sql,params,IngoreCaseHashMap.class,null);
	}
	
	public static Map load2Map(Connection conn,String sql) {
		return load2Map(conn,sql,(List)null);
	}
	
	public static long getSequence(Connection conn,String sequence) {
		return SequenceUtils.getSequenceNextValue(sequence);
		//return load(conn,JdbcUtils.SELECT + sequence+".nextval from dual",long.class);
	}
	
	public static long getSequence(String sequence) {
		return getSequence(null, sequence);
	}
	
	public static int insert(Connection conn,String sql,List params){
		return update(conn, sql, params);
	}
	
	public static int insert(Connection conn,String sql){
		return update(conn, sql, (List)null);
	}
	
	public static int insert(String sql,List params) throws SQLException{
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			return update(conn, sql, params);
		}finally{
			Pool.getPool().free(conn);
		}
	}
	
	public static String getTableName(Class clazz) {
		POJOWrapper pojoWrapper = createPOJOWrapper(clazz);
        if(pojoWrapper.table == null){
        	pojoWrapper = createPOJOWrapper(clazz.getSuperclass());
        }
        return pojoWrapper.table.name();
	}
	
	public static class SqlAndParams{
		public String sql;
		public List params;
		
		public SqlAndParams(String sql, List params){
			this.sql = sql;
			this.params = params;
		}
	}
	
	public static SqlAndParams getInsertSQL(Object obj){
		POJOWrapper pojoWrapper = createPOJOWrapper(obj.getClass());
        if(pojoWrapper.table == null){
        	pojoWrapper = createPOJOWrapper(obj.getClass().getSuperclass());
        }
        String tableName = pojoWrapper.table.name();
        StringBuffer sb = new StringBuffer();
        List params = new ArrayList();
        sb.append("insert into ").append(tableName).append("(");
        sb.append(StringUtils.join(pojoWrapper.columnNames.iterator(), ","));
        sb.append(") values (");
        //sb.append(StringUtils.repeat("?",",",pojoWrapper.columnNames.size()));
        for(Column column : pojoWrapper.columns){
            if(column.pk() && column.sequence().length() > 0){
                sb.append(column.sequence()).append(".nextval").append(",");
            }else {
            	sb.append("?,");
            	params.add(pojoWrapper.getValue(obj, column.name()));
            }
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        return new JdbcUtils.SqlAndParams(sb.toString(), params);
	}

    /**
     * @param conn
     * @param obj  PO对象 class StkPO extends Stk, 增加用于sql的字段： private String insertTimeSql = null;
     *             Stk stk = new StkPO();
     *             stk.setCode("999999");
     *             stk.setInsertTimeSql("sysdate");
     * @return
     */
    public static int insert(Connection conn, Object obj){
        POJOWrapper pojoWrapper = createPOJOWrapper(obj.getClass());
        if(pojoWrapper.table == null){
        	pojoWrapper = createPOJOWrapper(obj.getClass().getSuperclass());
        }
        System.out.println(pojoWrapper.columnNames);
        String tableName = pojoWrapper.table.name();
        StringBuffer sb = new StringBuffer();
        List params = new ArrayList();
        sb.append("insert into ").append(tableName).append("(");
        sb.append(StringUtils.join(pojoWrapper.columnNames.iterator(), ","));
        sb.append(") values (");
        //sb.append(StringUtils.repeat("?",",",pojoWrapper.columnNames.size()));
        for(Column column : pojoWrapper.columns){
            if(column.pk() && column.sequence().length() > 0){
                sb.append(column.sequence()).append(".nextval").append(",");
            }else {
                boolean hasColumnSql = pojoWrapper.hasColumn(column.name()+"_Sql");
                if(hasColumnSql){
                    String value = String.valueOf(pojoWrapper.getValue(obj, column.name()+"_Sql"));
                    sb.append(value).append(",");
                    if(value.indexOf("?") >= 0){
                        params.add(pojoWrapper.getValue(obj, column.name()));
                    }
                }else {
                    sb.append("?,");
                    params.add(pojoWrapper.getValue(obj, column.name()));
                }
            }
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        return insert(conn, sb.toString(), params);
    }
    
    @Deprecated
    public static int update(Connection conn, Object obj){
    	POJOWrapper pojoWrapper = createPOJOWrapper(obj.getClass());
    	POJOWrapper pojoWrapperSuper = createPOJOWrapper(obj.getClass().getSuperclass());
        String tableName = pojoWrapperSuper.table.name();
        StringBuffer sb = new StringBuffer();
        List params = new ArrayList();
        sb.append("update ").append(tableName).append(" set ");
        List<Column> pks = new ArrayList<Column>();
        for(Column column : pojoWrapperSuper.columns){
            if(column.pk() ){
            	pks.add(column);
            }else {
            	sb.append(column.name()).append("=?,");
                boolean hasColumnSql = pojoWrapper.hasColumn(column.name()+"_Sql");
                if(hasColumnSql){
                    String value = String.valueOf(pojoWrapper.getValue(obj, column.name()+"_Sql"));
                    params.add(value);
                }else {
                    params.add(pojoWrapperSuper.getValue(obj, column.name()));
                }
            }
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(" where ");
        if(pks.size() == 0){
        	List<String> propPks = (List<String>)pojoWrapper.getValue(obj, "pks");
        	if(propPks != null){
	        	if(propPks.size() == 0){
	        		throw new RuntimeException((pojoWrapperSuper.table==null?pojoWrapper.table.name():pojoWrapperSuper.table.name())+" has't PK error.") ;
	        	}else{
	        		for(String pk : propPks){
	    		        sb.append(pk).append("=? and ");
	    		        params.add(pojoWrapper.getValue(obj, pk));
	            	}
	        		sb.delete(sb.length()-5,sb.length());
	        	}
        	}
        }else{
        	if(pks.size() > 0){
	        	for(Column pk : pks){
			        sb.append(pk.name()).append("=? and ");
			        params.add(pojoWrapperSuper.getValue(obj, pk.name()));
	        	}
	        	sb.delete(sb.length()-5,sb.length());
        	}
        }
        return insert(conn, sb.toString(), params);
    }
    
    public static boolean isPkHasValue(Object obj){
    	POJOWrapper pojoWrapper = createPOJOWrapper(obj.getClass());
    	List<String> propPks = (List<String>)pojoWrapper.getValue(obj, "pks");
    	if(propPks != null && propPks.size() > 0){
    		for(String pk : pojoWrapper.pks){
    			if(pojoWrapper.getValue(obj, pk) == null){
    				return false;
    			}
    		}
    		return true;
    	}
    	POJOWrapper pojoWrapperSuper = createPOJOWrapper(obj.getClass().getSuperclass());
    	if(pojoWrapperSuper.pks.size() > 0){
    		for(String pk : pojoWrapperSuper.pks){
    			if(pojoWrapperSuper.getValue(obj, pk) == null){
    				return false;
    			}
    		}
    		return true;
    	}
    	throw new RuntimeException((pojoWrapperSuper.table==null?pojoWrapper.table.name():pojoWrapperSuper.table.name())+" has't PK error.") ;
    }
    
    public static int insertOrUpdate(Connection conn, Object obj){
    	if(isPkHasValue(obj)){
    		return JdbcUtils.update(conn, obj);
    	}else{
    		return JdbcUtils.insert(conn, obj);
    	}
    }
    
    /*
     * ------------------------------------------- PO ---------------------------------------------------
     */
    public static interface PO {
    	public Map<String, Object> getPKs();
    	public void setPK(String key);
    	public void setPK(String key, Object value);
    	public Map<String, Object> getInsertColumns();
    	public void setInsertColumn(String key, Object value);
    	public Map<String, Object> getSelectColumns();
    	public void setSelectColumn(String key, Object value);
    	public Map<String, Object> getUpdateColumns();
    	public void setUpdateColumn(String key);
    	public void putUpdateColumn(String key, Object value);
    	public Map<String, Object> getConditionColumns();
    	public void setConditionColumn(String key);
    	public void setConditionColumn(String key, Object value);
    }

    public static class POBase implements PO {
    	private Map PKs;
    	private Map insertColumns;
    	private Map selectColumns;
    	private Map updateColumns;
    	private Map conditionColumns;
    	
    	public POBase() {}
    	
    	public Map<String, Object> getPKs() {
    		return PKs;
    	}
    	public Map<String, Object> getInsertColumns() {
    		return insertColumns;
    	}
    	public Map<String, Object> getSelectColumns() {
    		return selectColumns;
    	}
    	public Map<String, Object> getUpdateColumns() {
    		return updateColumns;
    	}
    	public Map<String, Object> getConditionColumns() {
    		return conditionColumns;
    	}

    	@Override
		public void setPK(String key) {
    		this.setPK(key, "?");
    	}
		@Override
		public void setPK(String key, Object value) {
			if(PKs == null){
				PKs = new IngoreCaseHashMap();
			}
			PKs.put(key, value);
		}

		@Override
		public void setInsertColumn(String key, Object value) {
			if(insertColumns == null){
				insertColumns = new IngoreCaseHashMap();
			}
			insertColumns.put(key, value);
		}

		@Override
		public void setSelectColumn(String key, Object value) {
			if(selectColumns == null){
				selectColumns = new IngoreCaseHashMap();
			}
			selectColumns.put(key, value);
		}

		public void setUpdateColumn(String key){
			this.putUpdateColumn(key, "?");
		}
		@Override
		public void putUpdateColumn(String key, Object value) {
			if(updateColumns == null){
				updateColumns = new IngoreCaseHashMap();
			}
			updateColumns.put(key, value);
		}

		@Override
		public void setConditionColumn(String key){
			this.setConditionColumn(key, "?");
		}
		@Override
		public void setConditionColumn(String key, Object value) {
			if(conditionColumns == null){
				conditionColumns = new IngoreCaseHashMap();
			}
			conditionColumns.put(key, value);
		}
    }
    
    public static class POBaseInsert extends POBase {
    	private String whereCondition;
    	
    	public void setWhereCondition(String whereCondition){
    		this.whereCondition = whereCondition;
    	}
    	
    	public String getWhereCondition() {
    		return this.whereCondition;
    	}
    }
    
    public static int insert(Connection conn, Object obj, PO po) {
    	POJOWrapper pojoWrapper = createPOJOWrapper(obj.getClass());
    	String tableName = pojoWrapper.table.name();
        StringBuffer sb = new StringBuffer();
        List params = new ArrayList();
        sb.append("insert into ").append(tableName).append("(");
        sb.append(StringUtils.join(pojoWrapper.columnNames.iterator(), ","));
        sb.append(") values (");
        for(Column column : pojoWrapper.columns){
            if(column.pk() && column.sequence().length() > 0){
                sb.append(column.sequence()).append(".nextval").append(",");
            }else {
                if(po.getInsertColumns() != null && po.getInsertColumns().get(column.name()) != null){
                    String value = String.valueOf(po.getInsertColumns().get(column.name()));
                    sb.append(value).append(",");
                    if(value.indexOf("?") >= 0){//对于类似这种情况： to_char(?)
                        params.add(pojoWrapper.getValue(obj, column.name()));
                    }
                }else {
                    sb.append("?,");
                    params.add(pojoWrapper.getValue(obj, column.name()));
                }
            }
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        System.out.println(sb.toString());
        return insert(conn, sb.toString(), params);
    }
    
    public static int update(Connection conn, Object obj, PO po) {
    	POJOWrapper pojoWrapper = createPOJOWrapper(obj.getClass());
        String tableName = pojoWrapper.table.name();
        StringBuffer sb = new StringBuffer();
        List params = new ArrayList();
        sb.append("update ").append(tableName).append(" set ");
        if(po.getUpdateColumns() != null && po.getUpdateColumns().size() > 0){
        	for(Map.Entry<String, Object> entry : po.getUpdateColumns().entrySet()){
	            String value = String.valueOf(entry.getValue());
	            sb.append(entry.getKey()).append("=").append(value).append(",");
	            if(value.indexOf("?") >= 0){//对于类似这种情况： to_char(?)
	                params.add(pojoWrapper.getValue(obj, entry.getKey()));
	            }
        	}
        }else{
        	for(Column column : pojoWrapper.columns){
            	if(column.pk())continue;
            	sb.append(column.name()).append("=").append("?,");
            	params.add(pojoWrapper.getValue(obj, column.name()));
            }
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(" where ");
    	if(po.getConditionColumns() != null && po.getConditionColumns().size() > 0){
    		for(Map.Entry<String, Object> entry : po.getConditionColumns().entrySet()){
		        String value = String.valueOf(entry.getValue());
    			sb.append(entry.getKey()).append("=").append(value).append(" and ");
		        if(value.indexOf("?") >= 0){//对于类似这种情况： to_char(?)
                    params.add(pojoWrapper.getValue(obj, entry.getKey()));
                }
        	}
    	}else if(po.getPKs() != null && po.getPKs().size() > 0){
    		for(Map.Entry<String, Object> entry : po.getPKs().entrySet()){
		        sb.append(entry.getKey()).append("=").append(entry.getValue()).append(" and ");
		        params.add(pojoWrapper.getValue(obj, entry.getKey()));
        	}
    	}else if(pojoWrapper.pks.size() > 0){
    		for(String pk : pojoWrapper.pks){
		        sb.append(pk).append("=? and ");
		        params.add(pojoWrapper.getValue(obj, pk));
        	}
    	}else{
    		throw new RuntimeException((pojoWrapper.table==null?pojoWrapper.table.name():pojoWrapper.table.name())+" has't PK error.") ;
    	}
    	sb.delete(sb.length()-5,sb.length());
    	System.out.println(sb.toString());
        return insert(conn, sb.toString(), params);
    }
    
    
	public static int insertOrUpdate(Connection conn,String sql,List params){
		return update(conn, sql, params);
	}
	
	public static int update(Connection conn,String sql,List params){
		PreparedStatement pstm = null ;
		try{
			pstm = conn.prepareStatement(sql) ;
			JdbcUtils.prepareParams(pstm,params) ;
			return pstm.executeUpdate();
		}catch(Exception e){
			throw new RuntimeException(sql+";\n"+params,e) ;
		}finally{
			close(null,pstm);
		}
	}
	
	public static int SYNC_TASK_ID = 0;//指定某次task所有sql(可能有多个表)的一个task id
	public static boolean SYNC_SWITCH = false; //要不要把sql插入stk_sync表的总开关
	
	public static void main(String[] args) throws Exception {
	}
	
	public static int delete(Connection conn,String sql,List params){
		return update(conn, sql, params);
	}
	
	public static int delete(Connection conn,String sql){
		return update(conn, sql, (List)null);
	}
	
	public static void updateBatch(Connection conn,String sql,List<List> params, int max){
		PreparedStatement pstm = null ;
		try{
			int i = 0;
			pstm = conn.prepareStatement(sql);
			for(List param : params){
				JdbcUtils.prepareParams(pstm,param) ;
				pstm.addBatch();
				if(++i % max == 0){
					pstm.executeBatch();
				}
			}
			pstm.executeBatch();
		}catch(Exception e){
			throw new RuntimeException(sql+";\n"+params,e) ;
		}finally{
			close(null,pstm) ;
		}
	}
	
	
	public static final int JOIN_INNER = 0;
	public static final int JOIN_FULL = 4;
	public static final int JOIN_LEFT_OUTER = 1;
	public static final int JOIN_RIGHT_OUTER = 2;
	
	public static<T> Query<T> query(Class resultClass,String alias){
		return new Query<T>(resultClass,alias);
	}
	
	public static class Query<T> {
		private Table<T> rootTable;
		private StringBuffer whereCondition = new StringBuffer(128);
		private String sql = null;
		
		private Query(){
		}
		
		private Query(Class resultClass,String alias){
			this.rootTable = new Table(resultClass,alias);
		}
		
		public Query addJoin(String alias1,String[] columns1,
				Class tableClass2,String alias2,String[] columns2,int joinType){
			if(columns1.length != columns2.length){
				throw new RuntimeException("columns1 length["+columns1.length+"] <> columns2 length["+columns2.length+"].");
			}
			for(int i=0;i<columns1.length;i++){
				this.addJoin(alias1, columns1[i], tableClass2, alias2, columns2[i],joinType);
			}
			return this;
		}
		
		public Query addJoin(String alias1,String column1,
				Class tableClass2,String alias2,String column2,int joinType){
			Table ptable = rootTable.findTable(alias1);
			if(ptable == null){
				throw new RuntimeException("alias ["+alias1+"] is not exists.");
			}else{
				Join join = null;
				for(Join j:(List<Join>)ptable.getJoins()){
					if(j.parentTable.alias.equalsIgnoreCase(alias1) && j.childTable.alias.equalsIgnoreCase(alias2)){
						join = j;
						break;
					}
				}
				if(join == null){
					join = new Join(new Table(tableClass2,alias2),column2,ptable,column1,joinType);
					ptable.addJoin(join);
				}else{
					join.addColumns(column1, column2);
				}
			}
			return this;
		}
		
		public Query addJoin(String alias1,String[] columns1,
				Class tableClass2,String alias2,String[] columns2){
			return this.addJoin(alias1, columns1, tableClass2, alias2, columns2, JOIN_INNER);
		}
		public Query addJoin(String alias1,String column1,
				Class tableClass2,String alias2,String column2){
			return this.addJoin(alias1, column1, tableClass2, alias2, column2, JOIN_INNER);
		}
		
		public Query addCondition(String whereCondition){
			this.whereCondition.append(whereCondition);
			return this;
		}
		
		public List<T> list(Connection conn){
			return this.list(conn,null);
		}
		
		public List<T> list(Connection conn,List params){
			PreparedStatement pstm = null ;
			ResultSet rs = null ;
			List<T> resultObjects = new ArrayList<T>();
			try{
				pstm = conn.prepareStatement(this.toSQL()) ;
				JdbcUtils.prepareParams(pstm,params) ;
				rs = pstm.executeQuery() ;
				while(rs.next()){
					rootTable.load(rs,resultObjects);
				}
				return resultObjects;
			}catch(SQLException e){
				throw new RuntimeException(sql+";\n"+params,e) ;
			}finally{
				close(rs,pstm) ;
			}
		}
		
		public String toSQL(){
			if(sql != null){
				return sql;
			}
			String joins2whereCondition = this.afterWhere();
			StringBuffer sb = new StringBuffer(512);
			sb.append("select ").append(this.afterSelect());
			sb.append(" from ").append(this.afterFrom());
			if(joins2whereCondition.length() > 0 || this.whereCondition.length() > 0){
				sb.append(" where ");
			   	sb.append(joins2whereCondition);
			   	if(joins2whereCondition.length() > 0 && whereCondition.length() > 0){
					sb.append(" and ").append(this.whereCondition);
			   	}else{
			   		sb.append(this.whereCondition);
			   	}
			}
			this.sql = sb.toString();
			return sql;
		}
		
		private String afterFrom(){
			StringBuffer sb = new StringBuffer(128);
			this.rootTable.afterFrom(sb);
			if(sb.length() > 0){
				return sb.toString().substring(1);
			}
			return sb.toString();
		}
		private String afterWhere(){
			StringBuffer sb = new StringBuffer(128);
			this.rootTable.afterWhere(sb);
			if(sb.length() > 0){
				return sb.toString().substring(5);
			}
			return sb.toString();
		}
		private String afterSelect(){
			StringBuffer sb = new StringBuffer(256);
			this.rootTable.afterSelect(sb);
			if(sb.length() > 0){
				return sb.toString().substring(1);
			}
			return sb.toString();
		}
		
		private static class Table<T> {
			private String alias;
			private POJOWrapper pw;
			private List<Join> joins = new ArrayList<Join>();
			//private List<T> resultObjects = null;
			
			Table(Class tableClass,String alias){
				this.alias = alias;
				this.pw = JdbcUtils.createPOJOWrapper(tableClass);
			}
			List<Join> getJoins(){
				return joins;
			}
			List<Table> getChildTables(){
				List<Table> children = new ArrayList<Table>();
				for(Join join:joins){
					children.add(join.childTable);
				}
				return children;
			}
			void addJoin(Join join){
				this.joins.add(join);
			}
			List<String> getPK(){
				return pw.pks;
			}
			List<String> getColumns(){
				return pw.columnNames;
			}
			
			Table findTable(String alias){
				if(this.alias.equalsIgnoreCase(alias)){
					return this;
				}
				for(Table t:this.getChildTables()){
					if(t.alias.equalsIgnoreCase(alias)){
						return t;
					}else{
						t.findTable(alias);
					}
				}
				return null;
			}
			List<String> getPK(String alias){
				if(this.alias.equalsIgnoreCase(alias)){
					return this.getPK();
				}
				for(Table t:this.getChildTables()){
					if(t.alias.equalsIgnoreCase(alias)){
						return t.getPK();
					}else{
						t.getPK(alias);
					}
				}
				return null;
			}
			
			
			Object load(ResultSet rs,List<T> resultObjects) throws SQLException{
				Object instance = null;
				/*if(resultObjects == null)
					resultObjects = new ArrayList<T>();*/
				for(Object o:resultObjects){
					boolean equal = true;
					for( String col : (getPK().size()==0?getColumns():getPK()) ){
						Object o1 = pw.getValue(o,col);
						Object o2 = pw.getValue(new StringBuffer().append(this.alias).append(POJOWrapper.BETWEEN_ALIAS_COLUMN).append(col).toString(),rs);//"dc___ccm_url"
						IDataTypeHandler handler = JdbcUtils.COMMON_DATA_TYPE.get(o1.getClass().getName());
						if(handler == null){
							throw new RuntimeException("column["+col+"], "+o1+" can not compare to "+o2);
						}
						if(handler!=null && !handler.equals(o1, o2)){
							equal = false;
							break;
						}
					}
					if(equal){
						instance = o;
						break;
					}
				}
				if(instance == null){
					instance = newBeanInstance(pw.beanClass) ;
					for( String col : getColumns() ){
						pw.setValue(instance, col,null, pw.getValue(new StringBuffer().append(this.alias).append(POJOWrapper.BETWEEN_ALIAS_COLUMN).append(col).toString(), rs));//"dc___ccm_url"
					}
					for(Join join:joins){
						List<T> resultObjects2 = new ArrayList<T>();
						Object childObj = join.childTable.load(rs,resultObjects2);
						pw.setValue(instance, join.childTable.pw.table.name(),
								join.childTable.pw.table.name()+join.getParentColumnsAsString(),childObj);
						
						//pw.setValue(childObj, join.parentTable.pw.table.name(), null, instance);
					}
					resultObjects.add((T)instance);
				}else{
					for(Join join:joins){
						List<T> resultObjects2 = new ArrayList<T>();
						Object childObj = join.childTable.load(rs,resultObjects2);
						pw.setValue(instance, join.childTable.pw.table.name(),
								join.childTable.pw.table.name()+join.getParentColumnsAsString(),childObj);
					}
				}
				return instance;
			}
			
			void afterSelect(StringBuffer sql){
				for(String col : getColumns()){
					sql.append(',').append(alias).append('.').append(col).append(" as ").append(alias).append(POJOWrapper.BETWEEN_ALIAS_COLUMN).append(col);
				}
				for(Join join:joins){
					if(join.childTable != null /*&& join.childTable.joins.size() > 0*/){
						join.childTable.afterSelect(sql);
					}
				}
			}
			void afterFrom(StringBuffer sql){
				sql.append(',').append(this.pw.table.name()).append(' ').append(alias);
				for(Join join:joins){
					if(join.childTable != null /*&& join.childTable.joins.size() > 0*/){
						join.childTable.afterFrom(sql);
					}
				}
			}
			void afterWhere(StringBuffer sql){
				for(Join join:joins){
					for ( int j = 0; j < join.parentColumns.length; j++ ) {
						sql.append( " and " )
								.append( join.parentTable.alias )
								.append( '.' )
								.append( join.parentColumns[j] );
						if ( join.joinType == JOIN_RIGHT_OUTER || join.joinType == JOIN_FULL ) sql.append( "(+)" );
						sql.append( '=' )
								.append( join.childTable.alias )
								.append( '.' )
								.append( join.childColumns[j] );
						if ( join.joinType == JOIN_LEFT_OUTER || join.joinType == JOIN_FULL ) sql.append( "(+)" );
					}
					if(join.childTable != null && join.childTable.joins.size() > 0){
						join.childTable.afterWhere(sql);
					}
				}
			}
			
			public String toString(){
				return pw.beanClass.getName()+","+alias;
			}
		}
		
		public static final String $ = "_";
		
		private static class Join {
			private Query.Table parentTable;
			private Query.Table childTable;
			private String[] parentColumns;
			private String[] childColumns;
			private int joinType;
			
			/*Join(Table childTable,String[] childColumns,Table parentTable,String[] parentColumns,int joinType){
				this.childTable = childTable;
				this.childColumns = childColumns;
				this.parentTable = parentTable;
				this.parentColumns = parentColumns;
				this.joinType = joinType;
			}*/
			
			Join(Query.Table childTable,String childColumn,Query.Table parentTable,String parentColumn,int joinType){
				this.childTable = childTable;
				this.parentTable = parentTable;
				addColumns(childColumn,parentColumn);
				this.joinType = joinType;
			}
			
			void addColumns(String childColumn,String parentColumn){
				if(childColumns == null){
					childColumns = new String[]{childColumn};
					parentColumns = new String[]{parentColumn};
				}else{
					for(int i=0;i<childColumns.length;i++){
						if(childColumn.equalsIgnoreCase(childColumns[i]) && parentColumn.equalsIgnoreCase(parentColumns[i])){
							return;
						}
					}
					childColumns = Join.addToArray(childColumns, childColumn);
					parentColumns = Join.addToArray(parentColumns, parentColumn);
				}
			}
			
			private static String[] addToArray(String[] oldArray, String newObject){
				int length = oldArray.length ;
				String[] newArray = (String[]) Array.newInstance(oldArray.getClass().getComponentType(), length + 1) ;
				System.arraycopy(oldArray, 0, newArray, 0, length) ;
				newArray[length] = newObject ;
				return newArray ;
			}
			
			String getParentColumnsAsString(){
				StringBuffer sb = new StringBuffer($);
				for(String columnName:parentColumns){
					sb.append(columnName).append($);
				}
				return sb.toString().substring(0,sb.length()-$.length());
			}

			public String toString(){
				return "parentTable="+parentTable+",childTable="+childTable;
			}
		}

	}
	
	
	
	public static interface RowLoader<T> extends ResultSetLoader {
		public void load(T obj,ResultSet rs) throws SQLException ;
	}
	public static interface ColumnLoader<T> extends ResultSetLoader {
		public boolean load(T obj,String columnName,ResultSet rs) throws SQLException ;
	}
	
	public static interface IDataTypeHandler<T> {
		public Object getValue(ResultSet rs,int i) throws SQLException ;
		public Object getValue(ResultSet rs,String columnName) throws SQLException ;
		public boolean equals(T o1,T o2) ;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public @interface Table {
		public String name();
		public String alias() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface Column {
		public String name();
		public boolean pk() default false;
        public String sequence() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface FK {
		public String name();
	}
	
	/*@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface Index {
		public String name();
		public boolean unique() default false;
	}*/
	
	

	
	
	/*
	 * --------------------------
	 * ----- private begin ------
	 * --------------------------
	 */
	
	/**
	 * @param resultClass mapping to row by column name to property name. eg. policy_id = policyId
	 */
	private static<T> List<T> list(Connection conn,String sql,List params,Class resultClass,ResultSetLoader loader) {
		if(CommonConstant.SQL_SELECT_SHOW)System.out.println("sql execute list="+sql);
		PreparedStatement pstm = null ;
		ResultSet rs = null ;
		List<T> results = new ArrayList<T>() ;
		try{
			pstm = conn.prepareStatement(sql) ;
			JdbcUtils.prepareParams(pstm,params) ;
			rs = pstm.executeQuery() ;
			while(rs.next()){
				results.add((T)JdbcUtils.rs2Object(rs,resultClass,loader)) ;
			}
			return results ;
		}catch(SQLException e){
			throw new RuntimeException(sql+";\n"+params,e) ;
		}finally{
			close(rs,pstm) ;
		}
	}
	
	private static<T> List<T> list(String sql,List params,Class resultClass,ResultSetLoader loader) {
		if(CommonConstant.SQL_SELECT_SHOW)System.out.println("sql execute list="+sql);
		PreparedStatement pstm = null ;
		ResultSet rs = null ;
		Connection conn = null;
		List<T> results = new ArrayList<T>() ;
		try{
			conn = Pool.getPool().getConnection();
			pstm = conn.prepareStatement(sql) ;
			JdbcUtils.prepareParams(pstm,params) ;
			rs = pstm.executeQuery() ;
			while(rs.next()){
				results.add((T)JdbcUtils.rs2Object(rs,resultClass,loader)) ;
			}
			return results ;
		}catch(SQLException e){
			throw new RuntimeException(sql+";\n"+params,e) ;
		}finally{
			close(rs,pstm) ;
			Pool.getPool().free(conn);
		}
	}
	
	private static<T> T load(Connection conn,String sql,List params,Class resultClass,ResultSetLoader loader) {
		if(CommonConstant.SQL_SELECT_SHOW)System.out.println("sql execute load="+sql);
		PreparedStatement pstm = null ;
		ResultSet rs = null ;
		try{
			pstm = conn.prepareStatement(sql) ;
			JdbcUtils.prepareParams(pstm,params) ;
			rs = pstm.executeQuery() ;
			T instance = null;
			if(rs.next()){
				instance = (T)JdbcUtils.rs2Object(rs,resultClass,loader);
				/*if(rs.next()){
					throw new RuntimeException("result is more than one row.") ;
				}*/
			}
			return instance ;
		}catch(SQLException e){
			throw new RuntimeException(sql+";\n"+params,e) ;
		}finally{
			close(rs,pstm) ;
		}
	}
	
	private static<T> T load(String sql,List params,Class resultClass,ResultSetLoader loader) {
		if(CommonConstant.SQL_SELECT_SHOW)System.out.println("sql execute load="+sql);
		PreparedStatement pstm = null ;
		ResultSet rs = null ;
		Connection conn = null;
		try{
			conn = Pool.getPool().getConnection();
			pstm = conn.prepareStatement(sql) ;
			JdbcUtils.prepareParams(pstm,params) ;
			rs = pstm.executeQuery() ;
			T instance = null;
			if(rs.next()){
				instance = (T)JdbcUtils.rs2Object(rs,resultClass,loader);
				if(rs.next()){
					throw new RuntimeException("result is more than one row.") ;
				}
			}
			return instance ;
		}catch(SQLException e){
			throw new RuntimeException(sql+";\n"+params,e) ;
		}finally{
			close(rs,pstm);
			Pool.getPool().free(conn);
		}
	}
	
	private static Object newBeanInstance(Class cls){
		try {
			return cls.newInstance() ;
		}catch (java.lang.InstantiationException e){
			throw new RuntimeException("cann't init bean instance:" + cls + ", if this Class represents an abstract class, an interface, an array class, a primitive type, or void; or if the class has no nullary constructor; or if the instantiation fails for some other reason ", e) ;
		}catch (Exception e) {
			throw new RuntimeException("cann't init bean instance:" + cls, e) ;
		} 
	}
	
	public static void prepareParams(PreparedStatement st,List params) throws SQLException{
		if(params != null){
			for(int i = 0 ; i < params.size() ; i++){
				if(params.get(i) == null/* || "null".equals(params.get(i))*/){
					st.setObject(i+1, null, Types.VARCHAR);
				}else{
					if(params.get(i) instanceof ClobHandler){
						ClobHandler rd = (ClobHandler)params.get(i);
						st.setCharacterStream(i+1, rd.getCharacterStream(), (int)rd.length());
					}else if(params.get(i) instanceof java.util.Date){
						st.setTimestamp(i+1, new Timestamp(((Date)params.get(i)).getTime()));
					}else{
						st.setObject(i+1, params.get(i)) ;
					}
				}
			}
		}
	}
	
	/*private static<T> Object rs2Object(ResultSet rs, T resultClass,ResultSetLoader<T> loader) throws SQLException {
		return JdbcUtils.rs2Object(rs, resultClass.getClass(), loader);
	}*/
	
	private static<T> Object rs2Object(ResultSet rs, Class resultClass,ResultSetLoader<T> loader) throws SQLException {
		if(resultClass == null && loader == null){
			throw new RuntimeException("result class/row loader can not be null.") ;
		}
		Class rowClass = resultClass==null?loader.resultClass():resultClass;
		if(rowClass.isPrimitive()){
			return getResult(rs,rowClass.getName(),1);
		}
		IDataTypeHandler handler = (IDataTypeHandler)COMMON_DATA_TYPE.get(rowClass.getName());
		if(handler != null){
			return handler.getValue(rs, 1);
		}
		Object instance = newBeanInstance(rowClass) ;
		if(loader != null && loader instanceof JdbcUtils.RowLoader){
			((JdbcUtils.RowLoader)loader).load((T)instance,rs);
			return instance;
		}
		
		boolean isCollection = false;
		if(instance instanceof Collection){
			isCollection = true;
		}
		boolean isMap = false ;
		if(instance instanceof Map){
			isMap = true ;
		}
		boolean isColumnLoader = false;
		if(loader != null && loader instanceof JdbcUtils.ColumnLoader){
			isColumnLoader = true;
		}
		
		POJOWrapper pw = createPOJOWrapper(rowClass) ;
		ResultSetMetaData meta = rs.getMetaData() ;
		int count = meta.getColumnCount() ;
		for(int i = 1 ; i <= count ; i++){
			String colName = meta.getColumnLabel(i) ;
			
			if(isColumnLoader && ((JdbcUtils.ColumnLoader)loader).load(instance,colName, rs)){
				continue;
			}
			Object object = pw.getValue(colName, rs);
			if(isMap){
				if(instance instanceof IngoreCaseHashMap){
					colName = colName.toUpperCase();
				}
				((Map) instance).put(colName, object) ;
			}else if(isCollection){
				((Collection)instance).add(object);
			}else{
				pw.setValue(instance, colName,null, object) ;
			}
		}
		return instance;
	}
	
	
	private static interface ResultSetLoader<T> {
		public Class resultClass();
	}
	
	static {
		
		COMMON_DATA_TYPE.put("short", new IntegerHandler());
		COMMON_DATA_TYPE.put(Short.class.getName(), new ShortHandler()) ;
		
		COMMON_DATA_TYPE.put("int", new IntegerHandler());
		COMMON_DATA_TYPE.put(Integer.class.getName(), new IntegerHandler()) ;
		
		COMMON_DATA_TYPE.put("boolean", new BooleanHandler()) ;
		COMMON_DATA_TYPE.put(Boolean.class.getName(), new BooleanHandler()) ;
		
		COMMON_DATA_TYPE.put("long", new LongHandler()) ;
		COMMON_DATA_TYPE.put(Long.class.getName(), new LongHandler()) ;
		
		COMMON_DATA_TYPE.put("float", new FloatHandler()) ;
		COMMON_DATA_TYPE.put(Float.class.getName(), new FloatHandler()) ;
		
		COMMON_DATA_TYPE.put("double", new DoubleHandler()) ;
		COMMON_DATA_TYPE.put(Double.class.getName(), new DoubleHandler()) ;
					
		COMMON_DATA_TYPE.put("java.util.Date", new DateUtilHandler()) ;
		COMMON_DATA_TYPE.put("java.sql.Date", new DateSQLHandler()) ;
		
		COMMON_DATA_TYPE.put(Timestamp.class.getName(), new TimestampHandler()) ;
		
		COMMON_DATA_TYPE.put(String.class.getName(), new StringHandler()) ;
		
		COMMON_DATA_TYPE.put("char", new CharHandler()) ;
		COMMON_DATA_TYPE.put(Character.class.getName(), new CharHandler()) ;
		
		COMMON_DATA_TYPE.put(BigDecimal.class.getName(), new BigDecimalHandler());
		
		COMMON_DATA_TYPE.put(byte[].class.getName(), new BytesHandler()) ;
		COMMON_DATA_TYPE.put(Blob.class.getName(), new BlobHandler()) ;
		COMMON_DATA_TYPE.put(Clob.class.getName(), new ClobHandler()) ;
			
	}
	
	private static abstract class AbstractDataTypeHandler<T> implements IDataTypeHandler<T> {
		public boolean equals(T o1,T o2){
			if(o1.getClass().isPrimitive() && o2.getClass().isPrimitive()){
				return o1 == o2;
			}
			return convert(o1,o2);
		}
		
		public boolean convert(T o1,T o2){
			return o1.equals(o2);
		}
	}
	
	private static class ShortHandler extends AbstractDataTypeHandler<Short> {
		public Object getValue(ResultSet rs,int i) throws SQLException {
			Object o = rs.getShort(i);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}
		
		public boolean convert(Short o1,Short o2){
			return o1.shortValue()==o2.shortValue();
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			Object o = rs.getShort(columnName);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}
	}
	private static class IntegerHandler extends AbstractDataTypeHandler<Integer> {
		public Object getValue(ResultSet rs,int i) throws SQLException {
			Object o = rs.getInt(i);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			Object o = rs.getInt(columnName);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}
		public boolean convert(Integer o1,Integer o2){
			return o1.intValue()==o2.intValue();
		}
	}
	private static class StringHandler extends AbstractDataTypeHandler<String>{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			return rs.getString(i);
		}
		
		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
/*			try {
				System.out.println(new String(rs.getString(columnName).getBytes("iso8859_1"),"gbk"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}*/
			return rs.getString(columnName);
		}
	}
	private static class LongHandler extends AbstractDataTypeHandler<Long>{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			Object o = rs.getLong(i);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			Object o = rs.getLong(columnName);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}
		public boolean convert(Long o1,Long o2){
			return o1.longValue()==o2.longValue();
		}
	}
	private static class FloatHandler extends AbstractDataTypeHandler<Float>{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			Object o = rs.getFloat(i);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			Object o = rs.getFloat(columnName);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}
		public boolean convert(Float o1,Float o2){
			return o1.floatValue()==o2.floatValue();
		}
	}
	private static class DoubleHandler extends AbstractDataTypeHandler<Double>{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			Object o = rs.getDouble(i);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			Object o = rs.getDouble(columnName);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}
		public boolean convert(Double o1,Double o2){
			return o1.doubleValue()==o2.doubleValue();
		}
	}
	private static class DateUtilHandler extends AbstractDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			java.sql.Date d = rs.getDate(i);
			if(d == null){
				return null;
			}
			return new java.util.Date(d.getTime());
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			java.sql.Date d = rs.getDate(columnName);
			if(d == null){
				return null;
			}
			return new java.util.Date(d.getTime());
		}
	}
	private static class DateSQLHandler extends AbstractDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			return rs.getDate(i);
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			return rs.getDate(columnName);
		}
	}
	private static class TimestampHandler extends AbstractDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			return rs.getTimestamp(i);
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			return rs.getTimestamp(columnName);
		}
	}
	private static class BigDecimalHandler extends AbstractDataTypeHandler<BigDecimal>{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			return rs.getBigDecimal(i);
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			return rs.getBigDecimal(columnName);
		}
		public boolean convert(BigDecimal o1,BigDecimal o2){
			return o1.compareTo(o2)==0;
		}
	}
	private static class CharHandler extends AbstractDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			return (char)rs.getInt(i);//to-do test
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			return (char)rs.getInt(columnName);
		}
	}
	private static class BooleanHandler extends AbstractDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			String value = rs.getString(i);
			return "Y".equalsIgnoreCase(value);//to-do test
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			String value = rs.getString(columnName);
			return "Y".equalsIgnoreCase(value);
		}
	}
	private static class BytesHandler extends AbstractDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			return rs.getBytes(i);
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			return rs.getBytes(columnName);
		}
	}
	private static class BlobHandler extends AbstractDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			Blob blob = rs.getBlob(i);
			byte buffer[] = null;
			if(blob != null){
				buffer = blob.getBytes(1L, (int)blob.length());
			}
	        return buffer;
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			Blob blob = rs.getBlob(columnName);
			byte buffer[] = null;
			if(blob != null){
				buffer = blob.getBytes(1L, (int)blob.length());
			}
	        return buffer;
		}
	}
	private static class ClobHandler extends AbstractDataTypeHandler{
		private Reader reader = null;
		private int length;
		private String string;
		
		public ClobHandler(){};
		
		public ClobHandler(String string){
			this.string = string;
			this.reader = new StringReader(string);
			this.length = string.length();
		}
		
		public Reader getCharacterStream() throws SQLException {
			return reader;
		}
		
		public long length() {
			return length;
		}
		
		public Object getValue(ResultSet rs,int i) throws SQLException {
			Clob clob = rs.getClob(i);
			Reader buffer = null;
			if(clob != null){
				buffer = clob.getCharacterStream();
			}
	        return buffer;
		}

		public Object getValue(ResultSet rs, String columnName)
				throws SQLException {
			Clob clob = rs.getClob(columnName);
			Reader buffer = null;
			if(clob != null){
				buffer = clob.getCharacterStream();
			}
	        return buffer;
		}
		
		public String toString() {
			return this.string;
		}
		
	}

	
	private static Object getResult(ResultSet rs,String className,String columnName) throws SQLException {
		IDataTypeHandler handler = (IDataTypeHandler)COMMON_DATA_TYPE.get(className);
		if(handler != null){
			return handler.getValue(rs, columnName);
		}
		else{//other type
			Object o = rs.getObject(columnName);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}
	}
	private static Object getResult(ResultSet rs,String className,int i) throws SQLException {
		IDataTypeHandler handler = (IDataTypeHandler)COMMON_DATA_TYPE.get(className);
		if(handler != null){
			return handler.getValue(rs, i);
		}
		else{//other type
			Object o = rs.getObject(i);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}
	}
	
	private static void error(Exception e,String msg){
		throw new RuntimeException("[ERROR]"+msg,e);
	}
	
	public static void close(ResultSet rs,Statement st){
		if (rs != null) {
            try {
            	rs.close();
            	rs = null;
            } catch (Exception e) {
                error(e,"error on close java.sql.ResultSet.");
            }
        }
		if(st != null){
			try {
				st.close() ;
				st = null;
			} catch (SQLException e) {
				error(e,"error on close java.sql.Statement.");
			}
		}
	}
	
	private static Map<Class,POJOWrapper> pojoMap = new HashMap<Class,POJOWrapper>();
	
	private static POJOWrapper createPOJOWrapper(Class beanClass){
		POJOWrapper pw = pojoMap.get(beanClass);
		//if(pw == null){
			pw = new JdbcUtils.POJOWrapper(beanClass);
			pojoMap.put(beanClass, pw);
		//}
		return pw;
	}
	
	public static class IngoreCaseHashMap extends LinkedHashMap {
		public Object get(Object s){
			if(s instanceof String)
				return super.get(((String)s).toUpperCase());
			else
				throw new RuntimeException("this Map only support String key, not support "+s.getClass());
		}
		
		public Object put(Object k,Object v){
			if(k instanceof String)
				return super.put(((String)k).toUpperCase(),v);
			return super.put(k, v);
		}
	}
	
	public static class LowerHashMap extends LinkedHashMap {
		public Object get(Object s){
			if(s instanceof String)
				return super.get(((String)s).toLowerCase());
			else
				throw new RuntimeException("this Map only support String key, not support "+s.getClass());
		}
		
		public Object put(Object k,Object v){
			if(k instanceof String)
				return super.put(((String)k).toLowerCase(),v);
			return super.put(k, v);
		}
	}
	
	/**
	 * note: ignore case of property name.
	 * support property.property
	 */
	private static class POJOWrapper {
		
		public final static String BETWEEN_ALIAS_COLUMN = "___";
		
		private Map<String,POJOPropertyDescriptor> pds = new HashMap<String,POJOPropertyDescriptor>(100) ;
		private Class beanClass ;
        private List<Column> columns = new ArrayList<Column>(100);
		private List<String> columnNames = new ArrayList<String>(100);
		private List<String> pks = new ArrayList<String>();
		private Table table ;
		
		public POJOWrapper(Class beanClass){
			this.beanClass = beanClass ;
			table = (Table)this.beanClass.getAnnotation(Table.class);

			BeanInfo bi;
			try {
				bi = Introspector.getBeanInfo(beanClass);
			} catch (IntrospectionException e) {
				throw new RuntimeException("fail to contruct beanwrapper:" + beanClass, e) ;
			}
			
			PropertyDescriptor[] pd = bi.getPropertyDescriptors();
			for(int i = 0 ; i < pd.length ; i++){
				this.pds.put(pd[i].getName().toUpperCase(), 
						new POJOPropertyDescriptor(pd[i],pd[i].getPropertyType(),pd[i].getReadMethod(),pd[i].getWriteMethod())) ;
			}
			Field[] fields = beanClass.getDeclaredFields();
			for(Field field:fields){
				Column column = field.getAnnotation(Column.class);
                if (column != null){
                    columns.add(column);
					columnNames.add(column.name());
					if(column.pk()){
						pks.add(column.name().toUpperCase());
					}
				}
				POJOPropertyDescriptor p = pds.get(field.getName().toUpperCase());
				if(column != null && p != null){
					this.pds.put(column.name().toUpperCase(), p);
					continue;
				}
				
				FK fk = field.getAnnotation(FK.class);
				if(fk != null){
					this.pds.put(fk.name().toUpperCase(), p);
				}
			}
		}
		
		/*public Table getTable(){
			return this.table ;
		}*/
			
		public void setValue(Object beanInstance, String columnName, String columnNameWithFKColumn,Object value){
			do{
				int i = columnName.indexOf('.');
				String prop = null;
				if(i >= 0){
					prop = columnName.substring(i+1,columnName.length());
					columnName = columnName.substring(0,i);
				}
				POJOPropertyDescriptor pd = getPropertyDescriptor(columnName,columnNameWithFKColumn) ;
				if(pd == null){
					return;
					//throw new RuntimeException("unknown property[" + columnName + "] in :" + this.beanClass);
				}
				Method method = null;
				if(i >= 0){
					method = pd.getReadMethod();
				}else{
					method = pd.getWriteMethod();
				}
				if (method != null) {
					try {
						if(i >= 0){
							Object obj = method.invoke(beanInstance,new Object[]{});
							if(obj == null){
								obj = newBeanInstance(method.getReturnType());
							}
							Class returnClass = method.getReturnType();
							POJOWrapper pw = createPOJOWrapper(returnClass);
							pw.setValue(obj, prop,null, value);
							value = obj;
						}else{
							Class returnClass = pd.getPropertyType();
							if(Collection.class.isAssignableFrom(returnClass)){
								method = pd.getReadMethod();
								Collection collection = (Collection)method.invoke(beanInstance,new Object[]{});
								if(collection == null){
									collection = new ArrayList();
								}
								collection.add(value);
								method = pd.getWriteMethod();
								method.invoke(beanInstance, new Object[]{collection});
							}else{
								method.invoke(beanInstance, new Object[]{value});
							}
						}
					} catch (Exception e) {
						throw new RuntimeException("property:" + columnName+"["+pd.getPropertyType()+"]" + ",value:"+value+"["+ value.getClass() + "] not writable in :" + this.beanClass, e) ;
					} 
				}else{
					throw new RuntimeException("property:" + columnName + " not writable in :" + this.beanClass) ;
				}
				if(i < 0){
					break;
				}
			}while(true);
		}
		
		public Object getValue(String columnName,ResultSet rs) throws SQLException {
			POJOPropertyDescriptor pd = getPropertyDescriptor(columnName,null);
			if(pd == null){
				return rs.getObject(columnName);
				//throw new RuntimeException("unknown property[" + propName + "] in :" + this.beanClass) ;
			}
			String className = pd.getPropertyType().getName();
			return getResult(rs,className,columnName);
		}

        public boolean hasColumn(String columnName) {
            return getPropertyDescriptor(columnName,null)!=null?true:false;
        }
		
		public Object getValue(Object beanInstance,String columnName) {
			POJOPropertyDescriptor pd = getPropertyDescriptor(columnName,null) ;
			if(pd == null){
				return null;
				//throw new RuntimeException("unknown property[" + columnName + "] in :" + this.beanClass);
			}
			Method method = pd.getReadMethod();
			if (method != null) {
				try {
					Object obj = method.invoke(beanInstance,new Object[]{});
					return obj;
				} catch (Exception e) {
					throw new RuntimeException("property:" + columnName+"["+pd.getPropertyType()+"]" + " not readable in :" + this.beanClass, e) ;
				}
			}else{
				throw new RuntimeException("property:" + columnName + " not readable in :" + this.beanClass) ;
			}
		}
		
		private POJOPropertyDescriptor getPropertyDescriptor(String columnName,String columnNameWithFKColumn){
			columnName = columnName.toUpperCase();
			if(columnName.indexOf(BETWEEN_ALIAS_COLUMN) >= 0){
				columnName = columnName.split(BETWEEN_ALIAS_COLUMN)[1];
			}
			POJOPropertyDescriptor pd = null;
			if(columnNameWithFKColumn != null){
				columnNameWithFKColumn = columnNameWithFKColumn.toUpperCase();
				pd = this.pds.get(columnNameWithFKColumn) ;
				if(pd != null){
					return pd;
				}
				String propName = columnNameToFieldName(columnNameWithFKColumn);
				pd = this.pds.get(propName);
				if(pd != null){
					this.pds.put(columnNameWithFKColumn, pd);
					return pd;
				}
			}
			pd = this.pds.get(columnName) ;
			if(pd != null){
				return pd;
			}else{
				String propName = columnNameToFieldName(columnName);
				pd = pds.get(propName);
				if(pd != null){
					this.pds.put(columnName, pd);
					return pd;
				}
				return pd;
			}
		}
		
		private String columnNameToFieldName(String str){
			return str.replaceAll("_", "");
		}
		
		private static class POJOPropertyDescriptor{
			
			private PropertyDescriptor pd;
			private Method readMethod;
			private Method writeMethod;
			private Class propertyType;
			
			POJOPropertyDescriptor(PropertyDescriptor pd,Class propertyType,Method readMethod,Method writeMethod){
				this.pd = pd;
				this.propertyType = propertyType;
				this.readMethod = readMethod;
				this.writeMethod = writeMethod;
			}
			
			public PropertyDescriptor getPropertyDescriptor(){
				return this.pd;
			}
			public Class getPropertyType(){
				return this.propertyType;
			}
			public Method getReadMethod(){
				return this.readMethod;
			}
			public Method getWriteMethod(){
				return this.writeMethod;
			}
		}
			
	}
	
	
	public static Object createClob(String string) {
		if(string == null)return null;
		return new ClobHandler(string);
	}
	
	public static CLOB createClob(Connection conn, String string) throws Exception{
		CLOB clob   = oracle.sql.CLOB.createTemporary(conn.unwrap(OracleConnection.class), false,oracle.sql.CLOB.DURATION_SESSION);
		clob.putString(1,  string);
		return clob;
	}
	
}
