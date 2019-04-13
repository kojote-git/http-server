package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.FunctionalResponse;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.PathVariables;
import com.jkojote.server.ServerConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


class MockRequestResolver implements ServerConfiguration {
	private RequestResolution resolution;


	MockRequestResolver(FunctionalResponse functionalResponse) {
		if (functionalResponse == null) {
			this.resolution = null;
		} else {
			this.resolution = new MockRequestResolution(new FunctionalResponseWrapper(functionalResponse));
		}
	}

	@Override
	public RequestResolution resolveRequest(HttpRequest request) {
		return resolution;
	}

	@Override
	public HttpResponse getResponseOnError(HttpStatus status, ErrorData data) {
		return null;
	}

	private static class MockRequestResolution implements RequestResolution {
		private ControllerMethod method;

		private MockRequestResolution(ControllerMethod method) {
			this.method = method;
		}

		@Override
		public ControllerMethod getMethod() {
			return method;
		}

		@Override
		public PathVariables getPathVariables() {
			return null;
		}
	}

	private static class FunctionalResponseWrapper implements ControllerMethod {
		private static final List<Parameter> PARAMETERS;

		static {
			PARAMETERS = Collections.unmodifiableList(Arrays.asList(
					new TypeParameter(0, HttpRequest.class),
					new TypeParameter(1, PathVariables.class)
			));
		}

		private FunctionalResponse fr;

		private FunctionalResponseWrapper(FunctionalResponse fr) {
			this.fr = fr;
		}

		@Override
		public HttpResponse execute(Object[] args) {
			return fr.process((HttpRequest) args[0], (PathVariables) args[1]);
		}

		@Override
		public List<Parameter> getParameters() {
			return PARAMETERS;
		}
	}
}
