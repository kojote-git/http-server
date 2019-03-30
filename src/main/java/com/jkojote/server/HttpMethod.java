package com.jkojote.server;

public enum HttpMethod {
	GET("GET"), PUT("PUT"), PATCH("PATCH"), DELETE("DELETE"),
	POST("POST"), OPTIONS("OPTIONS"), HEAD("HEAD"),
	TRACE("TRACE"), CONNECT("CONNECT");

	private String stringRepresentation;

	HttpMethod(String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
	}

	@Override
	public String toString() {
		return stringRepresentation;
	}
}
