package com.sun.imageloader.imagedecoder.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import com.sun.imageloader.core.ImageSettings;

import android.graphics.Bitmap;

public interface ImageDecoder {

	
	public Bitmap decodeImage(File imageFile_, ImageSettings settings_, boolean shouldResizeForIO_)  throws IOException, URISyntaxException;
	
	public Bitmap decodeImage(InputStream inputStream_, ImageSettings settings_,  boolean shouldResizeForIO_) throws IOException, URISyntaxException;
	
}
