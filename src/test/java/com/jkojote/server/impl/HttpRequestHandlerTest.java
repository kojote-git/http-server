package com.jkojote.server.impl;

import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.RequestResolver;
import com.jkojote.server.bodies.ByteResponseBody;
import com.jkojote.server.utils.IOUtils;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpRequestHandlerTest {
	private Socket socket;
	private RequestResolver resolver;
	private ByteArrayOutputStream responseStream;
	private byte[] expectedResponse;

	@Before
	public void before() throws IOException {
		responseStream = new ByteArrayOutputStream();
		socket = mock(Socket.class);
		when(socket.getInputStream()).then(mock -> {
			try {
				byte[] request = IOUtils.readResource("request");
				return new ByteArrayInputStream(request);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		when(socket.getOutputStream()).then(mock -> responseStream);
		expectedResponse = IOUtils.readResource("response");
	}

	@Test
	public void handleRequest() {
		resolver = new MockRequestResolver((httpRequest, pathVariables) -> {
			assertEquals("GET / HTTP/1.1", httpRequest.getRequestLine());
			assertEquals(HttpMethod.GET, httpRequest.getMethod());
			String response = "hello";
			return HttpResponseBuilder.create()
				.addHeader("Content-type", "text/plain")
				.addHeader("Content-length", "" + 5)
				.setStatus(HttpStatus.of(200, "OK"))
				.setResponseBody(new ByteResponseBody(response.getBytes()))
				.build();
		});
		HttpRequestHandler handler = new HttpRequestHandler(socket, resolver);
		handler.run();
		assertArrayEquals(expectedResponse, responseStream.toByteArray());
	}
}
