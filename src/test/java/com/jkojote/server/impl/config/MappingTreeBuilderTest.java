package com.jkojote.server.impl.config;

import com.jkojote.server.HttpMethod;
import com.jkojote.server.exceptions.InvalidControllerBasePathException;
import com.jkojote.server.exceptions.InvalidControllerMethodException;
import com.jkojote.server.exceptions.InvalidPathTemplateException;
import com.jkojote.server.impl.config.controllers.Controller1;
import com.jkojote.server.impl.config.controllers.Controller2;
import com.jkojote.server.impl.config.controllers.InvalidController_InvalidBasePath;
import com.jkojote.server.impl.config.controllers.InvalidController_InvalidPathTemplate;
import com.jkojote.server.impl.config.controllers.InvalidController_InvalidSignature;
import com.jkojote.server.impl.config.controllers.InvalidTestController_InvaludReturnType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MappingTreeBuilderTest {
	private MappingTreeBuilder builder = new MappingTreeBuilder();

	@Test
	public void build_controller1() {
		Controller1 tc = new Controller1();
		MappingTree tree = builder.build(tc);

		PathNode root = tree.getRoot();
		PathNode t1 = root.findChildNodeByValue("t1");
		PathNode a = t1.findChildNodeByValue("a");
		PathNode b = a.findChildNodeByValue("b");

		assertEquals(1, a.getPresentMethods().size());
		assertTrue(a.getPresentMethods().contains(HttpMethod.GET));

		assertEquals(2, b.getPresentMethods().size());
		assertTrue(b.getPresentMethods().contains(HttpMethod.GET));
		assertTrue(b.getPresentMethods().contains(HttpMethod.POST));
	}

	@Test(expected = InvalidControllerBasePathException.class)
	public void build_invalidController_invalidBasePath() {
		builder.build(new InvalidController_InvalidBasePath());
	}

	@Test
	public void buildTwoTree_mergeThem() {
		MappingTree merged = builder.build(new Controller1())
				.mergeWith(builder.build(new Controller2()));

		/*
		 * Must be:
		 *           root
		 *          /    \
		 *        t1      t2
		 *       /       /  \
		 *      a       a    b
		 *     /
		 *    b (GET, POST)
		 */
		PathNode root = merged.getRoot();
		PathNode t1 = root.findChildNodeByValue("t1");
		PathNode t2 = root.findChildNodeByValue("t2");

		PathNode t1A = t1.findChildNodeByValue("a");
		PathNode t1AB = t1A.findChildNodeByValue("b");

		assertEquals(1, t1A.getPresentMethods().size());
		assertTrue(t1A.getPresentMethods().contains(HttpMethod.GET));

		assertEquals(2, t1AB.getPresentMethods().size());
		assertTrue(t1AB.getPresentMethods().contains(HttpMethod.GET));
		assertTrue(t1AB.getPresentMethods().contains(HttpMethod.POST));
		assertTrue(t1AB.isLeaf());

		PathNode t2A = t2.findChildNodeByValue("a");
		PathNode t2B = t2.findChildNodeByValue("b");

		assertEquals(1, t2A.getPresentMethods().size());
		assertTrue(t2A.getPresentMethods().contains(HttpMethod.GET));
		assertTrue(t2A.isLeaf());

		assertEquals(1, t2B.getPresentMethods().size());
		assertTrue(t2B.getPresentMethods().contains(HttpMethod.GET));
		assertTrue(t2B.isLeaf());
	}

	@Test(expected = InvalidPathTemplateException.class)
	public void build_invalidController_invalidPathTemplate() {
		builder.build(new InvalidController_InvalidPathTemplate());
	}

	@Test(expected = InvalidControllerMethodException.class)
	public void build_invalidController_invalidReturnType() {
		builder.build(new InvalidTestController_InvaludReturnType());
	}
}
