package com.jkojote.server;

public interface PathVariables {

	String getPathVariable(String name);

	Iterable<PathVariable> getPathVariable();

	interface PathVariable {

		String getName();

		String getValue();
	}
}
