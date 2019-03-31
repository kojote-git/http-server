package com.jkojote.server.impl;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PatternsTest {

	@Test
	public void methodPattern_testMethodPattern() {
		String get = "GET / HTTP/1.1";
		String post = "POST / HTTP/1.1";
		String delete = "DELETE / HTTP/1.1";
		String put = "PUT / HTTP/1.1";
		Pattern methodPattern = HttpRequestHandler.METHOD_PATTERN;
		find("GET", get, methodPattern);
		find("PUT", put, methodPattern);
		find("POST", post, methodPattern);
		find("DELETE", delete, methodPattern);
	}

	@Test
	public void urlPattern_testUrlPattern() {
		Pattern pattern = HttpRequestHandler.URL_PATTERN;
		find("/", "GET / HTTP/1.1", pattern);
		find("/hello", "GET /hello HTTP/1.1", pattern);
		find("/hello?abc=5", "GET /hello?abc=5 HTTP/1.1", pattern);
		find("/hello?a=0&b=1", "GET /hello?a=0&b=1 HTTP/1.1", pattern);
		find("/?", "GET /? HTTP/1.1", pattern);
		find("/?&", "GET /?& HTTP/1.1", pattern);
		find("/?&%", "GET /?&% HTTP/1.1", pattern);
		find("/?%25=%25", "GET /?%25=%25 HTTP/1.1", pattern);
	}

	private void find(String expected, String requestLine, Pattern pattern) {
		Matcher matcher = pattern.matcher(requestLine);
		assertTrue(matcher.find());
		assertEquals(expected, matcher.group());
	}

}
