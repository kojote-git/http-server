package com.jkojote.server.impl.config;

import com.jkojote.server.FunctionalResponse;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.ServerConfiguration;
import com.jkojote.server.annotation.GetMapping;
import com.jkojote.server.annotation.PostMapping;
import com.jkojote.server.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * An implementation of {@link ServerConfiguration} interface that uses tree-like structure
 * to hold mappings between requests and methods that process these requests.<br/>
 *
 * The mapping consists of path template, http method and, actually, the method that processes
 * the requests which comes to the server by this path with the http method.
 * For example, schematically the mapping might look like this: <br/><br/>
 *
 * {@code /echo/{message}} - {@code GET} - {@code method}. <br/><br/>
 *
 * It basically means that the requests that comes by the path {@code /echo/{message}} with
 * the {@code GET} http method are processed by the specified {@code method}. <br/><br/>
 *
 * Moreover, this object also supports processing of user-defined controllers - objects that have
 * annotated methods. The annotated method is annotated with {@link RequestMapping},
 * {@link GetMapping}, {@link PostMapping} etc. annotations.
 */
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

	public TreeServerConfiguration addMapping(String template,
											  HttpMethod httpMethod,
											  FunctionalResponse functionalResponse) {
		tree.addFunctionalResponse(template, httpMethod, functionalResponse);
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
