package com.jkojote.server.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOUtils {
	private IOUtils() { }

	public static void transfer(InputStream in, OutputStream out, int bufferSize) throws IOException {
		byte[] buffer = new byte[bufferSize];
		int read;
		while ((read = in.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
	}

	public static byte[] readResource(String path) throws IOException {
		return readResource(path, IOUtils.class.getClassLoader());
	}

	public static byte[] readResource(String path, ClassLoader classLoader) throws IOException {
		return readResource(path, classLoader, 4096);
	}

	public static byte[] readResource(String path, ClassLoader classLoader, int bufferSize) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = classLoader.getResourceAsStream(path);
		if (in == null) {
			throw new FileNotFoundException("Cannot find resource: " + path);
		}
		try {
			transfer(in, out, bufferSize);
		} finally {
			in.close();
		}
		return out.toByteArray();
	}
}
