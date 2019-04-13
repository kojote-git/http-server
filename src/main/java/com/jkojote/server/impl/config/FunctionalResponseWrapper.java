package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.FunctionalResponse;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.PathVariables;
import com.jkojote.server.impl.TypeParameter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class FunctionalResponseWrapper implements ControllerMethod {
	private static final List<Parameter> PARAMETERS;

	static {
		PARAMETERS = Collections.unmodifiableList(Arrays.asList(
			new TypeParameter(0, HttpRequest.class),
			new TypeParameter(1, PathVariables.class)
		));
	}

	private FunctionalResponse fr;

	FunctionalResponseWrapper(FunctionalResponse fr) {
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
