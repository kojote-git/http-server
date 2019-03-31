package com.jkojote.server.impl;

import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpResponseBody;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.bodies.EmptyResponseBody;

import java.util.ArrayList;
import java.util.Collection;

public class HttpResponseBuilder {
	private Collection<HttpHeader> headers;
	private HttpStatus status;
	private HttpResponseBody responseBody;

	private HttpResponseBuilder() {
		this.headers = new ArrayList<>();
	}

	public static HttpResponseBuilder create() {
		return new HttpResponseBuilder();
	}

	public HttpResponseBuilder addHeader(HttpHeader header) {
		headers.add(header);
		return this;
	}

	public HttpResponseBuilder addHeader(String name, String value) {
		headers.add(HttpHeader.of(name, value));
		return this;
	}

	public HttpResponseBuilder setStatus(HttpStatus status) {
		this.status = status;
		return this;
	}

	public HttpResponseBuilder setResponseBody(HttpResponseBody responseBody) {
		this.responseBody = responseBody;
		return this;
	}

	public HttpResponse build() {
		if (responseBody == null) {
			responseBody = EmptyResponseBody.INSTANCE;
		}
		return new HttpResponseImpl(status, headers, responseBody);
	}
}
