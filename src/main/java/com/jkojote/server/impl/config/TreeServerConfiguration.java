package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.ServerConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TreeServerConfiguration implements ServerConfiguration {
	private MappingTree tree;
	private MappingTreeBuilder treeBuilder;
	private Map<HttpStatus, Function<ErrorData, HttpResponse>> errorResponses;

	public TreeServerConfiguration() {
		this.tree = new MappingTree();
		this.errorResponses = new HashMap<>();
		this.treeBuilder = new MappingTreeBuilder();
	}

	@Override
	public RequestResolution resolveRequest(HttpRequest request) {
		return tree.resolveRequest(request);
	}

	@Override
	public HttpResponse getResponseOnError(HttpStatus status, ErrorData errorData) {
		Function<ErrorData, HttpResponse> producer = errorResponses.get(status);
		return producer == null ? null : producer.apply(errorData);
	}

	public TreeServerConfiguration addControllerMethod(String template,
													   HttpMethod httpMethod,
													   ControllerMethod controllerMethod) {
		tree.addControllerMethod(template, httpMethod, controllerMethod);
		return this;
	}

	public TreeServerConfiguration addController(Object controller) {
		return addController(controller, MergeConflictOption.THROW_EXCEPTION);
	}

	public TreeServerConfiguration addController(Object controller, MergeConflictOption mergeConflictOption) {
		MappingTree tree = treeBuilder.build(controller);
		this.tree.mergeWith(tree, mergeConflictOption);
		return this;
	}

	public TreeServerConfiguration addResponseOnError(HttpStatus status,
													  Function<ErrorData, HttpResponse> producer) {
		errorResponses.put(status, producer);
		return this;
	}
}
