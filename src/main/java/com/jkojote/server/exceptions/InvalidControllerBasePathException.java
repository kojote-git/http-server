package com.jkojote.server.exceptions;

public class InvalidControllerBasePathException extends MappingException {
	private String basePath;

	public InvalidControllerBasePathException(String message, String basePath) {
		super(message);
		this.basePath = basePath;
	}

	public String getBasePath() {
		return basePath;
	}
}
