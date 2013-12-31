package com.sun.imageloader.memorizer.api;

public class InterruptedImageLoadException extends Exception {

	private static final long serialVersionUID = 2220427131392491536L;

	public InterruptedImageLoadException() {
		super();
	}

	public InterruptedImageLoadException(String message) {
		super(message);
	}

	public InterruptedImageLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public InterruptedImageLoadException(Throwable cause) {
		super(cause);
	}
}
