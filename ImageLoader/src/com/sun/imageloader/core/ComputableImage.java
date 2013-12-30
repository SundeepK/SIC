package com.sun.imageloader.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.widget.ImageView;
import com.sun.imageloader.cache.api.MemoryCache;
import com.sun.imageloader.core.api.FailedTaskReason;
import com.sun.imageloader.core.api.ImageTaskListener;
import com.sun.imageloader.core.api.FailedTaskReason.ExceptionType;
import com.sun.imageloader.downloader.api.ImageRetriever;
import com.sun.imageloader.downloader.impl.ImageRetrieverFactory;
import com.sun.imageloader.imagedecoder.api.ImageDecoder;
import com.sun.imageloader.imagedecoder.utils.L;
import com.sun.imageloader.memorizer.api.Computable;
import com.sun.imageloader.memorizer.api.InterruptedImageLoadException;

public class ComputableImage implements Computable<ImageSettings, Bitmap> {

	
	private static final String TAG = ComputableImage.class.getName();
	private static final String THREAD_NAME = Thread.currentThread().getName();
	private final ImageDecoder _imageDecoder;
	private final MemoryCache<ImageKey, Bitmap> _lruCache;
	private final MemoryCache<ImageKey, File> _diskCache;
	private final ImageWriter _imageWriter;
	private ImageTaskListener _taskListener;
    final ConcurrentHashMap<Integer, ImageKey> _viewKeyMap; 

	
	/**
	 * {@link ImageLoaderTask} is used to perform the long task to retrieving the {@link Bitmap} from either the internal cache, disk or from a network call.
	 * 
	 * 
	 * @param imageDecoder_
	 * 			used to decode the image to a {@link Bitmap}
	 * @param imageSettings_
	 * 			contains the various objects associated with the {@link Bitmap} that will be loaded onto an {@link ImageView}
	 * @param imageRetriever_
	 * 			used to obtain an {@link InputStream} containing the bytes to decode into a {@link Bitmap}
	 * @param configs_
	 * 			config which contains references to various internal caches and data structures
	 * @param taskListener_
	 * 			listener needed to perform special operations at certain events
	 */
	public ComputableImage(ImageDecoder imageDecoder_,
			MemoryCache<ImageKey, Bitmap> lruCache_, MemoryCache<ImageKey, File> diskCache_, 
			ImageWriter imageWriter_, ImageTaskListener taskListener_, 	ConcurrentHashMap<Integer, ImageKey> viewKeyMap_ ) {
		_imageDecoder = imageDecoder_;
		_lruCache = lruCache_;
		_imageWriter = imageWriter_;
		_diskCache = diskCache_;
		_taskListener = taskListener_;
		_viewKeyMap = viewKeyMap_;
	}


	@Override
	public Bitmap compute(ImageSettings valueToCompute_) throws InterruptedImageLoadException {
		
		_viewKeyMap.put(valueToCompute_.getImageView().hashCode(), valueToCompute_.getImageKey());
		valueToCompute_.getImageView().setTag(valueToCompute_.getImageKey());
		
		Bitmap decodedImage = null;

		decodedImage = _lruCache.getValue(valueToCompute_.getImageKey());

		if (decodedImage == null) {
			decodedImage = loadBitmap(valueToCompute_);
		}

		if (decodedImage != null) {
			_lruCache.put(valueToCompute_.getImageKey(), decodedImage);
		}
		
		if(!isViewStillValid(valueToCompute_))
			throw new InterruptedImageLoadException("ImageView is no longer valid, so interupting image load");
		
		return decodedImage;
	}

