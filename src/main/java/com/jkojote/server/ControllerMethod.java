package com.jkojote.server;

/**
 * An object that processes http requests
 */
public interface ControllerMethod {

	HttpResponse process(HttpRequest request, PathVariables variables);

}
