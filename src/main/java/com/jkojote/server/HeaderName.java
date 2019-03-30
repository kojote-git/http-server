package com.jkojote.server;

import static com.jkojote.server.utils.Preconditions.checkNotNull;

public final class HeaderName {
	private final String value;
	private int hashCode;

	private HeaderName(String value) {
		this.value = value;
	}

	public static HeaderName of(String value) {
		checkNotNull(value);
		return new HeaderName(value);
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof HeaderName) {
			HeaderName name = (HeaderName) obj;
			return value.equalsIgnoreCase(name.value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = value.toLowerCase().hashCode();
		}
		return hashCode;
	}

	@Override
	public String toString() {
		return value;
	}
}
