package com.sun.imageloader.core.api;

import java.net.URI;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;

import com.sun.imageloader.core.ImageKey;

public class Settings {

	final private URI _url;
	final private ImageKey _imageKey;
	final private CompressFormat _compressformat;
	final private Config _bitmapConfig;
	final private int _imageQuality;
	/**
	 * Used to keep references to the {@link URI} and {@link ImageView} that will be used to display and retrieve {@link Bitmap} for
	 * images
	 * 
	 * @param url_
	 * @param imageKey_
	 */
	public Settings( URI url_, ImageView imageView_, ImageKey imageKey_, CompressFormat compressformat_, 
			Config bitmapConfig_, int imageQuality_){
		_url = url_;
		_imageKey = imageKey_;
		_compressformat = compressformat_;
		_bitmapConfig = bitmapConfig_;
		_imageQuality = imageQuality_;
	}
	
	public String getFinalFileName(){
		return _imageKey.getImageFilename() + "." + _compressformat.name();
	}
	
	public URI getUrl() {
		return _url;
	}
	
	public ImageKey getImageKey() {
		return _imageKey;
	}

	public CompressFormat getCompressformat() {
		return _compressformat;
	}

	public Config getBitmapConfig() {
		return _bitmapConfig;
	}

	public int getImageQuality() {
		return _imageQuality;
	}
	
	
}
