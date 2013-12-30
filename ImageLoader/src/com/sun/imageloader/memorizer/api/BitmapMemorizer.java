package com.sun.imageloader.memorizer.api;

import java.util.concurrent.Callable;

import android.graphics.Bitmap;

import com.sun.imageloader.core.ImageSettings;

public class BitmapMemorizer extends AMemorizer<ImageSettings, Bitmap> {

	public BitmapMemorizer(Computable<ImageSettings, Bitmap> computable_) {
		super(computable_);
	}

	@Override
	protected Callable<Bitmap> getCallable(ImageSettings computable_) {
		
		return null;
	}

}
