package com.sun.imageloader.cache.impl;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


import com.sun.imageloader.cache.api.MemoryCache;

public abstract class SoftCache<K, V> implements MemoryCache<K, V> {

	private int _maxSizeMemory;
	private final Map<K, Reference<V>> _lruSoftMap;
	public final static int ONE_MB = 1024 * 1024;

	/**
	 * Abstract class which implements a SoftCache of references. This allows any objects to be removed at the garbage collectors own will.
	 * 
	 * @param maxSizeMemory_
	 * 			the max memory the cache should consume in MB. For example, a value of 1 will limit the cache to consume no more than 1MB.
	 */
	public SoftCache(int maxSizeMemory_) {
		_lruSoftMap = new ConcurrentHashMap<K, Reference<V>>(20, 0.75f);
		_maxSizeMemory = maxSizeMemory_ * ONE_MB;
	}

	/**
	 * The size of the the object in the cache, measured in bytes
	 *  
	 * @param value_
	 * @return
	 */
	protected abstract float sizeOfValue(V value_);
	
	protected int getMaxCacheSizeInMB(){
		return _maxSizeMemory;
	}

	@Override
	public boolean put(K key_, V value_) {

		_lruSoftMap.put(key_, creatObjectReference(value_));

		return true;
	}

	protected abstract Reference<V> creatObjectReference(V value_);

	@Override
	public V getValue(K key_) {
		Reference<V> valueReference = _lruSoftMap.get(key_);

		if (valueReference != null) {
			V value = valueReference.get();
			return value;
		} else {
			return null;
		}

	}

	
	protected void remove(K key_) {
		_lruSoftMap.remove(key_);

	}

	@Override
	public void clear() {
		_lruSoftMap.clear();

	}

	@Override
	public Collection<K> getKeys() {
		Set<K> keySet = _lruSoftMap.keySet();
		return keySet;
	}
}
