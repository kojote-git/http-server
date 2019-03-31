package com.jkojote.server.bodies;

import com.jkojote.server.HttpRequestBody;

import java.io.InputStream;

public class StreamRequestBody implements HttpRequestBody {
	private InputStream in;
	private long contentLength;

	public StreamRequestBody(long contentLength, InputStream in) {
		this.contentLength = contentLength;
		this.in = in;
	}

	@Override
	public long getContentLength() {
		return contentLength;
	}

	@Override
	public InputStream getInputStream() {
		return in;
	}
}
