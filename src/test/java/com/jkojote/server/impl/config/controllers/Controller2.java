package com.jkojote.server.impl.config.controllers;

import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.PathVariables;
import com.jkojote.server.annotation.GetMapping;
import com.jkojote.server.annotation.RequestMapping;

@RequestMapping("/t2")
public class Controller2 {

	@GetMapping("a")
	public HttpResponse a(HttpRequest request, PathVariables vars) {
		return null;
	}

	@GetMapping("b")
	public HttpResponse b(HttpRequest request, PathVariables vars) {
		return null;
	}
}
