package com.sun.imageloader.downloader.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;


import android.net.Uri;

import com.sun.imageloader.downloader.api.ImageRetriever;

public class ImageDownloader implements ImageRetriever {

	private final int _maxRedirectCount;
	private final int _maxTimeOut;
	private final int _maxReadTimeOut;
	private final static int BUFFER_SIZE = 16 * 1024;

	
	public ImageDownloader(int maxRedirectCount_, int maxTimeOut_, int maxReadTimeOut_){
		_maxRedirectCount = maxRedirectCount_;
	    _maxTimeOut =	maxTimeOut_ ;
	    _maxReadTimeOut = maxReadTimeOut_;
	}

	
	@Override
	public InputStream getStream(URI imageUr_) throws URISyntaxException, IOException {
	
		return getImageFromNetwork(imageUr_);
	}

	private InputStream getImageFromNetwork(URI imageUri_) throws URISyntaxException, IOException {
		HttpURLConnection imageUrlConn = null;
		try {
			imageUrlConn = openConnection(imageUri_);
			int redirectionCount= 0;
			while(redirectionCount < _maxRedirectCount && imageUrlConn.getResponseCode() / 100 == 3 ){
				imageUrlConn = openConnection(new URI (imageUrlConn.getHeaderField("Location")));
	            redirectionCount++;
			}	
			imageUrlConn.connect();

		} catch (IOException e) {
			if (imageUrlConn != null) {
				consumeErrorStream(imageUrlConn);
			}
		}	
		return new BufferedInputStream(imageUrlConn.getInputStream(), BUFFER_SIZE);

	}
	
	/**
	 * Opens the connection to the {@link URI} specified 
	 * 
	 * @param imageUri_
	 * 			the {@link Uri} of the image to load
	 * @return
	 * 			{@link HttpURLConnection} instance created after openin the conection
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	protected HttpURLConnection openConnection(URI imageUri_) throws IOException, URISyntaxException{
		
		HttpURLConnection imageUrlConn = (HttpURLConnection) imageUri_.toURL().openConnection();
		imageUrlConn.setConnectTimeout(_maxTimeOut);
		imageUrlConn.setReadTimeout(_maxReadTimeOut);
		return imageUrlConn;
				
	}
	
	private void consumeErrorStream(HttpURLConnection connection_) throws IOException{
		
		try {
			InputStream errorStream = new BufferedInputStream(connection_.getInputStream(), BUFFER_SIZE);
			errorStream.close();
		} catch (IOException ex) {
			throw new IOException(ex);
		}
	}
	

}
