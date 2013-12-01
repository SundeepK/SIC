package com.sun.imageloader.core.api;

import com.sun.imageloader.core.ImageSettings;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ImageTaskListener extends ImageFailListenter {

	
	
	/**
	 * Listener method which is invoked before the image is being retrieved.
	 * 
	 * @param imageSettings_
	 * 			contains the object references necessary to load a {@link Bitmap} onto an {@link ImageView}
	 */
	public void preImageLoad(ImageSettings imageSettings_);
	
	/**
	 * Listener method to invoke when an image has been successfully loaded and displayed through an {@link ImageView}.
	 * 
	 * @param bitmap_
	 * 			that was successfully loaded from cache, the disk or a network call
	 * @param imageSettings_
	 * 			that contain the necessary objects needed to display the image onto an {@link ImageView}
	 */
	public void onImageLoadComplete(Bitmap bitmap_, ImageSettings imageSettings_);
	

	
}
