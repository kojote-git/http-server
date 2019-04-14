package com.jkojote.server.impl.config.converters;

import com.jkojote.server.PathVariableConverter;
import com.jkojote.server.exceptions.PathVariableConversionException;

public final class IntConverter implements PathVariableConverter<Integer> {
	public static final IntConverter INSTANCE = new IntConverter();

	private IntConverter() {
	}

	@Override
	public Integer convert(String pathVariable) throws PathVariableConversionException {
		try {
			return Integer.parseInt(pathVariable);
		} catch (NumberFormatException e) {
			throw new PathVariableConversionException(e, e.getMessage(), pathVariable);
		}
	}
}
