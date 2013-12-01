package com.sun.imageloader.core;

import java.net.URI;

import com.sun.imageloader.core.api.Settings;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;

public class ImageSettings  extends Settings{

	private final  ImageView _imageView;

	private final int _width;
	private final int _height;
	private final boolean  _shouldUseSampleSizeFromImageKey;
	


	/**
	 * Used to keep references to the {@link URI} and {@link ImageView} that will be used to display and retrieve {@link Bitmap} for
	 * images
	 * 
	 * @param url_
	 * @param imageView_
	 * @param imageKey_
	 */
	public ImageSettings( URI url_, ImageView imageView_, ImageKey imageKey_, CompressFormat compressformat_, 
			Config bitmapConfig_, int imageQuality_, int width_, int height_, boolean shouldUseSampleSizeFromImageKey_){
		super(url_, imageView_, imageKey_, compressformat_, bitmapConfig_, imageQuality_);
		_imageView = imageView_;
		_width = width_;
		_height = height_;
		_shouldUseSampleSizeFromImageKey = shouldUseSampleSizeFromImageKey_;
	}
	
	/**
	 * Used to keep references to the {@link URI} and {@link ImageView} that will be used to display and retrieve {@link Bitmap} for
	 * images
	 * 
	 * @param url_
	 * @param imageView_
	 * @param imageKey_
	 */
	public ImageSettings( URI url_, ImageView imageView_, ImageKey imageKey_, CompressFormat compressformat_, 
			Config bitmapConfig_, int imageQuality_, int width_, int height_){
		super(url_, imageView_, imageKey_, compressformat_, bitmapConfig_, imageQuality_);
		_imageView = imageView_;
		_width = width_;
		_height = height_;
		_shouldUseSampleSizeFromImageKey = true;
	}
	
	/**
	 * Used to keep references to the {@link URI} and {@link ImageView} that will be used to display and retrieve {@link Bitmap} for
	 * images
	 * 
	 * @param url_
	 * @param imageView_
	 * @param imageKey_
	 */
	public ImageSettings( URI url_, ImageView imageView_, ImageKey imageKey_, CompressFormat compressformat_, 
			Config bitmapConfig_, int imageQuality_){
		super(url_, imageView_, imageKey_, compressformat_, bitmapConfig_, imageQuality_);
		_imageView = imageView_;
		_width = 0;
		_height = 0;
		_shouldUseSampleSizeFromImageKey = true;
	}
	
	public ImageView getImageView() {
		return _imageView;
	}

	public int getDestWidth() {
		return _width;
	}

	public int getDestHeight() {
		return _height;
	}
	
	public boolean shouldUseSampleSizeFromImageKey() {
		return _shouldUseSampleSizeFromImageKey;
	}
}
