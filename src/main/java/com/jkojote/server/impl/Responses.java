package com.jkojote.server.impl;

import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;

class Responses {
	static final HttpResponse NOT_FOUND;
	static final HttpResponse BAD_REQUEST;

	static {
		NOT_FOUND = HttpResponseBuilder.create()
			.setStatus(HttpStatus.of(404, "Not Found"))
			.addHeader(HttpHeader.of("Content-Type", "text/plain; charset=utf-8"))
			.build();
		BAD_REQUEST = HttpResponseBuilder.create()
			.setStatus(HttpStatus.of(400, "Bad Request"))
			.addHeader(HttpHeader.of("Content-Type", "text/plain; charset=utf-8"))
			.build();
	}

	private Responses() { }

}
