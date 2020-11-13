package com.stk123.task.quartz;

import com.stk123.task.quartz.job.BaiduNewsSearchJob;
import com.stk123.task.quartz.job.BreakoutTrendLineJob;
import com.stk123.task.quartz.job.Job1450;
import com.stk123.task.quartz.job.PPIIndexNewHighJob;
import com.stk123.task.quartz.job.ResearchReportJob;
import com.stk123.task.quartz.job.ShutdownJob;
import com.stk123.task.quartz.job.XueqiuUserLongArticleJob;
import com.stk123.task.quartz.job.XueqiuStockArticleJob;
import com.stk123.task.quartz.job.XueqiuUserJob;
import com.stk123.task.quartz.job.t.ShortTrade;

public class QuartzRobot {

	public static void main(String[] args) throws Exception {
		
		/*StockRealTimeJob kRealTimeMonitor = new StockRealTimeJob();		
		QuartzManager.addJob("StockRealTimeJob1",kRealTimeMonitor,"0 26,56 9,10,13,14 ? * MON-FRI *");
		QuartzManager.addJob("StockRealTimeJob2",kRealTimeMonitor,"0 26 11 ? * MON-FRI *");*/

		
		/*IndexRealTimeJob indexMonitor = new IndexRealTimeJob();		
		QuartzManager.addJob("IndexRealTimeJob1",indexMonitor,"0 30,40,50 9 ? * MON-FRI *");
		QuartzManager.addJob("IndexRealTimeJob2",indexMonitor,"0 0,10,20,25 11 ? * MON-FRI *");
		QuartzManager.addJob("IndexRealTimeJob3",indexMonitor,"0 0,10,20,30,40,50 10,13,14 ? * MON-FRI *");*/
		
		
		/*OneMinuteJob oneMinuteJob = new OneMinuteJob();
		QuartzManager.addJob("OneMinuteJob1",oneMinuteJob,"0 30/1 9 ? * MON-FRI *");
		QuartzManager.addJob("OneMinuteJob2",oneMinuteJob,"0 0/1 10,13,14 ? * MON-FRI *");
		QuartzManager.addJob("OneMinuteJob3",oneMinuteJob,"0 0-30/1 11 ? * MON-FRI *");*/
		
		XueqiuStockArticleJob xueqiuStockArticleJob = new XueqiuStockArticleJob();
		QuartzManager.addJob("XueqiuStockArticleJob",xueqiuStockArticleJob,"0 0/1 * ? * * *");

		
		Job1450 job1450 = new Job1450();
		//QuartzManager.addJob("Job1450",job1450,"0 50 14 ? * MON-FRI *");
		
		BreakoutTrendLineJob breakoutJob = new BreakoutTrendLineJob();
		//QuartzManager.addJob("BreakoutTrendLineJob",breakoutJob,"00 40 9 ? * MON-FRI *");
		
		BaiduNewsSearchJob baiduNewSearchJob = new BaiduNewsSearchJob();
		QuartzManager.addJob("BaiduNewsSearchJob",baiduNewSearchJob,"0 0 9 ? * * *");
		
		XueqiuUserLongArticleJob xueqiuUserLongArticleJob = new XueqiuUserLongArticleJob();
		//QuartzManager.addJob("XueqiuUserLongArticleJob",xueqiuUserLongArticleJob,"0 0 4 ? * * *");
		
		XueqiuUserJob xueqiuUserJob = new XueqiuUserJob();
		QuartzManager.addJob("XueqiuUserJob",xueqiuUserJob,"0 0 3 ? * SAT *");
		
		ResearchReportJob researchReportJob = new ResearchReportJob();
		QuartzManager.addJob("ResearchReportJob",researchReportJob,"0 30 1/2 ? * * *");
		
		PPIIndexNewHighJob pPIIndexNewHighJob = new PPIIndexNewHighJob();
		QuartzManager.addJob("PPIIndexNewHighJob",pPIIndexNewHighJob,"0 0 20 ? * * *");
		
		ShortTrade strade = new ShortTrade();
		//QuartzManager.addJob("ShortTrade",strade,"0 25 9 ? * MON-FRI *");
		
		
		ShutdownJob shutdownJob = new ShutdownJob();
		QuartzManager.addJob("ShutdownJob",shutdownJob,"0 0 0 ? * * *");
	}

}
