package com.stk123.task;

import com.stk123.task.quartz.job.ResearchReportJob;
import com.stk123.task.quartz.job.XueqiuStockArticleJob;
import com.stk123.task.quartz.job.XueqiuUserJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})  //https://github.com/spring-projects/spring-boot/tree/v2.1.3.RELEASE/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure
@EnableScheduling
public class TaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskApplication.class, args);
    }

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    TaskScheduler taskScheduler;

    // fixedRate就是每分钟一次，不论你业务执行花费了多少时间。我都是1分钟执行1次，
    // fixedDelay是当任务执行完毕后1分钟在执行
    // https://www.jianshu.com/p/ef18af5a9c1d
    // https://blog.csdn.net/qq_34125349/article/details/77430956

    XueqiuStockArticleJob xueqiuStockArticleJob = new XueqiuStockArticleJob();
    @Scheduled(cron = "0 0/1 * ? * *")
    public void xueqiuStockArticleJob() throws Exception {
        xueqiuStockArticleJob.execute(null);
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

    @Scheduled(cron = "0 0 0 ? * *")
    public void stopAll(){
        scheduler.shutdown();
    }

}
