package com.sun.imageloader.core.api;

public class FailedTaskReason{
	
	public enum ExceptionType {
		
		IOException,
		URISyntaxException,
		OutOfMemoryError,
		InterruptedException;
		
	}
	
	ExceptionType _exceptionType;
	Throwable _throwableOnException;

	/**
	 * Used to keep track of {@link Exception} and error states that occurred during image loading. Use this to handle specific exceptions that
	 * occurred.
	 * 
	 * @param exceptionType_
	 * 			identifies the {@link Exception} that occurred during the retrieval of the image
	 * @param throwableOnException_
	 * 			identifies the actual error that occurred
	 */
	public FailedTaskReason (ExceptionType exceptionType_, Throwable throwableOnException_){
		_exceptionType = exceptionType_;
		_throwableOnException = throwableOnException_;
	}
	
	public ExceptionType getExceptionType(){
		return _exceptionType;
	}
	
	public Throwable getThrowable(){
		return	_throwableOnException;
	}
	
}

