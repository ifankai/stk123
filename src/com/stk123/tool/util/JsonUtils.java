package com.stk123.tool.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.stk123.json.Column;
import com.stk123.json.SQL;
import com.stk123.tool.util.collection.Name2Value;

import net.sf.ezmorph.MorphException;
import net.sf.ezmorph.object.AbstractObjectMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultValueProcessor;
import net.sf.json.processors.JsonBeanProcessor;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.JSONUtils;
import net.sf.json.util.PropertyFilter;

public class JsonUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SQL sql = new SQL();
		sql.setSql("update stk set code=? where code=?");
		List<Column> params = new ArrayList<Column>();
		Column col = new Column();
		col.setValue("300023");
		params.add(col);
		col = new Column();
		col.setValue("400023");
		params.add(col);
		sql.setParams(params);
		String json = JsonUtils.getJsonString4JavaPOJO(sql, null);
		System.out.println(json);
		
		String s = "{\"clazz\":\"\",\"params\":[{\"type\":\"\",\"value\":\"300023\"},{\"type\":\"\",\"value\":\"400023\"}],\"sql\":\"update stk set code=? where code=?\"}";
		Map<String, Class> m = new HashMap<String, Class>();
        m.put("params", Column.class);
		sql = (SQL)JsonUtils.getObject4Json(s, SQL.class, m);
		System.out.println(sql.getParams());
	}
	
	private static String[] formats = { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" };

	public static Object getObject4Json(String json, Class cls) {
        JSONUtils.getMorpherRegistry().registerMorpher(new TimestampMorpher(formats));
        JSONObject jsonObject = JSONObject.fromObject(json);
        return JSONObject.toBean(jsonObject, cls);
    }
	
	public static Object getObject4Json(String jsonString,Class pojoCalss, Map<String, Class> m){
		//JSONUtils.getMorpherRegistry().registerMorpher(new TimestampMorpher(formats));
		JSONObject jsonObject = JSONObject.fromObject( jsonString ); 
        return JSONObject.toBean(jsonObject,pojoCalss,m);
    }
	
	public static List getList4Json(String jsonString, Class pojoClass){
        JSONUtils.getMorpherRegistry().registerMorpher(new TimestampMorpher(formats));
		return getList4Json(jsonString, pojoClass, null);
	}
	
    public static List getList4Json(String jsonString, Class pojoClass, Map<String, Class> m){
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        Object pojoValue;
        
        List list = new ArrayList();
        for ( int i = 0 ; i<jsonArray.size(); i++){
        	if(jsonArray.get(i) instanceof Collection){
        		JSONArray jsonObject = (JSONArray)jsonArray.get(i);
        		pojoValue = JSONArray.toCollection(jsonObject,pojoClass);
                list.add(pojoValue);
        	}else{
        		JSONObject jsonObject = (JSONObject)jsonArray.get(i);
        		pojoValue = JSONObject.toBean(jsonObject,pojoClass,m);
                list.add(pojoValue);
        	}
        }
        return list;

    }

    
    /** *//**
     * 将java对象转换成json字符串,并设定日期格式
     * @param javaObj
     * @param dataFormat
     * @return
     */
    public static String getJsonString4JavaPOJO(Object javaObj , String dateFormat){
        return getJsonString4JavaPOJO(javaObj, null, dateFormat);
    }
    
    public static String getJsonString4JavaPOJO(Object javaObj){
		JsonConfig jsonConfig = configJson();
        if(javaObj instanceof Collection){
        	return JSONArray.fromObject(javaObj, jsonConfig).toString();
        }else{
        	return JSONObject.fromObject(javaObj, jsonConfig).toString();
        }
    }
    
    public static String getJsonString4JavaPOJO(Object javaObj, List<String> includes , String dateFormat){
        JsonConfig jsonConfig = configJson(includes, dateFormat);
        if(javaObj instanceof Collection){
        	return JSONArray.fromObject(javaObj,jsonConfig).toString();
        }else{
        	return JSONObject.fromObject(javaObj,jsonConfig).toString();
        }
    }
    
    
    
	
	/**
	 * 普通纯List转为Json
	 * @param params
	 * @return [{"name":100,"value":"java.lang.Integer"},{"name":"test","value":"java.lang.String"},{"name":1501404622450,"value":"java.sql.Timestamp"}]
	 */
	public static String parseListToJson(List params){
		List<Name2Value> list = new ArrayList<Name2Value>();
		for(Object param : params){
			Object obj = param;
			if(param instanceof java.util.Date){
				obj = ((java.util.Date)obj).getTime();
			}else if(param instanceof java.sql.Timestamp){
				obj = ((java.sql.Timestamp)obj).getTime();
			}
			Name2Value nv = new Name2Value(obj, param==null?Object.class:param.getClass());
			list.add(nv);
		}
		return JsonUtils.getJsonString4JavaPOJO(list);
	}
	
	public static List parseJsonToList(String json) {
		List<Name2Value> list = JsonUtils.getList4Json(json, Name2Value.class);
		List params = new ArrayList();
		for(Name2Value nv : list){
			String type = (String)nv.getValue();
			Object param = nv.getName();
			if("java.util.Date".equals(type)){
				param = new java.util.Date((long)nv.getName());
			}else if("java.sql.Timestamp".equals(type)){
				param = new java.sql.Timestamp((long)nv.getName());
			}
			params.add(param);
		}
		
		return params;
	}
	
    
	
    public static String string2Json(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '/':
				sb.append("\\/");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}
    
    
    
    public static JsonConfig configJson(){
    	JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.registerDefaultValueProcessor(Integer.class,  
		        new DefaultValueProcessor() {  
		            public Object getDefaultValue(Class type) {  
		                return null;  
		            }  
		        });
		jsonConfig.registerDefaultValueProcessor(Double.class,  
		        new DefaultValueProcessor() {  
		            public Object getDefaultValue(Class type) {  
		                return null;  
		            }  
		        });
		//jsonConfig.registerJsonBeanProcessor(Timestamp.class, new DateJsonBeanProcessor());
		return jsonConfig;
    }
    
    /** *//**
     * 
     * @param excludes
     * @param datePattern
     * @return
     */
	public static JsonConfig configJson(final List<String> includes, String datePattern) {
		JsonConfig jsonConfig = new JsonConfig();
		//jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor(datePattern));
		jsonConfig.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor(datePattern));
		if(includes != null){
			jsonConfig.setJsonPropertyFilter(new PropertyFilter(){
			    public boolean apply(Object source, String name, Object value) {
			        if(includes.contains(name)) {
			        	return false;
			        } else {
			        	return true;
			        }
			    }
			});
		}
		
		jsonConfig.registerDefaultValueProcessor(Integer.class,  
		        new DefaultValueProcessor() {  
		            public Object getDefaultValue(Class type) {  
		                return null;  
		            }  
		        });
		jsonConfig.registerDefaultValueProcessor(Double.class,  
		        new DefaultValueProcessor() {  
		            public Object getDefaultValue(Class type) {  
		                return null;  
		            }  
		        });
		return jsonConfig;
	}
	
	private static JsonConfig configJson(String[] excludes, String datePattern) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(excludes);
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor(datePattern));
		jsonConfig.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor(datePattern));
		
		jsonConfig.registerDefaultValueProcessor(Integer.class,  
	        new DefaultValueProcessor() {  
	            public Object getDefaultValue(Class type) {  
	                return null;  
	            }  
	        });
		jsonConfig.registerDefaultValueProcessor(Double.class,  
		        new DefaultValueProcessor() {  
		            public Object getDefaultValue(Class type) {  
		                return null;  
		            }  
		        });
		return jsonConfig;
	}
	
	
	public static List testJsonArray(String str) {
		//JSONUtils.getMorpherRegistry().registerMorpher(new TimestampMorpher(formats));
        JSONArray jsonArr = JSONArray.fromObject(str);
        List list = new ArrayList();
        for (Object json : jsonArr) {
            String jsonStr = json.toString();
           	if(isString(jsonStr)){
            	list.add(jsonStr);
            }else if(isJson(jsonStr)){
            	list.add(testJson(jsonStr.toString()));
            }else if(isJsonArray(jsonStr)){
              	list.add(testJsonArray(jsonStr.toString()));
         	}    
        }
        return list;
    }
    
    public static Map testJson(String str) {
        JSONObject json = JSONObject.fromObject(str);
        Iterator<?> it = json.keySet().iterator();
        Map map = new HashMap();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = json.getString(key);
            if (isString(value)) {
                map.put(key, value);
            }else if (isJson(value)) {
                map.put(key, testJson(value));
            }else if (isJsonArray(value)) {
                map.put(key, testJsonArray(value));
            }
        }
        return map;
    }
 
    public static boolean isJson(String s) {
        boolean flag = true;
        try {
            JSONObject.fromObject(s);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
 
    public static boolean isJsonArray(String s) {
        boolean flag = true;
        try {
            JSONArray.fromObject(s);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
 
    public static boolean isString(String s) {
        return !isJson(s) && !isJsonArray(s);
    }
}

class TimestampMorpher extends AbstractObjectMorpher {
    /*** 日期字符串格式 */
    private String[] formats;

    public TimestampMorpher(String[] formats) {
        this.formats = formats;
    }

    public Object morph(Object value) {
        if (value == null) {
            return null;
        }
        if (Timestamp.class.isAssignableFrom(value.getClass())) {
            return (Timestamp) value;
        }
        if (!supports(value.getClass())) {
            throw new MorphException(value.getClass() + " 是不支持的类型");
        }
        String strValue = (String) value;
        SimpleDateFormat dateParser = null;
        for (int i = 0; i < formats.length; i++) {
            if (null == dateParser) {
                dateParser = new SimpleDateFormat(formats[i]);
            } else {
                dateParser.applyPattern(formats[i]);
            }
            try {
                return new Timestamp(dateParser.parse(strValue.toLowerCase()).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Class morphsTo() {
        return Timestamp.class;
    }

    public boolean supports(Class clazz) {
        return String.class.isAssignableFrom(clazz);
    }

}


class DateJsonBeanProcessor implements JsonBeanProcessor  
{  
    public JSONObject processBean( Object bean, JsonConfig jsonConfig ) {  
          JSONObject jsonObject = null;
          System.out.println("bean==="+bean);
          if( bean instanceof java.sql.Date ){  
             bean = new Date( ((java.sql.Date) bean).getTime() );  
          }  
          if( bean instanceof java.sql.Timestamp ){  
            bean = new Date( ((java.sql.Timestamp) bean).getTime() );  
          }  
          if( bean instanceof Date ){  
             jsonObject = new JSONObject();  
             jsonObject.element("time", ( (Date) bean ).getTime());  
          }else{  
             jsonObject = new JSONObject( true );  
          }  
          return jsonObject;  
       }  
}  

class DateJsonValueProcessor implements JsonValueProcessor {
    
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";   
    private DateFormat dateFormat;   

    /** *//**  
     * 构造方法.  
     *  
     * @param datePattern 日期格式  
     */  
    public DateJsonValueProcessor(String datePattern) {   
        if( null == datePattern )
            dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);  
        else
            dateFormat = new SimpleDateFormat(datePattern); 
        
    }   
    
    /**//* （非 Javadoc）
     * @see net.sf.json.processors.JsonValueProcessor#processArrayValue(java.lang.Object, net.sf.json.JsonConfig)
     */
    public Object processArrayValue(Object arg0, JsonConfig arg1) {
        return process(arg0);   
    }

    /**//* （非 Javadoc）
     * @see net.sf.json.processors.JsonValueProcessor#processObjectValue(java.lang.String, java.lang.Object, net.sf.json.JsonConfig)
     */
    public Object processObjectValue(String arg0, Object arg1, JsonConfig arg2) {
        return process(arg1);   
    }
    
    private Object process(Object value) {
    	if(value == null)return value;
    	if(value instanceof Timestamp){
    		return dateFormat.format(new Date(((Timestamp)value).getTime()));
    	}else{
    		return dateFormat.format((Date) value);
    	}
    }   
    
    
}
