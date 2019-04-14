package com.jkojote.server;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class HttpHeaderTest {

	@Test
	public void equals_testEquality() {
		HttpHeader h1 = HttpHeader.of("content-length", "1");
		HttpHeader h2 = HttpHeader.of("Content-Length", "1");
		HttpHeader h3 = HttpHeader.of("Content-Length", "2");

		assertEquals(h1, h1);
		assertEquals(h1, h2);
		assertNotEquals(h1, h3);
	}
}
