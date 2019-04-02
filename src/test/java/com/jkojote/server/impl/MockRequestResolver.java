package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.PathVariables;
import com.jkojote.server.ServerConfiguration;


class MockRequestResolver implements ServerConfiguration {
	private RequestResolution resolution;

	MockRequestResolver(ControllerMethod method) {
		if (method == null) {
			this.resolution = null;
		} else {
			this.resolution = new MockRequestResolution(method);
		}
	}

	@Override
	public RequestResolution resolveRequest(HttpRequest request) {
		return resolution;
	}

	@Override
	public HttpResponse getResponseOnError(HttpStatus status, String message) {
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
}
