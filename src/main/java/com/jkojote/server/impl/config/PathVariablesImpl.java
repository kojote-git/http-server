package com.jkojote.server.impl.config;

import com.jkojote.server.PathVariables;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.stream.Collectors;

class PathVariablesImpl implements PathVariables {
	private Map<String, String> pathVariables;

	PathVariablesImpl() {
		this.pathVariables = new HashMap<>();
	}

	@Override
	public String getPathVariable(String name) {
		return pathVariables.get(name);
	}

	@Override
	public Iterable<PathVariable> getPathVariable() {
		return pathVariables.entrySet().stream()
				.map(e -> new PathVariableImpl(e.getKey(), e.getValue()))
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	@Override
	public int size() {
		return pathVariables.size();
	}

	void addPathVariable(String name, String value) {
		pathVariables.put(name, value);
	}

	private static class PathVariableImpl implements PathVariable {
		private String name;
		private String value;

		PathVariableImpl(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getValue() {
			return value;
		}
	}
}