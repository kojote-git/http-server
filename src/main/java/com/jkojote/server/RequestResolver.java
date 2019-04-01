package com.jkojote.server;

public interface RequestResolver {

	RequestResolution resolveRequest(HttpRequest request);

	interface RequestResolution {

		ControllerMethod getMethod();

		PathVariables getPathVariables();
	}

}
