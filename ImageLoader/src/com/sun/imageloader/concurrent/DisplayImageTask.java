package com.sun.imageloader.concurrent;

import com.sun.imageloader.core.ImageKey;
import com.sun.imageloader.core.ImageSettings;
import com.sun.imageloader.core.api.ImageTaskListener;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class DisplayImageTask implements Runnable {

	private final ImageSettings _imageSettings;
	private final Bitmap _bitmap;
	private ImageTaskListener _taskListener;
	/**
	 * {@linkplain DisplayImageTask} is used to display the {@linkplain Bitmap} into the {@linkplain ImageView}.
	 * 
	 * @param imageSettings_
	 * 			the settings associated with the {@linkplain Bitmap} that will be loaded
	 * @param bitmap_
	 * 			the {@linkplain Bitmap} to display
	 * @param imageViewMap_
	 * 			the map which associates {@linkplain ImageView} objects with the {@link ImageKey} 
	 * 			
	 */
	public DisplayImageTask (ImageSettings imageSettings_, Bitmap bitmap_, ImageTaskListener taskListener_){
		_imageSettings = imageSettings_;
		_bitmap = bitmap_;
		_taskListener = taskListener_;
	}
	
	@Override
	public void run() {
			if (_imageSettings.getImageView().getTag().equals(_imageSettings.getImageKey())){
			_taskListener.preImageLoad(_imageSettings);
			_imageSettings.getImageView().setImageBitmap(_bitmap);
			_taskListener.onImageLoadComplete(_bitmap, _imageSettings);
		}
	         
	}


}
