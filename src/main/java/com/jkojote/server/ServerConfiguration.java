package com.jkojote.server;

public interface ServerConfiguration {

	RequestResolution resolveRequest(HttpRequest request);

	HttpResponse getResponseOnError(HttpStatus status, ErrorData errorData);

	interface RequestResolution {

		ControllerMethod getMethod();

		PathVariables getPathVariables();
	}

	interface ErrorData {

		String getMessage();

		Object getProperty(String name);

		boolean hasProperty(String name);
	}
}
