package com.sun.imageloader.cache.impl;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import android.graphics.Bitmap;

import com.sun.imageloader.cache.api.MemoryCache;
import com.sun.imageloader.core.ImageKey;
import com.sun.imageloader.imagedecoder.utils.KeyUtils;
import com.sun.imageloader.imagedecoder.utils.L;

public class DiskCache implements MemoryCache<ImageKey, File> {

	private static final String TAG = DiskCache.class.getName();
	private File _diskCacheLocationDir;
	private final ConcurrentMap<ImageKey, Reference<File>> _imageFiles;

	/**
	 * 
	 * {@linkplain DiskCache} is used to maintain a soft cache of {@linkplain File} objects which are associated to an {@linkplain ImageKey}. 
	 * This allows images to be retrieves easily from the disk according to the {@linkplain ImageKey}.    
	 * 
	 * @param diskCacheLocationDir_
	 * 			The {@linkplain File} object containg the directory to cache  {@linkplain Bitmap} objects from
	 * @param maxFilesToCache_
	 * 			The maximum number of files to cache
	 * @param shouldLoadAllFilesFromDisk_
	 * 			whether to load all image files into cache on startup
	 */
	public DiskCache(File diskCacheLocationDir_, int maxFilesToCache_,
			boolean shouldLoadAllFilesFromDisk_) {
		_diskCacheLocationDir = diskCacheLocationDir_;
		_imageFiles = new ConcurrentHashMap<ImageKey, Reference<File>>(0, 0.75f);

		if (shouldLoadAllFilesFromDisk_)
			loadImagesFromDisk(_imageFiles);
	}

	/**
	 * Load {@linkplain File} objects and fill internal cache will these objects
	 * 
	 * @param imageFiles_ 
	 * 			a {@linkplain ConcurrentMap} to add all {@linkplain File} objects into
	 */
	private void loadImagesFromDisk(
			ConcurrentMap<ImageKey, Reference<File>> imageFiles_) {

		if(!_diskCacheLocationDir.exists()){
			L.v(TAG, "no dir exists for: " + _diskCacheLocationDir.getAbsolutePath());			
			if( _diskCacheLocationDir.mkdirs())
				L.v(TAG, "Successfully created new cache disk location: " + _diskCacheLocationDir.getAbsolutePath());			
		}
		
		String[] extentions = new String[] { ".jpg", ".jpeg", ".png" };

		final File[] imageFiles = _diskCacheLocationDir
				.listFiles(new ImageFileFilter(extentions));

		new Thread(new Runnable() {

			@Override
			public void run() {
				if (imageFiles != null) {
					for (File imageFile : imageFiles) {
						if (imageFile != null && imageFile.exists()){
							_imageFiles.put(KeyUtils.createImageKey(imageFile.getName()),
									new SoftReference<File>(imageFile));
							L.v(TAG, imageFile.getAbsolutePath());
						}
					}
				}
			}
		}).start();
		
		

	}

	@Override
	public boolean put(ImageKey key_, File value) {

		_imageFiles.put(key_,new SoftReference<File>(value));

		return true;
	}

	@Override
	public File getValue(ImageKey key_) {
		Reference<File> imageFile = _imageFiles.get(key_);
			
		if (imageFile != null) {
			return imageFile.get();
		}
		
		return null;
	}
	
	public File getNewImageFile(ImageKey key_){
		File imageFile = new File(_diskCacheLocationDir, key_.getImageFilename()); 
		_imageFiles.put(key_, new SoftReference<File>(imageFile));
		return imageFile;
	}

	/**
	 * This implementation will remove the image file from both the internal map
	 * and also the disk
	 */
	@Override
	public void remove(ImageKey key_, boolean removeAllOccurences_) {
		File imageFileRef = remove(key_);
		File imageFileToRemove = imageFileRef;
		if (imageFileToRemove != null && removeAllOccurences_) {
			boolean sta = imageFileToRemove.delete();
			L.v(TAG,"Deleting file in disk, attempted to delete file with status: "	+ sta);
		}

	}

	private File remove(ImageKey key_) {
		return _imageFiles.remove(key_).get();
	}

	/**
	 * Clear the internal cache of files
	 */
	@Override
	public void clear() {
		_imageFiles.clear();
	}

	@Override
	public Collection<ImageKey> getKeys() {

		Set<ImageKey> keys = _imageFiles.keySet();

		return keys;
	}



}
