package com.stk123.model.quartz;

import java.text.ParseException;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.DateBuilder.*;

public class QuartzManager {
	
	private static SchedulerFactory sf = new StdSchedulerFactory();
	private static String JOB_GROUP_NAME = "group1";
	private static String TRIGGER_GROUP_NAME = "trigger1";

	/** */
	/**
	 * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
	 * 
	 * @param jobName
	 *            任务名
	 * @param job
	 *            任务
	 * @param time
	 *            时间设置，参考quartz说明文档
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public static void addJob(String jobName, Job job, String time) throws SchedulerException, ParseException {
		Scheduler sched = sf.getScheduler();
		JobDetail jobDetail = newJob(job.getClass()).withIdentity(jobName, JOB_GROUP_NAME).build();// 任务名，任务组，任务执行类
		// 触发器
		CronTrigger trigger = newTrigger().withIdentity(jobName, TRIGGER_GROUP_NAME)// 触发器名,触发器组
	            .withSchedule(cronSchedule(time)).build(); 
		sched.scheduleJob(jobDetail, trigger);
		// 启动
		if (!sched.isShutdown())
			sched.start();
	}
	
	/** */
	/**
	 * 添加一个定时任务
	 * 
	 * @param jobName
	 *            任务名
	 * @param jobGroupName
	 *            任务组名
	 * @param triggerName
	 *            触发器名
	 * @param triggerGroupName
	 *            触发器组名
	 * @param job
	 *            任务
	 * @param time
	 *            时间设置，参考quartz说明文档
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Job job,
			String time) throws SchedulerException, ParseException {
		Scheduler sched = sf.getScheduler();
		JobDetail jobDetail = newJob(job.getClass()).withIdentity(jobName, jobGroupName).build();// 任务名，任务组，任务执行类
		// 触发器
		CronTrigger trigger = newTrigger().withIdentity(triggerName, triggerGroupName)// 触发器名,触发器组
	            .withSchedule(cronSchedule(time)).build(); 
		sched.scheduleJob(jobDetail, trigger);
		if (!sched.isShutdown())
			sched.start();
	}

	/** */
	/**
	 * 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
	 * 
	 * @param triggerName
	 * @param time
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public static void modifyJobTime(String triggerName, String time) throws SchedulerException, ParseException {
		Scheduler sched = sf.getScheduler();
		Trigger trigger = sched.getTrigger(TriggerKey.triggerKey(triggerName, TRIGGER_GROUP_NAME));
		if (trigger != null) {
			CronTrigger ct = (CronTrigger) trigger;
			ct.getTriggerBuilder().withSchedule(cronSchedule(time));
			sched.rescheduleJob(TriggerKey.triggerKey(triggerName, TRIGGER_GROUP_NAME), ct);
		}
	}

	/** */
	/**
	 * 修改一个任务的触发时间
	 * 
	 * @param triggerName
	 * @param triggerGroupName
	 * @param time
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public static void modifyJobTime(String triggerName, String triggerGroupName, String time)
			throws SchedulerException, ParseException {
		Scheduler sched = sf.getScheduler();
		Trigger trigger = sched.getTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));
		if (trigger != null) {
			CronTrigger ct = (CronTrigger) trigger;
			ct.getTriggerBuilder().withSchedule(cronSchedule(time));
			sched.rescheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName), ct);
		}
	}

	/** */
	/**
	 * 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
	 * 
	 * @param jobName
	 * @throws SchedulerException
	 */
	public static void removeJob(String jobName) throws SchedulerException {
		Scheduler sched = sf.getScheduler();
		sched.pauseTrigger(TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME));// 停止触发器
		sched.unscheduleJob(TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME));// 移除触发器
		sched.deleteJob(JobKey.jobKey(jobName, JOB_GROUP_NAME));// 删除任务
	}

	/** */
	/**
	 * 移除一个任务
	 * 
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 * @throws SchedulerException
	 */
	public static void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName)
			throws SchedulerException {
		Scheduler sched = sf.getScheduler();
		sched.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));// 停止触发器
		sched.unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName));// 移除触发器
		sched.deleteJob(JobKey.jobKey(jobName, jobGroupName));// 删除任务
	}
	
	public static void shutdown() throws Exception{
		sf.getScheduler().shutdown();
	}
}
