package com.sun.imageloader.core;

import java.net.URI;
import java.net.URISyntaxException;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.AbsListView.OnScrollListener;
import com.sun.imageloader.concurrent.DisplayImageTask;
import com.sun.imageloader.concurrent.ImageLoaderTask;
import com.sun.imageloader.core.api.ImageTaskListener;
import com.sun.imageloader.imagedecoder.utils.KeyUtils;
import com.sun.imageloader.imagedecoder.utils.L;


public class UrlImageLoader implements OnScrollListener {

	protected static final String TAG = UrlImageLoader.class.getName();
	private static final String ERROR_INIT_CONFIG_NULL = "UrlImageLoader cannot be instantiated with a null UrlImageLoaderConfiguration";
	private static final String ERROR_NOT_INIT = "UrlImageLoaderConfiguration is null, call init()";
	private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments supplied";
	private UrlImageLoaderConfiguration _ImageLoaderConfig;
	private UrlImageTaskExecutor _urlImageLoaderTaskExecutor;
	
	public void invalidate(){
			synchronized (UrlImageLoader.class) {
					_ImageLoaderConfig = null;
		}
	}

	public UrlImageLoader(UrlImageLoaderConfiguration configuration_) {
		init(configuration_);
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
		
		//Get the ImageSettings specific to the image to load and the ImageView
		ImageSettings imageSpecificSettings = getImageSettings(uri_, sampleSize_, imageView_);
		Bitmap bmp = attemptLoadBitmapFromCache(imageSpecificSettings);
		
		//important, tag the view so we know is it reused or not in the future.
		//the same view can be tagged multiple times, we check the ImageKey associated with the View
		//when we attempt to load the image in a thread
		tagImageView(imageSpecificSettings); 

		if (bmp != null && !bmp.isRecycled()) {
			L.v( TAG,  "Loaded bitmap image from cache, using url: " + uri_);
			DisplayImageTask displayTask = new DisplayImageTask(imageSpecificSettings, bmp, listener_);
			_ImageLoaderConfig._imageViewUpdateHandler.post(displayTask);
		}else{
			startNewImageLoadTask(imageSpecificSettings, listener_);
		}
	
	}
	
	private void tagImageView(ImageSettings imageSpecificSettings_){
		ImageView imageView = imageSpecificSettings_.getImageView();
		imageView.setTag(imageSpecificSettings_.getImageKey());
	}
	
	private Bitmap attemptLoadBitmapFromCache(ImageSettings imageSpecificSettings_){
		return _ImageLoaderConfig._lruMemoryCache.getValue(imageSpecificSettings_.getImageKey());
	}
	
	private void startNewImageLoadTask(ImageSettings imageSpecificSettings_, ImageTaskListener listener_){
		ImageView imageView  =  imageSpecificSettings_.getImageView();
		L.v( TAG,  "No image found in cache, attempting to fetch image via network or disk: " + imageSpecificSettings_.getUrl());
		imageView.setImageDrawable(_ImageLoaderConfig._onLoadingDrawable);
		ImageLoaderTask	displayTask = new ImageLoaderTask(_ImageLoaderConfig._bitmapMemorizer, 
				imageSpecificSettings_,  _ImageLoaderConfig._imageViewUpdateHandler, 
				listener_, _ImageLoaderConfig._flingLock);
		_urlImageLoaderTaskExecutor.sumbitTask(displayTask);
	}
	

	private ImageSettings getImageSettings(String uri_, int sampleSize_, ImageView imageView_) throws URISyntaxException{
		URI imageUri = new URI(uri_);
		int key = KeyUtils.getPathKey(imageUri);
		ImageKey imageKey = new ImageKey(key, sampleSize_);
		ImageSettings imageSpecificSettings = new ImageSettings(imageUri, imageView_, imageKey, _ImageLoaderConfig._compressFormat,
				_ImageLoaderConfig._configType, _ImageLoaderConfig._imageQuality);
		return imageSpecificSettings;
	}

	private void checkConfiguration() {
		if (_ImageLoaderConfig == null) {
			throw new IllegalStateException(ERROR_NOT_INIT);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			_ImageLoaderConfig._flingLock.resume();
			L.v(TAG, ""+ OnScrollListener.SCROLL_STATE_IDLE);
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			L.v(TAG, ""+ OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
			_ImageLoaderConfig._flingLock.resume();
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			L.v(TAG, ""+ OnScrollListener.SCROLL_STATE_FLING);
			_ImageLoaderConfig._flingLock.pause();
			break;
		}
	}

}
