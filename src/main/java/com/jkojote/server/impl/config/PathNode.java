package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpMethod;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

class PathNode {
	private static Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{.*?\\}");
	private static Pattern CURLY_BRACES = Pattern.compile("\\{|\\}");

	private String value;
	private String pathVariableName;
	private Map<HttpMethod, ControllerMethod> controllers;
	private boolean pathVariable;
	private List<PathNode> children;
	private List<PathNode> readonlyChildren;

	PathNode(String value) {
		this.value = value;
		this.controllers = new EnumMap<>(HttpMethod.class);
		this.pathVariable = PATH_VARIABLE_PATTERN.matcher(value).matches();
		this.children = new LinkedList<>();
		this.readonlyChildren = Collections.unmodifiableList(this.children);
		if (pathVariable) {
			pathVariableName = CURLY_BRACES.matcher(value).replaceAll("");
		}
	}

	boolean isPathVariable() {
		return pathVariable;
	}

	String getValue() {
		return value;
	}

	String getPathVariableName() {
		return pathVariableName;
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
