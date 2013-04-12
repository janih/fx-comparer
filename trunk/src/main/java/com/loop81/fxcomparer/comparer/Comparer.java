package com.loop81.fxcomparer.comparer;

import java.io.File;

import com.loop81.fxcomparer.comparer.ComparableArchive.ComparableEntry;
import com.loop81.fxcomparer.comparer.ComparisonResult.ComparisonEntry;
import com.loop81.fxcomparer.comparer.ComparisonResult.ComparisonEntry.ChangeState;


public class Comparer {

	private static final String FILE_ZIP = ".zip";
	private static final String FILE_JAR = ".jar";
	private static final String FILE_WAR = ".war";
	
	/**
	 * 
	 * @param archive1
	 * @param archive2
	 * @return
	 * @throws ArchiveException
	 */
	public ComparisonResult compare(ComparableArchive archive1, ComparableArchive archive2) throws ArchiveException {
		archive1.build();
		archive2.build();
		
		ComparisonResult result = new ComparisonResult(archive2.getSize() - archive1.getSize());
		
		for (ComparableEntry entry : archive2.getEntriesInFile()) {
			result.addEntry(compare(archive1.remove(entry), entry));
		}
		
		for (ComparableEntry entry : archive1.getEntriesInFile()) {
			result.addEntry(compare(entry, null));
		}
		
		result.sort();
		
		return result;
	}
	
	/**
	 * Compare the two given files with the entry1 as the old entry and entry2 as the new.
	 * 
	 * <p>
	 *   If the first entry is null and the last is not null it is threaten as a new file. <br />
	 *   If the first entry is not null and the last is null it is threaten as a deleted file. <br />
	 *   Otherwise the two entries are compare to see if they have changed.
	 * </p>
	 * 
	 * @param entry1 The old entry to compare against.
	 * @param entry2 The new entry to compare with.
	 * @return A {@link ComparisonEntry} describing the result from the compare.
	 */
	protected ComparisonEntry compare(ComparableEntry entry1, ComparableEntry entry2) {
		if (entry1 == null) {
			return new ComparisonEntry(ChangeState.NEW, entry2.getByteSize(), entry2);
		} else  if (entry2 == null) {
			return new ComparisonEntry(ChangeState.REMOVED, -1 * entry1.getByteSize(), entry1);
		} else {
			if (entry1.getByteSize() == Long.MIN_VALUE && entry2.getByteSize() == Long.MIN_VALUE) {
				return new ComparisonEntry(ChangeState.UNCOMPARABLE, Long.MIN_VALUE, entry1);
			} else if (entry1.getByteSize() == entry2.getByteSize()) {
				return new ComparisonEntry(ChangeState.SAME, 0, entry1);
			} else {
				long sizeChange = entry2.getByteSize() == Long.MIN_VALUE 
						|| entry1.getByteSize() == Long.MIN_VALUE 
							? Long.MIN_VALUE : entry2.getByteSize() - entry1.getByteSize();
				return new ComparisonEntry(ChangeState.MODIFIED, sizeChange, entry1);
			}
		}
 	}
	
	/** Control if the given file is a supported file to be used to compare with. */
	public boolean isFileSupported(File file) {
		return file.getName().endsWith(FILE_ZIP) || file.getName().endsWith(FILE_WAR) 
				|| file.getName().endsWith(FILE_JAR);
	}
}	
