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
        //实例化浏览器对象  
        WebSpec spec = new WebSpec().ie();  
        //隐藏浏览器窗体  
        //spec.hide();  
        //打开目标页面  
        spec.open(url);  
        //打印网页源*  
        //System.out.println(spec.source());  
        
        //login in
        //spec.find.a().with.id("username").set("ifankai@aliyun.com");
        //spec.find.a().with.id("password").set("181302kevin");
        
        //click ifankai
        spec.find.div().with.title("ifankai").click();
        
        //send message
        spec.find.textArea().with.id("userInputArea").set("");
        spec.find.div().with.id("sendButton").click();
        
        //关闭所有窗口  
        spec.closeAll();*/
		System.out.println(HtmlUtils.removeHTML("回复<a href=\"http://xueqiu.com/n/活在当下_123\"  target=\"_blank\">@活在当下_123</a>: 【变与不变】猴子都被吹胀得变猪了，哪还有力气跳着去大闹天宫？猪都瘦成猴样，饿极了也是会造反的。变的是形态，不变的是对安全的把握。<img src=\"http://js.xueqiu.com/images/face/20smile-smile.png\" title=\"[笑]\" alt=\"[笑]\"   height=\"24\" />"));
		sendSMS(HtmlUtils.removeHTML("回复<a href=\"http://xueqiu.com/n/活在当下_123\"  target=\"_blank\">@活在当下_123</a>: 【变与不变】猴子都被吹胀得变猪了，哪还有力气跳着去大闹天宫？猪都瘦成猴样，饿极了也是会造反的。变的是形态，不变的是对安全的把握。<img src=\"http://js.xueqiu.com/images/face/20smile-smile.png\" title=\"[笑]\" alt=\"[笑]\"   height=\"24\" />"));
	}
	
	private final static String BaiDuHi = "http://web.im.baidu.com/";
	
	public static void sendSMS(List<String> msgs){
		if(true)return;//暂时屏蔽此功能
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
