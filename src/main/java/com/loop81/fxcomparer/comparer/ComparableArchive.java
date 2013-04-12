package com.loop81.fxcomparer.comparer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.loop81.fxcomparer.comparer.ArchiveException.ExceptionType;

/**
 * A {@link ComparableArchive} is the representation of a valid archive containing information about the archive 
 * together with entries for all the files in the archive. To get access to the information within the archive you
 * first need to call .build(). Calling any of the folderCount, fileCount, or entries before .build() has been called
 * a {@link IllegalStateException} is thrown.
 * 
 * @author Allitico
 */
public class ComparableArchive {

	private State internalState = State.INIT;
	
	private List<ComparableEntry> entries;
	
	private final File archive;

	private int folderCount = 0;
	
	private int filesCount = 0;
	
	public ComparableArchive(File archive) throws ArchiveException {
		isValidArchive(archive);
		
		this.archive = archive;
	}
	
	/** 
	 * Control that the given archive is a valid ZIP-archive which there is data which we can read from.
	 *
	 * @param archive The archive to control if it is a valid archive.
	 * @throws ArchiveException If the archive is not valid or supported.
	 */
	private void isValidArchive(File archive) throws ArchiveException {
		if (archive != null) {
			if (!archive.exists()) {
				throw new ArchiveException(ExceptionType.NOT_FOUND, archive, "The given file '" 
						+ archive.getAbsolutePath() +"' do not exists.");
			}
			try (ZipFile file = new ZipFile(archive)) {
				if (file.size() == 0) {
					throw new ArchiveException(ExceptionType.READ, archive, 
							"The given archive is either empty or not a supported archive.");
				}
			} catch (ZipException e) {
				throw new ArchiveException(ExceptionType.READ, archive, e);
			} catch (IOException e) {
				throw new ArchiveException(ExceptionType.IO, archive, e);
			}
		} else {
			throw new ArchiveException(ExceptionType.NULL, "A archive can not be null...");
		}
	}

		
	/**
	 * Read all the entries in the file and create a {@link ComparableEntry} for the entries. Calling this method 
	 * makes the calls to folderCount, fileCount, and entries valid. 
	 * 
	 * @throws ArchiveException If the archive can not be loaded or the data could not be read.
	 */
	public void build() throws ArchiveException {
		entries = new ArrayList<>();
		
		try (ZipFile zipFile = new ZipFile(archive)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry entry = enumeration.nextElement();
				if (entry.isDirectory()) {
					entries.add(ComparableEntry.createFolderEntry(entry.getName()));
					folderCount++;
				} else {
					entries.add(ComparableEntry.createFileEntry(entry.getName(), 
							entry.getSize() == -1 ? Long.MIN_VALUE : entry.getSize()));
					filesCount++;
				}
			}
			internalState = State.BUILD;
		} catch (IOException e) {
			throw new ArchiveException(ExceptionType.IO, archive, e);
		}
	}
	
	private void isValidToCall() {
		if (internalState != State.BUILD) {
			throw new IllegalStateException("You have to call .build() before calling this method.");
		}
	}
	
	/** Control if this archive contains the given entry, and remove it if it exists. */
	public ComparableEntry remove(ComparableEntry entry) {
		for (ComparableEntry entryInArchive : getEntriesInFile()) {
			if (entry.equals(entryInArchive)) {
				getEntriesInFile().remove(entryInArchive);
				return entryInArchive;
			}
		}
		return null;
	}
	
	public long getSize() {
		return archive.length();
	}
	
	public String getPath() {
		return archive.getPath();
	}
	
	public int getFilesCount() {
		isValidToCall();
		return filesCount;
	}

	public int getFoldersCount() {
		isValidToCall();
		return folderCount;
	}
	
	public List<ComparableEntry> getEntriesInFile() {
		isValidToCall();
		return entries;
	}
	
	/**
	 * Simple state for a {@link ComparableArchive}. Used to control which methods that the user can call. 
	 * 
	 * @author Allitico
	 */
	private enum State {
		INIT,
		BUILD
	}
	
	public static class ComparableEntry implements Comparable<ComparableEntry> {
		private String entryName;
		
		private boolean folder;
		
		private long byteSize;
		
		private ComparableEntry() {
			
		}
		
		protected static ComparableEntry createFolderEntry(String entryName) {
			ComparableEntry folder = new ComparableEntry();
			folder.entryName = entryName;
			folder.folder = true;
			return folder;
		}
		
		protected static ComparableEntry createFileEntry(String entryName, long byteSize) {
			ComparableEntry file = new ComparableEntry();
			file.entryName = entryName;
			file.byteSize = byteSize;
			return file;
		}
		
		
		public String getEntryName() {
			return entryName;
		}
		
		/** Return the byte size if it is known, otherwise {@link Long#MIN_VALUE} is returned. */
		public long getByteSize() {
			return byteSize;
		}
		
		public boolean isFolder() {
			return folder;
		}
		
		/** Two entrys are equal if the path is the same. */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ComparableEntry) {
				return ((ComparableEntry) obj).getEntryName().equals(getEntryName());
			} else {
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			return getEntryName().hashCode();
		}

		/** Compare the two entries by comparing there paths. */
		@Override
		public int compareTo(ComparableEntry other) {
			return getEntryName().compareTo(other.getEntryName());
		}
	}
}
