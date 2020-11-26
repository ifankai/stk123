package com.stk123.task;

import com.stk123.common.util.Arrays;
import com.stk123.task.quartz.job.ResearchReportJob;
import com.stk123.task.quartz.job.XueqiuStockArticleJob;
import com.stk123.task.quartz.job.XueqiuUserJob;
import com.stk123.task.schedule.InitialData;
import com.stk123.task.schedule.InitialKLine;
import com.stk123.task.ws.StkWebSocketClient;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TaskCofig {

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
    private XueqiuStockArticleJob xueqiuStockArticleJob;

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

    // fixedRate就是每分钟一次，不论你业务执行花费了多少时间。我都是1分钟执行1次，
    // fixedDelay是当任务执行完毕后1分钟在执行
    // https://www.jianshu.com/p/ef18af5a9c1d
    // https://blog.csdn.net/qq_34125349/article/details/77430956

    @Scheduled(cron = "0 0 0 ? * *")
    public void stopAll(){
        scheduler.shutdown();
    }


    @Scheduled(cron = "0 0/1 * ? * *")
    public void xueqiuStockArticleJob() throws Exception {
        if(!ArrayUtils.contains(environment.getActiveProfiles(), "company")) {
            xueqiuStockArticleJob.execute(null);
        }
    }

    XueqiuUserJob xueqiuUserJob = new XueqiuUserJob();
    @Scheduled(cron = "0 0 3 ? * SAT")
    public void xueqiuUserJob() throws Exception {
        xueqiuUserJob.execute(null);
    }

    ResearchReportJob researchReportJob = new ResearchReportJob();
    @Scheduled(cron = "0 30 1/2 ? * *")
    public void researchReportJob() throws Exception {
        researchReportJob.execute(null);
    }

    @Scheduled(cron = "0 30 15 ? * MON-FRI")
    public void initialKLineCN() {
        try {
            initialKLine.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Scheduled(cron = "0 30 5 ? * TUE-SAT")
    public void initialKLineUS() {
        try {
            initialKLine.run("US");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    @Scheduled(initialDelay = 1, fixedDelay = Integer.MAX_VALUE)
    @Scheduled(cron = "0 0 2 ? * MON,FRI")
    public void initialDataCN() {
        try {
            initialData.run(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Scheduled(cron = "0 0 8 ? * TUE,SAT")
    public void initialDataUS() {
        try {
            initialData.run(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
