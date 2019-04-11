package com.jkojote.server.impl.config.controllers;

import com.jkojote.server.HttpRequest;
import com.jkojote.server.PathVariables;
import com.jkojote.server.annotation.GetMapping;
import com.jkojote.server.annotation.RequestMapping;

@RequestMapping("/itc4")
public class InvalidTestController_InvaludReturnType {

	@GetMapping("/")
	public void invalidReturnType(HttpRequest req, PathVariables vars) {
	}
}
