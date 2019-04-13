package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.PathVariables;
import com.jkojote.server.exceptions.PathVariableFormatException;

import java.util.List;

import static com.jkojote.server.ControllerMethod.Parameter;

class DefaultArgumentResolver implements ArgumentsResolver {

	@Override
	public Object[] resolve(ControllerMethod method,
							HttpRequest request,
							PathVariables variables) throws PathVariableFormatException {
		List<Parameter> parameters = method.getParameters();
		Object[] args = new Object[parameters.size()];
		for (Parameter parameter : parameters) {
			int index = parameter.getIndex();
			if (parameter.getType() == HttpRequest.class) {
				args[index] = request;
			} else if (parameter.getType() == PathVariables.class) {
				args[index] = variables;
			} else if (parameter.isPathVariable()) {
				String name = parameter.getName();
				String variable = variables.getPathVariable(parameter.getName());
				try {
					args[index] = parameter.getConverter().apply(variables.getPathVariable(name));
				} catch (RuntimeException e) {
					throw new PathVariableFormatException(e, "cannot convert path variable",
						name, variable
					);
				}
			}
		}
		return args;
	}
}
