package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

class RefControllerMethod implements ControllerMethod {
	private Object controller;
	private Method method;
	private List<Parameter> parameters;

	RefControllerMethod(Object controller, Method method, List<Parameter> parameters) {
		this.controller = controller;
		this.method = method;
		this.parameters = Collections.unmodifiableList(parameters);
	}

	@Override
	public HttpResponse execute(Object[] args) {
		try {
			return (HttpResponse) method.invoke(controller, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Parameter> getParameters() {
		return parameters;
	}
}
