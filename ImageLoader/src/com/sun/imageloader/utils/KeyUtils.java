package com.sun.imageloader.utils;

import java.net.URI;

import com.sun.imageloader.core.ImageKey;

public final class  KeyUtils {
	private static final String TAG = KeyUtils.class.getName();
	
	public static String getSanitizedImageName(URI url_){
		String path = url_.getPath().replaceAll("[^a-zA-Z]+", "_");	
		L.v(TAG, "Path used is: " + path);
		return path;
	}
	
	public static int getPathKey(URI url_){
		int key  = url_.getPath().hashCode();
		return key;
	}
	
	public static ImageKey createImageKey(String filename_){
		String[] parts = filename_.split("[|.]");
		int ikey = Integer.parseInt(parts[0]); 
		L.v(TAG, "" + ikey);
		int sampleSize = Integer.parseInt(parts[1]);
		L.v(TAG, "" + sampleSize);
		ImageKey key = new ImageKey(ikey, sampleSize);
		L.v(TAG, key.toString());
		return  key;
	}
	
	
}
