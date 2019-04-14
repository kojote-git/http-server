package com.jkojote.server.responses;

import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.bodies.EmptyResponseBody;
import com.jkojote.server.utils.IOUtils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class StreamResponseTest {
	private HttpResponse response;
	private byte[] message;

	public StreamResponseTest() {
		message = "hello".getBytes();
		response = new StreamHttpResponse(HttpStatus.OK, new ByteArrayInputStream(message))
				.addHeader("Age", 0)
				.addHeader("Content-Language", "en")
				.addHeader("Content-Language", (Object) "en");
	}

	@Test
	public void getStatus() {
		assertEquals(HttpStatus.OK, response.getStatus());
	}

	@Test
	public void getHeader() {
		String age = response.getHeader("age");
		String contentLanguage = response.getHeader("content-language");
		String contentType = response.getHeader("content-type");

		assertEquals(String.valueOf(0), age);
		assertEquals("en", contentLanguage);
		assertEquals("application/octet-stream", contentType);
	}

	@Test
	public void getBody() throws IOException {
		byte[] actual = IOUtils.readToBytes(response.getResponseBody().getInputStream());
		assertArrayEquals(message, actual);
	}

	@Test
	public void testEmptyBody() {
		HttpResponse response = new StreamHttpResponse(HttpStatus.OK, null);
		assertSame(EmptyResponseBody.INSTANCE, response.getResponseBody());
	}
}
