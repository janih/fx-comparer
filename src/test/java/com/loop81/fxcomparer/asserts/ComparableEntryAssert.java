package com.loop81.fxcomparer.asserts;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;

import com.loop81.fxcomparer.comparer.ComparableArchive.ComparableEntry;

public class ComparableEntryAssert extends AbstractAssert<ComparableEntryAssert, ComparableEntry> {

	protected ComparableEntryAssert(ComparableEntry actual) {
		super(actual, ComparableEntryAssert.class);
	}

	public static ComparableEntryAssert assertThat(ComparableEntry actual) {
		return new ComparableEntryAssert(actual);
	}
	
	public ComparableEntryAssert hasEntryName(String expectedEntryName) {
		isNotNull();
		Assertions.assertThat(actual.getEntryName()).isEqualTo(expectedEntryName);
		return this;
	}
	
	public ComparableEntryAssert hasByteSize(long expectedByteSize) {
		isNotNull();
		Assertions.assertThat(actual.getByteSize()).isEqualTo(expectedByteSize);
		return this;
	}
	
	public ComparableEntryAssert isFolder() {
		isNotNull();
		Assertions.assertThat(actual.isFolder()).isTrue();
		return this;
	}
}
