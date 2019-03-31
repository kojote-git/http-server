package com.jkojote.server.bodies;

import com.jkojote.server.HttpResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteResponseBody implements HttpResponseBody {
	private byte[] bytes;

	public ByteResponseBody(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(bytes);
	}
}
