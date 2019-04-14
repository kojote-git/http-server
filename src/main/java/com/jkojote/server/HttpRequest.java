package com.jkojote.server;

/**
 * An object that holds all necessary information about http request to be later processed by
 * controllers.
 */
public interface HttpRequest {

	/**
	 * @return request line that consists of http method, path and http version
	 */
	String getRequestLine();

	/**
	 * @return requested resource without specifying query string
	 */
	String getPath();

	/**
	 * @return http method of this request
	 */
	HttpMethod getMethod();

	/**
	 * @return all headers of this request
	 */
	Iterable<HttpHeader> getHeaders();

	/**
	 * @param name name of the header
	 * @return 	header value associated with given name or null if the request doesn't
	 * 			have such a header
	 */
	String getHeader(String name);

	/**
	 * @return 	body of the request that is never null
	 * 			regardless if the request has actual body or not
	 */
	HttpRequestBody getBody();

	/**
	 * @return 	query string that that is never null regardless
	 * 			if the request has a query string or not
	 */
	QueryString getQueryString();
}
