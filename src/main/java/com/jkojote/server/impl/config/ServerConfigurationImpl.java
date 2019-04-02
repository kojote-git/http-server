package com.jkojote.server.impl.config;

import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.ServerConfiguration;

public class ServerConfigurationImpl implements ServerConfiguration {

	@Override
	public RequestResolution resolveRequest(HttpRequest request) {
		return null;
	}

	@Override
	public HttpResponse getResponseOnError(HttpStatus status, String errorMessage) {
		return null;
	}

}
