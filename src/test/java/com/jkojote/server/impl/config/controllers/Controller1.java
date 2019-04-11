package com.jkojote.server.impl.config.controllers;

import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.PathVariables;
import com.jkojote.server.annotation.GetMapping;
import com.jkojote.server.annotation.PostMapping;
import com.jkojote.server.annotation.RequestMapping;

@RequestMapping("/t1")
public class Controller1 {

	@GetMapping("a")
	public HttpResponse a(HttpRequest req, PathVariables vars) {
		return null;
	}

	@GetMapping("a/b")
	public HttpResponse abGet(HttpRequest req, PathVariables vars) {
		return null;
	}

	@PostMapping("a/b")
	public HttpResponse abPost(HttpRequest req, PathVariables vars) {
		return null;
	}

}