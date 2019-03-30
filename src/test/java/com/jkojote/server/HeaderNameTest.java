package com.jkojote.server;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class HeaderNameTest {

	@Test(expected = NullPointerException.class)
	public void of_createIllegalHeaderName() {
		HeaderName.of(null);
	}

	@Test
	public void of_createLegalHeaderName() {
		HeaderName.of("header");
	}

	@Test
	public void equals_testEquality() {
		assertEquals(HeaderName.of("HEADER"), HeaderName.of("header"));
		assertNotEquals(HeaderName.of("HEADER1"), HeaderName.of("HEADER2"));
	}

	@Test
	public void getValue_returnsCorrectHeaderName() {
		String value = "HEADER";
		HeaderName name = HeaderName.of(value);
		assertEquals(value, name.getValue());
	}

	@Test
	public void hashCode_testHashCode() {
		HeaderName n1 = HeaderName.of("HEADER");
		HeaderName n2 = HeaderName.of("header");
		assertEquals(n1, n2);
		assertEquals(n1.hashCode(), n2.hashCode());

		int h1 = n1.hashCode();
		int h2 = n1.hashCode();
		assertEquals(h1, h2);
	}
}
