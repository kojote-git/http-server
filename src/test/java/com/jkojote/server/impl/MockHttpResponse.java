package com.jkojote.server.impl;

import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpResponseBody;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.utils.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

//TODO
class MockHttpResponse implements HttpResponse {
	private HttpStatus status;
	private Iterable<HttpHeader> headers;
	private HttpResponseBody responseBody;

	MockHttpResponse(HttpStatus status, Iterable<HttpHeader> headers, HttpResponseBody body) {
		this.status = status;
		this.headers = headers;
		this.responseBody = body;
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
}
