package com.jkojote.server.impl;

import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;

class Responses {
	static final HttpResponse NOT_FOUND;
	static final HttpResponse BAD_REQUEST;
	static final HttpResponse INTERNAL_ERROR;

	static {
		NOT_FOUND = HttpResponseBuilder.create()
			.setStatus(HttpStatus.NOT_FOUND)
			.build();
		BAD_REQUEST = HttpResponseBuilder.create()
			.setStatus(HttpStatus.BAD_REQUEST)
			.build();
		INTERNAL_ERROR = HttpResponseBuilder.create()
			.setStatus(HttpStatus.INTERNAL_ERROR)
			.build();
	}

	private Responses() { }

}
