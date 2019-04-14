package com.jkojote.server;

import java.util.List;

public interface ControllerMethod {

	HttpResponse execute(Object[] args);

	List<Parameter> getParameters();

	interface Parameter {

		int getIndex();

		boolean isPathVariable();

		String getName();

		PathVariableConverter<?> getConverter();

		Class<?> getType();
	}

}
