package com.jkojote.server.impl;

import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.QueryString;
import com.jkojote.server.ServerConfiguration;
import com.jkojote.server.bodies.ByteResponseBody;
import com.jkojote.server.utils.IOUtils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;

import javax.print.DocFlavor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpRequestHandlerTest {

	@Test
	public void handleRequest_case1() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequestFromResource("request_case1", responseStream);
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
		Socket socket = mockSocketWithRequestFromResource("request_case2", responseStream);
		ServerConfiguration resolver = new MockRequestResolver((req, vars) -> null);
		HttpRequestHandler handler = new HttpRequestHandler(socket, resolver);
		handler.run();
		assertEquals(new MockHttpResponse(responseStream.toByteArray()), Responses.BAD_REQUEST);
	}

	@Test
	public void handleRequest_case3() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequestFromResource("request_case3", responseStream);
		ServerConfiguration resolver = new MockRequestResolver(null);
		HttpRequestHandler handler = new HttpRequestHandler(socket, resolver);
		handler.run();
		assertEquals(new MockHttpResponse(responseStream.toByteArray()), Responses.NOT_FOUND);
	}

	@Test
	public void handleRequest_case4() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequest("GET /hello? HTTP/1.1\r\n", responseStream);
		ServerConfiguration resolver = new MockRequestResolver((req, vars) -> {
			assertEquals("/hello", req.getPath());
			Iterator<?> iterator = req.getQueryString().getParameters().iterator();
			assertFalse(iterator.hasNext());
			return null;
		});
		new HttpRequestHandler(socket, resolver).run();
	}

	@Test
	public void handleRequest_case5() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequest("GET /? HTTP/1.1\r\n", responseStream);
		ServerConfiguration resolver = new MockRequestResolver((req, vars) -> {
			assertEquals("/", req.getPath());
			Iterator<?> iterator = req.getQueryString().getParameters().iterator();
			assertFalse(iterator.hasNext());
			return null;
		});
		new HttpRequestHandler(socket, resolver).run();
	}

	@Test
	public void handleRequest_case6() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequest("GET  HTTP/1.1\r\n", responseStream);
		ServerConfiguration resolver = new MockRequestResolver(null);
		new HttpRequestHandler(socket, resolver).run();
		HttpResponse response = new MockHttpResponse(responseStream.toByteArray());
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
	}

	@Test
	public void handleRequest_case7() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequest("METHOD /root HTTP/1.1\r\n", responseStream);
		ServerConfiguration resolver = new MockRequestResolver(null);
		new HttpRequestHandler(socket, resolver).run();
		HttpResponse response = new MockHttpResponse(responseStream.toByteArray());
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
	}

	@Test
	public void handleRequest_case8() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequest("GET /root HTTP/1.1\rn\n", responseStream);
		ServerConfiguration resolver = new MockRequestResolver(null);
		new HttpRequestHandler(socket, resolver).run();
		HttpResponse response = new MockHttpResponse(responseStream.toByteArray());
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
	}

	@Test
	public void handleRequest_case9() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequestFromResource("request_case9", responseStream);
		ServerConfiguration resolver = new MockRequestResolver(null);
		new HttpRequestHandler(socket, resolver).run();
		HttpResponse response = new MockHttpResponse(responseStream.toByteArray());
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
	}

	@Test
	public void handleRequest_testParsedQueryParams() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequest("GET /hello?a=1&b=2 HTTP/1.1\r\n", responseStream);
		ServerConfiguration resolver = new MockRequestResolver((req, vars) -> {
			QueryString str = req.getQueryString();
			String a = str.getParameterValue("a");
			String b = str.getParameterValue("b");
			assertEquals("1", a);
			assertEquals("2", b);
			return null;
		});
		HttpRequestHandler handler = new HttpRequestHandler(socket, resolver);
		handler.run();
	}

	@Test
	public void handleRequest_testParsedEncodedQueryParams() throws IOException {
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		Socket socket = mockSocketWithRequest("GET /hello?path=%2Fhello HTTP/1.1\r\n", responseStream);
		ServerConfiguration resolver = new MockRequestResolver((req, vars) -> {
			QueryString str = req.getQueryString();
			String a = str.getParameterValue("path");
			assertEquals("/hello", a);
			return null;
		});
		HttpRequestHandler handler = new HttpRequestHandler(socket, resolver);
		handler.run();
	}

	private Socket mockSocketWithRequest(String request, OutputStream responseStream) throws IOException {
		Socket socket = mock(Socket.class);
		when(socket.getInputStream()).then(mock -> {
			return new ByteArrayInputStream(request.getBytes());
		});
		when(socket.getOutputStream()).then(mock -> responseStream);
		return socket;
	}

	private Socket mockSocketWithRequestFromResource(String pathToRequest, OutputStream responseStream) throws IOException {
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
