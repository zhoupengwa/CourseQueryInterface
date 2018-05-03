package com.xhban.exception;

public class CheckCodeObtainFailedException extends Exception {
	private String mMessage = null;
	private static final long serialVersionUID = 2L;

	public CheckCodeObtainFailedException(String message) {
		super();
		mMessage = message;
	}

	@Override
	public String toString() {
		return mMessage;
	}

}
