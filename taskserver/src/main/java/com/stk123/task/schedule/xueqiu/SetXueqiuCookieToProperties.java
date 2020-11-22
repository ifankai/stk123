package com.stk123.task.schedule.xueqiu;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.stk123.util.ServiceUtils;

public class SetXueqiuCookieToProperties {
	
	//属性文件的路径
    static String profilepath="D:\\share\\workspace\\stk123\\xueqiu.cookie.properties";
    
    //采用静态方法
    private static Properties props = new Properties();

	public static void main(String[] args) {
		String cookie = ServiceUtils.getSysClipboardText();
		String[] cookies = StringUtils.split(cookie, "\n");
		String sCookie = cookies[cookies.length-1];
        System.out.println("cookie:"+sCookie);
		if(StringUtils.containsIgnoreCase(sCookie, "xueqiu.com") || StringUtils.containsIgnoreCase(sCookie, "xq_id_token")) {
            System.out.println("new cookie:" + sCookie);

            readValue("xueqiu.cookie");
            updateProperties("xueqiu.cookie", sCookie);
            System.out.println("操作完成");
        }else{
            System.out.println("操作出错");
        }
	}
	
	public static String readValue(String key) {
        Properties props = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(profilepath));
            props.load(in);
            String value = props.getProperty(key);
            System.out.println(key +"键的值是："+ value);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public static void updateProperties(String keyname,String keyvalue) {
        try {
            props.load(new FileInputStream(profilepath));
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
            OutputStream fos = new FileOutputStream(profilepath);           
            props.setProperty(keyname, keyvalue);
            // 以适合使用 load 方法加载到 Properties 表中的格式，
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
            props.store(fos, "Update '" + keyname + "' value:" + keyvalue);
        } catch (IOException e) {
            System.err.println("属性文件更新错误");
        }
    }

}
