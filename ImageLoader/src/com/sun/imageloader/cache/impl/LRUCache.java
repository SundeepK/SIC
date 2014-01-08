package com.sun.imageloader.cache.impl;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EntryWeigher;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;
import com.sun.imageloader.core.ImageKey;
import com.sun.imageloader.utils.L;

public class LRUCache extends SoftCache<ImageKey, Bitmap> implements EntryWeigher<ImageKey, Bitmap>,  EvictionListener<ImageKey, Bitmap>{

	private final ConcurrentMap<ImageKey, Bitmap> _lruHardCache;
	private int _currentSizeMemory;
	private static final String TAG = LRUCache.class.getName();
	private AtomicBoolean _isAlreadyTrimmingCache = new AtomicBoolean();
	private static final int INITAIL_CAP = 20;
	
	/**
	 * This implementation keeps a reference to {@link Bitmap} objects keyed to it's {@link ImageKey}. Once the internal cache size
	 * reaches an internal limit specified in MB through the constructor, an item is evicted. This is then promoted to the 
	 * softcache which maintains a {@link SoftReference} to the {@link Bitmap} object, which can be removed by when a GC happens.
	 * 
	 * This implementation is thread safe
	 * 
	 * @param maxSizeMemory_ 
	 * 			measured in MB
	 */
	public LRUCache(int maxSizeMemory_) {
		super(maxSizeMemory_);
		Log.v(TAG, "max mem size " + getMaxCacheSizeInMB());
		// this is where the magic happens
		_lruHardCache = new ConcurrentLinkedHashMap.Builder<ImageKey, Bitmap>()
			    .maximumWeightedCapacity((getMaxCacheSizeInMB()))
				.initialCapacity(INITAIL_CAP)
				.weigher(this)
				.listener(this)
				.build();
	}


	@Override
	protected float sizeOfValue(Bitmap value_) {
		float memory =  (value_.getRowBytes() * value_.getHeight()) ;
		L.v(TAG, "Memory size if image: " + memory) ;
		return memory;
	}

	/**
	 * @param key_ {@link ImageKey} which is used to uniquely identify a {@link Bitmap} image
	 * @param value_ the {@link Bitmap} image
	 * 
	 * @return true if the previous value is not null
	 */
	@Override
	public boolean put(ImageKey key_, Bitmap value_) {

		if (key_ == null || value_ == null) {
			throw new NullPointerException("key or value supplied was null");
		}

		Bitmap previousBitmap;
		previousBitmap = _lruHardCache.put(key_, value_);
				
		if(previousBitmap != null){
			return true;
		}else{
			return false;
		}

//		synchronized (_putLock) {
//			_currentSizeMemory += sizeOfValue(value_);
//			if (previousBitmap != null) {
//				_currentSizeMemory -= sizeOfValue(value_);
//			}
//		}

	}

	
	/**
	 * 
	 * This method is now deprecated, but is still here for reference purposes.
	 * 
	 * Trim the cache size
	 *  
	 * @param maxMemorySize_
	 * 
	 */
	@Deprecated
	protected void trimeCache(int maxMemorySize_) {
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

	/**
	 * Retrieve the {@link Bitmap} object from the internal cache
	 * 
	 * @param key_ the {@link Image} used to retrieve the {@link Bitmap} in the cache
	 * @return the {@link Bitmap} image
	 */
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



	@Override 
	public int weightOf(ImageKey key, Bitmap value) {
		    float bytes = sizeOfValue(value);
		    return (int) bytes;
	}

	@Override
	public void onEviction(ImageKey key_, Bitmap value_) {
		L.v(TAG, "Evicted ImageKey=" + key_ + ", with bitmap size=" + sizeOfValue(value_));
		super.put(key_, value_);
	}
	
	
	

}
