package com.stk123.common.util;

import com.thoughtworks.xstream.XStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class XmlUtils {

	public static List<String> getList4Xml(String xml) throws Exception {
		SAXBuilder sb=new SAXBuilder();
		StringReader read = new StringReader(xml);
        InputSource source = new InputSource(read);
		Document doc =sb.build(source);
		Element root = doc.getRootElement();
		List<Element> list = root.getChildren();
		List<String> result = new ArrayList<String>();
		for(Element s : list){
			result.add(s.getText());
		}
		return result;
	}
	
	public static Object getObject4Xml(String xml, Map<String,Class> clazz){
		XStream xstream = new XStream(); 
		for(Map.Entry<String,Class> entry : clazz.entrySet()){
			xstream.alias(entry.getKey(),entry.getValue());
		}
        return xstream.fromXML(xml);
	}
	
	public static String getXml4Object(Object obj, Map<String,Class> clazz){
		XStream xstream = new XStream(); 
		for(Map.Entry<String,Class> entry : clazz.entrySet()){
			xstream.alias(entry.getKey(),entry.getValue());
		}
        return xstream.toXML(obj);
	}
	
	public static void main(String[] args) throws Exception{
		String page = CommonHttpUtils.get("http://www.webxml.com.cn/WebServices/ChinaStockWebService.asmx/getStockInfoByCode?theStockCode=sz002570", null, "GBK");
		/*
<?xml version="1.0" encoding="utf-8"?>
<ArrayOfString xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://WebXml.com.cn/">
  <string>sz002570</string>
  <string>贝因美</string>
  <string>2013-08-13 15:05:49</string>
  <string>33.98</string>
  <string>34.37</string>
  <string>34.40</string>
  <string>-0.39</string>
  <string>33.50</string>
</ArrayOfString>
		*/
		Map<String,Class> map = new HashMap<String,Class>();
		map.put("ArrayOfString", List.class);
		List<String> obj = (List<String>)getObject4Xml(page,map);
		System.out.println(obj);
		System.out.println(getXml4Object(obj, map));
	}

}
