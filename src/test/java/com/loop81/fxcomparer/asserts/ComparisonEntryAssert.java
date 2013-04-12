package com.loop81.fxcomparer.asserts;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;

import com.loop81.fxcomparer.comparer.ComparisonResult.ComparisonEntry;
import com.loop81.fxcomparer.comparer.ComparisonResult.ComparisonEntry.ChangeState;

public class ComparisonEntryAssert extends AbstractAssert<ComparisonEntryAssert, ComparisonEntry> {

	private ComparisonEntryAssert(ComparisonEntry actual) {
		super(actual, ComparisonEntryAssert.class);
	}

	public static ComparisonEntryAssert assertThat(ComparisonEntry actual){
		return new ComparisonEntryAssert(actual);
	}

	public ComparisonEntryAssert hasFileName(String expectedFileName) {
		isNotNull();
		Assertions.assertThat(actual.getOriginalEntry().getEntryName()).isEqualTo(expectedFileName);
		return this;
	}

	public ComparisonEntryAssert hasState(ChangeState expectedState) {
		isNotNull();
		Assertions.assertThat(actual.getChangeState()).isEqualTo(expectedState);
		return this;
	}

	public ComparisonEntryAssert sizeChangeIs(long expectedSizeChange) {
		isNotNull();
		Assertions.assertThat(actual.getSizeChange()).isEqualTo(expectedSizeChange);
		return this;
	}

	public ComparisonEntryAssert isFolder() {
		isNotNull();
		Assertions.assertThat(actual.isFolder()).isTrue();
		return this;
	}
}
