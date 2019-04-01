package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HeaderName;
import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequestBody;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.PathVariables;
import com.jkojote.server.QueryString;
import com.jkojote.server.RequestResolver;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.bodies.StreamRequestBody;
import com.jkojote.server.utils.IOUtils;

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
	private static final byte[] CRLF = "\r\n".getBytes();
	private static final byte[] HTTP_VERSION = "HTTP/1.1".getBytes();
	private static final byte[] SPACE = " ".getBytes();
	private static final byte[] COLON = ":".getBytes();
	private static int WRITE_BUFFER_SIZE = 4096;

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
			RequestResolver.RequestResolution resolvedRequest = resolver.resolveRequest(request);
			if (resolvedRequest == null) {
				writeResponse(out, Responses.NOT_FOUND);
				return;
			}
			ControllerMethod method = resolvedRequest.getMethod();
			PathVariables pathVariables = resolvedRequest.getPathVariables();
			writeResponse(out, method.process(request, pathVariables));
		} catch (BadRequestException e) {
			writeResponse(out, Responses.BAD_REQUEST);
		}
	}

	private HttpRequest readRequest(InputStream in) throws IOException {
		String firstLine = readLine(in);
		RequestLine requestLine = parseRequestLine(firstLine);
		Map<HeaderName, String> headers = readHeaders(in);
		long contentLength = getContentLength(headers);
		HttpRequestBody body = getRequestBody(contentLength, in);
		QueryString queryString = extractQueryString(requestLine, true);
		return new HttpRequestImpl(
			firstLine, requestLine.path, requestLine.method,
			body, queryString, headers
		);
	}

	private RequestLine parseRequestLine(String requestLine) {
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
			line = readLine(in);
		}
		return headers;
	}

	private QueryString extractQueryString(RequestLine requestLine, boolean ignoreMalformedParameters) {
		String url = requestLine.path;
		int beginIndex = url.indexOf('?');
		if (beginIndex == -1) {
			return new QueryStringImpl("");
		}
		String queryString = url.substring(beginIndex);
		return new QueryStringImpl(queryString, ignoreMalformedParameters);
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
		private String path;

		RequestLine(HttpMethod method, String url) {
			this.method = method;
			this.path = url;
		}
	}

	private void writeResponse(OutputStream out, HttpResponse response) throws IOException {
		writeStatus(out, response);
		writeHeaders(out, response);
		writeBody(out, response);
	}

	private void writeStatus(OutputStream out, HttpResponse response) throws IOException {
		HttpStatus status = response.getStatus();
		out.write(HTTP_VERSION);
		out.write(SPACE);
		out.write(intToBytes(status.getCode()));
		out.write(SPACE);
		out.write(status.getReasonPhrase().getBytes());
		out.write(CRLF);
	}

	private void writeHeaders(OutputStream out, HttpResponse response) throws IOException {
		Iterable<HttpHeader> headers = response.getHeaders();
		for (HttpHeader header : headers) {
			out.write(header.getName().getBytes());
			out.write(COLON);
			out.write(header.getValue().getBytes());
			out.write(CRLF);
		}
		out.write(CRLF);
	}

	private void writeBody(OutputStream out, HttpResponse response) throws IOException {
		try (InputStream in = response.getResponseBody().getInputStream()) {
			IOUtils.transfer(in, out, WRITE_BUFFER_SIZE);
		}
	}

	private byte[] intToBytes(int i) {
		return String.valueOf(i).getBytes();
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
