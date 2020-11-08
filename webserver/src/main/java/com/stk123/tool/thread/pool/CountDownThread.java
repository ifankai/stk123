package com.stk123.tool.thread.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class CountDownThread {

	// static ExecutorService executorService = Executors.newFixedThreadPool(1);
	static final BlockingQueue queue = new ArrayBlockingQueue(1);

	public static void main(String[] args) throws InterruptedException,
			ExecutionException {
		int threads = 5;
		CountDownLatch countDownLatch = new CountDownLatch(threads);
		for (int i = 0; i < threads; i++) {
			SubThread thread = new SubThread(2000 * (i + 1), countDownLatch);
			thread.start();
		}
		// Future future = executorService.submit(thread);
		mainThreadOtherWork();
		System.out.println("now waiting sub thread done.");
		// future.get();
		// queue.take();
		countDownLatch.await();
		// try {
		// thread.join();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		System.out.println("now all done.");
		// executorService.shutdown();
	}

	private static void mainThreadOtherWork() {
		System.out.println("main thread work start");
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("main thread work done.");
	}

	public static class SubThread extends Thread {

		// private BlockingQueue queue;
		private CountDownLatch countDownLatch;
		private long work;

		/**
		 * @param queue
		 */
		// public SubThread(BlockingQueue queue) {
		// this.queue = queue;
		// this.work = 5000L;
		// }

		public SubThread(long work, CountDownLatch countDownLatch) {
			// this.queue = queue;
			this.countDownLatch = countDownLatch;
			this.work = work;
		}

		@Override
		public void run() {
			try {
				working();
			} finally {
				// try {
				// queue.put(1);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				countDownLatch.countDown();
			}

		}

		private void working() {
			System.out.println(getName() + " sub thread start working.");
			busy();
			System.out.println(getName() + " sub thread stop working.");
		}

		private void busy() {
			try {
				sleep(work);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
