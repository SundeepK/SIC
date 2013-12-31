package com.sun.imageloader.core;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import com.sun.imageloader.cache.api.MemoryCache;
import com.sun.imageloader.cache.impl.DiskCache;
import com.sun.imageloader.cache.impl.LRUCache;
import com.sun.imageloader.core.api.ImageTaskListener;
import com.sun.imageloader.imagedecoder.api.ImageDecoder;
import com.sun.imageloader.imagedecoder.impl.SimpleImageDecoder;
import com.sun.imageloader.imagedecoder.utils.L;
import com.sun.imageloader.memorizer.api.BitmapMemorizer;
import com.sun.imageloader.memorizer.api.Computable;
import com.sun.imageloader.memorizer.api.IMemorizer;


final public class UrlImageLoaderConfiguration {
	
	static final String TAG = UrlImageLoaderConfiguration.class.getName();

	final MemoryCache<ImageKey, Bitmap> _lruMemoryCache;
	final Handler _imageViewUpdateHandler;
	final MemoryCache<ImageKey, File> _diskCache;
	final IMemorizer<ImageSettings, Bitmap> _bitmapMemorizer;
    final ConcurrentHashMap<Integer, ImageKey> _viewKeyMap; // Associate {@link ImageView} and image being loaded using a unique key in an internal {@link SparseArray}. 
	final ImageTaskListener _taskListener;
	final File _diskCacheLocation;
	final int _imageQuality;
	final Config _configType;
	final CompressFormat _compressFormat;
	final ThreadPoolExecutor _threadPoolForTaskExecutor;
	final Drawable _onLoadingDrawable;
	final Computable<ImageSettings, Bitmap> _computable;


	private UrlImageLoaderConfiguration (Builder builder){
		_lruMemoryCache = builder._memoryCache;
		_imageViewUpdateHandler = builder._imageViewUpdateHandler;
		_diskCache = builder._diskCache;
		_viewKeyMap = builder._viewKeyMap;
		_taskListener = builder._taskListener;
		_diskCacheLocation = builder._diskCacheLocation;
		_imageQuality = builder._imageQuality;
		_configType = builder._configType;
		_compressFormat = builder._compressFormat;
		_threadPoolForTaskExecutor = builder._threadPoolForTaskExecutor;
		_onLoadingDrawable = builder._onLoadingDrawable;
		_computable = builder._computable;
		_bitmapMemorizer = builder._bimapMemorizer;
	}
	
	public static class Builder {
		
		private ConcurrentHashMap<Integer, ImageKey> _viewKeyMap;
		private int _maxCacheMemorySizeInMB;
		private MemoryCache<ImageKey, Bitmap> _memoryCache;
		private Handler _imageViewUpdateHandler;
		private ImageWriter _imageWriter;
		private boolean _useExternalStorage = true;
		private String _directoryName;
		private File _diskCacheLocation;
		private MemoryCache<ImageKey, File> _diskCache;
		private IMemorizer<ImageSettings, Bitmap> _bimapMemorizer;
		private ImageTaskListener _taskListener;
		private int _imageQuality;
		private Config _configType;
		private	CompressFormat _compressFormat;
		private ThreadPoolExecutor _threadPoolForTaskExecutor;
		private Drawable _onLoadingDrawable;
		private Computable<ImageSettings, Bitmap> _computable;
		private ImageDecoder _imageDecoder;

		/**
		 * Builder to construct the {@link UrlImageLoaderConfiguration} used by {@link UrlImageLoader} to retrieve images.
		 */
		public Builder() {
			_imageViewUpdateHandler = new Handler();
		}
		
		/**
		 * 
		 * @param maxCacheMemorySizeInMB_
		 * 			the max memory in MB the internal cache should occupy
		 * @return
		 */
		public Builder setMaxCacheMemorySize(int maxCacheMemorySizeInMB_){
			long availableMemory = Runtime.getRuntime().maxMemory();	
			if(maxCacheMemorySizeInMB_ > availableMemory || maxCacheMemorySizeInMB_ <= 0)
				throw new IllegalArgumentException("The specified memory to allocate for cache is larger than the current heap size or is <= 0");
				
			this._maxCacheMemorySizeInMB = maxCacheMemorySizeInMB_;	
			return this;
		}
		
		public Builder setDirectoryName(String directoryName_){
			this._directoryName = directoryName_;
			return this;
		}
		
		public Builder setImageQuality(int quality_){
			this._imageQuality = quality_;
			return this;
		}
		
		public Builder shouldLog(boolean shouldLog_){
				L.shouldLog(shouldLog_);
				return this;
		}
		
