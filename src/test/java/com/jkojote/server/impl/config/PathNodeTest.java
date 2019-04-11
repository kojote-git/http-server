package com.jkojote.server.impl.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PathNodeTest {

	@Test
	public void copy_testCopy() {
		PathNode root = new PathNode(null, "");
		PathNode a = new PathNode(root, "a");
		PathNode c = new PathNode(a, "c");

		root.addChild(a);
		a.addChild(c);

		PathNode rootCopy = root.copy();
		PathNode aCopy = root.findChildNodeByValue("a");
		PathNode cCopy = aCopy.findChildNodeByValue("c");

		assertEquals(1, rootCopy.getChildren().size());
		assertEquals(1, aCopy.getChildren().size());
		assertTrue(cCopy.isLeaf());

		assertNotNull(cCopy);
	}
}
