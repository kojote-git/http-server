package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.FunctionalResponse;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.PathVariables;
import com.jkojote.server.exceptions.MergeConflictException;

import java.util.regex.Pattern;

import static com.jkojote.server.ServerConfiguration.RequestResolution;

/**
 * Mapping tree is a special data structure, that contains
 * mappings for controller methods. <br/>
 *
 * Each node of this tree is represented by {@link PathNode}.
 * @see PathNode
 */
class MappingTree {
	private static Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{.*?\\}");

	private PathNode root;

	MappingTree() {
		this.root = new PathNode(null, "");
	}

	private MappingTree(PathNode root) {
		this.root = root;
	}

	PathNode getRoot() {
		return root;
	}

	MappingTree mergeWith(MappingTree tree, MergeConflictOption conflictOption) {
		return mergeTree(this.root, tree.root, conflictOption);
	}

	MappingTree mergeWith(MappingTree tree) {
		return mergeWith(tree, MergeConflictOption.THROW_EXCEPTION);
	}

	MappingTree copy() {
		PathNode newRoot = root.copy();
		return new MappingTree(newRoot);
	}

	private MappingTree mergeTree(PathNode thisRoot, PathNode targetRoot, MergeConflictOption conflictOption) {
		for (PathNode targetChild : targetRoot.getChildren()) {
			PathNode thisChild = thisRoot.findChildNodeByValue(targetChild);
			if (thisChild == null) {
				thisRoot.addChild(targetChild.copy());
			} else if (targetChild.isLeaf()) {
				mergeNode(thisChild, targetChild, conflictOption);
			} else {
				mergeNode(thisChild, targetChild, conflictOption);
				mergeTree(thisChild, targetChild, conflictOption);
			}
		}
		return this;
	}

	private void mergeNode(PathNode target, PathNode toBeMerged, MergeConflictOption conflictOption) {
		for (HttpMethod method : toBeMerged.getPresentMethods()) {
			if (target.hasMethod(method)) {
				resolveMergeConflict(target, toBeMerged, method, conflictOption);
			} else {
				target.putControllerMethod(method, toBeMerged.getControllerMethod(method));
			}
		}
	}

	private void resolveMergeConflict(PathNode a, PathNode b, HttpMethod method, MergeConflictOption conflictOption) {
		switch (conflictOption) {
			case THROW_EXCEPTION:
				throw new MergeConflictException(
					"merge conflict for path " + a.buildPath(),
					method,
					a.buildPath()
				);
			case OVERWRITE:
				a.putControllerMethod(method, b.getControllerMethod(method));
				break;
			case SILENT:
				break;
		}
	}

	void addFunctionalResponse(String pathTemplate,
							   HttpMethod httpMethod,
							   FunctionalResponse functionalResponse) {
		addFunctionalResponse(pathTemplate, httpMethod, functionalResponse, MergeConflictOption.THROW_EXCEPTION);
	}

	void addFunctionalResponse(String pathTemplate,
							   HttpMethod httpMethod,
							   FunctionalResponse functionalResponse,
							   MergeConflictOption mergeConflictOption) {
		addControllerMethod(
			pathTemplate, httpMethod,
			new FunctionalResponseWrapper(functionalResponse),
			mergeConflictOption
		);
	}


	void addControllerMethod(String pathTemplate,
							 HttpMethod httpMethod,
							 ControllerMethod controllerMethod,
							 MergeConflictOption mergeConflictOption) {
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
				nextNode = new PathNode(currentNode, nodeValue);
				currentNode.addChild(nextNode);
			}
			currentNode = nextNode;
		}
		putMethod(currentNode, httpMethod,
				controllerMethod,
				pathTemplate,
				mergeConflictOption
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

	/*
	 * Traverse the tree until it finds the necessary controller method to process
	 * given request or return null if there is no such controller method
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

	private void putMethod(PathNode finalNode,
						   HttpMethod httpMethod,
						   ControllerMethod controllerMethod,
						   String pathTemplate,
						   MergeConflictOption option) {
		if (finalNode.getControllerMethod(httpMethod) != null) {
			switch (option) {
				case THROW_EXCEPTION:
					throw new MergeConflictException(
						"mergeTree conflict with template " + pathTemplate, httpMethod,
						pathTemplate
					);
				case OVERWRITE:
					finalNode.putControllerMethod(httpMethod, controllerMethod);
					break;
				case SILENT:
					break;
			}
		} else {
			finalNode.putControllerMethod(httpMethod, controllerMethod);
		}
	}

	private static class RequestResolutionImpl implements RequestResolution {
		private ControllerMethod functionalResponse;
		private PathVariables pathVariables;

		private RequestResolutionImpl(ControllerMethod functionalResponse,
									  PathVariables pathVariables) {
			this.functionalResponse = functionalResponse;
			this.pathVariables = pathVariables;
		}

		@Override
		public ControllerMethod getMethod() {
			return functionalResponse;
		}

		public PathVariables getPathVariables() {
			return pathVariables;
		}
	}
}
