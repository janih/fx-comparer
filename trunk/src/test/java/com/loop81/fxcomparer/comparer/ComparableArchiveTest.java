package com.loop81.fxcomparer.comparer;

import java.io.File;
import java.util.List;

import org.fest.assertions.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.loop81.fxcomparer.TestHelper;
import com.loop81.fxcomparer.asserts.ComparableArchiveAssert;
import com.loop81.fxcomparer.asserts.ComparableEntryAssert;
import com.loop81.fxcomparer.comparer.ArchiveException;
import com.loop81.fxcomparer.comparer.ComparableArchive;
import com.loop81.fxcomparer.comparer.ComparableArchive.ComparableEntry;

public class ComparableArchiveTest {
	
	private static final File EMPTY_FILE = new File("");
	private static final File TEXT_FILE = TestHelper.getFile(ComparerTest.class, "file.txt");
	
	/**
	 * Original file, 439b.
	 * 
	 * <pre>
	 *   file.zip
	 *    | file1.txt 5b
	 *    |--folder
	 *    |   | file2.txt 12b
	 * </pre>
	 */
	private static final File ZIP_ARCHIVE = TestHelper.getFile(ComparerTest.class, "file.zip");
	
	private ComparableArchive comparableArchive;

	@BeforeMethod
	public void init() throws ArchiveException {
		comparableArchive = new ComparableArchive(ZIP_ARCHIVE);
	}
	
	@Test
	public void shouldBePossibleToAccessBasicData() {
		ComparableArchiveAssert.assertThat(comparableArchive).hasByteSize(439).pathContains("\\file.zip");
	}
	
	@DataProvider
	protected Object[][] faultyArchives() {
		return new Object[][] {
				{ null },
				{ EMPTY_FILE},
				{ EMPTY_FILE},
				{ TEXT_FILE }
		};
	}
	
	@Test(dataProvider = "faultyArchives", expectedExceptions = { ArchiveException.class })
	public void shouldNotBePossibleToCreateUsingFaultyArchives(File archive) throws ArchiveException {
		new ComparableArchive(archive);
	}
	
	@Test(expectedExceptions = { IllegalStateException.class })
	public void shouldNotBePossibleToCallGetFilesWhenBuildHasNotBenCalled() {
		comparableArchive.getFilesCount();
	}
	
	@Test(expectedExceptions = { IllegalStateException.class })
	public void shouldNotBePossibleToCallGetFoldersWhenBuildHasNotBenCalled() {
		comparableArchive.getFoldersCount();
	}
	
	@Test(expectedExceptions = { IllegalStateException.class })
	public void shouldNotBePossibleToCallGetEntriesWhenBuildHasNotBenCalled() {
		comparableArchive.getEntriesInFile();
	}
	
	@Test
	public void shouldBePossibleToAnalyzeArchive() throws ArchiveException {
		comparableArchive.build();
		
		ComparableArchiveAssert.assertThat(comparableArchive).hasFileCount(2).hasFoldersCount(1).hasByteSize(439);
		
		List<ComparableEntry> resultEntries = comparableArchive.getEntriesInFile();
		Assertions.assertThat(resultEntries.size()).isEqualTo(3);
		
		ComparableEntryAssert.assertThat(resultEntries.get(0))
				.hasEntryName("file1.txt")
				.hasByteSize(5);
		
		ComparableEntryAssert.assertThat(resultEntries.get(1))
				.hasEntryName("folder/")
				.isFolder();
		
		ComparableEntryAssert.assertThat(resultEntries.get(2))
				.hasEntryName("folder/file2.txt")
				.hasByteSize(12);
	}
}
