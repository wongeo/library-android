package com.feng.parallel;

public class StateThread extends Thread {
	private boolean canceled;
	private Runnable runnable;
	private boolean repeatMode;

	public StateThread() {
		this.setPriority(Thread.MIN_PRIORITY);
	}

	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void start() {
		canceled = false;
		super.start();
	}

	@Override
	public void run() {
		if(repeatMode) {
			while(!canceled) {
				runnable.run();	
			}
		} else {
			runnable.run();			
		}
	}

	public void cancel() {
		canceled = true;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public boolean hasCanceled() {
		return canceled;
	}

	public void setRepeatMode(boolean b) {
		repeatMode = b;
	}
}