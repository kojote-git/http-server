package com.jkojote.server.impl;

import com.jkojote.server.HttpHeader;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.utils.IOUtils;
import com.jkojote.server.utils.IOUtilsTest;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class MockHttpResponseTest {

	@Test
	public void caseOne() throws IOException {
		byte[] contents = IOUtils.readResource("mock_response_case1");
		HttpResponse response = new MockHttpResponse(contents);
		assertEquals(HttpStatus.OK, response.getStatus());
		assertHeadersSizeEqual(2, response);
		assertEquals(" text/plain; charset=\"UTF-8\"", response.getHeader("Content-type"));
		assertEquals(" 11", response.getHeader("Content-length"));
		assertEquals("hello world", readBody(response));
	}

	@Test
	public void caseTwo() throws IOException {
		byte[] contents = IOUtils.readResource("mock_response_case2");
		HttpResponse response = new MockHttpResponse(contents);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
		assertHeadersSizeEqual(2, response);
		assertEquals(" text/plain", response.getHeader("Content-type"));
		assertEquals(" 5", response.getHeader("Content-length"));
		assertEquals("hello", readBody(response));
	}

	@Test
	public void caseThree() throws IOException {
		byte[] contents = IOUtils.readResource("mock_response_case3");
		HttpResponse response = new MockHttpResponse(contents);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
		assertHeadersSizeEqual(0, response);
		assertEquals("", readBody(response));
	}

	@Test(expected = MalformedResponseException.class)
	public void caseFour() throws IOException {
		byte[] contents = IOUtils.readResource("mock_response_case4");
		HttpResponse response = new MockHttpResponse(contents);
	}

	private void assertHeadersSizeEqual(int size, HttpResponse response) {
		int i = 0;
		Iterator<HttpHeader> iterator = response.getHeaders().iterator();
		while (iterator.hasNext()) {
			iterator.next();
			i++;
		}
		assertEquals(i, size);
	}

	private String readBody(HttpResponse response) {
		StringBuilder sb = new StringBuilder();
		try (InputStream in = response.getResponseBody().getInputStream()) {
			int i;
			while ((i = in.read()) > 0) {
				sb.append((char) i);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

}
