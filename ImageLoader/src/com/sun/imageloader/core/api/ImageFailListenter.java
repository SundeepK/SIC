package com.sun.imageloader.core.api;

import com.sun.imageloader.core.ImageSettings;

public interface ImageFailListenter {

	/**
	 * Listener method to invoke when an image was not successfully loaded. Override this to handle {@link Exception} that may have 
	 * occured during image loading.
	 * 
	 * @param failureReason_
	 * @param imageSettings_
	 */
	public void onImageLoadFail(FailedTaskReason failureReason_, ImageSettings imageSettings_);
	
}
