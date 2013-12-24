package com.sun.imageloader.concurrent;

import java.util.concurrent.Callable;

import com.sun.imageloader.core.api.Computable;

import android.graphics.Bitmap;

public class ComputableCallable<T,V> implements Callable<V> {

	private Computable<T, V> _computable;
	private T _key;
	
	public ComputableCallable(Computable<T, V> computable_, T key_){
		_computable = computable_;
		_key = key_;
	}

	@Override
	public V call() throws Exception {
		return _computable.compute(_key);
	}
	

}
