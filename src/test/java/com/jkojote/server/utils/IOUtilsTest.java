package com.jkojote.server.utils;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class IOUtilsTest {

	@Test
	public void readToBytes() throws IOException {
		byte[] bytes = new byte[] { 1, 2, 3, 4, 5 };
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		byte[] out = IOUtils.readToBytes(in);
		assertArrayEquals(bytes, out);
	}
}
