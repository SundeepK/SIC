package com.sun.imageloader.memorizer.api;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.sun.imageloader.concurrent.ComputableCallable;
import com.sun.imageloader.core.ImageKey;
import com.sun.imageloader.core.ImageSettings;
import com.sun.imageloader.core.api.Settings;
import com.sun.imageloader.imagedecoder.utils.L;

public abstract class AMemorizer<T extends ImageSettings,V> implements IMemorizer<T,V> {
	private static final String TAG = AMemorizer.class.getName();
	private ConcurrentHashMap<T, Future<V>> _bitmapFutureCache;
	private Computable<T, V> _computable;
	private final ConcurrentHashMap<Integer, ImageKey> _viewKeyMap;

	/**
	 * AMemorizer is used to encapsulates a Computable so that Future tasks can be cached and computed when nessicary.
	 * 
	 * @param computable_ the Computable that will compute the value when given an input
	 */
	protected AMemorizer(Computable<T, V> computable_,  ConcurrentHashMap<Integer, ImageKey> viewKeyMap_){
		_computable =  computable_;
		_bitmapFutureCache  = new ConcurrentHashMap<T, Future<V>>();
		_viewKeyMap = viewKeyMap_;
	}
	
	protected abstract Callable<V> getCallable(T computable_);
	
	private boolean ifExists(ImageSettings imageSettings_) {
		int viewKey = imageSettings_.getImageView().hashCode();
		ImageKey key = _viewKeyMap.get(viewKey);
		
		if (key != null) {
			if (key.equals(imageSettings_.getImageKey())
					) {
				L.v(TAG, "View is still valid");
				return true;
			}else{
				L.v(TAG, "View is invalid now");
				_viewKeyMap.remove(viewKey);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public V executeComputable(T computableKey_) throws InterruptedImageLoadException, ExecutionException {
		
				
		Future<V> future = _bitmapFutureCache.get(computableKey_);
		V returnValue = null;
		
//		if(!ifExists(computableKey_) && future!= null){
//			future.cancel(true);
//		}

		
		if(future == null){
			Callable<V> callableToExecute = new ComputableCallable<T, V>(_computable, computableKey_);
			FutureTask<V> futueTask = new FutureTask<V>(callableToExecute);
			future = _bitmapFutureCache.putIfAbsent(computableKey_, futueTask);
			if (future == null) { 
				future = futueTask;
				futueTask.run();
			}
		}
		
		try {
			
//			if(!ifExists(computableKey_) && future!= null){
//				future.cancel(true);
//			}else{
				 returnValue = future.get();
//			}
			
		}catch (CancellationException e) {
			L.v(TAG, "Future task was cancelled, so removing task from cache with key: " + computableKey_);
			_bitmapFutureCache.remove(computableKey_);
		}catch (InterruptedException e){
			_bitmapFutureCache.remove(computableKey_);
			throw new InterruptedImageLoadException("Image load was interrupted", e);
		}
		
		_bitmapFutureCache.remove(computableKey_);
		return returnValue;
	}
	
}
