package com.jkojote.server.impl;

import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpResponseBody;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.bodies.ByteResponseBody;
import com.jkojote.server.utils.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MockHttpResponse implements HttpResponse {
	static final Pattern STATUS_LINE_PATTERN =
			Pattern.compile("^HTTP/[0-9]\\.[0-9] [0-9]{3} [a-zA-Z\\s]+$");
	static final Pattern HEADER_PATTERN =
			Pattern.compile("^[a-zA-Z\\-]+:([\\sa-z-A-Z0-9/=;]|(\".*?\"))*$");

	private HttpStatus status;
	private Iterable<HttpHeader> headers;
	private HttpResponseBody responseBody;

	MockHttpResponse(HttpStatus status, Iterable<HttpHeader> headers, HttpResponseBody body) {
		this.status = status;
		this.headers = headers;
		this.responseBody = body;
	}

	MockHttpResponse(byte[] bytes) {
		String response = new String(bytes);
		ResponseTokenizer tokenizer = new ResponseTokenizer(response);
		validateResponse(tokenizer);
		tokenizer.reset();
		responseBody = new ByteResponseBody(tokenizer.body.getBytes());
		status = parseStatus(tokenizer);
		headers = parseHeaders(tokenizer);
	}

	private HttpStatus parseStatus(ResponseTokenizer tokenizer) {
		tokenizer.next();
		String statusLine = tokenizer.getLine();
		int statusCode = getStatusCode(statusLine);
		String reasonPhrase = getReasonPhrase(statusLine);
		return HttpStatus.of(statusCode, reasonPhrase);
	}

	private int getStatusCode(String statusLine) {
		Pattern pattern = Pattern.compile("[0-9]{3}");
		Matcher matcher = pattern.matcher(statusLine);
		matcher.find();
		return Integer.parseInt(matcher.group());
	}

	private String getReasonPhrase(String statusLine) {
		Pattern pattern = Pattern.compile("[a-zA-Z\\s]+$");
		Matcher matcher = pattern.matcher(statusLine);
		matcher.find();
		return matcher.group().trim();
	}

	private Iterable<HttpHeader> parseHeaders(ResponseTokenizer tokenizer) {
		Collection<HttpHeader> headers = new LinkedList<>();
		while (tokenizer.next()) {
			HttpHeader header = parseHeader(tokenizer.getLine());
			headers.add(header);
		}
		return headers;
	}

	private HttpHeader parseHeader(String header) {
		String[] values = header.split(":");
		String name = values[0];
		String value = (values.length == 1) ? "" : values[1];
		return HttpHeader.of(name, value);
	}

	private void validateResponse(ResponseTokenizer tokenizer) {
		validateResponseStatus(tokenizer);
		validateHeaders(tokenizer);
	}

	private void validateResponseStatus(ResponseTokenizer tokenizer) {
		if (!tokenizer.next()) {
			throw new MalformedResponseException("no status line present");
		}
		String statusLine = tokenizer.getLine();
		if (!STATUS_LINE_PATTERN.matcher(statusLine).matches()) {
			throw new MalformedResponseException();
		}
	}

	private void validateHeaders(ResponseTokenizer tokenizer) {
		while (tokenizer.next()) {
			validateHeader(tokenizer.getLine());
		}
	}

	private void validateHeader(String header) {
		if (!HEADER_PATTERN.matcher(header).matches()) {
			throw new MalformedResponseException("invalid header: [" + header + "]");
		}
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public Iterable<HttpHeader> getHeaders() {
		return headers;
	}

	@Override
	public String getHeader(String name) {
		for (HttpHeader header : headers) {
			if (name.equals(header.getName())) {
				return header.getValue();
			}
		}
		return null;
	}

	@Override
	public HttpResponseBody getResponseBody() {
		return responseBody;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) obj;
			return status.equals(response.getStatus())
					&& hasEqualHeaders(response)
					&& hasEqualBody(response);
		}
		return false;
	}

	private boolean hasEqualHeaders(HttpResponse response) {
		for (HttpHeader header : response.getHeaders()) {
			String thisHeader = getHeader(header.getName());
			if (thisHeader == null) {
				return false;
			}
			if (!thisHeader.equals(header.getValue())) {
				return false;
			}
		}
		for (HttpHeader header : getHeaders()) {
			String thatHeader = response.getHeader(header.getName());
			if (thatHeader == null) {
				return false;
			}
			if (!thatHeader.equals(header.getValue())) {
				return false;
			}
		}
		return true;
	}

	private boolean hasEqualBody(HttpResponse response) {
		try (InputStream thisIn = getResponseBody().getInputStream();
			 InputStream thatIn = response.getResponseBody().getInputStream()) {
			ByteArrayOutputStream thisOut = new ByteArrayOutputStream();
			ByteArrayOutputStream thatOut = new ByteArrayOutputStream();
			IOUtils.transfer(thisIn, thisOut, 4096);
			IOUtils.transfer(thatIn, thatOut, 4096);
			return Arrays.equals(thisOut.toByteArray(), thatOut.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private static class ResponseTokenizer {
		private static String CRLF = "\r\n";
		private LineToken head;
		private LineToken tail;
		private LineToken currentLine;
		private String body;

		ResponseTokenizer(String response) {
			tokenize(response);
		}

		void tokenize(String response) {
			String[] parts = response.split(CRLF + CRLF);
			String statusAndHeaders = parts[0];
			body = (parts.length == 1) ? "" : parts[1];
			String[] lines = statusAndHeaders.split(CRLF);
			for (String line : lines) {
				addToken(line);
			}
		}

		void addToken(String value) {
			if (head == null) {
				head = new LineToken(value);
				tail = head;
			} else {
				tail.next = new LineToken(value);
				tail = tail.next;
			}
		}

		boolean next() {
			if (currentLine == tail) {
				return false;
			} else if (currentLine == null) {
				currentLine = head;
			} else {
				currentLine = currentLine.next;
			}
			return currentLine != null;
		}

		String getLine() {
			return currentLine.value;
		}

		void reset() {
			currentLine = null;
		}
	}

	private static class LineToken {
		private String value;
		private LineToken next;

		LineToken(String value) {
			this.value = value;
		}
	}
}
