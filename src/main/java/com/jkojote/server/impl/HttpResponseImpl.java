package com.jkojote.server.impl;

import com.jkojote.server.HeaderName;
import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpResponseBody;
import com.jkojote.server.HttpStatus;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HttpResponseImpl implements HttpResponse {
	private HttpStatus status;
	private Map<HeaderName, HttpHeader> headers;
	private HttpResponseBody responseBody;

	public HttpResponseImpl(HttpStatus status, Iterable<HttpHeader> headers, HttpResponseBody responseBody) {
		this.status = status;
		this.headers = StreamSupport.stream(headers.spliterator(), false)
			.collect(Collectors.toMap(header ->
				HeaderName.of(header.getName()),
				Function.identity())
			);
		this.responseBody = responseBody;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public Iterable<HttpHeader> getHeaders() {
		return headers.values();
	}

	@Override
	public String getHeader(String name) {
		HttpHeader header = headers.get(HeaderName.of(name));
		return header == null ? null : header.getValue();
	}

	@Override
	public HttpResponseBody getResponseBody() {
		return responseBody;
	}
}
