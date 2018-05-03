package com.xhban.exception;

public class CourseInfoObtainFailedException extends Exception {

	private static final long serialVersionUID = 4L;
	private String mMessage = null;

	public CourseInfoObtainFailedException(String message) {
		super(message);
		mMessage = message;
	}

	@Override
	public String toString() {
		return mMessage;
	}

}
