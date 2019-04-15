package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpMethod;

import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A path nodes are objects that with their children and parents form tree-like hierarchical
 * structure where each path node represents one object in this structure. <br/><br/>
 *
 * It is designed specifically for this library to be used as a holder of mappings between
 * http methods and controller methods. <br/><br/>
 *
 * For example. Let's say we have following mappings:<br/>
 * - {@code /home/page} - GET <br/>
 * - {@code /home/page} - POST <br/><br/>
 * Explanation:<br/><br/>
 *
 * Path template {@code /home/page} is mapped to two controllers, one of which processes
 * GET requests and the other processes POST requests.<br/><br/>
 *
 * This template then forms a tree with the root in {@code home} node and the last node {@code page}
 * which in turn holds these two mappings.<br/>
 *
 * Then when the request with the path {@code /home/page} and http method GET comes, we need
 * to traverse the tree from root until we find necessary controller; otherwise if necessary
 * controller is not found during traversing, we return nothing.
 */
class PathNode {
	private static Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{.*?\\}");
	private static Pattern CURLY_BRACES = Pattern.compile("\\{|\\}");

	private String value;
	private String pathVariableName;
	private PathNode parent;
	private Map<HttpMethod, ControllerMethod> controllers;
	private boolean pathVariable;
	private Collection<PathNode> children;

	PathNode(PathNode parent, String value) {
		this.value = value;
		this.parent = parent;
		this.controllers = new EnumMap<>(HttpMethod.class);
		this.pathVariable = PATH_VARIABLE_PATTERN.matcher(value).matches();
		this.children = new LinkedList<>();
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

	Set<HttpMethod> getPresentMethods() {
		return controllers.keySet();
	}

	void putControllerMethod(HttpMethod method, ControllerMethod controllerMethod) {
		this.controllers.put(method, controllerMethod);
	}

	Collection<PathNode> getChildren() {
		return children;
	}

	void addChild(PathNode node) {
		children.add(node);
		node.parent = this;
	}

	void removeChild(PathNode node) {
		node = findChildNodeByValue(node);
		if (node != null) {
			children.remove(node);
		}
	}

	boolean isLeaf() {
		return children.size() == 0;
	}

	public PathNode getParent() {
		return parent;
	}

	boolean hasMethod(HttpMethod httpMethod) {
		return controllers.containsKey(httpMethod);
	}

	PathNode findChildNodeByValue(PathNode child) {
		return findChildNodeByValue(child.value);
	}

	PathNode findChildNodeByValue(String value) {
		for (PathNode thisChild: children) {
			if (thisChild.value.equals(value)) {
				return thisChild;
			}
		}
		return null;
	}

	PathNode copy() {
		PathNode copy = new PathNode(null, value);
		copy.controllers = new EnumMap<>(controllers);
		copy.controllers.putAll(this.controllers);
		if (!isLeaf()) {
			for (PathNode child : children) {
				copy.children.add(child.copy());
			}
		}
		return copy;
	}

	String buildPath() {
		PathNode parent = this.parent;
		StringBuilder path = new StringBuilder();
		path.append(value);
		while (parent != null) {
			path.append("/").append(parent.value);
			parent = parent.parent;
		}
		String[] nodes = path.toString().split("/");
		path.delete(0, path.length());
		path.append("/");
		for (int i = nodes.length - 1; i >= 0; i--) {
			path.append(nodes[i]).append("/");
		}
		return path.toString();
	}
}
