package com.sun.imageloader.utils;

import android.view.View;

import com.sun.imageloader.core.ImageKey;
import com.sun.imageloader.core.ImageSettings;

public final class ViewUtils {

	private static final String TAG = ViewUtils.class.getName();

	/**
	 * Utility method to check if a {@link View} is still valid by checking it's tag against the {@link ImageKey}
	 * 
	 * @param imageSettings_
	 * @return true if the {@link View} associated with a {@link ImageKey} is still valid
	 */
	public static boolean isViewStillValid(ImageSettings imageSettings_) {
		if (imageSettings_.getImageView().getTag().equals(imageSettings_.getImageKey())) {
			L.v(TAG, "View is still valid");
			return true;
		}else{
			L.v(TAG, "View is invalid now");
			return false;
		}
	}

}
