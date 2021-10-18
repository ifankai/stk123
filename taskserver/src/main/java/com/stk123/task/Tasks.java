package com.stk123.task;

import com.stk123.common.CommonUtils;
import com.stk123.common.util.WeatherUtils;
import com.stk123.service.task.TaskBuilder;
import com.stk123.service.task.TaskContainer;
import com.stk123.task.config.TaskCondition;
import com.stk123.task.quartz.job.PPIIndexNewHighJob;
import com.stk123.task.quartz.job.XueqiuUserJob;
import com.stk123.task.schedule.*;
import com.stk123.task.tool.TaskUtils;
import com.stk123.util.ServiceUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@CommonsLog
@Conditional({TaskCondition.class})
public class Tasks {

    @Autowired
    private Environment environment;
    @Autowired
    private TaskContainer taskContainer;

    @Autowired
    private InitialData initialData;
    @Autowired
    private InitialKLine initialKLine;
    @Autowired
    private NoticeRobot noticeRobot;
    @Autowired
    private NewsRobot newsRobot;
    @Autowired
    private XueqiuFollow xueqiuFollow;



    public void runSingleTask() {
        //initialKLine.run("analyse");
        initialKLine.run();
    }

    /*
    fixedRate就是每分钟一次，不论你业务执行花费了多少时间。我都是1分钟执行1次，
    fixedDelay是当任务执行完毕后1分钟在执行
    https://www.jianshu.com/p/ef18af5a9c1d
    https://blog.csdn.net/qq_34125349/article/details/77430956

    例子：
        @Scheduled(initialDelay = 1, fixedDelay = Integer.MAX_VALUE) 只执行1次

    */

    //@Scheduled(initialDelay = 1, fixedDelay = Integer.MAX_VALUE)
    public void main() {
        //researchReportJob();
        //initialKLineCN();
    }

    @Scheduled(cron = "0 0 8 ? * *") //每天早上8点检查天气看是否降温
    public void weatherCheck(){
        WeatherUtils.check();
    }


    @Scheduled(cron = "0 0/1 * ? * *") //每分钟1次
    public void xueqiuStockArticleJob() {
        taskContainer.start(XueqiuStockArticleTask.class);
        //taskContainer.start(TaskBuilder.of(BarTask.class, "HK"), TaskBuilder.of(SyncTask.class, "table=stk_text"));
    }
    //@Scheduled(cron = "0 0/10 * ? * *") //每10分钟1次
    public void syncTask() {
        if(!ArrayUtils.contains(environment.getActiveProfiles(), "company")) {
            taskContainer.start(SyncTask.class, "table=stk_text");
        }
    }

    XueqiuUserJob xueqiuUserJob = new XueqiuUserJob();
    @Scheduled(cron = "0 0 3 ? * SAT")
    public void xueqiuUserJob() {
        xueqiuUserJob.execute(null);
    }

    @Scheduled(cron = "0 30 1/2 ? * *")
    public void researchReportJob() {
        taskContainer.start(ResearchReportTask.class);
    }


    @Scheduled(cron = "0 10 16 ? * MON-FRI")
    public void initialKLine() {
        String reportDate = CommonUtils.formatDate(new Date(), CommonUtils.sf_ymd2);
        taskContainer.start(
            TaskBuilder.of(BarTask.class, "clearAll"),
            TaskBuilder.of(BarTask.class, "initCN"),
            TaskBuilder.of(BarTask.class, "analyseCN"),
            TaskBuilder.of(BarTask.class, "initHK"),
            TaskBuilder.of(BarTask.class, "analyseHK"),
            TaskBuilder.of(BarTask.class, "MyStocks", "report="+reportDate),
            TaskBuilder.of(BarTask.class, "AllStocks", "report="+reportDate),
            TaskBuilder.of(BarTask.class, "AllCNRps", "report="+reportDate),
            TaskBuilder.of(BarTask.class, "AllHKRps", "report="+reportDate),
            TaskBuilder.of(BarTask.class, "Bks", "report="+reportDate),
            TaskBuilder.of(BarTask.class, "analyseCNRpsStocksByStrategies", "report="+reportDate),
            TaskBuilder.of(BarTask.class, "Mass", "report="+reportDate),
            TaskBuilder.of(BarTask.class, "stat", "report="+reportDate)
            //TaskBuilder.of(BarTask.class, "clearAll"),
            //TaskBuilder.of(SyncTask.class, "table=stk_report_header"),
            //TaskBuilder.of(SyncTask.class, "table=stk_report_detail")
        );

        TaskPpi.run(); //生意社
        PPIIndexNewHighJob.run();
    }

