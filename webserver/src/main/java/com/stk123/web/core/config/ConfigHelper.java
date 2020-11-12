package com.stk123.web.core.config;

import java.net.URL;

import org.apache.commons.digester.Digester;

public class ConfigHelper {
	
	public static void main(String[] args) throws Exception {
		MvcConfig mvc = ConfigHelper.parseMvcConfig(MvcConfig.class,"mvc-config.xml");
		System.out.println(mvc.getPrefix());
		System.out.println(mvc.getForwards().size());
		System.out.println(mvc.getForwards().get("404"));
		System.out.println(mvc.getActions().size());
		System.out.println(mvc.getActions().get("search").getType());
		System.out.println(mvc.getActions().get("test").getForward());
	}

	public static URL getResource(Class clazz, String resName) {
		return clazz.getResource(resName);
	}
	
	public static MvcConfig parseMvcConfig(Class clazz, String resName) throws Exception {
		URL url = ConfigHelper.getResource(clazz, resName);
		Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("config", MvcConfig.class);
        digester.addObjectCreate("config/forwards/forward", ForwardConfig.class);
        digester.addObjectCreate("config/actions/action", ActionConfig.class);
        digester.addObjectCreate("config/actions/action/forward", ForwardConfig.class);
        
        digester.addSetProperties("config/forwards/forward");
        digester.addSetProperties("config/actions/action");
        digester.addSetProperties("config/actions/action/forward");
        
        digester.addSetNext("config/forwards/forward", "addForward");
        digester.addSetNext("config/actions/action", "addActionConfig");
        digester.addSetNext("config/actions/action/forward", "addForwardConfig");
        
        return (MvcConfig)digester.parse(url.openStream());
	}
}
