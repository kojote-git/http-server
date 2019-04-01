package com.jkojote.server.impl;

class MalformedResponseException extends RuntimeException {
	public MalformedResponseException() {
	}

	public MalformedResponseException(String message) {
		super(message);
	}

	public MalformedResponseException(String message, Throwable cause) {
		super(message, cause);
	}

	public MalformedResponseException(Throwable cause) {
		super(cause);
	}

	public MalformedResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
