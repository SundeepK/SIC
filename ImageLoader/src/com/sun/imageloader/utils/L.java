package com.sun.imageloader.utils;

import android.util.Log;

public final class L {
	//check this to enable logging
	private static boolean SHOULD_LOG = false;

	public static void shouldLog(boolean shouldLog_){
		SHOULD_LOG = shouldLog_;
	}
	
	public static void v(String tag_, String message_){
		if(SHOULD_LOG)
			Log.v(tag_, message_);
	}
	
	public static void e(String tag_, String message_){
		if(SHOULD_LOG)
			Log.e(tag_, message_);
	}
	
	public static void w(String tag_, String message_){
		if(SHOULD_LOG)
			Log.w(tag_, message_);
	}
	
}
