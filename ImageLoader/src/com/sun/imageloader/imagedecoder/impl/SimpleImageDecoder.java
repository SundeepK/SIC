package com.sun.imageloader.imagedecoder.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.sun.imageloader.core.ImageSettings;
import com.sun.imageloader.imagedecoder.api.ImageDecoder;
import com.sun.imageloader.imagedecoder.utils.L;

public class SimpleImageDecoder implements ImageDecoder {
	private static final String TAG = SimpleImageDecoder.class.getName();

	public SimpleImageDecoder() {

	}

	@Override
	public Bitmap decodeImage(File imageFile_, ImageSettings settings_,  boolean shouldResizeForIO_) throws IOException, URISyntaxException {
		return decodeImage(new BufferedInputStream(new FileInputStream(
				imageFile_)), settings_,  shouldResizeForIO_);
	}
	

	@Override
	public Bitmap decodeImage(InputStream bitmapStream_, ImageSettings settings_,  boolean shouldResizeForIO_) throws IOException, URISyntaxException {
		Bitmap bmp = null;
		try {
		
			if(shouldResizeForIO_){
				Options options = new Options();
				int sampleSize  =  settings_.getImageKey().getSampleSize();
				options.inSampleSize = sampleSize;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;	
				L.v(TAG, "Sample size used for decode is: " + sampleSize);
				bmp = BitmapFactory. decodeStream(bitmapStream_, null, options);		
			}else{
				bmp = BitmapFactory.decodeStream(bitmapStream_);
			}

		} finally {

			bitmapStream_.close();
		}

		return bmp;

	}

}
