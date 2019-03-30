package com.jkojote.server;

public interface HttpResponse {

	HttpStatus getStatus();

	Iterable<HttpHeader> getHeaders();

	String getHeader(String name);

	HttpResponseBody getResponseBody();
}
