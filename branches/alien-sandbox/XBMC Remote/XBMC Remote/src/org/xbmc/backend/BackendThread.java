package org.xbmc.backend;

class BackendThread extends Thread {
	
	private boolean mBusy = false;

	public BackendThread() {
		// TODO Auto-generated constructor stub
	}

	public BackendThread(Runnable runnable) {
		super(runnable);
		// TODO Auto-generated constructor stub
	}

	public BackendThread(String threadName) {
		super(threadName);
		// TODO Auto-generated constructor stub
	}

	public BackendThread(Runnable runnable, String threadName) {
		super(runnable, threadName);
		// TODO Auto-generated constructor stub
	}

	public BackendThread(ThreadGroup group, Runnable runnable) {
		super(group, runnable);
		// TODO Auto-generated constructor stub
	}

	public BackendThread(ThreadGroup group, String threadName) {
		super(group, threadName);
		// TODO Auto-generated constructor stub
	}

	public BackendThread(ThreadGroup group, Runnable runnable, String threadName) {
		super(group, runnable, threadName);
		// TODO Auto-generated constructor stub
	}

	public BackendThread(ThreadGroup group, Runnable runnable,
			String threadName, long stackSize) {
		super(group, runnable, threadName, stackSize);
		// TODO Auto-generated constructor stub
	}

	public boolean isBusy() {
		return mBusy;
	}
}
