package com.stk123.tool.db.sql;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CopyOfSQLUtils {
	
	/**
	 * @param resultClass mapping to row by column name to property name. eg. policy_id = policyId
	 */
	public static<T> List<T> list(Connection conn,String sql,List params,Class resultClass) {
		PreparedStatement pstm = null ;
		ResultSet rs = null ;
		List<T> results = new ArrayList<T>() ;
		try{
			pstm = conn.prepareStatement(sql) ;
			CopyOfSQLUtils.prepareParams(pstm,params) ;
			rs = pstm.executeQuery() ;
			while(rs.next()){
				results.add((T)CopyOfSQLUtils.rs2Object(rs,resultClass)) ;
			}
			return results ;
		}catch(SQLException e){
			throw new RuntimeException(sql+";\n"+params,e) ;
		}finally{
			close(rs,pstm) ;
		}
	}
	
	public static<T> List<T> list(Connection conn,String sql,Class resultClass) {
		return CopyOfSQLUtils.list(conn,sql,null,resultClass);
	}
	
	public static List<Map> list2Map(Connection conn,String sql,List params) {
		return CopyOfSQLUtils.<Map>list(conn,sql,params,IngoreCaseHashMap.class);
	}
	
	public static List<Map> list2Map(Connection conn,String sql) {
		return CopyOfSQLUtils.list2Map(conn, sql, (List)null);
	}
	
	public static<T> T load(Connection conn,String sql,List params,Class resultClass,CopyOfSQLUtils.RowLoader loader) {
		PreparedStatement pstm = null ;
		ResultSet rs = null ;
		try{
			pstm = conn.prepareStatement(sql) ;
			CopyOfSQLUtils.prepareParams(pstm,params) ;
			rs = pstm.executeQuery() ;
			T instance = null;
			if(rs.next()){
				instance = (T)CopyOfSQLUtils.rs2Object(rs,resultClass);
				if(rs.next()){
					throw new RuntimeException("result is more than one row.") ;
				}
			}
			return instance ;
		}catch(SQLException e){
			throw new RuntimeException(sql+";\n"+params,e) ;
		}finally{
			close(rs,pstm) ;
		}
	}
	
	/**
	 * Load first column value
	 * @param <T>
	 * @param conn
	 * @param sql
	 * @param resultClass return type
	 * @return return type is type of resultClass
	 */
	public static<T> T load(Connection conn,String sql,Class resultClass) {
		return load(conn,sql,null,resultClass,null);
	}
	
	public static<T> T load(Connection conn,String sql,Class resultClass,CopyOfSQLUtils.RowLoader loader) {
		return load(conn,sql,null,resultClass,loader);
	}
	
	public static List load2List(Connection conn,String sql,List params,Class resultClass) {
		return load(conn,sql,params,resultClass,null);
	}
	
	public static List load2List(Connection conn,String sql,List params) {
		return load2List(conn,sql,params,ArrayList.class);
	}
	
	public static List load2List(Connection conn,String sql) {
		return load2List(conn,sql,(List)null);
	}
	
	public static long getSequence(Connection conn,String sequence) {
		return load(conn,"select "+sequence+".nextval from dual",long.class);
	}
	
	public static int insert(Connection conn,String sql,List params){
		return update(conn, sql, params);
	}
	
	public static int update(Connection conn,String sql,List params){
		PreparedStatement pstm = null ;
		try{
			pstm = conn.prepareStatement(sql) ;
			CopyOfSQLUtils.prepareParams(pstm,params) ;
			return pstm.executeUpdate();
		}catch(SQLException e){
			throw new RuntimeException(sql+";\n"+params,e) ;
		}finally{
			close(null,pstm) ;
		}
	}
	
	public static int delete(Connection conn,String sql,List params){
		return update(conn, sql, params);
	}
	
	public static int[] batchUpdate(Connection conn,List<String> sql,List<List> params){
		return null;
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
	
	private static void prepareParams(PreparedStatement st,List params) throws SQLException{
		if(params != null)
			for(int i = 0 ; i < params.size() ; i++){
				st.setObject(i+1, params.get(i)) ;
			}
	}
	
	private static Object rs2Object(ResultSet rs, Class resultClass) throws SQLException {
		if(resultClass == null){
			throw new RuntimeException("result class can not be null.") ;
		}
		if(resultClass.isPrimitive()){
			return getResult(rs,resultClass.getName(),1);
		}
		IDataTypeHandler handler = (IDataTypeHandler)COMMON_DATA_TYPE.get(resultClass.getName());;
		if(handler != null){
			return handler.getValue(rs, 1);
		}

		Object instance = newBeanInstance(resultClass) ;
		
		boolean isCollection = false;
		if(instance instanceof Collection){
			isCollection = true;
		}
		boolean isMap = false ;
		POJOWrapper pw = null;
		if(instance instanceof Map){
			isMap = true ;
		}
		pw = createPOJOWrapper(resultClass) ;
		ResultSetMetaData  meta = rs.getMetaData() ;
		int count = meta.getColumnCount() ;
		for(int i = 1 ; i <= count ; i++){
			String colName = meta.getColumnLabel(i) ;
			if(resultClass != null){
				String fieldName = columnNameToFieldName(colName);
				Object value = pw.loadResult(instance, fieldName, rs, i) ;
				if(isMap){
					((Map) instance).put(colName, value) ;
				}else if(isCollection){
					((Collection)instance).add(value);
				}else{
					pw.setValue(instance, fieldName, value) ;
				}
			}else{
				error("ResultSet column:[" + colName + "] not in [" + resultClass.getName() + "].") ;
			}
		}
		return instance;
	}
	
	private static Map COMMON_DATA_TYPE = new HashMap() ;
	static {
		try {
			COMMON_DATA_TYPE.put("int", new IntegerHandler());
			COMMON_DATA_TYPE.put(Integer.class.getName(), new IntegerHandler()) ;
			
			//COMMON_DATA_TYPE.put("boolean", BooleanHandler.class.newInstance()) ;
			//COMMON_DATA_TYPE.put(Boolean.class.getName(), BooleanHandler.class.newInstance()) ;
			
			COMMON_DATA_TYPE.put("long", new LongHandler()) ;
			COMMON_DATA_TYPE.put(Long.class.getName(), new LongHandler()) ;
			
			COMMON_DATA_TYPE.put("float", new FloatHandler()) ;
			COMMON_DATA_TYPE.put(Float.class.getName(), new FloatHandler()) ;
			
			COMMON_DATA_TYPE.put("double", new DoubleHandler()) ;
			COMMON_DATA_TYPE.put(Double.class.getName(), new DoubleHandler()) ;
						
			COMMON_DATA_TYPE.put("java.util.Date", new DateUtilHandler()) ;
			COMMON_DATA_TYPE.put("java.sql.Date", new DateSQLHandler()) ;
			
			COMMON_DATA_TYPE.put("timestamp", new TimestampHandler()) ;
			COMMON_DATA_TYPE.put(Timestamp.class.getName(), new TimestampHandler()) ;
			
			COMMON_DATA_TYPE.put(String.class.getName(), new StringHandler()) ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static interface RowLoader {
		public Object getValue(ResultSet rs,String columnName) throws SQLException ;
	}
	
	public static abstract class DefaultRowLoader implements RowLoader {
		public Object getValue(ResultSet rs,String columnName) throws SQLException {
			return null;
		}
	}
	
	private static interface IDataTypeHandler {
		public Object getValue(ResultSet rs,int i) throws SQLException ;
	}
	static class IntegerHandler implements IDataTypeHandler {
		public Object getValue(ResultSet rs,int i) throws SQLException {
			Object o = rs.getInt(i);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}
	}
	static class StringHandler implements IDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			return rs.getString(i);
		}
	}
	static class LongHandler implements IDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			Object o = rs.getLong(i);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}
	}
	static class FloatHandler implements IDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			Object o = rs.getFloat(i);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}
	}
	static class DoubleHandler implements IDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			Object o = rs.getDouble(i);
			if(rs.wasNull()){
				return null;
			}
			return o;
		}
	}
	static class DateUtilHandler implements IDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			java.sql.Date d = rs.getDate(i);
			if(d == null){
				return null;
			}
			return new java.util.Date(d.getTime());
		}
		public Object getValue(String fieldValue) {
			//return DateUtil.stringToDate(fieldValue, "yyyy-MM-dd HH:mm:ss") ;
			return null;
		}
	}
	static class DateSQLHandler implements IDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			return rs.getDate(i);
		}
	}
	static class TimestampHandler implements IDataTypeHandler{
		public Object getValue(ResultSet rs,int i) throws SQLException {
			return rs.getTimestamp(i);
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
	
	private static String columnNameToFieldName(String str){
		return str.replaceAll("_", "").replaceAll("$", "");
	}
	
	private static void error(String msg){
		System.err.println("[ERROR]"+msg);
	}
	
	private static void close(ResultSet rs,Statement st){
		if (rs != null) {
            try {
            	rs.close();
            } catch (Exception e) {
                error("error on close java.sql.ResultSet.");
            }
        }
		if(st != null){
			try {
				st.close() ;
			} catch (SQLException e) {
				error("error on close java.sql.Statement.");
			}
		}
	}
	
	private static Map<Class,POJOWrapper> pojoMap = new HashMap<Class,POJOWrapper>();
	
	private static POJOWrapper createPOJOWrapper(Class beanClass){
		return pojoMap.get(beanClass) != null?pojoMap.get(beanClass):new CopyOfSQLUtils.POJOWrapper(beanClass) ;
	}
	
	static class IngoreCaseHashMap extends HashMap {
		public Object get(Object s){
			if(s instanceof String)
				return super.get(((String)s).toUpperCase());
			else
				throw new RuntimeException("this HashMap only support String key, not support "+s.getClass());
		}
	}
	
	/**
	 * ingore case of property 
	 */
	static class POJOWrapper {
		
		private Map propertyDescriptors = new HashMap() ;
		private Class beanClass ;
		
		public POJOWrapper(Class beanClass){
			this.beanClass = beanClass ;
			
			BeanInfo bi;
			try {
				bi = Introspector.getBeanInfo(beanClass);
			} catch (IntrospectionException e) {
				throw new RuntimeException("fail to contruct beanwrapper:" + beanClass, e) ;
			}
			
			PropertyDescriptor[] pd = bi.getPropertyDescriptors();
			for(int i = 0 ; i < pd.length ; i++){
				this.propertyDescriptors.put(pd[i].getName().toUpperCase(), pd[i]) ;
			}
		}
			
		public void setValue(Object beanInstance, String propName, Object value){
			do{
				int i = propName.indexOf('.');
				String prop = null;
				if(i >= 0){
					prop = propName.substring(i+1,propName.length());
					propName = propName.substring(0,i);
				}
				
				PropertyDescriptor pd = (PropertyDescriptor) this.propertyDescriptors.get(propName.toUpperCase()) ;
				if(pd == null){
					throw new RuntimeException("unknown property[" + propName + "] in :" + this.beanClass);
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
							POJOWrapper pw = createPOJOWrapper(method.getReturnType());
							pw.setValue(obj, prop, value);
							value = obj;
						}else{
							method.invoke(beanInstance, new Object[]{value});
						}
					} catch (Exception e) {
						throw new RuntimeException("property:" + propName+"["+pd.getPropertyType()+"]" + ",value:"+value+"["+ value.getClass() + "] not writable in :" + this.beanClass, e) ;
					} 
				}else{
					throw new RuntimeException("property:" + propName + " not writable in :" + this.beanClass) ;
				}
				if(i < 0){
					break;
				}
			}while(true);
		}
		
		public Object loadResult(Object beanInstance,String propName,ResultSet rs,int i) throws SQLException{
			PropertyDescriptor pd = (PropertyDescriptor) this.propertyDescriptors.get(propName) ;
			if(pd == null){
				return rs.getObject(i);
				//throw new RuntimeException("unknown property[" + propName + "] in :" + this.beanClass) ;
			}
			String className = pd.getPropertyType().getName();
			return getResult(rs,className,i);
		}
			
		public Object getValue(Object beanInstance, String propName) {
			PropertyDescriptor pd = (PropertyDescriptor) this.propertyDescriptors.get(propName) ;
			if(pd == null){
				throw new RuntimeException("unknown property[" + propName + "] in :" + this.beanClass) ;
			}
			Method readMethod = pd.getReadMethod();
			if (readMethod != null) {
				try {
					return readMethod.invoke(beanInstance, new Object[0]);
				} catch (Exception e) {
					throw new RuntimeException("property:" + propName + " not readable in :" + this.beanClass, e) ;
				} 
			}else{
				throw new RuntimeException("property:" + propName + " not readable in :" + this.beanClass) ;
			}
		}

	}
	
	


}
