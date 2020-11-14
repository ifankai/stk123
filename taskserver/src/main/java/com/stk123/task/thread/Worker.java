package com.stk123.task.thread;

public class Worker extends Thread {

	private final WorkerMgmt channel;
	private volatile boolean terminated = false;
	private volatile int error = 0;

	public Worker(String name, WorkerMgmt channel) {
		super(name);
		this.channel = channel;
	}

	public void run() {
		try {
			while (!terminated) {
				try {
					Request request = channel.takeRequest();
					error = request.execute();
					if(error != 0){
						terminated = true;
					}
				} catch (Exception e) {
					Log.error(e,"工人出现异常：");
					e.printStackTrace();
					terminated = true;
				}
			}
		} finally {
			System.out.println(Thread.currentThread().getName() + " is terminated.");
		}
	}

	public void stopThread() {
		terminated = true;
		interrupt();
	}

	public boolean isTerminated() {
		return terminated;
	}

	public int getError() {
		return error;
	}
}
