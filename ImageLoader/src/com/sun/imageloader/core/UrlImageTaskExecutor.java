package com.sun.imageloader.core;


import java.util.concurrent.ThreadPoolExecutor;

public class UrlImageTaskExecutor {
	
	UrlImageLoaderConfiguration _UrlImageLoaderConfig;
	private ThreadPoolExecutor _imageLoadExecutor;
	
	/**
	 * Task executor used to run {@link Thread} to retrieve and load images
	 * 
	 * @param configuration_
	 * 			containing a reference to a {@link ThreadPoolExecutor} used to execute {@link Runnable} for retreive and loading images
	 */
	public UrlImageTaskExecutor(UrlImageLoaderConfiguration configuration_) {
		
		_imageLoadExecutor =  configuration_._threadPoolForTaskExecutor;
		_imageLoadExecutor.allowCoreThreadTimeOut(true);
	}


	public void sumbitTask(Runnable task_){
		_imageLoadExecutor.execute(task_);
	}
	
}
