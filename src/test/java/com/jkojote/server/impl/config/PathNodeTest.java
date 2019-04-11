package com.jkojote.server.impl.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PathNodeTest {

	@Test
	public void copy_testCopy() {
		PathNode root = new PathNode(null, "");
		PathNode a = new PathNode(root, "a");
		PathNode c = new PathNode(a, "c");

		root.addChild(a);
		a.addChild(c);

		PathNode cCopy = c.copy();
		PathNode aCopy = cCopy.getParent();
		PathNode rootCopy = aCopy.getParent();

		assertEquals("c", cCopy.getValue());
		assertEquals("a", aCopy.getValue());
		assertEquals("", rootCopy.getValue());
		assertNull(rootCopy.getParent());
	}
}
