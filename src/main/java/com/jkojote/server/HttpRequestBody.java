package com.jkojote.server;

import java.io.InputStream;

/**
 * An object that holds a body of http request
 */
public interface HttpRequestBody {

	/**
	 * Content length is specified by Content-Length header of a request
	 * @return length of the body in bytes or 0 if body is empty
	 */
	long getContentLength();

	/**
	 * @return a stream through which the body can be read or empty stream if body is empty
	 */
	InputStream getInputStream();

	/**
	 * Reads the body into an object using given reader
	 * @param reader reader by which the body is read
	 * @return an object, read by the reader
	 */
	default <T> T read(BodyReader<T> reader) {
		return reader.read(this);
	}

	/**
	 * A utility interface that encapsulates the functionality of reading request body and
	 * converting the contents of the body into other object
	 * @param <T> type of an object that a body is converted into
	 */
	interface BodyReader<T> {

		T read(HttpRequestBody requestBody);

	}
}
