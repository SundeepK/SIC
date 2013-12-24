package com.sun.imageloader.core;

import java.net.URI;
import java.net.URISyntaxException;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.sun.imageloader.concurrent.DisplayImageTask;
import com.sun.imageloader.core.api.ImageTaskListener;
import com.sun.imageloader.downloader.api.ImageRetriever;
import com.sun.imageloader.downloader.impl.ImageRetrieverFactory;
import com.sun.imageloader.imagedecoder.api.ImageDecoder;
import com.sun.imageloader.imagedecoder.impl.SimpleImageDecoder;
import com.sun.imageloader.imagedecoder.utils.KeyUtils;
import com.sun.imageloader.imagedecoder.utils.L;


public class UrlImageLoader {

	protected static final String TAG = UrlImageLoader.class.getName();

	private static final String ERROR_INIT_CONFIG_NULL = "UrlImageLoader cannot be instantiated with a null UrlImageLoaderConfiguration";

	private static final String ERROR_NOT_INIT = "UrlImageLoaderConfiguration is null, call init()";

	private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments supplied";

	private UrlImageLoaderConfiguration _ImageLoaderConfig;
	private UrlImageTaskExecutor _urlImageLoaderTaskExecutor;

	private volatile static UrlImageLoader urlImageLoaderInstance;

	/** Returns singleton class instance */
	public static UrlImageLoader getInstance() {
		if (urlImageLoaderInstance == null) {
			synchronized (UrlImageLoader.class) {
				if (urlImageLoaderInstance == null) {
					urlImageLoaderInstance = new UrlImageLoader();
				}
			}
		}
		return urlImageLoaderInstance;
	}
	
	public void invalidate(){
			synchronized (UrlImageLoader.class) {
					urlImageLoaderInstance = null;
					_ImageLoaderConfig = null;
		}
	}

	protected UrlImageLoader() {
	}

	/**
	 * Creates a new {@link UrlImageLoader} if one does not already exist, using the {@link UrlImageLoaderConfiguration} passed in.
	 * 
	 * @param configuration_
	 * 				used to initialise {@link UrlImageLoader}
	 */
	public synchronized void init(UrlImageLoaderConfiguration configuration_) {
		if (configuration_ == null) {
			throw new IllegalArgumentException(ERROR_INIT_CONFIG_NULL);
		}
		if (this._ImageLoaderConfig == null) {
			L.v(TAG,
					"Initialize internal UrlImageLoaderConfiguration with config supplied");
			_urlImageLoaderTaskExecutor = new UrlImageTaskExecutor(configuration_);
			this._ImageLoaderConfig = configuration_;
		} else {
			Log.w(TAG,
					"To initailize UrlImageLoader with new config, please call invalidate first");
		}
	}

	/**
	 * Load an image and display it to the {@link ImageView} passed in. {@link UrlImageLoader} follows 3 basic flows:
	 * 
	 * <li>Load image from cache if it exists and return bitmap, else</li>
	 * <li>Load image from disk, decode bitmap, put into cache and return, else</li>
	 * <li>Load image via network call, decode bitmap, write to disk, put into internal cache and return</li>
	 * 
	 * @param uri_
	 * 			the String uri of the image to be parsed
	 * @param imageView_
	 * 			the {@link ImageView} to display the {@link Bitmap} image with
	 * @param sampleSize_
	 * 			used to scale the image by a certain amount. A value of 1 <= 0 will not scale the {@link Bitmap} ; a value of == 4 
	 * returns an image that is 1/4 the width/height of the original, and 1/16 the number of pixels. Any value which is not
	 * a power of 2 will be rounded to the closest power of 2.
	 * @throws URISyntaxException
	 */
	public void displayImage(String uri_, ImageView imageView_, int sampleSize_) throws URISyntaxException {
		displayImage(uri_, imageView_, sampleSize_, null);
	}
	
	
	/**
	 * Load an image and display it to the {@link ImageView} passed in. {@link UrlImageLoader} follows 3 basic flows:
	 * 
	 * <li>Load image from cache if it exists and return bitmap, else</li>
	 * <li>Load image from disk, decode bitmap, put into cache and return, else</li>
	 * <li>Load image via network call, decode bitmap, write to disk, put into internal cache and return</li>
	 * 
	 * @param uri_
	 * 			the String url of the image to be parsed
	 * @param imageView_
	 * 			the {@link ImageView} to display the {@link Bitmap} image with
	 * @param sampleSize_
	 * 			used to scale the image by a certain amount. A value of 1 <= 0 will not scale the {@link Bitmap} ; a value of == 4 
	 * returns an image that is 1/4 the width/height of the original, and 1/16 the number of pixels. Any value which is not
	 * a power of 2 will be rounded to the closest power of 2.
	 * @param listener_
	 * 			used to invoke the listener when certain events are complete
	 * 
	 * @throws URISyntaxException
	 */
	public void displayImage(String uri_, ImageView imageView_,
			int sampleSize_, ImageTaskListener listener_) throws URISyntaxException {
		checkConfiguration();

		if (imageView_ == null) {
			throw new IllegalArgumentException(ERROR_WRONG_ARGUMENTS);
		}
		if (listener_ == null) {
			listener_ = _ImageLoaderConfig._taskListener;
		}
			
		URI imageUri = new URI(uri_);

		int key = KeyUtils.getPathKey(imageUri);
		ImageKey imageKey = new ImageKey(key, sampleSize_);

		_ImageLoaderConfig._viewKeyMap.put(imageView_.hashCode(), imageKey);
		Bitmap bmp = _ImageLoaderConfig._lruMemoryCache.getValue(imageKey);
		ImageSettings imageSpecificSettings = new ImageSettings(imageUri, imageView_, imageKey, _ImageLoaderConfig._compressFormat,
				_ImageLoaderConfig._configType, _ImageLoaderConfig._imageQuality);

		if (bmp != null && !bmp.isRecycled()) {
			L.v( TAG,  "Loaded bitmap image from cache, using url: " + uri_);
			
			DisplayImageTask displayTask = new DisplayImageTask(imageSpecificSettings, bmp, _ImageLoaderConfig._viewKeyMap, listener_);
			_ImageLoaderConfig._imageViewUpdateHandler.post(displayTask);
					
		}else{
			
			L.v( TAG,  "No image found in cache, attempting to fetch image via network or disk: " + uri_);
			imageView_.setImageDrawable(_ImageLoaderConfig._onLoadingDrawable);
			ImageRetriever imageDownloader = ImageRetrieverFactory.getImageRetriever(imageUri);
			ImageDecoder decoder = new SimpleImageDecoder();
			ImageLoaderTask	displayTask = new ImageLoaderTask(decoder, imageSpecificSettings,  imageDownloader,
					_ImageLoaderConfig, listener_);
			_urlImageLoaderTaskExecutor.sumbitTask(displayTask);
			
		}
	
	}

	private void checkConfiguration() {
		if (_ImageLoaderConfig == null) {
			throw new IllegalStateException(ERROR_NOT_INIT);
		}
	}

}
