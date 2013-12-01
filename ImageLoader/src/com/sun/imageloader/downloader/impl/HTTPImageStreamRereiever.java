package com.sun.imageloader.downloader.impl;
//package com.sun.parallaxviewpage.imageretreiver.impl;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URISyntaxException;
//import java.net.URL;
//
//import com.sun.parallaxviewpage.imageretreiver.api.ImageRetriever;
//
//public class HTTPImageStreamRereiever implements ImageRetriever{
//
//	private int _maxTimeOut;
//	private int _maxReadTimeOut;
//	private final int _maxRedirectCount;
//	private static final int REDIRECT_CODE = 3;
//	private static final String  LOCATION = "Location";
//	protected final static int BUFFER_SIZE = 16 * 1024;
//
//	public HTTPImageStreamRereiever(int maxTimeOut_, int maxReadTimeOut_, int maxRedirectCount_) {
//		_maxTimeOut = maxTimeOut_;
//		_maxReadTimeOut = maxReadTimeOut_;
//		_maxRedirectCount =maxRedirectCount_;
//	}
//
//
//	@Override
//	public InputStream getStream(URL imageUrl_) throws URISyntaxException,
//			IOException {
//		return 	getInputStream(imageUrl_, 0);
//	}
//
//	
//	private InputStream getInputStream(URL imageUrl_, int redirectCount_) throws IOException{
//		HttpURLConnection imageUrlConnection = openConnection(imageUrl_);
//		imageUrlConnection.connect();
//		if((redirectCount_ < _maxRedirectCount )&& (imageUrlConnection.getResponseCode() / 100) == REDIRECT_CODE){
//			getInputStream(new URL(imageUrlConnection.getHeaderField(LOCATION)),redirectCount_++);
//		}
//		return new BufferedInputStream(imageUrlConnection.getInputStream(), BUFFER_SIZE);
//	}
//	
//    private HttpURLConnection openConnection(URL imageUrl_) throws IOException{
//    	HttpURLConnection imageUrlConn = (HttpURLConnection) imageUrl_.openConnection();
//		imageUrlConn.setConnectTimeout(_maxTimeOut);
//    	imageUrlConn.setReadTimeout(_maxReadTimeOut);
//    	return imageUrlConn;
//    }
//
//
//
//}
