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
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import com.sun.imageloader.core.ImageSettings;
import com.sun.imageloader.imagedecoder.api.ImageDecoder;
import com.sun.imageloader.imagedecoder.utils.L;

public class SimpleImageDecoder implements ImageDecoder {
	private static final String TAG = SimpleImageDecoder.class.getName();
	private Rect _srcRect = new Rect();
	private Rect _destRect = new Rect();
	
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
		L.v(TAG, "Destination height is:" + settings_.getDestHeight());

		Options options = getDecodeOptions(settings_);
		try {
		
			if(shouldResizeForIO_){
				if(settings_.getDestHeight() > 0 && settings_.getDestWidth() > 0){
					options.inJustDecodeBounds = true;
					decodeImageStream(bitmapStream_, options);	//we only want to get the image width/height here since options.inJustDecodeBounds = true
					bmp = resizeBitmapToFitDest(bitmapStream_, options, settings_);
				
				}else{
					bmp =	decodeImageStream( bitmapStream_, options);		
				}
			}else{
				bmp = BitmapFactory.decodeStream(bitmapStream_);
			}

		} finally {

			bitmapStream_.close();
		}

		return bmp;

	}
	
	/**
	 * Will resize the {@link Bitmap} passed into the parameter and attempt to fit it into the destination width and height 
	 * associated with the image. This will override the sampleSize passed into the {@link ImageSettings} since we want 
	 * to fit the {@link Bitmap} onto the destination resolution.
	 * 
	 * @param bitmapStream_ the {@link java.io.InputStream} of the  {@link android.graphics.Bitmap} image to decode
     * @param options_ the {@link android.graphics.BitmapFactory.Options} associated with the {@link android.graphics.Bitmap}
     * @param settings_ the {@link com.sun.imageloader.core.ImageSettings} associated with an image which contains the decode settings
     * @throws IOException
	 */
	private Bitmap resizeBitmapToFitDest(InputStream bitmapStream_, Options options_, ImageSettings settings_) throws IOException{
		bitmapStream_.reset();
		
		if(!settings_.shouldUseSampleSizeFromImageKey()){
			int sampleSize = getNewSampleSize(options_, settings_);
			options_.inSampleSize = sampleSize;
		}

		L.v(TAG, "Sample size after considering destination is:" + options_);
		options_.inJustDecodeBounds = false;
		Bitmap bitmapDest_ = decodeImageStream(bitmapStream_, options_); //actually decode the image here
		
		Matrix m = new Matrix();
	    RectF inRect = new RectF(0, 0, bitmapDest_.getWidth(), bitmapDest_.getHeight());
	    RectF outRect = new RectF(0, 0, settings_.getDestWidth(), settings_.getDestHeight());
	    m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
	    float[] values = new float[9];
	    m.getValues(values);

	    // resize bitmap
	    return  Bitmap.createScaledBitmap(bitmapDest_, (int) (bitmapDest_.getWidth() * values[0])
	    		, (int) (bitmapDest_.getHeight() * values[4]), true);
		

	}
	
	private int getNewSampleSize(Options options_,ImageSettings settings_){
		L.v(TAG, "Options out width: " + options_.outWidth);
		return  (int) Math.max(Math.ceil(options_.outHeight/settings_.getDestHeight()),
				Math.ceil(options_.outWidth/settings_.getDestWidth()));
	}
	
	private Bitmap decodeImageStream(InputStream bitmapStream_, Options options_){
		return BitmapFactory.decodeStream(bitmapStream_, null, options_);	
	}
	
	private Options getDecodeOptions(ImageSettings imageSettings_){
	 	Options options = new Options();
		int sampleSize  =  imageSettings_.getImageKey().getSampleSize();
		options.inSampleSize = sampleSize;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;	
		L.v(TAG, "Sample size used for decode is: " + sampleSize);
		return options;

	}

}
