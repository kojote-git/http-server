package com.jkojote.server;

public interface QueryString {

	Iterable<Parameter> getParameters();

	String getParameterValue(String name);

	interface Parameter {

		String getKey();

		String getValue();
	}
}
