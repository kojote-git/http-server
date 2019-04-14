package com.jkojote.server;

import java.util.function.Function;

/**
 * An object that holds path variables.<br/>
 *
 * A path variable is a part of a URI that can vary between different requests.
 * For example, consider this uri template: {@code /echo/{message}}.
 * Here the {@code {message}} is a path variable. When it comes to actual requests
 * such as {@code /echo/foo} or {@code /echo/bar} {@code foo} and {@code bar} are the
 * values of the variable. They are associated with name of the path variable and can be
 * then retrieved by this name.
 */
public interface PathVariables {

	/**
	 * @param name name of a path variable
	 * @return value of a path variable or null if the object does not contain such a path variable
	 */
	String getPathVariable(String name);

	/**
	 * A utility method to convert the path variable into an object of different type.
	 * @param name name of the path variable
	 * @param converter converted to be applied to the value of the path variable
	 * @return converted path variable
	 */
	default <T> T convertVariable(String name, Function<String, T> converter) {
		return converter.apply(getPathVariable(name));
	}

	/**
	 * @return all path variables that this object holds
	 */
	Iterable<PathVariable> getPathVariable();

	/**
	 * @return total number of path variables that this object holds
	 */
	int size();

	interface PathVariable {

		String getName();

		String getValue();
	}
}
