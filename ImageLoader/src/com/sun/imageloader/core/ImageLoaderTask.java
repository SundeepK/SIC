package com.sun.imageloader.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.concurrent.locks.Lock;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import com.sun.imageloader.cache.api.MemoryCache;
import com.sun.imageloader.cache.api.ReadWriteImageLock;
import com.sun.imageloader.concurrent.DisplayImageTask;
import com.sun.imageloader.core.api.FailedTaskReason;
import com.sun.imageloader.core.api.FailedTaskReason.ExceptionType;
import com.sun.imageloader.core.api.ImageTaskListener;
import com.sun.imageloader.downloader.api.ImageRetriever;
import com.sun.imageloader.imagedecoder.api.ImageDecoder;
import com.sun.imageloader.imagedecoder.utils.L;

public class ImageLoaderTask implements Runnable {

	private static final String TAG = ImageLoaderTask.class.getName();
	private static final String THREAD_NAME = Thread.currentThread().getName();
	protected final ImageSettings _imageSettings;
	protected final ImageDecoder _imageDecoder;
	protected final Handler _handler;
	protected final MemoryCache<ImageKey, Bitmap> _lruCache;
	protected final MemoryCache<ImageKey, File> _diskCache;
	protected final ImageWriter _imageWriter;
	protected final ImageRetriever _imageDownloader;
	protected final ReadWriteImageLock<ImageKey> _readWriteLock;
	protected final MemoryCache<ImageKey, Future<Bitmap>> _futureBimapCache;
	protected final UrlImageLoaderConfiguration _configs;
	
	/**
	 * {@link ImageLoaderTask} is used to perform the long task to retrieving the {@link Bitmap} from either the internal cache, disk or from a network call.
	 * 
	 * 
	 * @param imageDecoder_
	 * 			used to decode the image to a {@link Bitmap}
	 * @param imageSettings_
	 * 			contains the various objects associated with the {@link Bitmap} that will be loaded onto an {@link ImageView}
	 * @param imageDownloader_
	 * 			used to obtain an {@link InputStream} containing the bytes to decode into a {@link Bitmap}
	 * @param configs_
	 * 			config which contains references to various internal caches and data structures
	 * @param taskListener_
	 * 			listener needed to perform special operations at certain events
	 */
	public ImageLoaderTask(ImageDecoder imageDecoder_,ImageSettings imageSettings_,	ImageRetriever imageDownloader_,
			UrlImageLoaderConfiguration configs_, ImageTaskListener taskListener_) {
		_imageDecoder = imageDecoder_;
		_imageSettings = imageSettings_;
		_handler = configs_._imageViewUpdateHandler;
		_lruCache = configs_._lruMemoryCache;
		_imageWriter = configs_._imageWriter;
		_diskCache = configs_._diskCache;
		_imageDownloader = imageDownloader_;
		_readWriteLock = configs_._imageReadWriteLock;
		_taskListener = taskListener_;
		_configs= configs_;
	}

	/**
	 * perform the task of retrieving the {@link Bitmap} and loading it onto an {@link ImageView}
	 */
	@Override
	public void run() {

		Bitmap decodedImage = null;

		decodedImage = _lruCache.getValue(_imageSettings.getImageKey());

		if (decodedImage == null) {
			decodedImage = loadBitmap();
		}

		if (decodedImage != null) {
			_lruCache.put(_imageSettings.getImageKey(), decodedImage);
			postDisplayImage(decodedImage);
		}else{
			_configs._viewKeyMap.remove(_imageSettings.getImageView().hashCode());
		}

	}

	/**
	 * Load the {@link Bitmap} object from either the cache, disk or network call
	 * @return
	 */
	private Bitmap loadBitmap() {

		Bitmap imageToRetreive = null;
		Lock lock = _readWriteLock.getReadWriteLock(_imageSettings.getImageKey());
		try {
			lock.lock();
			ImageKey imageKey = _imageSettings.getImageKey();
			if(imageKey != _configs._viewKeyMap.get(_imageSettings.getImageView().hashCode())){
				return null;
			}
			imageToRetreive = tryLoadImageFromDisk();

			if (imageToRetreive == null) {
				imageToRetreive = tryLoadImageFromNetwork();

				if (imageToRetreive != null)
					L.v(TAG, THREAD_NAME + ": Loaded image from network successfully");

			} else {
				L.v(TAG, THREAD_NAME + ": Loaded image from disk successfully");
			}

		} catch (IOException e) {
			_taskListener.onImageLoadFail(new FailedTaskReason(ExceptionType.IOException, e), _imageSettings);
			e.printStackTrace();
		} catch (URISyntaxException e) {
			_taskListener.onImageLoadFail(new FailedTaskReason(ExceptionType.URISyntaxException, e), _imageSettings);
			e.printStackTrace();
		} catch (InterruptedException e) {
			_taskListener.onImageLoadFail(new FailedTaskReason(ExceptionType.URISyntaxException, e), _imageSettings);
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			_taskListener.onImageLoadFail(new FailedTaskReason(ExceptionType.OutOfMemoryError, e), _imageSettings);
			e.printStackTrace();
		}finally{
			lock.unlock();
		}

		return imageToRetreive;
	}

	/**
	 * Attempt to download the image from a network call and write to disk
	 * 
	 * @return
	 * 		{@link Bitmap} of the final decoded image
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private Bitmap tryLoadImageFromNetwork() throws IOException,
			URISyntaxException {
		if(_imageSettings.getImageKey() != _configs._viewKeyMap.get(_imageSettings.getImageView().hashCode())){
			return null;
		}
		
		Bitmap imageLoadedFromNetwork = null;
			
			InputStream stream = _imageDownloader.getStream(_imageSettings
					.getUrl());
			
			imageLoadedFromNetwork = _imageDecoder.decodeImage(stream, _imageSettings, true);
			_imageWriter.writeBitmapToDisk(_imageSettings,
					imageLoadedFromNetwork);

		return imageLoadedFromNetwork;
	}

	/**
	 * Load the {@link Bitmap} object from the disk and decode into bitmap
	 * @return
	 * 		the {@link Bitmap} object loaded from either the cache or disk. If nothing is found, then null is returned
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 */
	private Bitmap tryLoadImageFromDisk() throws IOException,
			URISyntaxException, InterruptedException {
		ImageKey imageKey = _imageSettings.getImageKey();

		if(imageKey != _configs._viewKeyMap.get(_imageSettings.getImageView().hashCode())){
			return null;
		}

		Bitmap cachedImage = _lruCache.getValue(imageKey);

		if (cachedImage != null) {
			return cachedImage;
		}
		
		File imageFile = _diskCache.getValue(imageKey);
		
		if(imageFile == null){
			imageFile = new File(_configs._diskCacheLocation, _imageSettings.getFinalFileName());
		}

		if (imageFile != null) {
			L.v(TAG,
					THREAD_NAME	+ ": File loaded from disk with path: "
							+ imageFile.getAbsolutePath());

			if (imageFile.exists()) {
				L.v(TAG, THREAD_NAME +
						"File exists and so decoding from the image from the disk: "
								+ imageFile.getAbsolutePath());
				Bitmap decodeImage = _imageDecoder.decodeImage(imageFile, _imageSettings, false);
				return decodeImage;
			}

		}
		return null;

	}

	private void postDisplayImage(Bitmap decodedImage_) {
		_handler.post(new DisplayImageTask(_imageSettings,
				decodedImage_, _configs._viewKeyMap, _taskListener));
	}

}
