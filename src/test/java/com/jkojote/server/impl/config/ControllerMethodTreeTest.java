package com.jkojote.server.impl.config;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpMethod;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ControllerMethodTreeTest {
	private ControllerMethod method = (req, var) -> null;

	@Test
	public void addUnit() {
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

	private PathNode getChildNode(PathNode parent, String value) {
		for (PathNode child : parent.getChildren()) {
			if (child.getValue().equals(value)) {
				return child;
			}
		}
		return null;
	}
}
