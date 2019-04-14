package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.PathVariableConverter;

public class TypeParameter implements ControllerMethod.Parameter {
	private int index;
	private Class<?> type;

	public TypeParameter(int index, Class<?> type) {
		this.index = index;
		this.type = type;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isPathVariable() {
		return false;
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public PathVariableConverter<?> getConverter() {
		return String::valueOf;
	}

	@Override
	public Class<?> getType() {
		return type;
	}
}
