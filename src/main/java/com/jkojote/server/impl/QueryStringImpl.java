package com.jkojote.server.impl;

import com.jkojote.server.QueryString;

import java.util.Map;
import java.util.stream.Collectors;

class QueryStringImpl implements QueryString {
	private Map<String, String> parameters;

	QueryStringImpl(String queryString) {
		//TODO parse query string
	}

	@Override
	public Iterable<Parameter> getParameters() {
		return parameters.entrySet().stream()
				.map(e -> new ParameterImpl(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}

	@Override
	public String getParameterValue(String name) {
		return parameters.get(name);
	}

	class ParameterImpl implements Parameter {
		private String key;
		private String value;

		ParameterImpl(String key, String value) {
			this.key = key;
			this.value = value;
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
