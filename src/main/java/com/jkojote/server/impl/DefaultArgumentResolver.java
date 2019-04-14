package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.PathVariables;
import com.jkojote.server.exceptions.PathVariableConversionException;

import java.util.List;

import static com.jkojote.server.ControllerMethod.Parameter;

class DefaultArgumentResolver implements ArgumentsResolver {

	@Override
	public Object[] resolve(ControllerMethod method,
							HttpRequest request,
							PathVariables variables) throws PathVariableConversionException {
		List<Parameter> parameters = method.getParameters();
		Object[] args = new Object[parameters.size()];
		for (Parameter parameter : parameters) {
			int index = parameter.getIndex();
			if (parameter.getType() == HttpRequest.class) {
				args[index] = request;
			} else if (parameter.getType() == PathVariables.class) {
				args[index] = variables;
			} else if (parameter.isPathVariable()) {
				String variable = variables.getPathVariable(parameter.getName());
				args[index] = parameter.getConverter().convert(variable);
			}
		}
		return args;
	}
}
