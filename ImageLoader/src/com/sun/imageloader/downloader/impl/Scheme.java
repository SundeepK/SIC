package com.sun.imageloader.downloader.impl;

import java.util.Locale;

import android.text.TextUtils;

public enum Scheme {

	HTTP("http"),
	HTTPS("https"),
	FILE("file"),
	UNKOWN("");
	
	private String _scheme;
	
	Scheme(String scheme_) {
		_scheme = scheme_;
	}
	
	public static Scheme matchScheme(String imageUrl_){
		if(!TextUtils.isEmpty(imageUrl_)){
			for(Scheme scheme : values()){
				if(scheme.isMatch(imageUrl_))
					return scheme;
			}
		}
		return UNKOWN;
		
	}

	
	private boolean isMatch(String imageUrl_){
		return	imageUrl_.toLowerCase(Locale.US).startsWith(_scheme);
	}

	
}
