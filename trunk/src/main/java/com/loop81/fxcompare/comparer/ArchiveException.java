package com.loop81.fxcompare.comparer;

import java.io.File;

public class ArchiveException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private File file;

	private ExceptionType type;

	public ArchiveException(ExceptionType type, String message) {
		super(message);
		this.type = type;
	}
	
	public ArchiveException(ExceptionType type, File file, String message) {
		super(message);
		this.type = type;
		this.file = file;
	}
	
	public ArchiveException(ExceptionType type, File file, Throwable t) {
		super(t);
		this.type = type;
		this.file = file;
	}

	public File getFile() {
		return file;
	}
	
	public ExceptionType getType() {
		return type;
	}
	
	public static enum ExceptionType {
		READ,
		IO,
		NOT_FOUND,
		NULL
	}
}
