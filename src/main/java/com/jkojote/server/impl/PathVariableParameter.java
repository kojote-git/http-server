package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.PathVariableConverter;

import java.util.function.Function;

public class PathVariableParameter implements ControllerMethod.Parameter {
	private int index;
	private String name;
	private PathVariableConverter<?> converter;
	private Class<?> type;

	public PathVariableParameter(int index, String name, PathVariableConverter<?> converter, Class<?> type) {
		this.index = index;
		this.name = name;
		this.converter = converter;
		this.type = type;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isPathVariable() {
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public PathVariableConverter<?> getConverter() {
		return converter;
	}

	@Override
	public Class<?> getType() {
		return type;
	}
}
