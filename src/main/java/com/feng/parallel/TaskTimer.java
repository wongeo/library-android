package com.feng.parallel;

import java.util.concurrent.CountDownLatch;

public class TaskTimer {
	private long interval;
	private Runnable task;
	private StateThread worker;
	public boolean IsRunning;
	private CountDownLatch latch;

	public TaskTimer() {
		latch = new CountDownLatch(1);
	}

	public void setInterval(long ms) {
		interval = ms;
	}

	public void setOnTickedHandler(Runnable task) {
		this.task = task;
	}

	public synchronized void start() {
		tryStop();
		worker = new StateThread();
		final StateThread thatWorker = worker;
		worker.setRunnable(new Runnable() {
			@Override
			public void run() {
				internalWorkerRun(thatWorker);
			}
		});
		worker.start();
	}

	public synchronized void tryStop() {
		if (worker != null) {
			worker.setCanceled(true);
			worker.interrupt();
			worker = null;
		}
	}

	public void waitForExit() {
		try {
			latch.await();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	private void onTicked() {
		if (task != null) {
			task.run();
		}
	}

	private void internalWorkerRun(StateThread thatWorker) {
		IsRunning = true;
		long st = 0;
		while (!thatWorker.hasCanceled()) {
			st = System.currentTimeMillis();
			try {
				onTicked();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				long leftSleepTime = interval - (System.currentTimeMillis() - st);
				try {
					if (leftSleepTime <= 0) {
						leftSleepTime = 500;
					}
					Thread.sleep(leftSleepTime);
				} catch (InterruptedException e) {

				}
				Thread.yield();
			}
		}
		IsRunning = false;
		latch.countDown();
	}
}