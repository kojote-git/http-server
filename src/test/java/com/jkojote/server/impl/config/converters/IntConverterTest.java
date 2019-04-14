package com.jkojote.server.impl.config.converters;

import com.jkojote.server.exceptions.PathVariableConversionException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntConverterTest {

	@Test
	public void convertValid() {
		int actual = IntConverter.INSTANCE.convert("1");
		assertEquals(1, actual);
	}

	@Test(expected = PathVariableConversionException.class)
	public void convertInvalid() {
		IntConverter.INSTANCE.convert("1.1");
	}
}
