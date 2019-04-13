package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.PathVariables;
import com.jkojote.server.annotation.DirectVariablesMapping;
import com.jkojote.server.annotation.PathVar;
import com.jkojote.server.exceptions.InvalidControllerMethodException;
import com.jkojote.server.impl.PathVariableParameter;
import com.jkojote.server.impl.TypeParameter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class ControllerMethodCreator {

	ControllerMethod create(String pathTemplate, Object controller, Method method) {
		if (hasDirectMapping(method)) {
			return createMapped(pathTemplate, controller, method);
		} else {
			return createDefault(controller, method);
		}
	}

	private boolean hasDirectMapping(Method method) {
		return method.getDeclaredAnnotation(DirectVariablesMapping.class) != null;
	}

	private ControllerMethod createMapped(String pathTemplate, Object controller, Method method) {
		int startIndex = getStartIndex(method);
		Parameter[] refParams = method.getParameters();
		Map<Integer, String> indexMappings = getIndexMappings(pathTemplate, startIndex);
		if (indexMappings.size() != refParams.length - startIndex) {
			throw new InvalidControllerMethodException(
				"number of path variable parameters doesn't match to template",
				method
			);
		}
		List<ControllerMethod.Parameter> parameters = new ArrayList<>();
		for (int i = 0; i < startIndex; i++) {
			parameters.add(translateTypeParameter(i, refParams[i], method));
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

	private ControllerMethod.Parameter translateTypeParameter(int index, Parameter parameter, Method method) {
		Class<?> type = parameter.getType();
		if (type == HttpRequest.class || type == PathVariables.class) {
			return new TypeParameter(index, type);
		} else {
			throw new InvalidControllerMethodException(
				"invalid parameter type for controller: " + type,
				method
			);
		}
	}

	private ControllerMethod createDefault(Object controller, Method method) {
		List<ControllerMethod.Parameter> parameters = new ArrayList<>();
		Parameter[] refParams = method.getParameters();
		for (int i = 0; i < refParams.length; i++) {
			Parameter parameter = refParams[i];
			Class<?> type = parameter.getType();
			PathVar pathVar = type.getAnnotation(PathVar.class);
			if (pathVar != null) {
				String pathVarName = pathVar.value();
				Function<String, Object> converter = getConverterForType(type);
				parameters.add(new PathVariableParameter(
					i, pathVarName, converter, type
				));
			} else {
				parameters.add(translateTypeParameter(i, parameter, method));
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

	private String getPathVariableName(String node) {
		return node.substring(1, node.length() - 1);
	}

	private Function<String, Object> getConverterForType(Class<?> type) {
		if (type == Integer.class || type == int.class) {
			return Integer::valueOf;
		} else if (type == Long.class || type == long.class) {
			return Long::valueOf;
		} else {
			return String::toString;
		}
	}
}
