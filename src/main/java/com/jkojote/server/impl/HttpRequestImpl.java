package com.jkojote.server.impl;

import com.jkojote.server.HeaderName;
import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpRequestBody;
import com.jkojote.server.QueryString;

import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class HttpRequestImpl implements HttpRequest {
	private String requestLine;
	private String path;
	private HttpMethod method;
	private Map<HeaderName, String> headers;
	private HttpRequestBody requestBody;
	private QueryString queryString;

	HttpRequestImpl(String requestLine,
						   String path,
						   HttpMethod method,
						   HttpRequestBody requestBody,
						   QueryString queryString,
						   Map<HeaderName, String> headers) {
		this.requestLine = requestLine;
		this.requestBody = requestBody;
		this.path = path;
		this.method = method;
		this.queryString = queryString;
		this.headers = headers;
	}

	@Override
	public String getRequestLine() {
		return requestLine;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public HttpMethod getMethod() {
		return method;
	}

	@Override
	public Iterable<HttpHeader> getHeaders() {
		return headers.entrySet().stream()
			.map(e -> HttpHeader.of(e.getKey().getValue(), e.getValue()))
			.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public String getHeader(String name) {
		return headers.get(HeaderName.of(name));
	}

	@Override
	public HttpRequestBody getBody() {
		return requestBody;
	}

	@Override
	public QueryString getQueryString() {
		return queryString;
	}
}
