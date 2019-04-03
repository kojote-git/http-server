package com.jkojote.server;

import static com.jkojote.server.utils.Preconditions.checkNotNull;

public final class HttpStatus {
	public static final HttpStatus OK = HttpStatus.of(200, "OK");
	public static final HttpStatus CREATED = HttpStatus.of(201, "Created");
	public static final HttpStatus ACCEPTED = HttpStatus.of(202, "Accepted");
	public static final HttpStatus NO_CONTENT = HttpStatus.of(204, "No Content");

	public static final HttpStatus BAD_REQUEST = HttpStatus.of(400, "Bad Request");
	public static final HttpStatus UNAUTHORIZED = HttpStatus.of(401, "Unauthorized");
	public static final HttpStatus NOT_FOUND = HttpStatus.of(404, "Not Found");
	public static final HttpStatus FORBIDDEN = HttpStatus.of(403, "Forbidden");

	public static final HttpStatus INTERNAL_ERROR = HttpStatus.of(500, "Internal Server Error");

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
