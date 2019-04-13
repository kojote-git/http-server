package com.jkojote.server.exceptions;

public class PathVariableFormatException extends RuntimeException {
	private String pathVariable;
	private String actualValue;

	public PathVariableFormatException(Throwable cause, String message, String pathVariable, String actualValue) {
		super(message, cause);
		this.pathVariable = pathVariable;
		this.actualValue = actualValue;
	}

	public String getActualValue() {
		return actualValue;
	}

	public String getPathVariable() {
		return pathVariable;
	}
}
