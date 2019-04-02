package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.PathVariables;

import static com.jkojote.server.ServerConfiguration.RequestResolution;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControllerMethodTreeTest {
	private ControllerMethod method = (req, var) -> null;

	@Test
	public void addControllerMethod_addMethodsAndCheckIfTreeIsSuccessfullyBuilt() {
		ControllerMethodTree tree = new ControllerMethodTree();
		tree.addControllerMethod("/", HttpMethod.GET, method);
		tree.addControllerMethod("/a/aa", HttpMethod.GET, method);
		tree.addControllerMethod("/a/ab", HttpMethod.POST, method);
		tree.addControllerMethod("/a/ab", HttpMethod.GET, method);
		tree.addControllerMethod("/a/ab/abc", HttpMethod.GET, method);
		tree.addControllerMethod("/b", HttpMethod.GET, method);
		PathNode root = tree.getRoot();

		assertEquals(2, root.getChildren().size());
		assertEquals(method, root.getControllerMethod(HttpMethod.GET));

		PathNode a = getChildNode(root, "a");
		assertNotNull(a);
		assertEquals(2, a.getChildren().size());

		PathNode ab = getChildNode(a, "ab");
		assertNotNull(ab);
		assertEquals(1, ab.getChildren().size());
		assertEquals(method, ab.getControllerMethod(HttpMethod.GET));
		assertEquals(method, ab.getControllerMethod(HttpMethod.POST));

		PathNode abc = getChildNode(ab, "abc");
		assertNotNull(abc);
		assertEquals(0, abc.getChildren().size());
		assertEquals(method, ab.getControllerMethod(HttpMethod.GET));

		PathNode b = getChildNode(root, "b");
		assertNotNull(b);
		assertEquals(0, b.getChildren().size());
		assertEquals(method, b.getControllerMethod(HttpMethod.GET));
	}

	@Test
	public void resolveControllerMethod() {
		ControllerMethod methodGet = (req, var) -> null;
		ControllerMethod methodPost = (req, var) -> null;
		ControllerMethodTree tree = new ControllerMethodTree();
		assertNotEquals(methodGet, methodPost);
		tree.addControllerMethod("/a/ab", HttpMethod.GET, methodGet);
		tree.addControllerMethod("/a/{ab}", HttpMethod.POST, methodPost);
		tree.addControllerMethod("/a/{ab}/ab", HttpMethod.POST, methodPost);
		tree.addControllerMethod("/a/cd/{b}/{c}/d/", HttpMethod.GET, methodGet);

		HttpRequest request = mockRequest("/a/ab", HttpMethod.GET);
		RequestResolution resolution = tree.resolveControllerMethod(request);
		assertNotNull(resolution);
		assertEquals(methodGet, resolution.getMethod());
		assertEquals(0, resolution.getPathVariables().size());

		request = mockRequest("/a/1", HttpMethod.POST);
		resolution = tree.resolveControllerMethod(request);
		assertNotNull(resolution);
		assertEquals(methodPost, resolution.getMethod());
		assertEquals(1, resolution.getPathVariables().size());
		assertEquals("1", resolution.getPathVariables().getPathVariable("ab"));

		request = mockRequest("/a/1/ab", HttpMethod.POST);
		resolution = tree.resolveControllerMethod(request);
		assertNotNull(resolution);
		assertEquals(methodPost, resolution.getMethod());
		assertEquals(1, resolution.getPathVariables().size());
		assertEquals("1", resolution.getPathVariables().getPathVariable("ab"));

		request = mockRequest("/a/cd/b/c/d", HttpMethod.GET);
		resolution = tree.resolveControllerMethod(request);
		PathVariables vars = resolution.getPathVariables();
		assertNotNull(resolution);
		assertEquals(methodGet, resolution.getMethod());
		assertEquals(2, vars.size());
		assertEquals("b", vars.getPathVariable("b"));
		assertEquals("c", vars.getPathVariable("c"));
	}

	private HttpRequest mockRequest(String path, HttpMethod method) {
		HttpRequest request = mock(HttpRequest.class);
		when(request.getPath()).then((mock) -> path);
		when(request.getMethod()).then((mock) -> method);
		return request;
	}

	private PathNode getChildNode(PathNode parent, String value) {
		for (PathNode child : parent.getChildren()) {
			if (child.getValue().equals(value)) {
				return child;
			}
		}
		return null;
	}
}
