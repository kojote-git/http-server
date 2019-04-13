package com.jkojote.server;

import java.util.List;
import java.util.function.Function;

public interface ControllerMethod {

	HttpResponse execute(Object[] args);

	List<Parameter> getParameters();

	interface Parameter {

		int getIndex();

		boolean isPathVariable();

		String getName();

		default Function<String, Object> getConverter() { return String::toString; }

		Class<?> getType();
	}

}
