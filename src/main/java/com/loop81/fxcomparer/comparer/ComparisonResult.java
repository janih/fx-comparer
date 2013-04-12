package com.loop81.fxcomparer.comparer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.loop81.fxcomparer.comparer.ComparableArchive.ComparableEntry;
import com.loop81.fxcomparer.comparer.ComparisonResult.ComparisonEntry.ChangeState;

public class ComparisonResult {

	private List<ComparisonEntry> entries = new ArrayList<>();
	
	private long byteChange;
	
	private boolean same = true;
	
	public ComparisonResult(long byteChange) {
		this.byteChange = byteChange;
	}
	
	public List<ComparisonEntry> getEntries() {
		return this.entries;
	}
	
	public boolean isSame() {
		return same;
	}
	
	public long getByteChange() {
		return byteChange;
	}
	
	public void addEntry(ComparisonEntry entry) {
		entries.add(entry);

		if (entry.getChangeState() != ChangeState.SAME) {
			same = false;
		}
	}
	
	public void sort() {
		Collections.sort(entries);
	}

	public static class ComparisonEntry implements Comparable<ComparisonEntry>{
		private ChangeState changeState;
		
		private long sizeChange;
		
		private ComparableEntry originalEntry;
		
		public ComparisonEntry(ChangeState changeState, long sizeChange, ComparableEntry originalEntry) {
			this.changeState = changeState;
			this.sizeChange = sizeChange;
			this.originalEntry = originalEntry;
		}
		
		public ChangeState getChangeState() {
			return changeState;
		}
		
		/** Return the change in bytes or if it is unknown {@link Long#MIN_VALUE}. */
		public long getSizeChange() {
			return sizeChange;
		}
		
		public ComparableEntry getOriginalEntry() {
			return originalEntry;
		}
		
		public boolean isFolder() {
			return originalEntry.isFolder();
		}

		public static enum ChangeState {
			REMOVED,
			NEW,
			MODIFIED,
			SAME,
			UNCOMPARABLE
		}
		
		@Override
		public int compareTo(ComparisonEntry otherEntry) {
			return getOriginalEntry().compareTo(otherEntry.getOriginalEntry());
		}

		@Override
		public String toString() {
			return "Entry state '" + getChangeState() 
					+ "', size change '" + getSizeChange() 
					+ "', is folder '" + isFolder() + "'";
		}
	}
}
