package com.jkojote.server.impl;

import com.jkojote.server.HeaderName;
import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequestBody;
import com.jkojote.server.bodies.StreamRequestBody;
import com.jkojote.server.utils.IOUtils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HttRequestImplTest {
	private HttpRequestImpl request;

	public HttRequestImplTest() {
		String requestLine = "GET /hello/world HTTP/1.1";
		String path = "/hello/world";
		byte[] bodyBytes = "hello".getBytes();
		HttpRequestBody body = new StreamRequestBody(
			bodyBytes.length, new ByteArrayInputStream(bodyBytes)
		);
		Map<HeaderName, String> headers = new HashMap<>();
		headers.put(HeaderName.of("Hello"), "World");
		request = new HttpRequestImpl(
			requestLine, path, HttpMethod.GET,
			body, null, headers
		);
	}

	@Test
	public void getHeaders() {
		Iterable<HttpHeader> headers = request.getHeaders();
		Iterator<HttpHeader> iterator = headers.iterator();
		assertTrue(iterator.hasNext());
		HttpHeader header = iterator.next();
		assertEquals("Hello", header.getName());
		assertEquals("World", header.getValue());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void getBody() throws IOException  {
		HttpRequestBody body = request.getBody();
		byte[] actual = "hello".getBytes();
		assertEquals(actual.length, body.getContentLength());
		InputStream in = body.getInputStream();
		for (int i = 0; i < actual.length; i++) {
			assertEquals(actual[i], (byte) in.read());
		}
	}

	@Test
	public void getHeader() {
		assertEquals("World", request.getHeader("Hello"));
		assertEquals("World", request.getHeader("hello"));
	}

	@Test
	public void getQueryString() {
		assertNull(request.getQueryString());
	}
}
