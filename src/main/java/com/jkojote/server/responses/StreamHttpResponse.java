package com.jkojote.server.responses;

import com.jkojote.server.HttpResponseBody;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.bodies.EmptyResponseBody;
import com.jkojote.server.bodies.StreamResponseBody;

import java.io.InputStream;

public class StreamHttpResponse extends AbstractHttpResponse {

	protected StreamHttpResponse(HttpStatus status, InputStream in) {
		super(status);
		HttpResponseBody body = (in == null) ? EmptyResponseBody.INSTANCE :
												new StreamResponseBody(in);
		setBody(body);
		putHeader("Content-Type", "application/octet-stream");
	}

	public StreamHttpResponse addHeader(String name, String value) {
		putHeader(name, value);
		return this;
	}

	public StreamHttpResponse addHeader(String name, Object value) {
		putHeader(name, value.toString());
		return this;
	}

	public StreamHttpResponse addHeader(String name, long value) {
		putHeader(name, String.valueOf(value));
		return this;
	}
}
