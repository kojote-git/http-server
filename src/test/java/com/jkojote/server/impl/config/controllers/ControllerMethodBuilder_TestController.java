package com.jkojote.server.impl.config.controllers;

import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.annotation.DirectVariablesMapping;
import com.jkojote.server.annotation.GetMapping;
import com.jkojote.server.annotation.PathVar;

public class ControllerMethodBuilder_TestController {

	@GetMapping("/cmb/{echo}")
	@DirectVariablesMapping
	public HttpResponse methodWithDirectMapping(String echo) {
		return null;
	}

	@GetMapping("/cmb/{echo}")
	@DirectVariablesMapping(startIndex = 1)
	public HttpResponse methodWithStartIndex(HttpRequest request, String echo) {
		return null;
	}

	@GetMapping("/cmb/{echo}/cmb/{n}")
	@DirectVariablesMapping(startIndex = 1)
	public HttpResponse methodWithTwoPathVariables(HttpRequest request, String echo, int n) {
		return null;
	}

	@GetMapping("/cmb/{echo}/cmb/{n}")
	public HttpResponse methodWithExplicitMapping(HttpRequest request,
												  @PathVar("echo") String echo,
												  @PathVar("n") long n) {
		return null;
	}

	@GetMapping("/cmb/{echo}/cmb/{n}")
	public HttpResponse methodWithExplicitMapping2(HttpRequest request,
												   @PathVar("echo") String echo) {
		return null;
	}

	@GetMapping("/cmb/{echo}/cmb/{n}")
	public HttpResponse methodWithExplicitMapping3(@PathVar("echo") String echo,
													HttpRequest request,
												   	@PathVar("n") int n) {
		return null;
	}

	@GetMapping("/cmb/{echo}")
	@DirectVariablesMapping(startIndex = -1)
	public HttpResponse methodWithNegativeStartIndex(String echo) {
		return null;
	}

	@GetMapping("/cmb/{message}")
	public HttpResponse methodWithDifferentPathVarName(@PathVar("msg") String message) {
		return null;
	}

	@GetMapping("/cmb")
	public HttpResponse methodWithInvalidParameterType(HttpResponse param) {
		return null;
	}

	@GetMapping("/cmb/{echo}")
	public HttpResponse methodWithUnconvertablePathVariable(@PathVar("echo") HttpRequest echo) {
		return null;
	}

	@GetMapping("/cmb/{echo}")
	@DirectVariablesMapping(startIndex = 1)
	public HttpResponse methodWithStartIndexThatExceedsNumberOfParameters(String echo) {
		return null;
	}

	// if it is direct mapping
	// all mapped path variables must go after another parameters
	// of the method
	@GetMapping("/cmb/{echo}/cmb/{n}")
	@DirectVariablesMapping
	public HttpResponse methodWithBrokenDirectMapping(String echo, int n, HttpRequest request) {
		return null;
	}

	@GetMapping("/cmb/{echo}/{n}")
	@DirectVariablesMapping(startIndex = 1)
	public HttpResponse methodWithWrongNumberOfPathVariables(HttpRequest request, String echo) {
		return null;
	}
}
