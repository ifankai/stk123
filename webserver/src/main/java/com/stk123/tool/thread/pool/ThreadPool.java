package com.stk123.tool.thread.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPool {
	
	private ExecutorService exec;
	private CompletionService pool;

	private ThreadPool(int poolSize){
		exec = Executors.newFixedThreadPool(poolSize);
		pool = new ExecutorCompletionService(exec);
	}
	
	public ThreadPool getInstance(int poolSize){
		return new ThreadPool(poolSize);
	}
	
	public Future addTask(Callable task){
		return pool.submit(task);
	}
	
	public Object getResult() throws Exception {
		return pool.take().get();
	}
	
	public void shutdown(){
		exec.shutdown();
	}

}
