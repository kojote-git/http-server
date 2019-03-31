package com.jkojote.server;

public interface RequestResolver {

	ResolvedRequest resolveRequest(HttpRequest request);

	interface ResolvedRequest {

		ControllerMethod getMethod();

		PathVariables getVariables();
	}

}
