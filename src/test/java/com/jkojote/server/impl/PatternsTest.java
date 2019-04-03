package com.jkojote.server.impl;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

	private void find(String expected, String requestLine, Pattern pattern) {
		Matcher matcher = pattern.matcher(requestLine);
		assertTrue(matcher.find());
		assertEquals(expected, matcher.group());
	}

	private void assertMatches(String str, Pattern pattern) {
		assertTrue(matches(str, pattern));
	}

	private void assertNotMatches(String str, Pattern pattern) {
		assertFalse(matches(str, pattern));
	}

	private boolean matches(String str, Pattern pattern) {
		return pattern.matcher(str).matches();
	}
}