		public Builder setImageType(CompressFormat compressFormat_){
			this._compressFormat = compressFormat_; 
			return this;
		}
		
		public Builder setImageConfig(Bitmap.Config config_){
			this._configType = config_; 
			return this;
		}
		
		public Builder setThreadExecutor(ThreadPoolExecutor threadPoolExecutor_){
			this._threadPoolForTaskExecutor = threadPoolExecutor_;
			return this;
		}
		
		public Builder useExternalStorage(boolean useExternalStorage_){
			this._useExternalStorage = useExternalStorage_;
			return this;
		}
		
		public Builder setOnloadingImage(Drawable colorDrawable_){
			this._onLoadingDrawable = colorDrawable_;
			return this;
		}
		
		/**
		 * Build an instance of {@link UrlImageLoaderConfiguration} to pass into the {@link UrlImageLoader#init(UrlImageLoaderConfiguration)} method to 
		 * Construct an instance of {@link UrlImageLoader}.
		 * 
		 * @return
		 * 		{@link UrlImageLoaderConfiguration}
		 */		
		public UrlImageLoaderConfiguration build(Context context_){
			initBuilder(context_);
			return new UrlImageLoaderConfiguration(this);
 
		}
		
		/**
		 * Used to initialise default object used by {@link UrlImageLoader} to retreive images with.
		 * 
		 */
		private void initBuilder(Context context_){
			
			_viewKeyMap = new ConcurrentHashMap<Integer, ImageKey>();
			
			if(_maxCacheMemorySizeInMB <= 0){
				_maxCacheMemorySizeInMB = 1;
			}
							
			if(_memoryCache ==null)
				_memoryCache = new LRUCache(_maxCacheMemorySizeInMB);
			
			if(_diskCacheLocation == null){
				_diskCacheLocation = loadDefaultDiskDir();
			}
			
			if(_imageWriter == null){
				
				if(_useExternalStorage){
						_imageWriter = new ImageWriter(_diskCacheLocation);
					
				}else{
					_imageWriter = new ImageWriter(getInternalDir(context_));
				}
			}
			
			if(_diskCache == null){
				_diskCache = new DiskCache(_diskCacheLocation, 50, true);
			}
			
			if(_taskListener == null){
				_taskListener = new SimpleImageListenerImpl();
			}
			
			if(_imageQuality <= 0){
				_imageQuality = 100;
			}
			
			if(_configType == null){
				_configType = Config.ARGB_8888;
			}
			
			if(_compressFormat == null){
				_compressFormat = CompressFormat.JPEG;
			}
			
			if(_threadPoolForTaskExecutor == null){
				_threadPoolForTaskExecutor = new ThreadPoolExecutor(4, 6, 60, TimeUnit.SECONDS,
		                new LinkedBlockingQueue<Runnable>());
			}
			
			if(_onLoadingDrawable == null){
				_onLoadingDrawable = new ColorDrawable(Color.BLACK);
			}
			
			if(_imageDecoder == null)
				_imageDecoder = new SimpleImageDecoder();
			
		
			if(_computable == null)
				_computable = new ComputableImage(_imageDecoder, _memoryCache,
						_diskCache, _imageWriter, _taskListener,_viewKeyMap);	

			if(_bimapMemorizer == null)
				_bimapMemorizer = new BitmapMemorizer(_computable, _viewKeyMap);
			
		
				
		}
		
		private File getInternalDir(Context context_){
			File storageDir = context_.getFilesDir();
			L.v(TAG, "Using internal storage, and path to store image: " + storageDir.getAbsolutePath());		
			return storageDir;
		}
		
		private File loadDefaultDiskDir(){
			String state = Environment.getExternalStorageState();
			File diskCacheLocation = null;
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				
				String defaultDirectory = Environment.DIRECTORY_PICTURES + "/SICimageCache";
				L.v(TAG, "External local path is: " + defaultDirectory);

				if(TextUtils.isEmpty(_directoryName)){
					diskCacheLocation = new File(Environment.getExternalStorageDirectory() ,"/"+defaultDirectory);
				}else{						
					L.v(TAG, "External local path is: " + Environment.getExternalStorageDirectory());
					diskCacheLocation = new File(Environment.getExternalStorageDirectory(), "/"+_directoryName);
				}
				//Add a long check to see if we can even create the directory or if we can even write to it
				if(!diskCacheLocation.exists()){
					if(!diskCacheLocation.mkdirs()){
						if(!diskCacheLocation.getParentFile().mkdirs()){
							if(!diskCacheLocation.canWrite()){
								throw new IllegalStateException("Unable to create disk cache directory to save images since there is no write access");
							}
						}
					}
				}
			}
			return diskCacheLocation;
		}

	}

}