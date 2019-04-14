package com.jkojote.server;

import com.jkojote.server.exceptions.PathVariableConversionException;

public interface PathVariableConverter<TargetType> {

	TargetType convert(String pathVariable) throws PathVariableConversionException;

}
