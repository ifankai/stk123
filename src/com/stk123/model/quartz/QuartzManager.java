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
	 * ���һ����ʱ����ʹ��Ĭ�ϵ�������������������������������
	 * 
	 * @param jobName
	 *            ������
	 * @param job
	 *            ����
	 * @param time
	 *            ʱ�����ã��ο�quartz˵���ĵ�
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public static void addJob(String jobName, Job job, String time) throws SchedulerException, ParseException {
		Scheduler sched = sf.getScheduler();
		JobDetail jobDetail = newJob(job.getClass()).withIdentity(jobName, JOB_GROUP_NAME).build();// �������������飬����ִ����
		// ������
		CronTrigger trigger = newTrigger().withIdentity(jobName, TRIGGER_GROUP_NAME)// ��������,��������
	            .withSchedule(cronSchedule(time)).build(); 
		sched.scheduleJob(jobDetail, trigger);
		// ����
		if (!sched.isShutdown())
			sched.start();
	}
	
	/** */
	/**
	 * ���һ����ʱ����
	 * 
	 * @param jobName
	 *            ������
	 * @param jobGroupName
	 *            ��������
	 * @param triggerName
	 *            ��������
	 * @param triggerGroupName
	 *            ����������
	 * @param job
	 *            ����
	 * @param time
	 *            ʱ�����ã��ο�quartz˵���ĵ�
	 * @throws SchedulerException
	 * @throws ParseException
	 */
	public static void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Job job,
			String time) throws SchedulerException, ParseException {
		Scheduler sched = sf.getScheduler();
		JobDetail jobDetail = newJob(job.getClass()).withIdentity(jobName, jobGroupName).build();// �������������飬����ִ����
		// ������
		CronTrigger trigger = newTrigger().withIdentity(triggerName, triggerGroupName)// ��������,��������
	            .withSchedule(cronSchedule(time)).build(); 
		sched.scheduleJob(jobDetail, trigger);
		if (!sched.isShutdown())
			sched.start();
	}

	/** */
	/**
	 * �޸�һ������Ĵ���ʱ��(ʹ��Ĭ�ϵ�������������������������������)
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
	 * �޸�һ������Ĵ���ʱ��
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
	 * �Ƴ�һ������(ʹ��Ĭ�ϵ�������������������������������)
	 * 
	 * @param jobName
	 * @throws SchedulerException
	 */
	public static void removeJob(String jobName) throws SchedulerException {
		Scheduler sched = sf.getScheduler();
		sched.pauseTrigger(TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME));// ֹͣ������
		sched.unscheduleJob(TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME));// �Ƴ�������
		sched.deleteJob(JobKey.jobKey(jobName, JOB_GROUP_NAME));// ɾ������
	}

	/** */
	/**
	 * �Ƴ�һ������
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
		sched.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));// ֹͣ������
		sched.unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName));// �Ƴ�������
		sched.deleteJob(JobKey.jobKey(jobName, jobGroupName));// ɾ������
	}
	
	public static void shutdown() throws Exception{
		sf.getScheduler().shutdown();
	}
}
