package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HeaderName;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequestBody;
import com.jkojote.server.PathVariables;
import com.jkojote.server.QueryString;
import com.jkojote.server.RequestResolver;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.bodies.StreamRequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HttpRequestHandler implements Runnable {
	static final Pattern METHOD_PATTERN = Pattern.compile(
		"GET|POST|PUT|PATCH|OPTIONS|DELETE|HEAD"
	);
	static final Pattern URL_PATTERN = Pattern.compile(
		"(/[a-zA-Z0-9%_]*)+(\\?([a-zA-Z0-9%_]*(=[a-zA-Z0-9%_]*)?&?)*)?"
	);

	private Socket socket;
	private RequestResolver resolver;

	HttpRequestHandler(Socket socket, RequestResolver resolver) {
		this.socket = socket;
		this.resolver = resolver;
	}

	@Override
	public void run() {
		try (InputStream in = socket.getInputStream();
			 OutputStream out = socket.getOutputStream()) {
			handleRequest(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleRequest(InputStream in, OutputStream out) throws IOException {
		try {
			HttpRequest request = readRequest(in);
			RequestResolver.ResolvedRequest resolvedRequest = resolver.resolveRequest(request);
			if (resolvedRequest == null) {
				writeResponse(out, Responses.NOT_FOUND);
				return;
			}
			ControllerMethod method = resolvedRequest.getMethod();
			PathVariables pathVariables = resolvedRequest.getVariables();
			writeResponse(out, method.process(request, pathVariables));
		} catch (BadRequestException e) {
			writeResponse(out, Responses.BAD_REQUEST);
		}
	}

	//TODO
	private HttpRequest readRequest(InputStream in) throws IOException {
		RequestLine requestLine = readRequestLine(in);
		Map<HeaderName, String> headers = readHeaders(in);
		long contentLength = getContentLength(headers);
		HttpRequestBody body = getRequestBody(contentLength, in);
		QueryString queryString = extractQueryString(requestLine);
		return null;
	}

	private RequestLine readRequestLine(InputStream in) throws IOException {
		String requestLine = readLine(in);
		Matcher methodMatcher = METHOD_PATTERN.matcher(requestLine);
		Matcher uriMatcher = URL_PATTERN.matcher(requestLine);
		if (!methodMatcher.find()) {
			throw new BadRequestException();
		}
		if (!uriMatcher.find()) {
			throw new BadRequestException();
		}
		String method = methodMatcher.group();
		String uri = uriMatcher.group();
		return new RequestLine(Enum.valueOf(HttpMethod.class, method), uri);
	}

	private Map<HeaderName, String> readHeaders(InputStream in) throws IOException {
		Map<HeaderName, String> headers = new HashMap<>();
		String line = readLine(in);
		while (!line.isEmpty()) {
			String[] header = line.split(":");
			String name = header[0];
			String value = (header.length == 1) ? "" : header[1];
			headers.put(HeaderName.of(value), name);
		}
		return headers;
	}

	//TODO
	private QueryString extractQueryString(RequestLine requestLine) {
		String url = requestLine.url;
		return null;
	}

	private long getContentLength(Map<HeaderName, String> headers) {
		String header = headers.get(HeaderName.of("Content-Length"));
		try {
			return header == null ? 0 : Long.parseLong(header);
		} catch (NumberFormatException e) {
			throw new BadRequestException();
		}
	}

	private HttpRequestBody getRequestBody(long contentLength, InputStream in) {
		return new StreamRequestBody(contentLength, in);
	}

	private class RequestLine {
		private HttpMethod method;
		private String url;

		RequestLine(HttpMethod method, String url) {
			this.method = method;
			this.url = url;
		}
	}

	// TODO
	private void writeResponse(OutputStream out, HttpResponse response) {

	}

	private String readLine(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		int c;
		while ((c = in.read()) > 0) {
			if (c == '\r') {
				if (in.read() != '\n') {
					throw new BadRequestException();
				}
				return sb.toString();
			}
			sb.append((char) c);
		}
		return sb.toString();
	}

}
