package com.xhban.exception;

public class LoadFailedException extends Exception {
	private static final long serialVersionUID = 1L;
	private String mMessage = null;

	public LoadFailedException(String message) {
		super(message);
		mMessage = message;
	}

	@Override
	public String toString() {
		return mMessage;
	}
}
