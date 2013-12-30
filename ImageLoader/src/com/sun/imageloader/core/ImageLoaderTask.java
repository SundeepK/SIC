package com.sun.imageloader.core;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;
import com.sun.imageloader.concurrent.DisplayImageTask;
import com.sun.imageloader.core.api.ImageTaskListener;
import com.sun.imageloader.imagedecoder.utils.L;
import com.sun.imageloader.memorizer.api.IMemorizer;
import com.sun.imageloader.memorizer.api.InterruptedImageLoadException;

public class ImageLoaderTask implements Runnable {
	private static final String TAG = ImageLoaderTask.class.getName();
	private final IMemorizer<ImageSettings, Bitmap> _bitmapMemoizer;
	private final ImageSettings _imageSettings;
	private final Handler _handler;
	private final ImageTaskListener _imageListener;
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
	public ImageLoaderTask(IMemorizer<ImageSettings, Bitmap> bitmapMemoizer_,
			ImageSettings imageSetings_, Handler handler_, ImageTaskListener listener_) {
		_bitmapMemoizer =bitmapMemoizer_;
		_imageSettings = imageSetings_;
		_handler = handler_;
		_imageListener = listener_;
	}

	/**
	 * perform the task of retrieving the {@link Bitmap} and loading it onto an {@link ImageView}
	 */
	@Override
	public void run() {
		Bitmap decodedImage = loadBitmap();
		if(decodedImage != null){
			postDisplayImage(decodedImage);
			L.v(TAG, "Successfully loaded image, now attempting to display to View");
		}else{
			L.w(TAG, "Unable to load image, so not displayig Bitmap to View");
		}
			
	}

	/**
	 * Load the {@link Bitmap} object from either the cache, disk or network call
	 * @return
	 */
	private Bitmap loadBitmap() {
		Bitmap loadedBitmap = null;
		try {
			loadedBitmap = _bitmapMemoizer.executeComputable(_imageSettings);
		} catch (InterruptedImageLoadException e) {
			L.w(TAG, "Image load was cancelled, so returing null Bitmap");
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return loadedBitmap;
	}

	
	private void postDisplayImage(Bitmap decodedImage_) {
		_handler.post(new DisplayImageTask(_imageSettings,
				decodedImage_, _imageListener));
	}

}
