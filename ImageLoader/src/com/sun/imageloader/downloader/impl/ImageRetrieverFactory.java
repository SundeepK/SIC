package com.sun.imageloader.downloader.impl;

import java.net.URI;

import com.sun.imageloader.downloader.api.ImageRetriever;
import com.sun.imageloader.imagedecoder.utils.L;

public final class ImageRetrieverFactory {

	private static final String TAG =  ImageRetrieverFactory.class.getName();
	private static ImageDownloader _imageLoader;
	
	private static synchronized ImageDownloader  getImageDownloader(URI imageUrl_, int maxRedirectCount_, int maxTimeOut_, int maxReadTimeOut_){
		if(_imageLoader == null){
			_imageLoader = new ImageDownloader(imageUrl_, maxRedirectCount_, maxTimeOut_, maxReadTimeOut_);
		}
		return _imageLoader;
	}
	
	/**
	 * Returns an instance of {@link ImageRetriever} to retrieve the image from either a network call or from the file directory
	 * 
	 * @param imageUrl_
	 * 			the image {@link URI} containing the path to the image
	 * @return
	 * 			an instance of {@link ImageRetriever} to fetch the image
	 */
	public static ImageRetriever getImageRetriever(URI imageUrl_){
		return getImageRetrieverFactory(imageUrl_, 5, 5000, 5000);
	}
	
	/**
	 * Returns an instance of {@link ImageRetriever} to retrieve the image from either a network call or from the file directory
	 * 
	 * @param imageUrl_
	 * 			the image {@link URI} containing the path to the image
	 * @param maxRedirectCount_
	 * 			the max number of redirects to follow
	 * @param maxTimeOut_
	 * 			the max timeout in ms before the connection is closed
	 * @param maxReadTimeOut_
	 * 			the max timeout in ms to read the data
	 * @return
	 * 			an instance of {@link ImageRetriever} to fetch the image
	 */
	public static ImageRetriever getImageRetriever(URI imageUrl_, int maxRedirectCount_, int maxTimeOut_, int maxReadTimeOut_){
		return getImageRetrieverFactory(imageUrl_, maxRedirectCount_, maxTimeOut_, maxReadTimeOut_);
	}
	
	private static ImageRetriever getImageRetrieverFactory(URI imageUrl_, int maxRedirectCount_, int maxTimeOut_, int maxReadTimeOut_){
		L.v(TAG, "Scheme detected is: " + imageUrl_.getScheme());
		switch (Scheme.matchScheme(imageUrl_.getScheme())) {
		case HTTP:
		case HTTPS:
			return getImageDownloader(imageUrl_, maxRedirectCount_, maxTimeOut_, maxReadTimeOut_);
		default:
			throw new UnsupportedOperationException(String.format("Unsupported URL", imageUrl_));
		}
				
	}
	
	
	
	
}
