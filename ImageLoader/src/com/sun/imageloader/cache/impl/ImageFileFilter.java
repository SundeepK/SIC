package com.sun.imageloader.cache.impl;

import java.io.File;
import java.io.FilenameFilter;

import com.sun.imageloader.utils.L;

public final class ImageFileFilter implements FilenameFilter{

	private String[] _fileExtentionsArray;
	private static final  String TAG = ImageFileFilter.class.getName();
	
	public ImageFileFilter( String[] fileExtentionsarray_){
		
		if(fileExtentionsarray_ == null )
			throw new IllegalArgumentException("Passed in a null string array");
		
		_fileExtentionsArray = fileExtentionsarray_;
	}

	
	@Override
	public boolean accept(File dir, String filename) {
		for(String extention : _fileExtentionsArray){
			if(filename.endsWith(extention)){
				L.v(TAG , "File name" + filename + " extentin " + (extention));
				return true;
			}
		}
				
		return false;
	}



}
