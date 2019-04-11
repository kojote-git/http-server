package com.jkojote.server.impl;

import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.bodies.ByteResponseBody;
import com.jkojote.server.bodies.EmptyResponseBody;

class Responses {
	static final HttpResponse NOT_FOUND;
	static final HttpResponse BAD_REQUEST;
	static final HttpResponse INTERNAL_ERROR;

	static {
		NOT_FOUND = HttpResponseBuilder.create()
			.setStatus(HttpStatus.NOT_FOUND)
			.setResponseBody(new ByteResponseBody("Not Found".getBytes()))
			.build();
		BAD_REQUEST = HttpResponseBuilder.create()
			.setStatus(HttpStatus.BAD_REQUEST)
			.setResponseBody(new ByteResponseBody("Bad Request".getBytes()))
			.build();
		INTERNAL_ERROR = HttpResponseBuilder.create()
			.setStatus(HttpStatus.INTERNAL_ERROR)
			.setResponseBody(new ByteResponseBody("Internal Error".getBytes()))
			.build();
	}

	private Responses() { }

}
