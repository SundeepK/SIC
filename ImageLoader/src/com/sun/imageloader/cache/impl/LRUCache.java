package com.sun.imageloader.cache.impl;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import android.graphics.Bitmap;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.sun.imageloader.core.ImageKey;
import com.sun.imageloader.imagedecoder.utils.L;

public class LRUCache extends SoftCache<ImageKey, Bitmap>{

	private final ConcurrentMap<ImageKey, Bitmap> _lruHardCache;
	private int _currentSizeMemory;
	private static final String TAG = LRUCache.class.getName();
	private Object _putLock = new Object();
	private AtomicBoolean _isAlreadyTrimmingCache = new AtomicBoolean();
//	public enum MemorySize {
//		
//	}
	
	/**
	 * 
	 * 
	 * @param maxSizeMemory_ 
	 * 			measured in MB
	 */
	public LRUCache(int maxSizeMemory_) {
		super(maxSizeMemory_);
		_lruHardCache = new ConcurrentLinkedHashMap.Builder<ImageKey, Bitmap>()
			    .maximumWeightedCapacity(50)
				.initialCapacity(20)
				.build();
	}

	@Override
	protected float sizeOfValue(Bitmap value_) {
		float memory =  (value_.getRowBytes() * value_.getHeight()) ;
		L.v(TAG, "Memory size if image: " + memory) ;
		return memory;
	}

	@Override
	public boolean put(ImageKey key_, Bitmap value_) {

		if (key_ == null || value_ == null) {
			throw new NullPointerException("key or value supplied was null");
		}

		Bitmap previousBitmap;
		previousBitmap = _lruHardCache.put(key_, value_);
		
		synchronized (_putLock) {
			_currentSizeMemory += sizeOfValue(value_);
			if (previousBitmap != null) {
				_currentSizeMemory -= sizeOfValue(value_);
			}
		}

		L.v(TAG, "Current memory: " + _currentSizeMemory);

		if (!_isAlreadyTrimmingCache.get()) {
			trimeCache(_maxSizeMemory);
		}
		return true;
	}
	
	/**
	 * Trime the cache size
	 *  
	 * @param maxMemorySize_
	 */
	protected synchronized void trimeCache(int maxMemorySize_) {
		_isAlreadyTrimmingCache.getAndSet(true);
		L.v(TAG, "Inside trime method, current max memopry size is in bytes: " + maxMemorySize_ + " cache size: " +_lruHardCache.size() );
		while(_currentSizeMemory >= maxMemorySize_){
			
			if (_currentSizeMemory < 0 || (_lruHardCache.isEmpty() && _currentSizeMemory != 0)) {
				throw new IllegalStateException(this.getClass().getName() + "Inconsistent memory size for cache when compared to cache size or memory size of map is below 0");
			}
				Map.Entry<ImageKey, Bitmap> entry = _lruHardCache.entrySet().iterator().next();
				Bitmap bitMapToRemove = entry.getValue();
				ImageKey bitMapToRemoveKey = entry.getKey();
				
				if(bitMapToRemove == null){
					break;
				}
				_currentSizeMemory -= sizeOfValue(bitMapToRemove);
				// lets add  image to the softcache so that we can retrieve it from the soft cache if there is a
				// cache miss in the hard cache. That is if any GC hasn't discarded it already
				// in that case we have no choice but to go ahead and retreive it again from url/disc.
				L.v(TAG, "Removing image with key: " + bitMapToRemoveKey.key());
				super.put(bitMapToRemoveKey, bitMapToRemove);
				_lruHardCache.remove(bitMapToRemoveKey);		
		}
		_isAlreadyTrimmingCache.getAndSet(false);
	}

	@Override
	protected Reference<Bitmap> creatObjectReference(Bitmap value_) {
		Reference<Bitmap> softReferenceBitMap = new SoftReference<Bitmap>(value_);
		return softReferenceBitMap;
	}

	@Override
	public Bitmap getValue(ImageKey key_) {
		Bitmap bitMap;
		bitMap = _lruHardCache.get(key_);

		if (bitMap != null) {
			return bitMap;
		}
		
		bitMap = super.getValue(key_);
		
		//If we find that the image exists in the softcache, then we can simply promote that to the hardCache
		if (bitMap != null) {
				put(key_, bitMap);
			return bitMap;
		}else{
			//Well where because we found nothing for the key specified, which means GC banished the bitmap to Object hell
			//TODO think about raising an exception rather than return null
			return null;
		}

	}
	
	@Override
	public void remove(ImageKey key_, boolean removeAllOccurences_){
		remove(key_);
		
		if(removeAllOccurences_)
			super.remove(key_);
	}

	
	protected void remove(String key_) {
		_lruHardCache.remove(key_);
	}

	@Override
	public Collection<ImageKey> getKeys() {
		return super.getKeys();
	}
	
	
	

}