    @Scheduled(cron = "0 10 17 ? * MON-FRI") //因为有延时，所以执行时间往后放
    public void updateHKCapitalFlow() {
        taskContainer.start(BarTask.class, "updateHKCapitalFlow");
    }

    @Scheduled(cron = "0 0 11 ? * MON-FRI")
    public void klineRealtime_am() {
        String reportDate = CommonUtils.formatDate(new Date(), CommonUtils.sf_ymd2);
        taskContainer.start(
            TaskBuilder.of(BarTask.class, "clearAll"),
            TaskBuilder.of(BarTask.class, "MyStocks", "realtime=1", "market=cn,hk", "report="+reportDate, "ampm=am"),
            TaskBuilder.of(BarTask.class, "AllStocks", "realtime=1", "report="+reportDate, "ampm=am"),
            TaskBuilder.of(BarTask.class, "clearAll")
        );
    }

    @Scheduled(cron = "0 30 14 ? * MON-FRI")
    public void klineRealtime_pm() {
        String reportDate = CommonUtils.formatDate(new Date(), CommonUtils.sf_ymd2);
        taskContainer.start(
            TaskBuilder.of(BarTask.class, "clearAll"),
            TaskBuilder.of(BarTask.class, "MyStocks", "realtime=1", "market=cn,hk", "report="+reportDate, "ampm=pm"),
            TaskBuilder.of(BarTask.class, "AllStocks", "realtime=1", "report="+reportDate, "ampm=pm"),
            TaskBuilder.of(BarTask.class, "clearAll")
        );
    }

    @Scheduled(cron = "0 30 5 ? * TUE-SAT")
    public void initialKLineUS() {
        String reportDate = CommonUtils.formatDate(CommonUtils.addDay(new Date(), -1), CommonUtils.sf_ymd2);
        taskContainer.start(BarTask.class, "US", "report="+reportDate);
    }

    @Scheduled(cron = "0 0 2 ? * MON,WED,FRI")
    public void initialDataCN() {
        initialData.run(1);
        taskContainer.start(
            TaskBuilder.of(StockTask.class, "clear"),
            TaskBuilder.of(StockTask.class, "CN"),
            TaskBuilder.of(StockTask.class, "HK"),
            TaskBuilder.of(StockTask.class, "clear")
        );
    }
    @Scheduled(cron = "0 0 8 ? * TUE,SAT")
    public void initialDataUS() {
        initialData.run(2);
    }


    @Scheduled(cron = "0 0 20 ? * *") //每天晚上8点，公告
    public void noticeRobot() {
        noticeRobot.run();
    }

    @Scheduled(cron = "0 0 20,22 ? * *") //每天晚上8点和10点，新闻
    public void newsRobot() {
        newsRobot.run();
    }

    @Scheduled(cron = "0 0 1 ? * SAT") //每周六凌晨1点，雪球个股关注人数
    public void xueqiuFollow() {
        xueqiuFollow.run();
    }

    @Scheduled(cron = "0 0 2 ? * SUN") //每周日凌晨2点
    public void backupDatabase(){
        TaskUtils.cmd("D:\\share\\workspace\\stk123\\oracle\\export_stk.bat");
    }

    //@Scheduled(cron = "0 0 6 ? * *") //每天6点同步database
    public void syncDatabase() {
        taskContainer.start(SyncTask.class);
    }

    @Scheduled(cron = "0 55 10 ? * MON-FRI")
    @Scheduled(cron = "0 25 14 ? * MON-FRI")
    @Scheduled(cron = "0 0 16 ? * MON-FRI")
    @Scheduled(cron = "0 0 18 ? * MON-FRI")
    @Scheduled(cron = "0 0 0 ? * *")
    public void gitPull(){
        //TaskUtils.cmd("D:\\share\\workspace\\stk123\\git_pull.bat");
    }

    @Scheduled(cron = "0 0/5 * ? * *") //每5分钟1次
    public void noticeFetch() {
        taskContainer.start(NoticeTask.class, "fetch");
    }
    @Scheduled(fixedDelay=5*60*1000) //task跑完后停5分钟再跑
    public void noticeAnalyze() {
        taskContainer.start(NoticeTask.class, false, "analyze");
    }

    @Scheduled(cron = "0 0 19 ? * *") //每天晚上7点，投资者关系
    public void investRobot() {
        try {
            InvestRobot.run(ServiceUtils.addDay(new Date(), -7));
        } catch (Exception e) {
            log.error("investRobot", e);
        }
    }

}
