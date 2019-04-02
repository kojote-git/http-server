package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.PathVariables;

import java.util.regex.Pattern;

import static com.jkojote.server.ServerConfiguration.RequestResolution;

class ControllerMethodTree {
	private static Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{.*?\\}");

	private PathNode root;

	ControllerMethodTree() {
		this.root = new PathNode("");
	}

	PathNode getRoot() {
		return root;
	}

	void addControllerMethod(String pathTemplate,
							 HttpMethod httpMethod,
							 ControllerMethod controllerMethod) {
		String[] paths = pathTemplate.split("/");
		PathNode currentNode = root;
		for (String path : paths) {
			if (path.isEmpty()) {
				continue;
			}
			PathNode variableNode = findPathVariableNode(currentNode);
			PathNode pathNode = findNodeForPath(currentNode, path);
			if (variableNode != null && isPathVariable(path)) {
				currentNode = variableNode;
				continue;
			}
			if (pathNode == null) {
				pathNode = new PathNode(path);
				currentNode.addChild(pathNode);
			}
			currentNode = pathNode;
		}
		currentNode.putControllerMethod(httpMethod, controllerMethod);
	}

	private PathNode findNodeForPath(PathNode parent, String path) {
		for (PathNode child : parent.getChildren()) {
			if (child.getValue().equals(path)) {
				return child;
			}
		}
		return null;
	}

	private PathNode findPathVariableNode(PathNode parent) {
		for (PathNode child : parent.getChildren()) {
			if (child.isPathVariable()) {
				return child;
			}
		}
		return null;
	}

	private boolean isPathVariable(String value) {
		return PATH_VARIABLE_PATTERN.matcher(value).matches();
	}

	RequestResolution resolveControllerMethod(HttpRequest request) {
		String[] paths = request.getPath().split("/");
		if (isRequestForRoot(request)) {
			return returnRoot(request);
		}
		PathNode currentNode = root;
		PathVariablesImpl pathVariables = new PathVariablesImpl();
		for (String path: paths) {
			path = path.trim();
			if (path.isEmpty()) {
				continue;
			}
			PathNode variableNode = null;
			boolean exactNodeFound = false;
			for (PathNode child : currentNode.getChildren()) {
				if (child.getValue().equals(path)) {
					currentNode = child;
					exactNodeFound = true;
					break;
				} else if (child.isPathVariable()) {
					variableNode = child;
				}
			}
			if (!exactNodeFound) {
				if (variableNode == null) {
					return null;
				}
				currentNode = variableNode;
				pathVariables.addPathVariable(
					variableNode.getPathVariableName(), path
				);
			}
		}
		if (!currentNode.hasMethod(request.getMethod())) {
			return null;
		}
		return new RequestResolutionImpl(
			currentNode.getControllerMethod(request.getMethod()),
			pathVariables
		);
	}

	private boolean isRequestForRoot(HttpRequest request) {
		return request.getPath().equals("/");
	}

	private RequestResolution returnRoot(HttpRequest req) {
		ControllerMethod method = root.getControllerMethod(req.getMethod());
		if (method == null) {
			return null;
		}
		return new RequestResolutionImpl(method, new PathVariablesImpl());
	}

	private static class RequestResolutionImpl implements RequestResolution {
		private ControllerMethod controllerMethod;
		private PathVariables pathVariables;

		private RequestResolutionImpl(ControllerMethod controllerMethod,
									  PathVariables pathVariables) {
			this.controllerMethod = controllerMethod;
			this.pathVariables = pathVariables;
		}

		@Override
		public ControllerMethod getMethod() {
			return controllerMethod;
		}

		public PathVariables getPathVariables() {
			return pathVariables;
		}
	}
}
