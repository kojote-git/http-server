package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpMethod;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

class PathNode {
	private static Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{.*?\\}");

	private String value;
	private HashMap<HttpMethod, ControllerMethod> controllers;
	private boolean pathVariable;
	private List<PathNode> children;
	private List<PathNode> readonlyChildren;

	PathNode(String value) {
		this.value = value;
		this.controllers = new HashMap<>();
		this.pathVariable = PATH_VARIABLE_PATTERN.matcher(value).matches();
		this.children = new LinkedList<>();
		this.readonlyChildren = Collections.unmodifiableList(this.children);
	}

	boolean isPathVariable() {
		return pathVariable;
	}

	String getValue() {
		return value;
	}

	ControllerMethod getControllerMethod(HttpMethod method) {
		return controllers.get(method);
	}

	void putControllerMethod(HttpMethod method, ControllerMethod controllerMethod) {
		this.controllers.put(method, controllerMethod);
	}

	List<PathNode> getChildren() {
		return readonlyChildren;
	}

	void addChild(PathNode child) {
		children.add(child);
	}

	boolean hasMethod(HttpMethod httpMethod) {
		return controllers.containsKey(httpMethod);
	}
}
