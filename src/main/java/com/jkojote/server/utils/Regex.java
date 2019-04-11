package com.jkojote.server.utils;

import java.util.regex.Pattern;

public final class Regex {
	public static Pattern PATH_TEMPLATE = Pattern.compile(
		"/?(([a-zA-Z0-9\\-.]+|(\\{[a-zA-Z0-9\\-.]+\\}))/?)*"
	);

	public static Pattern CONTROLLER_BASE_PATH = Pattern.compile(
		"/([a-zA-Z0-9\\-.]+/?)*"
	);
}
