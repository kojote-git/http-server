package com.jkojote.server.impl.config.controllers;

import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.annotation.GetMapping;
import com.jkojote.server.annotation.RequestMapping;

@RequestMapping("/itc3")
public class InvalidController_InvalidSignature {

	@GetMapping("/")
	public HttpResponse invalidSignature(HttpRequest request) {
		return null;
	}
}
