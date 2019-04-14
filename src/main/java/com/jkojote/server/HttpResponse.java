package com.jkojote.server;

/**
 * An object that holds a response
 */
public interface HttpResponse {

	/**
	 * @return status of response, never null
	 */
	HttpStatus getStatus();

	/**
	 * @return response headers, never null
	 */
	Iterable<HttpHeader> getHeaders();

	/**
	 * @param name name of the header
	 * @return 	header value associated with the name or null if
	 * 			the response doesnt have such a header
	 */
	String getHeader(String name);

	/**
	 * @return body of the response, never null.
	 */
	 HttpResponseBody getResponseBody();
}
