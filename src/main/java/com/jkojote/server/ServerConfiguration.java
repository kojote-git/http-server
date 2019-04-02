package com.jkojote.server;

public interface ServerConfiguration {

	RequestResolution resolveRequest(HttpRequest request);

	HttpResponse getResponseOnError(HttpStatus status, String errorMessage);

	interface RequestResolution {

		ControllerMethod getMethod();

		PathVariables getPathVariables();
	}

}
