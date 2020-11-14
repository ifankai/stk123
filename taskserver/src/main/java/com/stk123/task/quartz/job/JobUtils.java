package com.stk123.task.quartz.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.stk123.service.ServiceUtils;
import com.stk123.service.HttpUtils;
import com.stk123.common.util.JsonUtils;

public class JobUtils {
	
	public static String getMoneyFlow() throws Exception {
		String page = HttpUtils.get("http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKGN&type=ct&st=(BalFlowNetRate)&sr=-1&p=1&ps=50&js=callback&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK&rt=48336427", null,"UTF-8");
		//System.out.println(page);
		String spage = StringUtils.substringBetween(page, "(", ")");
		//System.out.println(spage);
		List<String> list = JsonUtils.testJsonArray(spage);
		StringBuffer sb = new StringBuffer("<a href=\"http://data.eastmoney.com/bkzj/gn.html\">资金流入板块[净占比排序]：</a><br>");
		List<List<String>> datas = new ArrayList<List<String>>();
		for(int i=0;i<15;i++){
			String s = list.get(i);
			String[] ss = s.split(",");
			List<String> row = new ArrayList<String>();
			row.add(ss[2]);
			row.add(ss[5]+"%");
			row.add(ServiceUtils.numberFormat2Digits(Double.parseDouble(ss[4])/10000)+"亿");
			row.add(ss[14]);
			datas.add(row);
		}
		sb.append(ServiceUtils.createHtmlTable(null, datas));
		return sb.toString();
	}
}
