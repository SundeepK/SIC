package com.sun.imageloader.core;

public class ImagePreferences {

	private int _targetWidth;
	private int _targetHeight;
	private boolean _useExactTargetSize;
	
	public ImagePreferences(int targetWidth_, int targetHeight_, boolean  useExactTargetSize_){
		_targetWidth = targetWidth_;
		_targetHeight = targetHeight_;
		_useExactTargetSize = useExactTargetSize_;
	}
	
	
	public int getTargetWidth(){
		return _targetWidth;
	}
	
	public int getTargetHeight(){
		return _targetHeight;
	}
	
	public boolean shouldUseExactTargetSize(){
		return _useExactTargetSize;
	}
	
	
}
