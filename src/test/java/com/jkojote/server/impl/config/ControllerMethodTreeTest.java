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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControllerMethodTreeTest {
	private ControllerMethod method = (req, var) -> null;

	@Test(expected = MergeConflictException.class)
	public void merge_mergeTwoTreesWithThrowExceptionOption_throwsException() {
		ControllerMethodTree t1 = new ControllerMethodTree();
		ControllerMethodTree t2 = new ControllerMethodTree();
		t1.addControllerMethod("/a/c", HttpMethod.GET, null);
		t1.addControllerMethod("/a/d", HttpMethod.GET, null);
		t1.addControllerMethod("/a/e", HttpMethod.GET, null);
		t1.addControllerMethod("/b/f", HttpMethod.GET, null);

		t2.addControllerMethod("/g", HttpMethod.GET, null);
		t2.addControllerMethod("/a/h", HttpMethod.GET, null);
		t2.addControllerMethod("/a/i", HttpMethod.GET, null);
		t2.addControllerMethod("/a/e", HttpMethod.GET, null);

		t1.mergeWith(t2, MergeConflictOption.THROW_EXCEPTION);
	}

	@Test
	public void merge_mergeTwoTreesWithOverwriteOption_testTheSecondTreeLeftUntouched() {
		ControllerMethodTree t1 = new ControllerMethodTree();
		ControllerMethodTree t2 = new ControllerMethodTree();

		t1.addControllerMethod("/a", HttpMethod.GET, null);
		t1.addControllerMethod("/a", HttpMethod.POST, null);
		t1.addControllerMethod("/b", HttpMethod.GET, null);

		t2.addControllerMethod("/a", HttpMethod.PUT, null);
		t2.addControllerMethod("/b", HttpMethod.POST, null);
		t2.addControllerMethod("/b", HttpMethod.PUT, null);
		t2.addControllerMethod("/c/d", HttpMethod.POST, null);

		t1.mergeWith(t2, MergeConflictOption.OVERWRITE);

		PathNode t2Root = t2.getRoot();

		PathNode a = t2Root.findChildNodeByValue("a");
		PathNode b = t2Root.findChildNodeByValue("b");
		PathNode c = t2Root.findChildNodeByValue("c");

		assertEquals(t2Root, a.getParent());
		assertEquals(t2Root, b.getParent());
		assertEquals(t2Root, c.getParent());

		PathNode d = c.findChildNodeByValue("d");

		assertEquals(c, d.getParent());
	}

	@Test
	public void merge_mergeTwoTreesWithOverwriteOption_testMergeCorrectness() {
		ControllerMethodTree t1 = new ControllerMethodTree();
		ControllerMethodTree t2 = new ControllerMethodTree();

		t1.addControllerMethod("/a", HttpMethod.GET, null);
		t1.addControllerMethod("/a", HttpMethod.POST, null);
		t1.addControllerMethod("/b", HttpMethod.GET, null);

		t2.addControllerMethod("/a", HttpMethod.PUT, null);
		t2.addControllerMethod("/b", HttpMethod.POST, null);
		t2.addControllerMethod("/b", HttpMethod.PUT, null);
		t2.addControllerMethod("/c", HttpMethod.POST, null);

		t1.mergeWith(t2, MergeConflictOption.OVERWRITE);

		PathNode t1Root = t1.getRoot();

		PathNode a = t1Root.findChildNodeByValue("a");
		PathNode b = t1Root.findChildNodeByValue("b");
		PathNode c = t1Root.findChildNodeByValue("c");

		assertEquals(3, a.getPresentMethods().size());
		assertTrue(a.getPresentMethods().contains(HttpMethod.GET));
		assertTrue(a.getPresentMethods().contains(HttpMethod.POST));
		assertTrue(a.getPresentMethods().contains(HttpMethod.PUT));

		assertEquals(3, b.getPresentMethods().size());
		assertTrue(b.getPresentMethods().contains(HttpMethod.GET));
		assertTrue(b.getPresentMethods().contains(HttpMethod.POST));
		assertTrue(b.getPresentMethods().contains(HttpMethod.PUT));

		assertEquals(1, c.getPresentMethods().size());
		assertTrue(c.getPresentMethods().contains(HttpMethod.POST));
	}

	@Test
	public void merge_mergeTwoTreesWithOverwriteOption_testChildPresence() {
		ControllerMethodTree t1 = new ControllerMethodTree();
		ControllerMethodTree t2 = new ControllerMethodTree();
		t1.addControllerMethod("/a/c", HttpMethod.GET, null);
		t1.addControllerMethod("/a/d", HttpMethod.GET, null);
		t1.addControllerMethod("/a/e", HttpMethod.GET, null);
		t1.addControllerMethod("/b/f", HttpMethod.GET, null);

		t2.addControllerMethod("/g", HttpMethod.GET, null);
		t2.addControllerMethod("/a/h", HttpMethod.GET, null);
		t2.addControllerMethod("/a/i", HttpMethod.GET, null);
		t2.addControllerMethod("/a/e", HttpMethod.POST, null);

		t1.mergeWith(t2, MergeConflictOption.OVERWRITE);

		PathNode root = t1.getRoot();
		PathNode a = root.findChildNodeByValue("a");
		PathNode b = root.findChildNodeByValue("b");
		PathNode g = root.findChildNodeByValue("g");
		assertNotNull(a);
		assertNotNull(b);
		assertNotNull(g);

		assertNotNull(a.findChildNodeByValue("c"));
		assertNotNull(a.findChildNodeByValue("d"));
		assertNotNull(a.findChildNodeByValue("e"));
		assertNotNull(a.findChildNodeByValue("h"));
		assertNotNull(a.findChildNodeByValue("i"));

		assertEquals(5, a.getChildren().size());
		assertEquals(1, b.getChildren().size());
		assertTrue(g.isLeaf());
	}

	@Test
	public void merge_MergeTwoTreesWithSilentOption() {
		ControllerMethodTree t1 = new ControllerMethodTree();
		ControllerMethodTree t2 = new ControllerMethodTree();
		t1.addControllerMethod("/a/c", HttpMethod.GET, null);
		t1.addControllerMethod("/a/d", HttpMethod.GET, null);
		t1.addControllerMethod("/a/e", HttpMethod.GET, null);
		t1.addControllerMethod("/b/f", HttpMethod.GET, null);

		t2.addControllerMethod("/g", HttpMethod.GET, null);
		t2.addControllerMethod("/a/h", HttpMethod.GET, null);
		t2.addControllerMethod("/a/i", HttpMethod.GET, null);
		t2.addControllerMethod("/a/e", HttpMethod.GET, null);

	}

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
	public void resolveControllerMethod_successfullyResolveMethods() {
		ControllerMethod methodGet = (req, var) -> null;
		ControllerMethod methodPost = (req, var) -> null;
		ControllerMethodTree tree = new ControllerMethodTree();
		assertNotEquals(methodGet, methodPost);
		tree.addControllerMethod("/a/ab", HttpMethod.GET, methodGet);
		tree.addControllerMethod("/a/ab", HttpMethod.POST, methodPost);
		tree.addControllerMethod("/a/{ab}", HttpMethod.POST, methodPost);
		tree.addControllerMethod("/a/{ab}/ab", HttpMethod.POST, methodPost);
		tree.addControllerMethod("/a/cd/{b}/{c}/d/", HttpMethod.GET, methodGet);

		HttpRequest request = mockRequest("/a/ab", HttpMethod.GET);
		RequestResolution resolution = tree.resolveRequest(request);
		assertNotNull(resolution);
		assertEquals(methodGet, resolution.getMethod());
		assertEquals(0, resolution.getPathVariables().size());

		request = mockRequest("/a/ab", HttpMethod.POST);
		resolution = tree.resolveRequest(request);
		assertNotNull(resolution);
		assertEquals(methodPost, resolution.getMethod());
		assertEquals(0, resolution.getPathVariables().size());

		request = mockRequest("/a/1", HttpMethod.POST);
		resolution = tree.resolveRequest(request);
		assertNotNull(resolution);
		assertEquals(methodPost, resolution.getMethod());
		assertEquals(1, resolution.getPathVariables().size());
		assertEquals("1", resolution.getPathVariables().getPathVariable("ab"));

		request = mockRequest("/a/1/ab", HttpMethod.POST);
		resolution = tree.resolveRequest(request);
		assertNotNull(resolution);
		assertEquals(methodPost, resolution.getMethod());
		assertEquals(1, resolution.getPathVariables().size());
		assertEquals("1", resolution.getPathVariables().getPathVariable("ab"));

		request = mockRequest("/a/cd/b/c/d", HttpMethod.GET);
		resolution = tree.resolveRequest(request);
		PathVariables vars = resolution.getPathVariables();
		assertNotNull(resolution);
		assertEquals(methodGet, resolution.getMethod());
		assertEquals(2, vars.size());
		assertEquals("b", vars.getPathVariable("b"));
		assertEquals("c", vars.getPathVariable("c"));
	}

	@Test
	public void resolveMethod_unsuccessfullyResolveMethods() {
		ControllerMethod methodGet = (req, var) -> null;
		ControllerMethod methodPost = (req, var) -> null;
		ControllerMethodTree tree = new ControllerMethodTree();
		assertNotEquals(methodGet, methodPost);
		tree.addControllerMethod("/a/ab", HttpMethod.GET, methodGet);
		tree.addControllerMethod("/{b}/", HttpMethod.GET, methodGet);

		HttpRequest request = mockRequest("/", HttpMethod.GET);
		RequestResolution resolution = tree.resolveRequest(request);
		assertNull(resolution);

		request = mockRequest("/a/ab", HttpMethod.POST);
		resolution = tree.resolveRequest(request);
		assertNull(resolution);

		request = mockRequest("/a/", HttpMethod.POST);
		resolution = tree.resolveRequest(request);
		assertNull(resolution);
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
