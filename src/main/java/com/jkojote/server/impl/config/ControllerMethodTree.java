package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.PathVariables;


import static com.jkojote.server.ServerConfiguration.RequestResolution;

class ControllerMethodTree {
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
		int currentPathUnitIndex = 0;
		PathNode currentNode = root;
		for (; currentPathUnitIndex < paths.length; currentPathUnitIndex++) {
			String path = paths[currentPathUnitIndex];
			if (path.isEmpty()) {
				continue;
			}
			PathNode pathNode = findNodeForPathUnit(currentNode, path);
			if (pathNode == null) {
				pathNode = new PathNode(path);
				currentNode.addChild(pathNode);
			}
			currentNode = pathNode;
		}
		currentNode.putControllerMethod(httpMethod, controllerMethod);
	}

	private PathNode findNodeForPathUnit(PathNode parent, String pathUnit) {
		for (PathNode child : parent.getChildren()) {
			if (child.getValue().equals(pathUnit)) {
				return child;
			}
		}
		return findPathVariableNode(parent);
	}

	private PathNode findPathVariableNode(PathNode parent) {
		for (PathNode child : parent.getChildren()) {
			if (child.isPathVariable()) {
				return child;
			}
		}
		return null;
	}

	//TODO
	RequestResolution resolveControllerMethod(HttpRequest request) {
		return null;
	}

	private static class RequestResolutionImpl {
		private ControllerMethod controllerMethod;
		private PathVariables pathVariables;

		private RequestResolutionImpl(ControllerMethod controllerMethod,
									  PathVariables pathVariables) {
			this.controllerMethod = controllerMethod;
			this.pathVariables = pathVariables;
		}

		public ControllerMethod getControllerMethod() {
			return controllerMethod;
		}

		public PathVariables getPathVariables() {
			return pathVariables;
		}
	}
}
