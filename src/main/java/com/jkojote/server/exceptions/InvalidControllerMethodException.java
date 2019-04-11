package com.jkojote.server.exceptions;

import java.lang.reflect.Method;

public class InvalidControllerMethodException extends MappingException {
	private Method method;

	public InvalidControllerMethodException(String message, Method method) {
		super(message);
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}
}
