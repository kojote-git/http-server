package com.jkojote.server.impl;

import com.jkojote.server.ErrorProperties;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.bodies.ByteResponseBody;
import com.jkojote.server.impl.config.EchoController;
import com.jkojote.server.impl.config.TreeServerConfiguration;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TreeServerConfigurationTest {
	private TreeServerConfiguration config;

	@Before
	public void init() {
		config = new TreeServerConfiguration();
		config
			.addMapping("/hello/{echo}", HttpMethod.GET, (req, vars) -> {
				String echo = vars.getPathVariable("echo");
				return stringResponse(HttpStatus.OK, echo);
			})
			.addMapping("/hello/{echo}/{message}", HttpMethod.GET, (req, vars) -> {
				String name = vars.getPathVariable("echo");
				String message = vars.getPathVariable("message");
				return stringResponse(HttpStatus.OK, name + "/" + message);
			})
			.addController(new EchoController())
			.addResponseOnError(HttpStatus.NOT_FOUND, (error) -> {
				String path = error.getProperty(ErrorProperties.PATH).toString();
				String response = "cannot find: " + path;
				return stringResponse(HttpStatus.NOT_FOUND, response);
			});
	}

	@Test
	public void test_case1() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Socket socket = mockSocket("GET /hello/echo HTTP/1.1\r\n", out);
		HttpRequestHandler handler = new HttpRequestHandler(socket, config);
		handler.run();
		HttpResponse expectedResponse = stringResponse(HttpStatus.OK, "echo");
		HttpResponse actualResponse = new MockHttpResponse(out.toByteArray());

		assertEquals(actualResponse, expectedResponse);
	}

	@Test
	public void test_case2() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Socket socket = mockSocket("GET /hello/echo/echo HTTP/1.1\r\n", out);
		HttpRequestHandler handler = new HttpRequestHandler(socket, config);
		handler.run();
		HttpResponse expectedResponse = stringResponse(HttpStatus.OK, "echo/echo");
		HttpResponse actualResponse = new MockHttpResponse(out.toByteArray());

		assertEquals(actualResponse, expectedResponse);
	}

	@Test
	public void testEchoController_echo() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Socket socket = mockSocket("GET /echo HTTP/1.1\r\n", out);
		HttpRequestHandler handler = new HttpRequestHandler(socket, config);
		handler.run();
		HttpResponse expectedResponse = stringResponse(HttpStatus.OK, "echo");
		HttpResponse actualResponse = new MockHttpResponse(out.toByteArray());

		assertEquals(actualResponse, expectedResponse);
	}

	@Test
	public void testEchoController_echo_message() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Socket socket = mockSocket("GET /echo/hello HTTP/1.1\r\n", out);
		HttpRequestHandler handler = new HttpRequestHandler(socket, config);
		handler.run();
		HttpResponse expectedResponse = stringResponse(HttpStatus.OK, "hello");
		HttpResponse actualResponse = new MockHttpResponse(out.toByteArray());

		assertEquals(actualResponse, expectedResponse);
	}

	@Test
	public void testEchoController_echo_message_n() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Socket socket = mockSocket("GET /echo/hello/3 HTTP/1.1\r\n", out);
		HttpRequestHandler handler = new HttpRequestHandler(socket, config);
		handler.run();
		HttpResponse expectedResponse = stringResponse(HttpStatus.OK, "hellohellohello");
		HttpResponse actualResponse = new MockHttpResponse(out.toByteArray());

		assertEquals(actualResponse, expectedResponse);
	}

	@Test
	public void test_case3() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Socket socket  = mockSocket("GET / HTTP/1.1\r\n", out);
		HttpRequestHandler handler = new HttpRequestHandler(socket, config);
		handler.run();
		HttpResponse expectedResponse = stringResponse(HttpStatus.NOT_FOUND, "cannot find: /");
		HttpResponse actualResponse = new MockHttpResponse(out.toByteArray());

		assertEquals(actualResponse, expectedResponse);
	}

	private HttpResponse stringResponse(HttpStatus status, String str) {
		byte[] bytes = str.getBytes();
		return HttpResponseBuilder.create()
				.setStatus(status)
				.addHeader("Content-Type", "text/plain; charset=\"UTF-8\"")
				.addHeader("Content-Length", String.valueOf(bytes.length))
				.setResponseBody(new ByteResponseBody(bytes))
				.build();
	}

	private Socket mockSocket(String getRequest, OutputStream out) throws IOException {
		Socket socket = mock(Socket.class);
		when(socket.getInputStream()).then(mock -> {
			return new ByteArrayInputStream(getRequest.getBytes());
		});
		when(socket.getOutputStream()).thenReturn(out);
		return socket;
	}
}
