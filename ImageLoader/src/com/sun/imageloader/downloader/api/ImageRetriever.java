package com.sun.imageloader.downloader.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;


public interface ImageRetriever {
	
	
	public InputStream getStream(URI imageUr_)  throws URISyntaxException, IOException;

}
