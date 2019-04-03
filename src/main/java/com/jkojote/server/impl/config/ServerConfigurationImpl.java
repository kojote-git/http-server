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

public class ServerConfigurationImpl implements ServerConfiguration {
	private ControllerMethodTree tree;
	private Map<HttpStatus, Function<ErrorData, HttpResponse>> errorResponses;

	public ServerConfigurationImpl() {
		this.tree = new ControllerMethodTree();
		this.errorResponses = new HashMap<>();
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

	public ServerConfigurationImpl addControllerMethod(String template,
													   HttpMethod httpMethod,
													   ControllerMethod controllerMethod) {
		tree.addControllerMethod(template, httpMethod, controllerMethod);
		return this;
	}

	// TODO
	public ServerConfigurationImpl addController(Object controller) {
		return this;
	}

	public ServerConfigurationImpl addResponseOnError(HttpStatus status,
													  Function<ErrorData, HttpResponse> producer) {
		errorResponses.put(status, producer);
		return this;
	}

}
