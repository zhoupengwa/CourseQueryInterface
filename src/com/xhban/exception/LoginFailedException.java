package com.xhban.exception;

public class LoginFailedException extends Exception {
	private static final long serialVersionUID = 3L;
	private String mMessage = null;

	public LoginFailedException(String message) {
		super(message);
		mMessage = message;
	}

	@Override
	public String toString() {

		return mMessage;
	}
}
