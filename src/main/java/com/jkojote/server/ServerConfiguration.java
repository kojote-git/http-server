package com.jkojote.server;

/**
 * An infrastructure object that resolves methods mapped to different requests.
 * Briefly speaking, its main responsibility is to search for necessary method to
 * process a certain request. Also it provides means to configure what the client receives back
 * if some exceptional situation occurs.
 *
 * @see com.jkojote.server.impl.config.TreeServerConfiguration
 */
public interface ServerConfiguration {

	/**
	 * Resolves given request
	 * @param request request to be resolved
	 * @return resolution of the request or null if it is not configured to resolve this request
	 */
	RequestResolution resolveRequest(HttpRequest request);

	/**
	 * @param status error status
	 * @param errorData an object that holds an information about exceptional situation
	 * @return response that is sent to back to the client when some exceptional situation occurs
	 */
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
