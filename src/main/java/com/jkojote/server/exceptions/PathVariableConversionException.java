package com.jkojote.server.exceptions;

public class PathVariableConversionException extends RuntimeException {
	private String actualValue;

	public PathVariableConversionException(Throwable cause, String message, String actualValue) {
		super(message, cause);
		this.actualValue = actualValue;
	}

	public String getActualValue() {
		return actualValue;
	}
}
