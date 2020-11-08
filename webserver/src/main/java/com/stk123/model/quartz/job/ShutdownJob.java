package com.stk123.model.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.model.quartz.QuartzManager;

public class ShutdownJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			//do something before shutdown
			
			//shutdown...
			QuartzManager.shutdown();
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

}
