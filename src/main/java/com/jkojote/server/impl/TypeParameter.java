package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;

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
		return null;
	}

	@Override
	public Class<?> getType() {
		return type;
	}
}
