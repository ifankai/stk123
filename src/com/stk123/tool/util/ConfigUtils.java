package com.stk123.tool.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import com.stk123.tool.db.TableTools;
import com.stk123.tool.db.util.CloseUtil;
import com.stk123.web.StkConstant;

public class ConfigUtils {
	
	private static Properties props = new Properties();
	
	static {
		try {
			ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
			ConfigUtils.setPropsFromResource("/stk.properties");
			
			InetAddress inet = InetAddress.getLocalHost();
			String ip = inet.getHostAddress();
			if(ip.startsWith("192")){
				setProp("is_dev", "Y");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getProp(String property) {
		return props.getProperty(property);
	}
	public static void setProp(String property, String value) {
		props.setProperty(property, value);
	}
	
	public static void setConfigFile(String file) {
		loadProperties(props,new File(file));
	}
	
	public static void loadProperties(Properties props,File f) {
        if (f == null || false == f.isFile()) {
            throw new RuntimeException("File "+f.getName()+" is not exists or not a file.");
        }
        InputStream fis = null;
        try {
            fis = new FileInputStream(f);
            props.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(fis);
        }
    }
	
	public static void setPropsFromResource(Class clazz, String resName) throws Exception {
		URL resUrl = clazz.getResource(resName);
        if (resUrl == null) {
        	throw new RuntimeException("Resource file "+resUrl+" is not exists.");
        }
        InputStream fis = null;
        try {
            fis = resUrl.openStream();
            props.load(fis);
        } finally {
        	CloseUtil.close(fis);
        }
	}
	
	/**
��һ��ǰ���� ��/��

�� / �������˹��̵ĸ�Ŀ¼�����繤��������myproject���� / ��������myproject

me.class.getResourceAsStream("/com/x/file/myfile.xml");

�ڶ���ǰ��û�� ��   / ��

����ǰ���Ŀ¼

me.class.getResourceAsStream("myfile.xml");

me.class.getResourceAsStream("file/myfile.xml");
	 * @param resName ǰ���/˵������srcΪ��Ŀ¼�����ӵĻ�����ConfigUtils.class����Ŀ¼Ϊ��Ŀ¼
	 * @throws Exception
	 */
	public static void setPropsFromResource(String resName) throws Exception {
		setPropsFromResource(ConfigUtils.class, resName);
	}
	
	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource("/com/stk123/tool/db/db.properties");
		ConfigUtils.setPropsFromResource("/stk.properties");
		String a = ConfigUtils.getProp("host");
		System.out.println(a);
		System.out.println(ConfigUtils.getProp("xe.db_url"));
	}

}
