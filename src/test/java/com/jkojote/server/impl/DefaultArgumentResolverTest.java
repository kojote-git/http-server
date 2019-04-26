package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.PathVariables;
import com.jkojote.server.impl.config.converters.IntConverter;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultArgumentResolverTest {

	@Test
	public void resolve_resolvesArguments() {
		DefaultArgumentResolver resolver = new DefaultArgumentResolver();
		PathVariables variables = mock(PathVariables.class);
		List<ControllerMethod.Parameter> parameters = Arrays.asList(
			new PathVariableParameter(0, "message", String::valueOf, String.class),
			new PathVariableParameter(1, "n", IntConverter.INSTANCE, int.class)
		);
		ControllerMethod method = mock(ControllerMethod.class);
		HttpRequest request = mockRequest(HttpMethod.GET, "/");
		when(method.getParameters()).thenReturn(parameters);
		when(variables.getPathVariable("echo")).thenReturn("message");
		when(variables.getPathVariable("n")).thenReturn("5");

		Object[] resolved = resolver.resolve(method, request, variables);

		assertEquals(String.class, resolved[0].getClass());
		assertEquals(Integer.class, resolved[1].getClass());

//		assertEquals("message", resolved[0].toString());
//		assertEquals(5, resolved[1]);
	}

	private HttpRequest mockRequest(HttpMethod method, String uri) {
		HttpRequest request = mock(HttpRequest.class);
		when(request.getMethod()).thenReturn(method);
		when(request.getPath()).thenReturn(uri);
		return request;
	}

}
