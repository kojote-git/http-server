package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.HttpResponse;
import com.jkojote.server.annotation.GetMapping;
import com.jkojote.server.exceptions.InvalidMappingException;
import com.jkojote.server.impl.config.controllers.ControllerMethodBuilder_TestController;

import org.junit.Test;
import static com.jkojote.server.ControllerMethod.Parameter;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ControllerMethodBuilderTest {
	private ControllerMethodBuilder builder;
	private Object controller;
	private Method methodWithDirectMapping;
	private Method methodWithStartIndex;
	private Method methodWithTwoPathVariables;
	private Method methodWithBrokenDirectMapping;
	private Method methodWithWrongNumberOfPathVariables;
	private Method methodWithExplicitMapping;
	private Method methodWithExplicitMapping2;
	private Method methodWithExplicitMapping3;
	private Method methodWithStartIndexThatExceedsNumberOfParameters;
	private Method methodWithNegativeStartIndex;
	private Method methodWithDifferentPathVarName;
	private Method methodWithInvalidParameterType;
	private Method methodWithUnconvertablePathVariable;

	public ControllerMethodBuilderTest() throws Exception {
		this.builder = new ControllerMethodBuilder();
		this.controller = new ControllerMethodBuilder_TestController();
		this.methodWithDirectMapping = controller.getClass()
			.getMethod("methodWithDirectMapping", String.class);
		this.methodWithStartIndex = controller.getClass()
			.getMethod("methodWithStartIndex", HttpRequest.class, String.class);
		this.methodWithTwoPathVariables = controller.getClass()
			.getMethod("methodWithTwoPathVariables", HttpRequest.class, String.class, int.class);
		this.methodWithBrokenDirectMapping = controller.getClass()
			.getMethod("methodWithBrokenDirectMapping", String.class, int.class, HttpRequest.class);
		this.methodWithWrongNumberOfPathVariables = controller.getClass()
			.getMethod("methodWithWrongNumberOfPathVariables", HttpRequest.class, String.class);
		this.methodWithExplicitMapping = controller.getClass()
			.getMethod("methodWithExplicitMapping", HttpRequest.class, String.class, long.class);
		this.methodWithExplicitMapping2 = controller.getClass()
			.getMethod("methodWithExplicitMapping2", HttpRequest.class, String.class);
		this.methodWithExplicitMapping3 = controller.getClass()
			.getMethod("methodWithExplicitMapping3", String.class, HttpRequest.class, int.class);
		this.methodWithStartIndexThatExceedsNumberOfParameters = controller.getClass()
			.getMethod("methodWithStartIndexThatExceedsNumberOfParameters", String.class);
		this.methodWithNegativeStartIndex = controller.getClass()
			.getMethod("methodWithNegativeStartIndex", String.class);
		this.methodWithDifferentPathVarName = controller.getClass()
			.getMethod("methodWithDifferentPathVarName", String.class);
		this.methodWithInvalidParameterType = controller.getClass()
			.getMethod("methodWithInvalidParameterType", HttpResponse.class);
		this.methodWithUnconvertablePathVariable = controller.getClass()
			.getMethod("methodWithUnconvertablePathVariable", HttpRequest.class);
	}

	private String getTemplate(Method method) {
		return method.getDeclaredAnnotation(GetMapping.class).value();
	}

	@Test
	public void buildOf_buildMethodWithDirectMapping() {
		ControllerMethod method = builder.buildOf(getTemplate(methodWithDirectMapping),
			controller, methodWithDirectMapping
		);

		assertEquals(1, method.getParameters().size());
		Parameter param = method.getParameters().get(0);
		assertTrue(param.isPathVariable());
		assertEquals("echo", param.getName());
		assertEquals(String.class, param.getType());
		assertEquals(0, param.getIndex());
	}

	@Test
	public void buildOf_buildMethodWithDirectMappingAndStartIndex() {
		ControllerMethod method = builder.buildOf(getTemplate(methodWithStartIndex),
			controller, methodWithStartIndex
		);

		assertEquals(2, method.getParameters().size());
		Parameter firstParam = method.getParameters().get(0);
		Parameter secondParam = method.getParameters().get(1);

		assertFalse(firstParam.isPathVariable());
		assertEquals(0, firstParam.getIndex());
		assertEquals(HttpRequest.class, firstParam.getType());
		assertEquals("", firstParam.getName());

		assertTrue(secondParam.isPathVariable());
		assertEquals(1, secondParam.getIndex());
		assertEquals(String.class, secondParam.getType());
		assertEquals("echo", secondParam.getName());
	}

	@Test
	public void buildOf_buildMethodWithTwoPathVariables() {
		ControllerMethod method = builder.buildOf(getTemplate(methodWithTwoPathVariables),
			controller, methodWithTwoPathVariables
		);

		assertEquals(3, method.getParameters().size());
		Parameter firstParam = method.getParameters().get(0);
		Parameter secondParam = method.getParameters().get(1);
		Parameter thirdParam = method.getParameters().get(2);

		assertFalse(firstParam.isPathVariable());
		assertEquals(0, firstParam.getIndex());
		assertEquals(HttpRequest.class, firstParam.getType());
		assertEquals("", firstParam.getName());

		assertTrue(secondParam.isPathVariable());
		assertEquals(1, secondParam.getIndex());
		assertEquals(String.class, secondParam.getType());
		assertEquals("echo", secondParam.getName());

		assertTrue(thirdParam.isPathVariable());
		assertEquals(2, thirdParam.getIndex());
		assertEquals(int.class, thirdParam.getType());
		assertEquals("n", thirdParam.getName());
	}

	@Test
	public void buildOf_buildMethodWithExplicitMapping() {
		ControllerMethod method = builder.buildOf(getTemplate(methodWithExplicitMapping),
			controller, methodWithExplicitMapping
		);

		assertEquals(3, method.getParameters().size());

		Parameter firstParam = method.getParameters().get(0);
		Parameter secondParam = method.getParameters().get(1);
		Parameter thirdParam = method.getParameters().get(2);

		assertFalse(firstParam.isPathVariable());
		assertEquals(0, firstParam.getIndex());
		assertEquals(HttpRequest.class, firstParam.getType());
		assertEquals("", firstParam.getName());

		assertTrue(secondParam.isPathVariable());
		assertEquals(1, secondParam.getIndex());
		assertEquals(String.class, secondParam.getType());
		assertEquals("echo", secondParam.getName());

		assertTrue(thirdParam.isPathVariable());
		assertEquals(2, thirdParam.getIndex());
		assertEquals(long.class, thirdParam.getType());
		assertEquals("n", thirdParam.getName());
	}

	@Test
	public void buildOf_buildMethodWithExplicitMapping2() {
		ControllerMethod method = builder.buildOf(getTemplate(methodWithExplicitMapping2),
				controller, methodWithExplicitMapping2
		);

		assertEquals(2, method.getParameters().size());

		Parameter firstParam = method.getParameters().get(0);
		Parameter secondParam = method.getParameters().get(1);

		assertFalse(firstParam.isPathVariable());
		assertEquals(0, firstParam.getIndex());
		assertEquals(HttpRequest.class, firstParam.getType());
		assertEquals("", firstParam.getName());

		assertTrue(secondParam.isPathVariable());
		assertEquals(1, secondParam.getIndex());
		assertEquals(String.class, secondParam.getType());
		assertEquals("echo", secondParam.getName());
	}

	@Test
	public void buildOf_buildMethodWithExplicitMapping3() {
		ControllerMethod method = builder.buildOf(getTemplate(methodWithExplicitMapping3),
				controller, methodWithExplicitMapping3
		);

		assertEquals(3, method.getParameters().size());

		Parameter firstParam = method.getParameters().get(0);
		Parameter secondParam = method.getParameters().get(1);
		Parameter thirdParam = method.getParameters().get(2);

		assertTrue(firstParam.isPathVariable());
		assertEquals(0, firstParam.getIndex());
		assertEquals(String.class, firstParam.getType());
		assertEquals("echo", firstParam.getName());

		assertFalse(secondParam.isPathVariable());
		assertEquals(1, secondParam.getIndex());
		assertEquals(HttpRequest.class, secondParam.getType());
		assertEquals("", secondParam.getName());

		assertTrue(thirdParam.isPathVariable());
		assertEquals(2, thirdParam.getIndex());
		assertEquals(int.class, thirdParam.getType());
		assertEquals("n", thirdParam.getName());
	}

	@Test(expected = InvalidMappingException.class)
	public void buildOf_buildMethodWithBrokenDirectMapping() {
		builder.buildOf(getTemplate(methodWithTwoPathVariables),
			controller,
			methodWithBrokenDirectMapping
		);
	}

	@Test(expected = InvalidMappingException.class)
	public void buildOf_buildMethodWithWrongNumberOfPathVariableParameters() {
		builder.buildOf(getTemplate(methodWithWrongNumberOfPathVariables),
			controller,
			methodWithWrongNumberOfPathVariables
		);
	}

	@Test(expected = InvalidMappingException.class)
	public void buildOf_buildMethodWithNegativeStartIndex() {
		builder.buildOf(getTemplate(methodWithNegativeStartIndex),
			controller, methodWithNegativeStartIndex
		);
	}

	@Test(expected = InvalidMappingException.class)
	public void buildOf_buildMethodWithStartIndexThatExceedsNumberOfParameters() {
		builder.buildOf(getTemplate(methodWithStartIndexThatExceedsNumberOfParameters),
			controller, methodWithStartIndexThatExceedsNumberOfParameters
		);
	}

	@Test(expected = InvalidMappingException.class)
	public void buildOf_buildMethodWithDifferentPathVarName() {
		builder.buildOf(getTemplate(methodWithDifferentPathVarName),
			controller, methodWithDifferentPathVarName
		);
	}

	@Test(expected = InvalidMappingException.class)
	public void buildOf_buildMethodWithInvalidParameterType() {
		builder.buildOf(getTemplate(methodWithInvalidParameterType),
			controller, methodWithInvalidParameterType
		);
	}

	@Test(expected = InvalidMappingException.class)
	public void buildOf_buildMethodWithUnconvertablePathVariable() {
		builder.buildOf(getTemplate(methodWithUnconvertablePathVariable),
			controller, methodWithUnconvertablePathVariable
		);
	}
}
