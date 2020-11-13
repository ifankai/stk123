package com.stk123.common.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.util.ResourceUtils;

public class CacheUtils {
	
	//-----stock cache key------------//
	public final static String KEY_STK_STOCK = "stock";
    public final static String KEY_STK_INDUSTRY = "industry";
    public final static String KEY_STK_K = "k";
    public final static String KEY_STK_FN = "fn";
    
    //-----forever cache key----------//
    public final static String KEY_FN_TYPE = "fnType";
    public final static String KEY_FN_TYPE_DISPLAY = "fnTypeDisplay";
    public final static String KEY_INDUSTRY_TYPE = "industryType";
    public final static String KEY_INDUSTRY_STK = "industry.";
    public final static String KEY_STKS_COLUMN_NAMES = "stksColumnNames";//多股同列显示的列名key
    public final static String KEY_INDEX_TREE = "indexTree";
	
    //one day key
    public final static String KEY_ONE_DAY = "oneday";
    
	private static final String path = "ehcache.xml";  
    private static CacheManager manager;
    private static final String DOT = ".";
  
    static{
        try {
            URL url = ResourceUtils.getURL("classpath:" + path);
            manager = CacheManager.create(url);
            for (String name : manager.getCacheNames()) {
                //System.out.println(name);
            }
            //System.out.println(System.getProperty("java.io.tmpdir"));
        }catch (Exception e){

        }
    }  
    
    public static void close(){
    	if(manager != null)manager.shutdown();
    }
  
    public static void put(String cacheName, String key, Object value) {  
        Cache cache = manager.getCache(cacheName);  
        Element element = new Element(key, value);  
        cache.put(element);
        //System.out.println("cache count="+cache.getSize());
    }  
  
    public static Object get(String cacheName, String key) {  
        Cache cache = manager.getCache(cacheName); 
        Element element = cache.get(key);
        return element == null ? null : element.getObjectValue();  
    }  
  
    public static Cache getCache(String cacheName) {  
        return manager.getCache(cacheName);  
    }  
  
    public static void remove(String cacheName, String key) {  
        Cache cache = manager.getCache(cacheName);  
        cache.remove(key);  
    }
    
    //tree structure cache
    public static void putTree(String cacheName, String path, Object value) {
    	int dot = path.indexOf(DOT);
    	if(dot < 0){
    		put(cacheName, path, value);
    	}else{
    		String key = path.substring(0, dot);
    		Map map = (Map)get(cacheName,key);
    		if(map == null){
				map = new HashMap();
				put(cacheName, key, map);
			}
    		String right = path.substring(dot+1, path.length());
    		while((dot = right.indexOf(DOT)) >= 0){
    			key = right.substring(0, dot);
    			Map m = (Map)map.get(key);
    			if(m == null){
    				m = new HashMap();
    				map.put(key, m);
    			}
    			map = m;
    			right = right.substring(dot+1, right.length());
    		}
    		map.put(right, value);
    	}
    }
    
    public static Object getTree(String cacheName, String path) {
    	int dot = path.indexOf(DOT);
    	if(dot < 0){
    		return get(cacheName, path);
    	}else{
    		String key = path.substring(0, dot);
    		Map map = (Map)get(cacheName,key);
    		if(map == null){
				return null;
			}
    		String right = path.substring(dot+1, path.length());
    		while((dot = right.indexOf(DOT)) >= 0){
    			key = right.substring(0, dot);
    			Map m = (Map)map.get(key);
    			if(m == null){
    				return null;
    			}
    			map = m;
    			right = right.substring(dot+1, right.length());
    		}
    		return map.get(right);
    	}
    }
    
    public static void removeTree(String cacheName, String path) {
    	int dot = path.indexOf(DOT);
    	if(dot < 0){
    		remove(cacheName, path);
    	}else{
    		String key = path.substring(0, dot);
    		Map map = (Map)get(cacheName,key);
    		if(map != null){
    			String right = path.substring(dot+1, path.length());
        		while((dot = right.indexOf(DOT)) >= 0){
        			key = right.substring(0, dot);
        			Map m = (Map)map.get(key);
        			if(m != null){
        				map = m;
            			right = right.substring(dot+1, right.length());
        			}
        			
        		}
        		map.remove(right);
			}
    		
    	}
    }
    
    public static boolean DISABLE = false;
    
    //--------stock cache--------//
    private final static String STOCK = "stock";
    public static void putByCode(String code, String path, Object value) {
    	if(!DISABLE){
    		CacheUtils.putTree(STOCK, code + DOT + path, value);
    	}
    }
    public static Object getByCode(String code, String path) {
    	return CacheUtils.getTree(STOCK, code + DOT + path);
    }
    public static void removeByCode(String code, String path) {
    	CacheUtils.removeTree(STOCK, code + DOT + path);
    }
    
    
    
    //------forever cache-------//
    private final static String FOREVER = "forever";
    public static void putForever(String path, Object value) {
    	if(!DISABLE){
    		CacheUtils.putTree(FOREVER, path, value);
    	}
    }
    public static Object getForever(String path) {
    	return CacheUtils.getTree(FOREVER, path);
    }
    public static void removeForever(String path) {
    	CacheUtils.removeTree(FOREVER, path);
    }
    
    
    
    
    
    
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(System.getProperty("java.io.tmpdir"));
		//指定ehcache.xml的位置  
        URL url = ResourceUtils.getURL("classpath:ehcache.xml");
		System.out.println(url.getPath());
		CacheManager manager = CacheManager.create(url);
		for(String name : manager.getCacheNames()){
			System.out.println(name);
		}
	    
	    CacheUtils.putByCode("000997", "name", "新大陆");
	    for(int i=0;i<1000;i++){
	    	CacheUtils.putByCode("000997"+i, "name", "新大陆"+i);
	    }
	    System.out.println(CacheUtils.getByCode("000997100", "name"));
	    manager.shutdown();
	}

}
