package com.sun.imageloader.core;


import java.net.URI;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;

import com.sun.imageloader.core.api.Settings;

public class ImageSettings  extends Settings{



	private final ImageView _imageView;
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
		return  _imageView;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _height;
		result = prime * result
				+ ((_imageView == null) ? 0 : _imageView.hashCode());
		result = prime * result
				+ (_shouldUseSampleSizeFromImageKey ? 1231 : 1237);
		result = prime * result + _width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageSettings other = (ImageSettings) obj;
		if (_height != other._height)
			return false;
		if (_imageView == null) {
			if (other._imageView != null)
				return false;
		} else if (!_imageView.equals(other._imageView))
			return false;
		if (_shouldUseSampleSizeFromImageKey != other._shouldUseSampleSizeFromImageKey)
			return false;
		if (_width != other._width)
			return false;
		return true;
	}
	
}
