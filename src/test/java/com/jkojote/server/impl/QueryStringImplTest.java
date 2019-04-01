package com.jkojote.server.impl;

import com.jkojote.server.QueryString;

import org.junit.Test;

import java.util.Iterator;
import static com.jkojote.server.QueryString.QueryParameter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QueryStringImplTest {

	@Test
	public void getParameters_checkParameters() {
		QueryString queryString = new QueryStringImpl("a=1&b=2&c=3");
		Iterator<QueryParameter> iterator = queryString.getParameters().iterator();

		checkNextParameter(iterator, "a", "1");
		checkNextParameter(iterator, "b", "2");
		checkNextParameter(iterator, "c", "3");

		assertFalse(iterator.hasNext());
	}

	@Test
	public void getParameters_initializeWithInvalidParameters_checkTheyAreIgnored() {
		QueryString queryString = new QueryStringImpl("a=1&b=2&c==&d=%&k=3&");
		Iterator<QueryParameter> iterator = queryString.getParameters().iterator();

		checkNextParameter(iterator, "a", "1");
		checkNextParameter(iterator, "b", "2");
		checkNextParameter(iterator, "c", "");
		checkNextParameter(iterator, "k", "3");

		assertFalse(iterator.hasNext());
	}

	@Test
	public void getParameter_checkParameters() {
		QueryString queryString = new QueryStringImpl("a=1&b=2&c=3");
		checkParameter(queryString, "a", "1");
		checkParameter(queryString, "b", "2");
		checkParameter(queryString, "c", "3");
	}

	@Test
	public void initializeEmpty() {
		QueryString queryString = new QueryStringImpl("");
		assertFalse(queryString.getParameters().iterator().hasNext());
	}

	private void checkParameter(QueryString queryString, String key, String expectedValue) {
		assertEquals(expectedValue, queryString.getParameterValue(key));
	}

	private void checkNextParameter(Iterator<QueryParameter> parameter,
								   String expectedKey,
								   String expectedValue) {
		assertTrue(parameter.hasNext());
		QueryParameter p = parameter.next();
		assertEquals(expectedKey, p.getKey());
		assertEquals(expectedValue, p.getValue());
	}
}
