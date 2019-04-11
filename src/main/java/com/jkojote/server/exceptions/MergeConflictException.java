package com.jkojote.server.exceptions;

import com.jkojote.server.HttpMethod;

public class MergeConflictException extends RuntimeException {
	private HttpMethod method;
	private String pathTemplate;

	public MergeConflictException(String message, HttpMethod method, String pathTemplate) {
		super(message);
		this.method = method;
		this.pathTemplate = pathTemplate;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getPathTemplate() {
		return pathTemplate;
	}
}
