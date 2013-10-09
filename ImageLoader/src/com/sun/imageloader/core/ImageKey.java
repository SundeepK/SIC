package com.sun.imageloader.core;



public class ImageKey {

	final int _imageKey;
	final String _imageName;
	private int _sampleSize;
	
	/**
	 * {@link ImageKey} used to uniquely identify an image according to the URL provided.
	 * 
	 * @param imageKey_
	 * @param sampleSize_
	 */
	public ImageKey(int imageKey_, int sampleSize_){
		_sampleSize = sampleSize_;
		_imageKey = imageKey_;
		_imageName = new StringBuilder().append(imageKey_).append("|")
				.append(_sampleSize).toString();
	}

	public int key(){
		return _imageKey;
	}

	public String getImageFilename(){
		return _imageName;
	}

	@Override
	public String toString() {
		
		return "Image key: " +  _imageKey + ", with sample size: " + _sampleSize;
	}
	
	public int getSampleSize(){
		return _sampleSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _imageKey;
		result = prime * result + _sampleSize;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageKey other = (ImageKey) obj;
		if (_imageKey != other._imageKey)
			return false;
		if (_sampleSize != other._sampleSize)
			return false;
		return true;
	}

	
}
