package com.loop81.fxcompare.asserts;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;

import com.loop81.fxcompare.comparer.ComparableArchive;

public class ComparableArchiveAssert extends AbstractAssert<ComparableArchiveAssert, ComparableArchive> {

	protected ComparableArchiveAssert(ComparableArchive actual) {
		super(actual, ComparableArchiveAssert.class);
	}

	public static ComparableArchiveAssert assertThat(ComparableArchive actual) {
		return new ComparableArchiveAssert(actual);
	}
	
	public ComparableArchiveAssert hasFileCount(int expectedFileCount) {
		isNotNull();
		Assertions.assertThat(actual.getFilesCount()).isEqualTo(expectedFileCount);
		return this;
	}
	
	public ComparableArchiveAssert hasFoldersCount(int expectedFolderCount) {
		isNotNull();
		Assertions.assertThat(actual.getFoldersCount()).isEqualTo(expectedFolderCount);
		return this;
	}
	
	public ComparableArchiveAssert hasByteSize(long expectedByteSize) {
		isNotNull();
		Assertions.assertThat(actual.getSize()).isEqualTo(expectedByteSize);
		return this;
	}
	
	public ComparableArchiveAssert pathContains(String expectedPath) {
		isNotNull();
		Assertions.assertThat(actual.getPath()).contains(expectedPath);
		return this;
	}
}
