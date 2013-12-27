package com.sun.imageloader.cache.impl;

import java.util.concurrent.Callable;

import android.graphics.Bitmap;

import com.sun.imageloader.cache.api.AMemorizer;
import com.sun.imageloader.core.ImageSettings;
import com.sun.imageloader.core.api.Computable;

public class BitmapMemorizer extends AMemorizer<ImageSettings, Bitmap> {

	public BitmapMemorizer(Computable<ImageSettings, Bitmap> computable_) {
		super(computable_);
	}

	@Override
	protected Callable<Bitmap> getCallable(ImageSettings computable_) {
		
		return null;
	}

}
