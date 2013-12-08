package com.sun.imageloader.downloader.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;


import com.sun.imageloader.downloader.api.ImageRetriever;

public class ImageDownloader implements ImageRetriever {

	protected final URI _imageUri;
	
	protected final int _maxRedirectCount;
	protected final int _maxTimeOut;
	protected final int _maxReadTimeOut;
	protected final static int BUFFER_SIZE = 16 * 1024;

	
	public ImageDownloader(URI imageUr_, int maxRedirectCount_, int maxTimeOut_, int maxReadTimeOut_){
		_imageUri = imageUr_;
		_maxRedirectCount = maxRedirectCount_;
	    _maxTimeOut =	maxTimeOut_ ;
	    _maxReadTimeOut = maxReadTimeOut_;
	}

	
	@Override
	public InputStream getStream(URI imageUr_) throws URISyntaxException, IOException {
	
		return getImageFromNetwork(imageUr_);
	}

	private InputStream getImageFromNetwork(URI imageUri_) throws URISyntaxException, IOException {

		HttpURLConnection imageUrlConn = openConnection(imageUri_);
		int redirectionCount= 0;
		while(redirectionCount < _maxRedirectCount && imageUrlConn.getResponseCode() / 100 == 3 ){
			imageUrlConn = openConnection(new URI (imageUrlConn.getHeaderField("Location")));
            redirectionCount++;
		}	
		imageUrlConn.connect();
		
		return new BufferedInputStream(imageUrlConn.getInputStream(), BUFFER_SIZE);
		
	}
	
	protected HttpURLConnection openConnection(URI imageUri_) throws IOException, URISyntaxException{
		
		HttpURLConnection imageUrlConn = (HttpURLConnection) imageUri_.toURL().openConnection();
		imageUrlConn.setConnectTimeout(_maxTimeOut);
		imageUrlConn.setReadTimeout(_maxReadTimeOut);
		return imageUrlConn;
				
	}

}
