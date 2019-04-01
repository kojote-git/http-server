package com.jkojote.server.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QueryStringTokenizerTest {

	@Test
	public void tokenize_tokenizeQueryString() {
		QueryStringTokenizer tokenizer = new QueryStringTokenizer("a=1&b=&c");

		checkNextToken(tokenizer, "a", "1");
		checkNextToken(tokenizer, "b", "");
		checkNextToken(tokenizer, "c", "");

		assertFalse(tokenizer.nextToken());
	}

	@Test
	public void tokenize_tokenizeEncodedQueryString() {
		String amp = "%26";
		String space = "%20";
		String eq = "%3D";
		String mod = "%25";
		QueryStringTokenizer tokenizer = new QueryStringTokenizer(
			String.format("amp=%s&space=%s&eq=%s&%s=%s",
				amp, space, eq, mod, mod
			)
		);

		checkNextToken(tokenizer, "amp", "&");
		checkNextToken(tokenizer, "space", " ");
		checkNextToken(tokenizer, "eq", "=");
		checkNextToken(tokenizer, "%", "%");

		assertFalse(tokenizer.nextToken());
	}

	@Test
	public void tokenize_ignoreMalformedParameters() {
		QueryStringTokenizer tokenizer = new QueryStringTokenizer(
			"a=1&b=%&%&c=2&"
		);

		checkNextToken(tokenizer, "a", "1");
		checkNextToken(tokenizer, "c", "2");
		assertFalse(tokenizer.nextToken());
	}

	private void checkNextToken(QueryStringTokenizer tokenizer, String expectedKey, String expectedValue) {
		assertTrue(tokenizer.nextToken());
		assertEquals(expectedKey, tokenizer.getKey());
		assertEquals(expectedValue, tokenizer.getValue());
	}
}
