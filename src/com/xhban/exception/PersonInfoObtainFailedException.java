package com.xhban.exception;

public class PersonInfoObtainFailedException extends Exception {
	private static final long serialVersionUID = 5L;
	private String mMessage = null;

	public PersonInfoObtainFailedException(String message) {
		super(message);
		mMessage = message;
	}

	@Override
	public String toString() {
		return mMessage;
	}
}
