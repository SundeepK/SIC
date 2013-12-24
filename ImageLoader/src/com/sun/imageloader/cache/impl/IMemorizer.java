package com.sun.imageloader.cache.impl;

import java.util.concurrent.ExecutionException;

public interface IMemorizer<T, V> {
	
	/**
	 * Returns the value after a computation
	 * 
	 * @param computable_ the value to be process
	 * @return a computed result 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public V executeComputable(T computable_) throws InterruptedException, ExecutionException;

}
