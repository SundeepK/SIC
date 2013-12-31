package com.sun.imageloader.core;

import java.util.concurrent.atomic.AtomicBoolean;

public class FlingLock {
	
	private final AtomicBoolean paused = new AtomicBoolean(false);
	private final Object pauseLock = new Object();

	void pause() {
		paused.set(true);
	}
	
	void resume() {
		paused.set(false);
		synchronized (pauseLock) {
			pauseLock.notifyAll();
		}
	}
	
	Object getPauseLock() {
		return pauseLock;
	}
	
	AtomicBoolean getPause() {
		return paused;
	}

}
