package com.jkojote.server.bodies;

import com.jkojote.server.HttpResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public final class EmptyResponseBody implements HttpResponseBody {
	public static final EmptyResponseBody INSTANCE = new EmptyResponseBody();
	private byte[] body = new byte[0];

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(body);
	}
}
