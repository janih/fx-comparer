package com.loop81.fxcomparer.asserts;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;

import com.loop81.fxcomparer.comparer.ComparisonResult;

public class ComparisonResultAssert extends AbstractAssert<ComparisonResultAssert, ComparisonResult> {

	private ComparisonResultAssert(ComparisonResult actual) {
		super(actual, ComparisonResultAssert.class);
	}
	
	public static ComparisonResultAssert assertThat(ComparisonResult actual) {
		return new ComparisonResultAssert(actual);
	}

	public ComparisonResultAssert isNotSame() {
		isNotNull();
		Assertions.assertThat(actual.isSame()).isFalse();
		return this;
	}

	public ComparisonResultAssert sizeChangeIs(long expectedChange) {
		isNotNull();
		Assertions.assertThat(actual.getByteChange()).isEqualTo(expectedChange);
		return this;
	}
}
