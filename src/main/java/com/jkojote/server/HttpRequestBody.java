package com.jkojote.server;

import java.io.InputStream;

public interface HttpRequestBody {

	long getContentLength();

	InputStream getInputStream();

	default <T> T read(BodyReader<T> reader) {
		return reader.read(this);
	}

	interface BodyReader<T> {
		T read(HttpRequestBody requestBody);
	}
}
