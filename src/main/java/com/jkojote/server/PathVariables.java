package com.jkojote.server;

public interface PathVariables {

	String getTemplateVariable(String name);

	Iterable<PathVariable> getTemplateVariables();

	interface PathVariable {

		String getName();

		String getValue();
	}
}
