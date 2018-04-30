package com.stk123.model.quartz.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.task.StkUtils;
import com.stk123.tool.baidu.BaiduSearch;
import com.stk123.tool.util.EmailUtils;
import com.stk123.tool.util.ExceptionUtils;
import com.stk123.tool.util.collection.Name2Value;

public class BaiduNewsSearchJob implements Job {
	
	private static List<Name2Value<String, Boolean>> NEWS_LIST = new ArrayList<Name2Value<String,Boolean>>();
	static {
		NEWS_LIST.add(new Name2Value("��ҵ ���� ����", false));
		NEWS_LIST.add(new Name2Value("��ҵ Ť��", false));
		NEWS_LIST.add(new Name2Value("��ҵ �յ�", false));
		NEWS_LIST.add(new Name2Value("ȫ�� ���� ��ֵ", false));
		NEWS_LIST.add(new Name2Value("ȫ��ҵ ����", false));
		NEWS_LIST.add(new Name2Value("����ʷ�µ� ��������", false));
		NEWS_LIST.add(new Name2Value("�������¸� ��ҵ����", false));
		NEWS_LIST.add(new Name2Value("��ҵ����", false));
		NEWS_LIST.add(new Name2Value("�����Ǽ�", true));
		NEWS_LIST.add(new Name2Value("�۸� ���� -�ɼ�", true));
		NEWS_LIST.add(new Name2Value("��ҵ ��ů", true));
		NEWS_LIST.add(new Name2Value("(��ҵ | ���� | Ӫ��) ���� (�½� | �µ�)", true));
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		for(Name2Value<String, Boolean> nv : NEWS_LIST){
			List<String> results;
			try {
				results = BaiduSearch.getBaiduNews(StkUtils.addDay(new Date(), -1), nv.getName(), nv.getValue());
				if(results.size() > 0){
					EmailUtils.send("�ٶ����� -- "+nv.getName(), wrap(StringUtils.join(results, "<br>")));
				}
				Thread.sleep(1000*2);
			} catch (Exception e) {
				EmailUtils.send("BaiduNewsSearchJob Error", ExceptionUtils.getException(e));
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
