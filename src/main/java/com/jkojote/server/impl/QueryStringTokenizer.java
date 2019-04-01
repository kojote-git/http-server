package com.jkojote.server.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

class QueryStringTokenizer {
	private QueryStringToken firstToken;
	private QueryStringToken lastToken;
	private QueryStringToken currentToken;
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

	boolean nextToken() {
		if (currentToken == lastToken) {
			return false;
		} else if (currentToken == null) {
			currentToken = firstToken;
		} else {
			currentToken = currentToken.nextToken;
		}
		return currentToken != null;
	}

	String getKey() {
		if (currentToken == null) {
			throw new IllegalStateException("tokenizer has run out of tokens");
		}
		return currentToken.key;
	}

	String getValue() {
		if (currentToken == null) {
			throw new IllegalStateException("tokenizer has run out of tokens");
		}
		return currentToken.value;
	}

	private static class QueryStringToken {
		private String key;
		private String value;
		private QueryStringToken nextToken;

		private QueryStringToken(String key, String value) {
			this.key = key;
			this.value = value;
		}

	}
}