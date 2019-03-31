package com.jkojote.server;

/**
 * An object that can handle one http request at instant
 */
public interface ControllerMethod {

	HttpResponse process(HttpRequest request, PathVariables variables);

}
