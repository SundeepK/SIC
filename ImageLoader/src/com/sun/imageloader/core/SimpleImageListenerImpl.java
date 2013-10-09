package com.sun.imageloader.core;

import android.graphics.Bitmap;

import com.sun.imageloader.core.api.FailedTaskReason;
import com.sun.imageloader.core.api.ImageTaskListener;

public class SimpleImageListenerImpl implements ImageTaskListener{

	@Override
	public void onImageLoadComplete(Bitmap bitmap_,
			ImageSettings imageSettings_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onImageLoadFail(FailedTaskReason failureReason_, ImageSettings imageSettings_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preImageLoad(ImageSettings imageSettings_) {
		// TODO Auto-generated method stub
	}


}
