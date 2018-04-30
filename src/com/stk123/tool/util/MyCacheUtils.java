package com.stk123.tool.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyCacheUtils {
	private static HashMap cacheMap = new HashMap();

	/**
	 * This class is singleton so private constructor is used.
	 */
	private MyCacheUtils() {
		super();
	}

	/**
	 * returns cache item from hashmap
	 * 
	 * @param key
	 * @return Cache
	 */
	private synchronized static Cache getCache(String key) {
		return (Cache) cacheMap.get(key);
	}

	/**
	 * Looks at the hashmap if a cache item exists or not
	 * 
	 * @param key
	 * @return Cache
	 */
	private synchronized static boolean hasCache(String key) {
		return cacheMap.containsKey(key);
	}

	/**
	 * Invalidates all cache
	 */
	public synchronized static void invalidateAll() {
		cacheMap.clear();
	}

	/**
	 * Invalidates a single cache item
	 * 
	 * @param key
	 */
	public synchronized static void invalidate(String key) {
		cacheMap.remove(key);
	}

	/**
	 * Adds new item to cache hashmap
	 * 
	 * @param key
	 * @return Cache
	 */
	private synchronized static void putCache(String key, Cache object) {
		cacheMap.put(key, object);
	}

	/**
	 * Reads a cache item's content
	 * 
	 * @param key
	 * @return
	 */
	public static Cache get(String key) {
		if (hasCache(key)) {
			Cache cache = getCache(key);
			if (cacheExpired(cache)) {
				//cache.setExpired(true);
				//invalidate(key);
				return null;
			}
			return cache;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param key
	 * @param content
	 * @param ttl
	 */
	public static void put(String key, Object content, long ttl) {
		Cache cache = new Cache();
		cache.setKey(key);
		cache.setValue(content);
		cache.setTimeOut(ttl + new Date().getTime());
		cache.setExpired(false);
		putCache(key, cache);
	}
	
	public static void put(String key, Object content) {
		Cache cache = new Cache();
		cache.setKey(key);
		cache.setValue(content);
		cache.setTimeOut(-1);
		cache.setExpired(false);
		putCache(key, cache);
	}

	/** @modelguid {172828D6-3AB2-46C4-96E2-E72B34264031} */
	private static boolean cacheExpired(Cache cache) {
		if (cache == null) {
			return false;
		}
		long milisExpire = cache.getTimeOut();
		if (milisExpire < 0) { // Cache never expires
			return false;
		}
		long milisNow = new Date().getTime();
		if (milisNow >= milisExpire) {
			return true;
		} else {
			return false;
		}
	}
}

class Cache {
	private String key;
	private Object value;
	private long timeOut;
	private boolean expired;

	public Cache() {
		super();
	}

	public Cache(String key, String value, long timeOut, boolean expired) {
		this.key = key;
		this.value = value;
		this.timeOut = timeOut;
		this.expired = expired;
	}

	public String getKey() {
		return key;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public Object getValue() {
		return value;
	}

	public void setKey(String string) {
		key = string;
	}

	public void setTimeOut(long l) {
		timeOut = l;
	}

	public void setValue(Object object) {
		value = object;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean b) {
		expired = b;
	}
}