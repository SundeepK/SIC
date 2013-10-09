SIC
===
SIC (Simple Image Cache for Android) is a simple light weight image caching library that will load images from a URL asynchronously.

## What does it do?
* Well aaah... it caches images for you in memory
* Built with multi threading in mind, it will retreive images from a network call or from the disk if it exists
* It's light weight so it doesn't intrude on your apps memory footprint

## Enhancements
* Support for loading from file system via a URI
* Cache gif's in memory
* Unit test's
* Stress test with large number or images
 
## Configuration
 
 **Default config**
 
 ``` java
public class MyActivity extends Activity {
	@Override
	public void onCreate() {
		super.onCreate();
		
		UrlImageLoaderConfiguration configs = new UrlImageLoaderConfiguration.Builder(getApplicationContext())
		.build();
		UrlImageLoader.getInstance().init(configs);
		UrlImageLoader sicImageLoader = UrlImageLoader.getInstance();
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
		
		UrlImageLoaderConfiguration configs = new UrlImageLoaderConfiguration.Builder(getApplicationContext())
		.setMaxCacheMemorySize(2) // 1 mb default
		.setDirectoryName("/storage/sdcard0/Pictures/cache") 
		// by default, a new directory will be created 
		// in the Pictures folder in the SD card if one exists, else internal sotrage is used
		.setImageQuality(60) 
		// default image quality is 100, lower settings are recomended for thumbnails
		.shouldLog(true) 
		// default is false, allows for some useful logging
		.setImageType(CompressFormat.JPEG) 
		// default compression type is JPEG
		.setImageConfig(Bitmap.Config.ARGB_4444) 
		// default is Bitmap.Config.ARGB_8888
		.setThreadExecutor(new ThreadPoolExecutor(4, 10, 120, TimeUnit.SECONDS,
		                new LinkedBlockingQueue<Runnable>()))
		// default is ThreadPoolExecutor(4, 6, 60, TimeUnit.SECONDS, 
		//new LinkedBlockingQueue<Runnable>())
		.useExternalStorage(true)
		// default is true
		.setOnloadingImage(new ColorDrawable(Color.Black)) 
		// default is a black Drawable,
		this is also used when nothing can be loaded
		.build(); 
		UrlImageLoader.getInstance().init(configs);
		UrlImageLoader sicImageLoader = UrlImageLoader.getInstance();
		sicImageLoader.displayImage("https://some.where.com/some_random_image.jpg", someImageView, 4);
		
	}
}
```
