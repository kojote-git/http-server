package com.jkojote.server.utils;

import org.junit.Test;

public class PreconditionsTest {

	@Test(expected = NullPointerException.class)
	public void checkNotNull_passNull() {
		Preconditions.checkNotNull(null);
	}

	@Test
	public void checkNotNull_passNonNullObject() {
		Preconditions.checkNotNull(new Object());
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkArgument_passIllegalArgument() {
		int arg = 5;
		Preconditions.checkArgument(arg < 0);
	}

	@Test
	public void checkArgument_passLegalArgument() {
		int arg = 5;
		Preconditions.checkArgument(arg > 0);
	}

}
