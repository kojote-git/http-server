package com.jkojote.server.impl;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.jkojote.server.impl.QueryStringTokenizer.QueryStringToken;

public class QueryStringTokenizerTest {

	@Test
	public void tokenize_tokenizeQueryString() {
		QueryStringTokenizer tokenizer = new QueryStringTokenizer("a=1&b=&c");
		Iterator<QueryStringToken> iterator = tokenizer.iterator();

		checkNextToken(iterator, "a", "1");
		checkNextToken(iterator, "b", "");
		checkNextToken(iterator, "c", "");

		assertFalse(iterator.hasNext());
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
		Iterator<QueryStringToken> iterator = tokenizer.iterator();

		checkNextToken(iterator, "amp", "&");
		checkNextToken(iterator, "space", " ");
		checkNextToken(iterator, "eq", "=");
		checkNextToken(iterator, "%", "%");

		assertFalse(iterator.hasNext());
	}

	@Test
	public void tokenize_ignoreMalformedParameters() {
		QueryStringTokenizer tokenizer = new QueryStringTokenizer(
			"a=1&b=%&%&c=2&"
		);
		Iterator<QueryStringToken> iterator = tokenizer.iterator();

		checkNextToken(iterator, "a", "1");
		checkNextToken(iterator, "c", "2");
		assertFalse(iterator.hasNext());
	}

	private void checkNextToken(Iterator<QueryStringToken> iterator, String expectedKey, String expectedValue) {
		assertTrue(iterator.hasNext());
		QueryStringToken nextToken = iterator.next();
		assertEquals(expectedKey, nextToken.getKey());
		assertEquals(expectedValue, nextToken.getValue());
	}
}
