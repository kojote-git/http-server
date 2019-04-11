package com.jkojote.server.bodies;

import com.jkojote.server.HttpResponseBody;

import java.io.InputStream;

public class StreamResponseBody implements HttpResponseBody {
	private InputStream in;

	public StreamResponseBody(InputStream in) {
		this.in = in;
	}

	@Override
	public InputStream getInputStream() {
		return in;
	}
}
