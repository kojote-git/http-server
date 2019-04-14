package com.jkojote.server.responses;

import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.bodies.EmptyResponseBody;
import com.jkojote.server.utils.IOUtils;

import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class StringHttpResponseTest {
	private HttpResponse response;
	private byte[] messageBytes;

	public StringHttpResponseTest() {
		messageBytes = "hello".getBytes();
		response = new StringHttpResponse(HttpStatus.OK, "hello")
			.addHeader("Age", 0)
			.addHeader("Content-Language", "en")
			.addHeader("Content-Language", (Object) "en");
	}

	@Test
	public void getStatus() {
		assertEquals(HttpStatus.OK, response.getStatus());
	}

	@Test
	public void getBody() throws IOException {
		byte[] expected = "hello".getBytes();
		byte[] actual = IOUtils.readToBytes(response.getResponseBody().getInputStream());
		assertArrayEquals(expected, actual);
	}

	@Test
	public void getHeader() {
		String contentLength = response.getHeader("content-length");
		String contentType = response.getHeader("content-type");
		String contentLanguage = response.getHeader("content-language");
		String age = response.getHeader("age");

		assertEquals(String.valueOf(messageBytes.length), contentLength);
		assertEquals("text/plain; charset=\"UTF-8\"", contentType);
		assertEquals("en", contentLanguage);
		assertEquals(String.valueOf(0), age);
	}

	@Test
	public void getHeaders() {
		Iterator<HttpHeader> iterator = response.getHeaders().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			i++;
			iterator.next();
		}
		assertEquals(4, i);
	}

	@Test
	public void testEmptyBody() {
		HttpResponse response = new StreamHttpResponse(HttpStatus.OK, null);
		assertSame(EmptyResponseBody.INSTANCE, response.getResponseBody());
	}
}
