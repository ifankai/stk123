package com.stk123.tool.baidu;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.watij.webspec.dsl.Tag;
import org.watij.webspec.dsl.WebSpec;

import com.stk123.tool.util.HtmlUtils;


public class BaiDuHi {
	
	public static void main(final String[] args) {
		/*String url = "http://web.im.baidu.com/";  
        //ʵ�������������  
        WebSpec spec = new WebSpec().ie();  
        //�������������  
        //spec.hide();  
        //��Ŀ��ҳ��  
        spec.open(url);  
        //��ӡ��ҳԴ*  
        //System.out.println(spec.source());  
        
        //login in
        //spec.find.a().with.id("username").set("ifankai@aliyun.com");
        //spec.find.a().with.id("password").set("181302kevin");
        
        //click ifankai
        spec.find.div().with.title("ifankai").click();
        
        //send message
        spec.find.textArea().with.id("userInputArea").set("");
        spec.find.div().with.id("sendButton").click();
        
        //�ر����д���  
        spec.closeAll();*/
		System.out.println(HtmlUtils.removeHTML("�ظ�<a href=\"http://xueqiu.com/n/���ڵ���_123\"  target=\"_blank\">@���ڵ���_123</a>: �����벻�䡿���Ӷ������͵ñ����ˣ��Ļ�����������ȥ�����칬�����ݳɺ�����������Ҳ�ǻ��췴�ġ��������̬��������Ƕ԰�ȫ�İ��ա�<img src=\"http://js.xueqiu.com/images/face/20smile-smile.png\" title=\"[Ц]\" alt=\"[Ц]\"   height=\"24\" />"));
		sendSMS(HtmlUtils.removeHTML("�ظ�<a href=\"http://xueqiu.com/n/���ڵ���_123\"  target=\"_blank\">@���ڵ���_123</a>: �����벻�䡿���Ӷ������͵ñ����ˣ��Ļ�����������ȥ�����칬�����ݳɺ�����������Ҳ�ǻ��췴�ġ��������̬��������Ƕ԰�ȫ�İ��ա�<img src=\"http://js.xueqiu.com/images/face/20smile-smile.png\" title=\"[Ц]\" alt=\"[Ц]\"   height=\"24\" />"));
	}
	
	private final static String BaiDuHi = "http://web.im.baidu.com/";
	
	public static void sendSMS(List<String> msgs){
		if(true)return;//��ʱ���δ˹���
        WebSpec spec = null;
        try{
        	spec = new WebSpec().ie();  
	        spec.hide();  
	        spec.open(BaiDuHi);  
	        //System.out.println(spec.source());  
	        //login
	        //spec.find.a().with.id("username").set("ifankai@aliyun.com");
	        //spec.find.a().with.id("password").set("181302kevin");
	        //click ifankai
	        spec.find.div().with.title("ifankai").click();
	        //send message
	        Tag testarea = spec.find.textArea().with.id("userInputArea");
	        Tag sendButton = spec.find.div().with.id("sendButton");
	        for(String msg : msgs){
	        	testarea.set(HtmlUtils.removeHTML(msg));
	        	sendButton.click();
	        }
        }finally{
        	if(spec != null)spec.closeAll();
        }
	}
	
	public static void sendSMS(String msg){
		List<String> msgs = new ArrayList<String>();
		msgs.add(StringUtils.substring(msg,0, 800));
		sendSMS(msgs);
	}
}
