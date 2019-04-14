package com.jkojote.server.exceptions;

public class InvalidMappingException extends RuntimeException {

	public InvalidMappingException(String message) {
		super(message);
	}

	public InvalidMappingException(String message, Throwable cause) {
		super(message, cause);
	}
}
