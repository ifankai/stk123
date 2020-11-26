package com.stk123.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import com.stk123.common.db.TableTools;
import com.stk123.common.db.util.CloseUtil;
import org.springframework.util.ResourceUtils;

public class ConfigUtils {
	
	private static Properties props = new Properties();
	
	static {
		try {
			ConfigUtils.setPropsFromResource(TableTools.class, "db.properties");
			ConfigUtils.setPropsFromResource("stk.properties");
			
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

	public static Properties getProps(){
	    return ConfigUtils.props;
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
//		URL resUrl = clazz.getResource(resName);
		URL resUrl = ConfigUtils.getURL(resName);
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

	public static Properties getPropertiesFromClassPath(String resName) throws Exception {
		setPropsFromResource(null, resName);
		return props;
	}
	
	/**
第一：前面有 “/”

“ / ”代表了工程的根目录，例如工程名叫做myproject，“ / ”代表了myproject

me.class.getResourceAsStream("/com/x/file/myfile.xml");

第二：前面没有 “   / ”

代表当前类的目录

me.class.getResourceAsStream("myfile.xml");

me.class.getResourceAsStream("file/myfile.xml");
	 * @param resName 前面加/说明是以src为根目录，不加的话是以ConfigUtils.class所在目录为根目录
	 * @throws Exception
	 */
	public static void setPropsFromResource(String resName) throws Exception {
		setPropsFromResource(ConfigUtils.class, resName);
	}

	public static URL getURL(String resName) throws FileNotFoundException {
		return ResourceUtils.getURL("classpath:"+resName);
	}
	
	public static void main(String[] args) throws Exception {
		ConfigUtils.setPropsFromResource("db.properties");
		ConfigUtils.setPropsFromResource("stk.properties");
		String a = ConfigUtils.getProp("host");
		System.out.println(a);
		System.out.println(ConfigUtils.getProp("xe.db_url"));
	}

}
