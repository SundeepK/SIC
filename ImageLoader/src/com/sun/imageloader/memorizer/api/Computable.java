package com.sun.imageloader.memorizer.api;

public interface Computable<T,V> {

	public V compute(T valueToCompute) throws InterruptedImageLoadException;
	
}
