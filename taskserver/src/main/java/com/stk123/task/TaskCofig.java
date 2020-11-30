package com.stk123.task;

import com.stk123.task.quartz.job.ResearchReportJob;
import com.stk123.task.quartz.job.XueqiuStockArticleJob;
import com.stk123.task.quartz.job.XueqiuUserJob;
import com.stk123.task.schedule.*;
import com.stk123.task.ws.StkWebSocketClient;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.PostConstruct;

@Configuration
@EnableScheduling
@CommonsLog
public class TaskCofig implements ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    private Environment environment;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    private StkWebSocketClient stkWebSocketClient;

    @Autowired
    private InitialData initialData;
    @Autowired
    private InitialKLine initialKLine;
    @Autowired
    private NoticeRobot noticeRobot;
    @Autowired
    private NewsRobot newsRobot;
    @Autowired
    private XueqiuStockArticleJob xueqiuStockArticleJob;
    @Autowired
    private XueqiuFollow xueqiuFollow;


    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }

    /*
    fixedRate就是每分钟一次，不论你业务执行花费了多少时间。我都是1分钟执行1次，
    fixedDelay是当任务执行完毕后1分钟在执行
    https://www.jianshu.com/p/ef18af5a9c1d
    https://blog.csdn.net/qq_34125349/article/details/77430956

    例子：
        @Scheduled(initialDelay = 1, fixedDelay = Integer.MAX_VALUE) 只执行1次

    */

    @Scheduled(initialDelay = 1, fixedDelay = Integer.MAX_VALUE)
    public void main() throws Exception {
        //researchReportJob();
        //initialKLineCN();
    }


    @PostConstruct
    @Scheduled(cron = "0 0/1 * ? * *")
    public void webSocketIsConnected() {
        boolean isConnected = stkWebSocketClient.isConnected();
        log.info("Websocket is Connected:"+isConnected);
        if(!isConnected){
            try {
                stkWebSocketClient.init();
            } catch (Exception e) {
                log.error("stkWebSocketClient.init()", e);
            }
        }
    }


    @Scheduled(cron = "0 0 0 ? * *")  //每天0点shutdown
    public void exit(){
        scheduler.shutdown();
        SpringApplication.exit(this.context, () -> 0);
    }


    @Scheduled(cron = "0 0/1 * ? * *") //每分钟1次
    public void xueqiuStockArticleJob() {
        if(!ArrayUtils.contains(environment.getActiveProfiles(), "company")) {
            xueqiuStockArticleJob.execute(null);
        }
    }

    XueqiuUserJob xueqiuUserJob = new XueqiuUserJob();
    @Scheduled(cron = "0 0 3 ? * SAT")
    public void xueqiuUserJob() {
        xueqiuUserJob.execute(null);
    }

    ResearchReportJob researchReportJob = new ResearchReportJob();
    @Scheduled(cron = "0 30 1/2 ? * *")
    public void researchReportJob() {
        researchReportJob.execute(null);
    }

    @Scheduled(cron = "0 30 15 ? * MON-SAT")
    public void initialKLineCN() {
        initialKLine.run();
    }
    @Scheduled(cron = "0 30 5 ? * TUE-SAT")
    public void initialKLineUS() {
        initialKLine.run("US");
    }


    @Scheduled(cron = "0 0 2 ? * MON,FRI")
    public void initialDataCN() {
        initialData.run(1);
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

}
