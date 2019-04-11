package com.jkojote.server.impl.config.controllers;

import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.PathVariables;
import com.jkojote.server.annotation.GetMapping;
import com.jkojote.server.annotation.RequestMapping;

@RequestMapping("/itc2")
public class InvalidController_InvalidPathTemplate {

	@GetMapping("{}")
	public HttpResponse a(HttpRequest request, PathVariables vars) {
		return null;
	}
}
