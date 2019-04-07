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
		String[] nodes = pathTemplate.split("/");
		PathNode currentNode = root;
		for (String nodeValue : nodes) {
			if (nodeValue.isEmpty()) {
				continue;
			}
			PathNode nextNode = findAnyNode(currentNode, nodeValue);
			if (nextNode != null && isPathVariable(nodeValue)) {
				if (!nodeValue.equals(nextNode.getValue())) {
					throw new IllegalStateException(
						"cannot have two different path variables at the same level"
					);
				}
			} else if (nextNode == null || nextNode.isPathVariable()) {
				nextNode = new PathNode(nodeValue);
				currentNode.addChild(nextNode);
			}
			currentNode = nextNode;
		}
		currentNode.putControllerMethod(httpMethod, controllerMethod);
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

	RequestResolution resolveRequest(HttpRequest request) {
		// split path such as /a/b/c into [a, b, c]
		// cannot figure out the name for the a, b and c itself
		// so each of them is called a node simply
		String[] nodes = request.getPath().split("/");
		if (isRequestForRoot(request)) {
			return returnRoot(request);
		}
		return resolveRequest(request, nodes);
	}

	/**
	 * Traverse the tree until it finds the appropriate controller method
	 * and return null if there is no appropriate controller method
	 */
	private RequestResolution resolveRequest(HttpRequest request, String[] nodes) {
		PathNode currentNode = root;
		PathVariablesImpl pathVariables = new PathVariablesImpl();
		for (String nodeValue: nodes) {
			nodeValue = nodeValue.trim();
			if (nodeValue.isEmpty()) {
				continue;
			}
			currentNode = findAnyNode(currentNode, nodeValue);
			if (currentNode == null) {
				return null;
			}
			if (currentNode.isPathVariable()) {
				pathVariables.addPathVariable(
					currentNode.getPathVariableName(), nodeValue
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

	private boolean isPathVariable(String value) {
		return PATH_VARIABLE_PATTERN.matcher(value).matches();
	}

	/**
	 * return child node of the parent that has the value the same as nodeValue;
	 * otherwise search for path variable and return it
	 * otherwise return null
	 */
	private PathNode findAnyNode(PathNode parent, String nodeValue) {
		PathNode res = null;
		PathNode variableNode = null;
		for (PathNode child : parent.getChildren()) {
			if (child.getValue().equals(nodeValue)) {
				res = child;
				break;
			} else if (child.isPathVariable()) {
				variableNode = child;
			}
		}
		return res == null ? variableNode : res;
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
