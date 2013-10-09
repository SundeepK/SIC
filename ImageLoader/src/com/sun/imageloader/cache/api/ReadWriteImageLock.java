package com.sun.imageloader.cache.api;

import java.util.concurrent.locks.ReentrantLock;

public interface ReadWriteImageLock<T> {
	
	/**
	 * 
	 * @param  key <T>
	 * 			The key used to retrieve the {@linkplain ReentrantLock} with.
	 * @return
	 * 			return the {@linkplain ReentrantLock} associated with the key, otherwise null
	 */
	public ReentrantLock getReadWriteLock(T key);		

}
