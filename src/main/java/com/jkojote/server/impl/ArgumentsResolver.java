package com.jkojote.server.impl;

import com.jkojote.server.ControllerMethod;
import com.jkojote.server.HttpRequest;
import com.jkojote.server.PathVariables;
import com.jkojote.server.exceptions.PathVariableConversionException;

interface ArgumentsResolver {

	Object[] resolve(ControllerMethod method, HttpRequest request, PathVariables variables) throws PathVariableConversionException;
}
