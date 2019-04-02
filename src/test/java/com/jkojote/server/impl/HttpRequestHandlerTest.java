package com.jkojote.server.impl;

import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.ServerConfiguration;
import com.jkojote.server.bodies.ByteResponseBody;
import com.jkojote.server.utils.IOUtils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpRequestHandlerTest {

	@Test
	public void handleRequest_case1() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequest("request_case1", responseStream);
		HttpResponse response = HttpResponseBuilder.create()
			.addHeader("Content-type", "text/plain")
			.addHeader("Content-length", "" + 5)
			.setStatus(HttpStatus.of(200, "OK"))
			.setResponseBody(new ByteResponseBody("hello".getBytes()))
			.build();

		ServerConfiguration resolver = new MockRequestResolver((httpRequest, pathVariables) -> {
			assertEquals("GET / HTTP/1.1", httpRequest.getRequestLine());
			assertEquals(HttpMethod.GET, httpRequest.getMethod());
			return response;
		});

		HttpRequestHandler handler = new HttpRequestHandler(socket, resolver);
		handler.run();
		assertEquals(new MockHttpResponse(responseStream.toByteArray()), response);
	}

	@Test
	public void handleRequest_case2() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequest("request_case2", responseStream);
		ServerConfiguration resolver = new MockRequestResolver((req, vars) -> null);
		HttpRequestHandler handler = new HttpRequestHandler(socket, resolver);
		handler.run();
		assertEquals(new MockHttpResponse(responseStream.toByteArray()), Responses.BAD_REQUEST);
	}

	@Test
	public void handleRequest_case3() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequest("request_case3", responseStream);
		ServerConfiguration resolver = new MockRequestResolver(null);
		HttpRequestHandler handler = new HttpRequestHandler(socket, resolver);
		handler.run();
		assertEquals(new MockHttpResponse(responseStream.toByteArray()), Responses.NOT_FOUND);
	}

	private Socket mockSocketWithRequest(String pathToRequest, OutputStream responseStream) throws IOException {
		Socket socket = mock(Socket.class);
		when(socket.getInputStream()).then(mock -> {
			try {
				byte[] request = IOUtils.readResource(pathToRequest);
				return new ByteArrayInputStream(request);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		when(socket.getOutputStream()).then(mock -> responseStream);
		return socket;
	}
}
