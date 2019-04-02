package com.jkojote.server;

public interface PathVariables {

	String getPathVariable(String name);

	Iterable<PathVariable> getPathVariable();

	int size();

	interface PathVariable {

		String getName();

		String getValue();
	}
}