	/**
	 * Load the {@link Bitmap} object from either the cache, disk or network call
	 * @return
	 * @throws InterruptedImageLoadException 
	 */
	private Bitmap loadBitmap(ImageSettings imageSettings_) throws InterruptedImageLoadException {

		Bitmap imageToRetreive = null;
		try {

			imageToRetreive = tryLoadImageFromDisk(imageSettings_);

			if (imageToRetreive == null) {
				imageToRetreive = tryLoadImageFromNetwork(imageSettings_);

				if (imageToRetreive != null)
					L.v(TAG, THREAD_NAME + ": Loaded image from network successfully");

			} else {
				L.v(TAG, THREAD_NAME + ": Loaded image from disk successfully");
			}

		} catch (IOException e) {
			_taskListener.onImageLoadFail(new FailedTaskReason(ExceptionType.IOException, e), imageSettings_);
			e.printStackTrace();
		} catch (URISyntaxException e) {
			_taskListener.onImageLoadFail(new FailedTaskReason(ExceptionType.URISyntaxException, e), imageSettings_);
			e.printStackTrace();
		} catch (InterruptedException e) {
			_taskListener.onImageLoadFail(new FailedTaskReason(ExceptionType.URISyntaxException, e), imageSettings_);
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			_taskListener.onImageLoadFail(new FailedTaskReason(ExceptionType.OutOfMemoryError, e), imageSettings_);
			e.printStackTrace();
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
	 * @throws InterruptedImageLoadException 
	 */
	private Bitmap tryLoadImageFromNetwork(ImageSettings imageSettings_) throws IOException,
			URISyntaxException, InterruptedImageLoadException {
		
		if(!isViewStillValid(imageSettings_))
			throw new InterruptedImageLoadException("ImageView is no longer valid, so interupting image load");
		
		Bitmap imageLoadedFromNetwork = null;
		ImageRetriever imageRetriever = ImageRetrieverFactory.getImageRetriever(imageSettings_.getUrl());
		InputStream stream = imageRetriever.getStream(imageSettings_
					.getUrl());
		
		imageLoadedFromNetwork = _imageDecoder.decodeImage(stream, imageSettings_, true);
		_imageWriter.writeBitmapToDisk(imageSettings_,imageLoadedFromNetwork);

		if(!isViewStillValid(imageSettings_))
			throw new InterruptedImageLoadException("ImageView is no longer valid, so interupting image load");
		
		return imageLoadedFromNetwork;
	}

	/**
	 * Load the {@link Bitmap} object from the disk and decode into bitmap
	 * @return
	 * 		the {@link Bitmap} object loaded from either the cache or disk. If nothing is found, then null is returned
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 * @throws InterruptedImageLoadException 
	 */
	private Bitmap tryLoadImageFromDisk(ImageSettings imageSettings_) throws IOException,
			URISyntaxException, InterruptedException, InterruptedImageLoadException {
		
		if(!isViewStillValid(imageSettings_))
			throw new InterruptedImageLoadException("ImageView is no longer valid, so interupting image load");
		
		ImageKey imageKey = imageSettings_.getImageKey();

		Bitmap cachedImage = _lruCache.getValue(imageKey);

		if (cachedImage != null) {
			return cachedImage;
		}
		
		
		File imageFile = _diskCache.getValue(imageKey);
		
		if(imageFile == null){
			imageFile = new File(_imageWriter.getFileSaveDirectoryPath(), imageSettings_.getFinalFileName());
		}

		if (imageFile != null) {
			L.v(TAG,
					THREAD_NAME	+ ": File loaded from disk with path: "
							+ imageFile.getAbsolutePath());

			if (imageFile.exists()) {
				L.v(TAG, THREAD_NAME +
						"File exists and so decoding from the image from the disk: "
								+ imageFile.getAbsolutePath());
				Bitmap decodeImage = _imageDecoder.decodeImage(imageFile, imageSettings_, false);
				return decodeImage;
			}

		}
		
		if(!isViewStillValid(imageSettings_))
			throw new InterruptedImageLoadException("ImageView is no longer valid, so interupting image load");
		
		return null;

	}

	private boolean isViewStillValid(ImageSettings imageSettings_) {
		int viewKey = imageSettings_.getImageView().hashCode();
		ImageKey key = _viewKeyMap.get(viewKey);
		if (key.equals(imageSettings_.getImageKey()) && (imageSettings_.getImageView().getTag().equals(key))) {
			L.v(TAG, "View is still valid");
			return true;
		}
		
		L.v(TAG, "View is invalid now");
		_viewKeyMap.remove(viewKey);
		return false;
	}


}
