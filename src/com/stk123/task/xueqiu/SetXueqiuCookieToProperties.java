package com.stk123.task.xueqiu;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.stk123.task.StkUtils;

public class SetXueqiuCookieToProperties {
	
	//�����ļ���·��
    static String profilepath="D:\\share\\workspace\\stk123\\xueqiu.cookie.properties";
    
    //���þ�̬����
    private static Properties props = new Properties();

	public static void main(String[] args) {
		String cookie = StkUtils.getSysClipboardText();
		String[] cookies = StringUtils.split(cookie, "\n");
		System.out.println("new cookie:"+cookies[cookies.length-1]);

		readValue("xueqiu.cookie");
		updateProperties("xueqiu.cookie", cookies[cookies.length-1]);       
	    System.out.println("�������");
	}
	
	public static String readValue(String key) {
        Properties props = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(profilepath));
            props.load(in);
            String value = props.getProperty(key);
            System.out.println(key +"����ֵ�ǣ�"+ value);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public static void updateProperties(String keyname,String keyvalue) {
        try {
            props.load(new FileInputStream(profilepath));
            // ���� Hashtable �ķ��� put��ʹ�� getProperty �����ṩ�����ԡ�
            // ǿ��Ҫ��Ϊ���Եļ���ֵʹ���ַ���������ֵ�� Hashtable ���� put �Ľ����
            OutputStream fos = new FileOutputStream(profilepath);           
            props.setProperty(keyname, keyvalue);
            // ���ʺ�ʹ�� load �������ص� Properties ���еĸ�ʽ��
            // ���� Properties ���е������б�����Ԫ�ضԣ�д�������
            props.store(fos, "Update '" + keyname + "' value:" + keyvalue);
        } catch (IOException e) {
            System.err.println("�����ļ����´���");
        }
    }

}
