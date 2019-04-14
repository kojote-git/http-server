package com.jkojote.server;

/**
 * An object that processes http requests in functional style
 */
public interface FunctionalResponse {

	/**
	 * @param request the request to be processed
	 * @param variables path variables that are associated with given request
	 * @return response as the result of processing given request
	 */
	HttpResponse process(HttpRequest request, PathVariables variables);

}
