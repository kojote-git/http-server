package com.jkojote.server;

import static com.jkojote.server.utils.Preconditions.checkNotNull;

public final class HttpStatus {
	private final int code;
	private final String reasonPhrase;

	private HttpStatus(int statusCode, String reasonPhrase) {
		this.code = statusCode;
		this.reasonPhrase = reasonPhrase;
	}

	public static HttpStatus of(int statusCode, String reasonPhrase) {
		checkNotNull(reasonPhrase);
		return new HttpStatus(statusCode, reasonPhrase);
	}

	public int getCode() {
		return code;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof HttpStatus) {
			HttpStatus status = (HttpStatus) obj;
			return code == status.code &&
					reasonPhrase.equals(status.reasonPhrase);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 31 * code + reasonPhrase.hashCode();
	}
}
