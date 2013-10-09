package com.sun.imageloader.cache.impl;

import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import com.sun.imageloader.cache.api.ReadWriteImageLock;
import com.sun.imageloader.core.ImageKey;

public class ImageLock implements ReadWriteImageLock<ImageKey> {

	private WeakHashMap<ImageKey, ReentrantLock> _weakLockImageLocks;

	/**
	 * Maintains an internal reference of {@linkplain ReentrantLock}  associated with an {@linkplain ImageKey } . 
	 * This can be used to ensure multiple threads do not share the
	 * same {@linkplain Bitmap}  keyed to the same {@linkplain ImageView}.
	 * 
	 */
	public ImageLock() {
		_weakLockImageLocks = new WeakHashMap<ImageKey, ReentrantLock>(0, 0.75f);
	}


	@Override
	public ReentrantLock getReadWriteLock(ImageKey key_) {
		ReentrantLock readWriteLock = null;

		synchronized (this) {
			readWriteLock = _weakLockImageLocks.get(key_);

			if (readWriteLock != null)
				return readWriteLock;

			readWriteLock = new ReentrantLock();

			_weakLockImageLocks.put(key_, readWriteLock);
		}

		return readWriteLock;
	}

}
