package com.loop81.fxcomparer.comparer;

import java.io.File;
import java.util.List;

import org.fest.assertions.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.loop81.fxcomparer.TestHelper;
import com.loop81.fxcomparer.asserts.ComparisonEntryAssert;
import com.loop81.fxcomparer.asserts.ComparisonResultAssert;
import com.loop81.fxcomparer.comparer.ArchiveException;
import com.loop81.fxcomparer.comparer.ComparableArchive;
import com.loop81.fxcomparer.comparer.Comparer;
import com.loop81.fxcomparer.comparer.ComparisonResult;
import com.loop81.fxcomparer.comparer.ComparableArchive.ComparableEntry;
import com.loop81.fxcomparer.comparer.ComparisonResult.ComparisonEntry;
import com.loop81.fxcomparer.comparer.ComparisonResult.ComparisonEntry.ChangeState;

public class ComparerTest {

	/**
	 * Original file.
	 * 
	 * <pre>
	 *   file.zip
	 *    | file1.txt 5b
	 *    |--folder
	 *    |   | file2.txt 12b
	 * </pre>
	 */
	private static final File ZIP_ARCHIVE = TestHelper.getFile(ComparerTest.class, "file.zip");
	
	/**
	 * Modified version of the original file.
	 * 
	 * <pre>
	 *   file_modified.zip
	 *    | file1_txt 12b
	 *    |-- folder
	 *    |    |-- subfolder
	 *    |    |    | file3.txt 4b 	
	 * </pre>
	 */
	private static final File ZIP_ARCHIVE_MODIFIED = TestHelper.getFile(ComparerTest.class, "file_modified.zip");
	 
	private Comparer comparer;
	
	@BeforeMethod
	public void init() {
		comparer = new Comparer();
	}
	
	@DataProvider
	protected Object[][] comparableEntries() { 
		return new Object[][] {
				{ createFolder("test/a/b"), createFolder("test/a/b"), ChangeState.SAME, 0},
				{ createFolder("test/a/b"), null, ChangeState.REMOVED, 0},
				{ null, createFolder("test/a/b"), ChangeState.NEW, 0},
				{ createFile("a/b/c", 10), createFile("a/b/c", 10), ChangeState.SAME, 0},
				{ createFile("a/b/c", 10), createFile("a/b/c", 20), ChangeState.MODIFIED, 10},
				{ createFile("a/b/c", 10), createFile("a/b/c", 5), ChangeState.MODIFIED, -5},
				{ createFile("a/b/c", 10), null, ChangeState.REMOVED, -10},
				{ null, createFile("a/b/c", 20), ChangeState.NEW, 20}
		};
	}
	
	@Test(dataProvider = "comparableEntries")
	public void shouldBePossibleToCompareComparableEntries(ComparableEntry entry1, 
			ComparableEntry entry2, ChangeState state, long sizeChange) {
		if ((entry1 != null && entry1.isFolder()) || (entry2 != null && entry2.isFolder())) {
			ComparisonEntryAssert.assertThat(comparer.compare(entry1, entry2)).hasState(state);
		} else {
			ComparisonEntryAssert.assertThat(comparer.compare(entry1, entry2)).hasState(state).sizeChangeIs(sizeChange);
		}
		
	}
	
	/**
	 * This test compares two archives and asserts that the expected results are as follows: 
	 * 
	 * 	<pre>
	 *   result
	 *    | file1.txt 5b -> 12b increased size of 7b
	 *    |-- folder -> The same
	 *    |    | file2.txt 12b -> Removed
	 *    |    |-- subfolder -> New
	 *    |    |    | file3.txt 4b -> New 
	 * </pre>
	 * 
	 * @throws ArchiveException Checked exception thrown from the compare if something fails. Should not be 
	 * throw in this method.
	 * @see ComparerTest#ZIP_ARCHIVE
	 * @see ComparerTest#ZIP_ARCHIVE_MODIFIED
	 */
	@Test
	public void shouldBePossibleToCompareTwoArchives() throws ArchiveException {
		ComparisonResult result = comparer.compare(new ComparableArchive(ZIP_ARCHIVE), 
				new ComparableArchive(ZIP_ARCHIVE_MODIFIED));
		
		ComparisonResultAssert.assertThat(result).isNotSame().sizeChangeIs(165);
		
		List<ComparisonEntry> entries = result.getEntries();
		Assertions.assertThat(entries.size()).isEqualTo(5);
		
		ComparisonEntryAssert.assertThat(entries.get(0))
				.hasFileName("file1.txt")
				.hasState(ChangeState.MODIFIED)
				.sizeChangeIs(7);
		
		ComparisonEntryAssert.assertThat(entries.get(1))
				.hasFileName("folder/")
				.hasState(ChangeState.SAME)
				.isFolder();
		
		ComparisonEntryAssert.assertThat(entries.get(2))
				.hasFileName("folder/file2.txt")
				.hasState(ChangeState.REMOVED)
				.sizeChangeIs(-12);
		
		ComparisonEntryAssert.assertThat(entries.get(3))
				.hasFileName("folder/subfolder/")
				.hasState(ChangeState.NEW)
				.isFolder();
		
		ComparisonEntryAssert.assertThat(entries.get(4))
				.hasFileName("folder/subfolder/file3.txt")
				.hasState(ChangeState.NEW)
				.sizeChangeIs(4);
	}
	
	private ComparableEntry createFolder(String folderPath) {
		return ComparableEntry.createFolderEntry(folderPath);
	}
	
	private ComparableEntry createFile(String filePath, long size) {
		return ComparableEntry.createFileEntry(filePath, size);
	}
}
