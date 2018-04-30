package com.stk123.tool.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class WorkerMgmt {
	private static final int MAX_REQUEST = 100;
	private final Request[] requestQueue;
	private int tail; // 下次要putRequest的位置
	private int head; // 下次要takeRequest的位置
	private int count; // Request的次数

	private final List<Worker> workers;
	private WorkerMonitor monitor;
	private int monitorSleep = 30;//秒钟

	public WorkerMgmt(int workerNo) {
		this.requestQueue = new Request[MAX_REQUEST];
		this.head = 0;
		this.tail = 0;
		this.count = 0;

		workers = new ArrayList<Worker>(workerNo);
		for (int i = 0; i < workerNo; i++) {
			workers.add(new Worker("Worker-" + i, this));
		}
		monitor = new WorkerMonitor("monitor");
	}
	
	public WorkerMgmt() {
		this.requestQueue = new Request[MAX_REQUEST];
		this.head = 0;
		this.tail = 0;
		this.count = 0;
		workers = new ArrayList<Worker>();
		monitor = new WorkerMonitor("monitor");
	}
	
	public void addWorker(int workerNo){
		for (int i = 0; i < workerNo; i++) {
			workers.add(new Worker("Worker-" + i, this));
		}
	}

	public void startWorkers() {
		for (int i = 0; i < workers.size(); i++) {
			workers.get(i).start();
		}
		try{
			//monitor.start();
		}catch(IllegalThreadStateException e){
			monitor = new WorkerMonitor("monitor");
			monitor.start();
		}
	}

	public void stopAllWorkers() {
		for (int i = 0; i < workers.size(); i++) {
			workers.get(i).stopThread();
		}
	}

	public synchronized void putRequest(Request request)
			throws InterruptedException {
		while (count >= requestQueue.length) {
			wait();
		}
		requestQueue[tail] = request;
		tail = (tail + 1) % requestQueue.length;
		count++;
		notifyAll();
	}

	public synchronized Request takeRequest() throws InterruptedException {
		while (count <= 0) {
			wait();
		}
		Request request = requestQueue[head];
		head = (head + 1) % requestQueue.length;
		count--;
		notifyAll();
		return request;
	}
	
	public int workerSize() {
		return workers.size();
	}
	
	public void setMonitorWorkerSleep(int m) {
		this.monitorSleep = m;
	}
	
	private Request terminatedRequest = null;//所有工人都terminate后的request工作。
	public void setRequestAllWorkersTerminated(Request request){
		this.terminatedRequest = request;
	}
	
	//worker 监控线程
	class WorkerMonitor extends Thread {
		
		private WorkerMonitor(String name){
			super(name);
		}
		
		public void run() {
			int error = 0;
			
			try {
				Thread.sleep(monitorSleep*1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			while (monitorSleep > 0) {
				try {
					Thread.sleep(monitorSleep*1000);
					Log.log((workers.size())+" worker(s) are working, still ["+count+"] requests");
					synchronized(workers) 
					{
						//isAllWorkerTerminated = true;
						for(int i=0;i<workers.size();i++){
							Worker worker = workers.get(i);
							if(worker.isTerminated()){
								workers.remove(i);
								Log.error(worker.getName()+" is termiated!");
								error = worker.getError();
							}
						}
					}
					
					if(workers.size() == 0){
						monitorSleep -= 2;
					}
				} catch (InterruptedException e) {
					monitorSleep = 0;
					e.printStackTrace();
				}
			}
			Log.log("terminated="+monitorSleep);
			Log.log("error="+error);
			try {
				if(terminatedRequest != null){
					terminatedRequest.execute();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
