package com.sun.imageloader.memorizer.api;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;

import com.sun.imageloader.core.ImageKey;
import com.sun.imageloader.core.ImageSettings;

public class BitmapMemorizer extends AMemorizer<ImageSettings, Bitmap> {

	public BitmapMemorizer(Computable<ImageSettings, Bitmap> computable_, ConcurrentHashMap<Integer, ImageKey> viewKeyMap_) {
		super(computable_, viewKeyMap_);
	}

	
	
	@Override
	protected Callable<Bitmap> getCallable(ImageSettings computable_) {
		
		return null;
	}

}
