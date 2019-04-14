package com.jkojote.server.impl.config.converters;

import com.jkojote.server.PathVariableConverter;
import com.jkojote.server.exceptions.PathVariableConversionException;

public final class LongConverter implements PathVariableConverter<Long> {
	public static final LongConverter INSTANCE = new LongConverter();

	private LongConverter() {
	}

	@Override
	public Long convert(String pathVariable) throws PathVariableConversionException {
		try {
			return Long.parseLong(pathVariable);
		} catch (NumberFormatException e) {
			throw new PathVariableConversionException(e, e.getMessage(), pathVariable);
		}
	}
}
