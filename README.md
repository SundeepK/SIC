SIC
===
SIC (Simple Image Cache for Android) is a simple light weight image caching library that will load images from a URL asynchronously. It aims to maximize cncurrency and provide an easy interface to load images to an ImageView.


## What does it do?
* Well aaah... it caches images for you in memory
* Built with multi threading in mind, it will retreive images from a network call or from the disk if it exists
* It's light weight 

## Enhancements
* Support for loading from file system via a URI
* Cache gif's in memory
* Unit test's
* Stress test with large number or images

Currently working on:
* Converting to gradle project

## Dependencies
* SIC has a dependencey on the ConcurrentLinkedHashmap implementation provided by this [project](https://code.google.com/p/concurrentlinkedhashmap/). I have added a jar file to downloads folder for ease of use.

 
## Configuration
 
 **Default config**
 
 ``` java
public class MyActivity extends Activity {
	@Override
	public void onCreate() {
		super.onCreate();
		
		UrlImageLoaderConfiguration configs = new UrlImageLoaderConfiguration.Builder()
		.build(this);
		UrlImageLoader sicImageLoader = new UrlImageLoader(configs);
		sicImageLoader.displayImage("https://some.where.com/random_image.jpg", someImageView, 4); 
		// param's: the URL, ImageView, sampleSize
	}
}

```

 **Custom builder**
 ``` java
public class MyActivity extends Activity {
	@Override
	public void onCreate() {
		super.onCreate();
		
		UrlImageLoaderConfiguration configs = new UrlImageLoaderConfiguration.Builder()
		.setMaxCacheMemorySize(5) // 1 mb default
		
		// by default, a new directory will be created 
		// in the Pictures folder in the SD card if one exists, else internal sotrage is used
		.setDirectoryName("/storage/sdcard0/Pictures/cache") 
		
		// default image quality is 100
		.setImageQuality(60) 
		
		// default is false, allows for some useful logging
		.shouldLog(true) 
		
		.setImageType(CompressFormat.JPEG) // default compression type is JPEG
		
		.setImageConfig(Bitmap.Config.ARGB_4444) // default is Bitmap.Config.ARGB_8888
		
		// default is ThreadPoolExecutor(4, 6, 60, TimeUnit.SECONDS, 
		//new LinkedBlockingQueue<Runnable>())
		.setThreadExecutor(new ThreadPoolExecutor(4, 10, 120, TimeUnit.SECONDS,
		                new LinkedBlockingQueue<Runnable>()))

		.useExternalStorage(true) // default is true
	
		// default is a black Drawable, this is also used when nothing can be loaded
		.setOnloadingImage(new ColorDrawable(Color.Black)) 
	
		// allows you to set the max time an image file will remain in the disk memory
		// default of zero doesn't remove old image files
		// the below will remove images older than 1 day
		.setMaxDeleteTime(1, TimeUnit.DAYS)
		
		.build(this); 
		
		UrlImageLoader sicImageLoader = new UrlImageLoader(configs);
		sicImageLoader.displayImage("https://some.where.com/some_random_image.jpg", someImageView, 4);
		
		ListView listView = (ListView) findViewById(R.id.listView);
        	listView.setAdapter(new CustomAdapter());
		listView.setOnScrollListener(new OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
				
				     //This will prevent images from being loaded if a fling is detected
				     sicImageLoader.onScrollStateChanged(view, scrollState);
				     
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
				}
			});
	}
}
```
