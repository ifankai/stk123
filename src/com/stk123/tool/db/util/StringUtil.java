package com.stk123.tool.db.util;

import java.util.Iterator;
import java.util.List;

public class StringUtil {
	
	public static boolean isEmpty(String s) {
		if (s == null)
			return true;
		if (s.trim().length() == 0)
			return true;
		return false;
	}
	
	public static boolean notEmpty(String s){
		return !isEmpty(s) ;
	}
	
	public static String list2DBInString(List list) {
		StringBuffer sb = new StringBuffer();
		Object item;
		for (Iterator iterator = list.iterator(); iterator.hasNext(); sb
				.append(",'").append(item).append("'"))
			item = iterator.next();

		return sb.substring(1);
	}
	
	public static String list2DBInNumber(List list) {
		StringBuffer sb = new StringBuffer();
		Object item;
		for (Iterator iterator = list.iterator(); iterator.hasNext(); sb
				.append(",").append(item))
			item = iterator.next();

		return sb.substring(1);
	}
}
