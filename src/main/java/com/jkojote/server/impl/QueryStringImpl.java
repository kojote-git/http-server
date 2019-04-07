package com.jkojote.server.impl;

import com.jkojote.server.QueryString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

class QueryStringImpl implements QueryString {
	private Map<String, String> parameters;

	QueryStringImpl(String queryString, boolean ignoreMalformedParameters) {
		this.parameters = new HashMap<>();
		if (!queryString.isEmpty()) {
			QueryStringTokenizer tokenizer =
					new QueryStringTokenizer(queryString, ignoreMalformedParameters);
			while (tokenizer.nextToken()) {
				parameters.put(tokenizer.getKey(), tokenizer.getValue());
			}
		}
	}

	QueryStringImpl(String queryString) {
		this(queryString, true);
	}

	@Override
	public Iterable<QueryParameter> getParameters() {
		return parameters.entrySet().stream()
			.map(ParameterImpl::new)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public String getParameterValue(String name) {
		return parameters.get(name);
	}

	private static class ParameterImpl implements QueryParameter {
		private String key;
		private String value;

		private ParameterImpl(Map.Entry<String, String> entry) {
			this.key = entry.getKey();
			this.value = entry.getValue();
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getValue() {
			return value;
		}
	}
}
