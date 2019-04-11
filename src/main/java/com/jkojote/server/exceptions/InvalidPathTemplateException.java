package com.jkojote.server.exceptions;

public class InvalidPathTemplateException extends MappingException {
	private String pathTemplate;

	public InvalidPathTemplateException(String message, String pathTemplate) {
		super(message);
		this.pathTemplate = pathTemplate;
	}

	public String getPathTemplate() {
		return pathTemplate;
	}
}
