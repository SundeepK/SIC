package com.sun.imageloader.computable.impl;

import com.sun.imageloader.memorizer.api.InterruptedImageLoadException;

public interface Computable<T,V> {

	public V compute(T valueToCompute) throws InterruptedImageLoadException;
	
}
