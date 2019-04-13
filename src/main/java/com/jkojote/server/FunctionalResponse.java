package com.jkojote.server;

/**
 * An object that processes http requests
 */
public interface FunctionalResponse {

	HttpResponse process(HttpRequest request, PathVariables variables);

}
