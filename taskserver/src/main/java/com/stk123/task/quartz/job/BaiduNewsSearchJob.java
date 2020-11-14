package com.stk123.task.quartz.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.service.ServiceUtils;
import com.stk123.service.baidu.BaiduSearch;
import com.stk123.common.util.EmailUtils;
import com.stk123.service.ExceptionUtils;
import com.stk123.common.util.collection.Name2Value;

public class BaiduNewsSearchJob implements Job {
	
	private static List<Name2Value<String, Boolean>> NEWS_LIST = new ArrayList<Name2Value<String,Boolean>>();
	static {
		NEWS_LIST.add(new Name2Value("行业 景气 回升", false));
		NEWS_LIST.add(new Name2Value("行业 扭亏", false));
		NEWS_LIST.add(new Name2Value("行业 拐点", false));
		NEWS_LIST.add(new Name2Value("全球 股市 估值", false));
		NEWS_LIST.add(new Name2Value("全行业 亏损", false));
		NEWS_LIST.add(new Name2Value("创历史新低 连续暴跌", false));
		NEWS_LIST.add(new Name2Value("创年内新高 行业复苏", false));
		NEWS_LIST.add(new Name2Value("产业需求", false));
		NEWS_LIST.add(new Name2Value("持续涨价", true));
		NEWS_LIST.add(new Name2Value("价格 倒挂 -股价", true));
		NEWS_LIST.add(new Name2Value("行业 回暖", true));
		NEWS_LIST.add(new Name2Value("(行业 | 收入 | 营收) 连续 (下降 | 下跌)", true));
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		for(Name2Value<String, Boolean> nv : NEWS_LIST){
			System.out.println("BaiduNewsSearchJob:"+nv.getName());
			List<String> results;
			try {
				results = BaiduSearch.getBaiduNews(ServiceUtils.addDay(new Date(), -1), nv.getName(), nv.getValue());
				if(results.size() > 0){
					EmailUtils.send("百度新闻 -- "+nv.getName(), wrap(StringUtils.join(results, "<br>")));
				}
				Thread.sleep(1000*2);
			} catch (Exception e) {
				EmailUtils.send("BaiduNewsSearchJob Error", ExceptionUtils.getExceptionAsString(e));
			}
		}
	}
	
	private String wrap(String body){
		StringBuffer sb = new StringBuffer();
		sb.append("<html><head>");
		sb.append("<style type=\"text/css\">");
		sb.append(".c-title{font-size: 14px;} ");
		sb.append(".c-summary{font-size: 13px;} ");
		sb.append("h3, .c-author{margin:0} ");
		sb.append(".c-row {margin:0px 0px 5px 0px; width:500px}");
		sb.append("a:link, a:visited {color: #0000CC;} ");
		sb.append("em {font-style: normal; color: #c60a00;} ");
		sb.append("</style>");
		sb.append("<body>");
		sb.append("\r");
		sb.append(body);
		sb.append("\r");
		sb.append("</body></html>");
		return sb.toString();
	}
	
	public static void main(String[] args) throws JobExecutionException {
		BaiduNewsSearchJob job = new BaiduNewsSearchJob();
		job.execute(null);
	}

}
