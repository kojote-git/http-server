package com.jkojote.server;

import java.util.function.Function;

public interface PathVariables {

	String getPathVariable(String name);

	default <T> T convertVariable(String name, Function<String, T> converter) {
		return converter.apply(getPathVariable(name));
	}

	Iterable<PathVariable> getPathVariable();

	int size();

	interface PathVariable {

		String getName();

		String getValue();
	}
}
