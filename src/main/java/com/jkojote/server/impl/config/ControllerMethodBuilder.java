package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.PathVariableConverter;
import com.jkojote.server.PathVariables;
import com.jkojote.server.annotation.DirectVariablesMapping;
import com.jkojote.server.annotation.PathVar;
import com.jkojote.server.exceptions.InvalidControllerMethodException;
import com.jkojote.server.exceptions.InvalidMappingException;
import com.jkojote.server.exceptions.PathVariableConversionException;
import com.jkojote.server.impl.PathVariableParameter;
import com.jkojote.server.impl.TypeParameter;
import com.jkojote.server.impl.config.converters.IntConverter;
import com.jkojote.server.impl.config.converters.LongConverter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ControllerMethodBuilder {

	ControllerMethod buildOf(String pathTemplate, Object controller, Method method)
	throws InvalidMappingException {
		if (hasDirectMapping(method)) {
			return createMapped(pathTemplate, controller, method);
		} else {
			return createDefault(pathTemplate, controller, method);
		}
	}

	private boolean hasDirectMapping(Method method) {
		return method.getDeclaredAnnotation(DirectVariablesMapping.class) != null;
	}

	private ControllerMethod createMapped(String pathTemplate, Object controller, Method method) {
		int startIndex = getStartIndex(method);
		Parameter[] refParams = method.getParameters();
		Map<Integer, String> indexMappings = getIndexMappings(pathTemplate, startIndex);
		int numberOfPathVariables = indexMappings.size();
		checkDirectMappingStartIndex(
			startIndex, refParams.length - 1
		);
		checkMappedVariablesGoAfterAnotherParameters(
				numberOfPathVariables, startIndex, refParams.length
		);
		checkNumberOfPathVariables(
			numberOfPathVariables, refParams.length - startIndex
		);
		List<ControllerMethod.Parameter> parameters = new ArrayList<>();
		for (int i = 0; i < startIndex; i++) {
			parameters.add(translateTypeParameter(i, refParams[i]));
		}
		for (int i = startIndex; i < refParams.length; i++) {
			Class<?> type = refParams[i].getType();
			parameters.add(new PathVariableParameter(i,
				indexMappings.get(i), getConverterForType(type),
				type
			));
		}
		return new RefControllerMethod(controller, method, parameters);
	}

	private ControllerMethod.Parameter translateTypeParameter(int index, Parameter parameter) {
		Class<?> type = parameter.getType();
		if (type == HttpRequest.class || type == PathVariables.class) {
			return new TypeParameter(index, type);
		} else {
			throw new InvalidMappingException(
				"invalid parameter type for controller: " + type
			);
		}
	}

	private ControllerMethod createDefault(String pathTemplate,
										   Object controller, Method method) {
		List<ControllerMethod.Parameter> parameters = new ArrayList<>();
		Parameter[] refParams = method.getParameters();
		Set<String> pathVars = getPathVariableNames(pathTemplate);
		for (int i = 0; i < refParams.length; i++) {
			Parameter parameter = refParams[i];
			Class<?> type = parameter.getType();
			PathVar pathVar = parameter.getAnnotation(PathVar.class);
			if (pathVar != null) {
				String pathVarName = pathVar.value();
				PathVariableConverter converter = getConverterForType(type);
				checkPathVariablePresent(pathVars, pathVarName);
				checkConverterNotNull(converter, type);
				parameters.add(new PathVariableParameter(
					i, pathVarName, converter, type
				));
			} else {
				parameters.add(translateTypeParameter(i, parameter));
			}
		}
		return new RefControllerMethod(controller, method, parameters);
	}

	private Map<Integer, String> getIndexMappings(String pathTemplate, int startIndex) {
		Map<Integer, String> res = new HashMap<>();
		String[] nodes = pathTemplate.split("/");
		for (String node : nodes) {
			if (nodeIsPathVariable(node)) {
				res.put(startIndex, getPathVariableName(node));
				startIndex++;
			}
		}
		return res;
	}

	private int getStartIndex(Method method) {
		return method.getDeclaredAnnotation(DirectVariablesMapping.class).startIndex();
	}

	private boolean nodeIsPathVariable(String node) {
		return node.startsWith("{") && node.endsWith("}");
	}

	private Set<String> getPathVariableNames(String pathTemplate) {
		String[] nodes = pathTemplate.split("/");
		Set<String> names = new HashSet<>();
		for (String node : nodes) {
			if (nodeIsPathVariable(node)) {
				names.add(getPathVariableName(node));
			}
		}
		return names;
	}

	private void checkPathVariablePresent(Set<String> pathVariableNames, String pathVariableName) {
		if (!pathVariableNames.contains(pathVariableName)) {
			throw new InvalidMappingException(
				"unknown path variable: " + pathVariableName
			);
		}
	}

	private String getPathVariableName(String node) {
		return node.substring(1, node.length() - 1);
	}

	private PathVariableConverter getConverterForType(Class<?> type) {
		if (type == Integer.class || type == int.class) {
			return IntConverter.INSTANCE;
		} else if (type == Long.class || type == long.class) {
			return LongConverter.INSTANCE;
		} else if (type == String.class) {
			return String::toString;
		} else {
			return null;
		}
	}

	private void checkConverterNotNull(PathVariableConverter converter, Class<?> type) {
		if (converter == null) {
			throw new InvalidMappingException(
				"cannot find suitable converter for the type " + type
			);
		}
	}

	private void checkNumberOfPathVariables(int expected, int actual) {
		if (expected != actual) {
			throw new InvalidMappingException(
				"number of path variable parameters doesn't match to template"
			);
		}
	}

	private void checkMappedVariablesGoAfterAnotherParameters(int numberOfPathVariables,
															  int startIndex,
															  int numberOfParameters) {
		if (startIndex + numberOfPathVariables < numberOfParameters) {
			throw new InvalidMappingException(
				"all mapped path variables parameters must " +
				"go after all other method parameters"
			);
		}
	}

	private void checkDirectMappingStartIndex(int startIndex, int limit) {
		if (startIndex < 0) {
			throw new InvalidMappingException(
				"startIndex of a @DirectVariablesMapping annotation must be positive"
			);
		}
		if (startIndex > limit) {
			throw new InvalidMappingException(
				"startIndex exceeds actual number of parameters of the methods"
			);
		}
	}
}
