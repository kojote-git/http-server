package com.jkojote.server;

public interface HttpRequest {

	String getRequestLine();

	String getResourceUri();

	HttpMethod getMethod();

	Iterable<HttpHeader> getHeaders();

	String getHeader(String name);

	HttpRequestBody getBody();

	QueryString getQueryString();
}
