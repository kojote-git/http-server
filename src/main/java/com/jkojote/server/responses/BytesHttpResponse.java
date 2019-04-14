package com.jkojote.server.responses;

import com.jkojote.server.HttpResponseBody;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.bodies.ByteResponseBody;
import com.jkojote.server.bodies.EmptyResponseBody;

public class BytesHttpResponse extends AbstractHttpResponse {

	protected BytesHttpResponse(HttpStatus status, byte[] bytes) {
		super(status);
		HttpResponseBody body = (bytes == null) ? EmptyResponseBody.INSTANCE :
													new ByteResponseBody(bytes);
		int length = (bytes == null) ? 0 : bytes.length;
		setBody(body);
		putHeader("Content-Length", "" + length);
		putHeader("Content-Type", "application/octet-stream");
	}

	public BytesHttpResponse addHeader(String name, String value) {
		putHeader(name, value);
		return this;
	}

	public BytesHttpResponse addHeader(String name, long value) {
		putHeader(name, String.valueOf(value));
		return this;
	}

	public BytesHttpResponse addHeader(String name, Object value) {
		putHeader(name, value.toString());
		return this;
	}
}
