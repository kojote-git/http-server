package com.jkojote.server.impl.config;

import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.HttpStatus;
import com.jkojote.server.PathVariables;
import com.jkojote.server.annotation.RequestMapping;
import com.jkojote.server.bodies.ByteResponseBody;
import com.jkojote.server.impl.HttpResponseBuilder;

public class EchoController {

	@RequestMapping("/echo")
	public HttpResponse echo(HttpRequest request, PathVariables variables) {
		return stringResponse(HttpStatus.OK, "echo");
	}

	@RequestMapping("/echo/{message}")
	public HttpResponse echoMessage(HttpRequest request, PathVariables variables) {
		String message = variables.getPathVariable("message");
		return stringResponse(HttpStatus.OK, message);
	}

	@RequestMapping("/echo/{message}/{n}")
	public HttpResponse echoMessageNTimes(HttpRequest request, PathVariables vars) {
		String message = vars.getPathVariable("message");
		int n = vars.convertVariable("n", Integer::parseInt);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(message);
		}
		return stringResponse(HttpStatus.OK, sb.toString());
	}

	private HttpResponse stringResponse(HttpStatus status, String string) {
		byte[] bytes = string.getBytes();
		return HttpResponseBuilder.create()
			.setStatus(status)
			.addHeader("Content-Length", bytes.length)
			.addHeader("Content-Type", "text/plain; charset=\"UTF-8\"")
			.setResponseBody(new ByteResponseBody(bytes))
			.build();
	}
}
