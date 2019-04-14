package com.jkojote.server;

import org.junit.Test;

import java.util.regex.Pattern;

import static com.jkojote.server.utils.Regex.PATH_TEMPLATE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegexTest {

	@Test
	public void pathTemplate() {
		Pattern pattern = PATH_TEMPLATE;

		assertTrue(pattern.matcher("").matches());
		assertTrue(pattern.matcher("a").matches());
		assertTrue(pattern.matcher("/").matches());
		assertTrue(pattern.matcher("/a").matches());
		assertTrue(pattern.matcher("/a/").matches());
		assertTrue(pattern.matcher("{a}").matches());
		assertTrue(pattern.matcher("/{a}").matches());
		assertTrue(pattern.matcher("/{a}/").matches());
		assertTrue(pattern.matcher("a/").matches());
		assertTrue(pattern.matcher("a/b").matches());
		assertTrue(pattern.matcher("a/b/").matches());
		assertTrue(pattern.matcher("/a/b/").matches());

		assertFalse(pattern.matcher("/{}").matches());
		assertFalse(pattern.matcher("/{}/").matches());
		assertFalse(pattern.matcher("{}").matches());
		assertFalse(pattern.matcher("{").matches());
		assertFalse(pattern.matcher("}").matches());
		assertFalse(pattern.matcher("/a{}").matches());
		assertFalse(pattern.matcher("a{}").matches());
		assertFalse(pattern.matcher("{}a").matches());
		assertFalse(pattern.matcher("/a{}/b").matches());
	}
}
