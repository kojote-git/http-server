package com.jkojote.server.utils;

public final class Preconditions {

	private Preconditions() { throw new AssertionError(); }

	public static void checkNotNull(Object object) {
		if (object == null) {
			throw new NullPointerException();
		}
	}

	public static void checkNotNull(Object object, String message) {
		if (object == null) {
			throw new NullPointerException(message);
		}
	}

	public static void checkArgument(boolean expression) {
		if (!expression) {
			throw new IllegalArgumentException();
		}
	}

	public static void checkArgument(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}
}
