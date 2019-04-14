package com.jkojote.server.impl.config.converters;

import com.jkojote.server.exceptions.PathVariableConversionException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LongConverterTest {

	@Test
	public void convertValid() {
		long actual = LongConverter.INSTANCE.convert("123");
		assertEquals(123, actual);
	}

	@Test(expected = PathVariableConversionException.class)
	public void convertInvalid() {
		LongConverter.INSTANCE.convert("123.123");
	}
}
