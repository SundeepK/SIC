package com.sun.imageloader.cache.impl;

import java.io.File;
import java.io.FilenameFilter;

public final class ImageFileFilter implements FilenameFilter{

	private String[] _fileExtentionsArray;
	
	public ImageFileFilter( String[] fileExtentionsarray_){
		
		if(fileExtentionsarray_ == null )
			throw new IllegalArgumentException("Passed in a null string array");
		
		_fileExtentionsArray = fileExtentionsarray_;
	}

	
	@Override
	public boolean accept(File dir, String filename) {
		
		for(String extention : _fileExtentionsArray){
			return filename.toLowerCase().endsWith(extention);
		}
				
		return false;
	}



}
