package com.jkojote.server.responses;

import com.jkojote.server.HttpStatus;
import com.jkojote.server.bodies.ByteResponseBody;
import com.jkojote.server.bodies.EmptyResponseBody;

public class StringHttpResponse extends AbstractHttpResponse {

	public StringHttpResponse(HttpStatus status, String str) {
		super(status);
		byte[] bytes = (str == null) ? null : str.getBytes();
		setBody(bytes == null ? EmptyResponseBody.INSTANCE : new ByteResponseBody(bytes));
		putHeader("Content-Type", "text/plain; charset=\"UTF-8\"");
		putHeader("Content-Length", "" + (bytes == null ? 0 : bytes.length));
	}

	public StringHttpResponse addHeader(String name, String value) {
		putHeader(name, value);
		return this;
	}
}
