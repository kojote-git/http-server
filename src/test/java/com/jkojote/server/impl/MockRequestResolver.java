package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.PathVariables;
import com.jkojote.server.RequestResolver;

class MockRequestResolver implements RequestResolver {
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
