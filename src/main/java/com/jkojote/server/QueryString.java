package com.jkojote.server;

/**
 * An object that represents a query string <br/>
 *
 * A query string is a part of a request URI that goes after question mark (?) and consists
 * of key-value pairs.
 */
public interface QueryString {

	/**
	 * @return all query parameters that this query string has
	 */
	Iterable<QueryParameter> getParameters();

	/**
	 * @param name name of the query parameter
	 * @return query parameter associated wit given name or null
	 */
	String getParameterValue(String name);

	/**
	 * An interface that holds key and value for one query parameter
	 */
	interface QueryParameter {

		String getKey();

		String getValue();
	}
}
