package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.PathVariables;
import com.jkojote.server.ServerConfiguration;
import com.jkojote.server.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TreeServerConfiguration implements ServerConfiguration {
	private ControllerMethodTree tree;
	private Map<HttpStatus, Function<ErrorData, HttpResponse>> errorResponses;

	public TreeServerConfiguration() {
		this.tree = new ControllerMethodTree();
		this.errorResponses = new HashMap<>();
	}

	@Override
	public RequestResolution resolveRequest(HttpRequest request) {
		return tree.resolveRequest(request);
	}

	@Override
	public HttpResponse getResponseOnError(HttpStatus status, ErrorData errorData) {
		Function<ErrorData, HttpResponse> producer = errorResponses.get(status);
		return producer == null ? null : producer.apply(errorData);
	}

	public TreeServerConfiguration addControllerMethod(String template,
													   HttpMethod httpMethod,
													   ControllerMethod controllerMethod) {
		tree.addControllerMethod(template, httpMethod, controllerMethod);
		return this;
	}

	public TreeServerConfiguration addController(Object controller) {
		Method[] methods = controller.getClass().getMethods();
		for (Method method : methods) {
			if (!hasAnnotation(method, RequestMapping.class)) {
				continue;
			}
			checkMethod(method);
			RequestMapping mapping = method.getDeclaredAnnotation(RequestMapping.class);
			String template = mapping.value();
			HttpMethod httpMethod = mapping.method();
			ControllerMethod controllerMethod =
				new AnnotatedControllerMethod(controller, method);
			addControllerMethod(template, httpMethod, controllerMethod);
		}
		return this;
	}

	private boolean hasAnnotation(Method method, Class<? extends Annotation> annotation) {
		return method.getDeclaredAnnotation(annotation) != null;
	}

	private void checkMethod(Method method) {
		checkNotStatic(method);
		checkAccess(method);
		checkParameters(method);
		checkReturnType(method);
	}

	private void checkNotStatic(Method method) {
		if (Modifier.isStatic(method.getModifiers())) {
			throw new IllegalStateException("Method cannot be static");
		}
	}

	private void checkAccess(Method method) {
		if (!Modifier.isPublic(method.getModifiers())) {
			throw new IllegalStateException("Method must be public");
		}
	}

	private void checkParameters(Method method) {
		if (method.getParameters().length != 2) {
			throw new IllegalStateException("Illegal number of parameters");
		}
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes[0] != HttpRequest.class) {
			throw new IllegalStateException("First parameter mus be of type HttpRequest");
		}
		if (parameterTypes[1] != PathVariables.class) {
			throw new IllegalStateException("Second parameter must be of type PathVariables");
		}
	}

	private void checkReturnType(Method method) {
		if (method.getReturnType() != HttpResponse.class) {
			throw new IllegalStateException("Return type must be of type HttpResponse");
		}
	}

	public TreeServerConfiguration addResponseOnError(HttpStatus status,
													  Function<ErrorData, HttpResponse> producer) {
		errorResponses.put(status, producer);
		return this;
	}

	private static class AnnotatedControllerMethod implements ControllerMethod {
		private Object controller;
		private Method method;

		AnnotatedControllerMethod(Object controller, Method method) {
			this.controller = controller;
			this.method = method;
		}

		@Override
		public HttpResponse process(HttpRequest request, PathVariables variables) {
			try {
				return (HttpResponse) method.invoke(controller, request, variables);
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
