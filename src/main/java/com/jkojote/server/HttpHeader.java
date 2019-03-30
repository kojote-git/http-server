package com.jkojote.server;

import static com.jkojote.server.utils.Preconditions.checkNotNull;

public final class HttpHeader {
	private final String name;
	private final String value;
	private int hashCode;

	private HttpHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static HttpHeader of(String name, String value) {
		checkNotNull(name);
		checkNotNull(value);
		return new HttpHeader(name, value);
	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof HttpHeader) {
			HttpHeader header = (HttpHeader) obj;
			return value.equals(header.value) &&
					name.equalsIgnoreCase(header.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = 31 * name.toLowerCase().hashCode() + value.hashCode();
		}
		return hashCode;
	}
}
