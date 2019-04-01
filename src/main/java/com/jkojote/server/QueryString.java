package com.jkojote.server;

public interface QueryString {

	Iterable<QueryParameter> getParameters();

	String getParameterValue(String name);

	interface QueryParameter {

		String getKey();

		String getValue();
	}
}
