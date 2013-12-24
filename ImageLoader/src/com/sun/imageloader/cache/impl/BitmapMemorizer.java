package com.sun.imageloader.cache.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import android.graphics.Bitmap;
import android.media.Image;

import com.sun.imageloader.cache.api.AMemorizer;
import com.sun.imageloader.core.ImageKey;
import com.sun.imageloader.core.api.Computable;

public class BitmapMemorizer extends AMemorizer<ImageKey, Bitmap> {

	@Override
	protected Callable<Bitmap> getCallable(ImageKey computable_) {
		
		return null;
	}

}
