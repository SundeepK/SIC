package com.sun.imageloader.core;

import java.util.concurrent.atomic.AtomicBoolean;

public class FlingLock {
	
	private final AtomicBoolean paused = new AtomicBoolean(false);
	private final Object pauseLock = new Object();

	public void pause() {
		paused.set(true);
	}
	
	public void resume() {
		paused.set(false);
		synchronized (pauseLock) {
			pauseLock.notifyAll();
		}
	}
	
	public Object getPauseLock() {
		return pauseLock;
	}
	
	public	AtomicBoolean getPause() {
		return paused;
	}

}
