package com.jkojote.server.responses;

import com.jkojote.server.HeaderName;
import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpResponseBody;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.bodies.EmptyResponseBody;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractHttpResponse implements HttpResponse {
	private Map<HeaderName, String> headers;
	private HttpResponseBody responseBody;
	private HttpStatus status;

	protected AbstractHttpResponse(HttpStatus status) {
		this.status = status;
		this.responseBody = EmptyResponseBody.INSTANCE;
		this.headers = new HashMap<>();
	}

	@Override
	public HttpStatus getStatus() {
		return status;
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
	public HttpResponseBody getResponseBody() {
		return responseBody;
	}

	protected void setBody(HttpResponseBody body) {
		this.responseBody = body;
	}

	protected void putHeader(String name, String value) {
		headers.put(HeaderName.of(name), value);
	}
}
