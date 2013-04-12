package com.loop81.fxcompare.utils;

/*
 * Copyright (c) 2013 http://www.loop81.com
 *
 * See the file license.txt for copying permission.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Simple provider which reads information about the current version for the application. The version is loaded
 * from the "version.properties" which is filled with information using a pre-package step in the Maven build.
 * 
 * @author Allitico
 */
public class VersionProvider {
	private final static String PROPERTIES_SOURCE = "/version.properties";
	private final static String PROPERTY_BUILD_NAME = "build.name";
	private final static String PROPERTY_BUILD_VERSION = "build.version";
	private final static String PROPERTY_BUILD_DATE = "build.date";
	
	private String name = "N/A";

	private String version = "N/A";

	private String buildDate = "N/A";
	
	public VersionProvider() {
		Properties properties = new Properties();
		try(InputStream in = getClass().getResourceAsStream(PROPERTIES_SOURCE)) {
			properties.load(in);
			name = properties.getProperty(PROPERTY_BUILD_NAME);
			version = properties.getProperty(PROPERTY_BUILD_VERSION);
			buildDate = properties.getProperty(PROPERTY_BUILD_DATE);
		} catch (IOException e) {
			// The version file should always be there.
		}
	}

	public String getName() {
		return name;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getBuildDate() {
		return buildDate;
	}
}
