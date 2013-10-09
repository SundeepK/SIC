package com.sun.imageloader.core;

import java.util.Map;

import com.sun.imageloader.core.api.ImageTaskListener;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class DisplayImageTask implements Runnable {

	ImageSettings _imageSettings;
	Bitmap _bitmap;
	Map<Integer, ImageKey> _imageViewMap;
	ImageTaskListener _taskListener;
	
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
	public DisplayImageTask (ImageSettings imageSettings_, Bitmap bitmap_, Map<Integer, ImageKey> imageViewMap_, ImageTaskListener taskListener_){
		_imageSettings = imageSettings_;
		_bitmap = bitmap_;
		_imageViewMap = imageViewMap_;
		_taskListener = taskListener_;
	}
	
	@Override
	public void run() {
		_taskListener.preImageLoad(_imageSettings);
		if(_imageSettings.getImageKey() == _imageViewMap.get(_imageSettings.getImageView().hashCode())){
			_imageSettings.getImageView().setImageBitmap(_bitmap);
		}
		_taskListener.onImageLoadComplete(_bitmap, _imageSettings);
		
		
	}

}
