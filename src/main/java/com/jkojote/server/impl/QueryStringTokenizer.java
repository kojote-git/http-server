package com.jkojote.server.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.NoSuchElementException;

class QueryStringTokenizer implements Iterable<QueryStringTokenizer.QueryStringToken> {
	private QueryStringToken firstToken;
	private QueryStringToken lastToken;
	private boolean ignoreMalformedParameters = true;

	QueryStringTokenizer(String queryString) {
		tokenize(queryString);
	}

	QueryStringTokenizer(String queryString, boolean ignoreMalformedParameters) {
		this.ignoreMalformedParameters = ignoreMalformedParameters;
		tokenize(queryString);
	}

	private void tokenize(String querySting) {
		String[] params = querySting.split("&");
		for (String param : params) {
			String[] kv = param.split("=");
			String key = kv[0];
			String value = (kv.length == 1) ? "" : kv[1];
			try {
				key = URLDecoder.decode(key, "UTF-8");
				value = URLDecoder.decode(value, "UTF-8");
			} catch (IllegalArgumentException e) {
				if (ignoreMalformedParameters) {
					continue;
				} else {
					throw new RuntimeException(e);
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			addToken(key, value);
		}
	}

	private void addToken(String key, String value) {
		if (firstToken == null) {
			firstToken = new QueryStringToken(key, value);
			lastToken = firstToken;
		} else {
			lastToken.nextToken = new QueryStringToken(key, value);
			lastToken = lastToken.nextToken;
		}
	}

	@Override
	public Iterator<QueryStringToken> iterator() {
		return new TokenIterator(this);
	}

	private static class TokenIterator implements Iterator<QueryStringToken> {
		private QueryStringToken currentToken;

		private TokenIterator(QueryStringTokenizer tokenizer) {
			this.currentToken = tokenizer.firstToken;
		}

		@Override
		public boolean hasNext() {
			return currentToken != null;
		}

		@Override
		public QueryStringToken next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			QueryStringToken token = currentToken;
			currentToken = currentToken.nextToken;
			return token;
		}
	}

	static class QueryStringToken {
		private String key;
		private String value;
		private QueryStringToken nextToken;

		private QueryStringToken(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}
}