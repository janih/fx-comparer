package com.loop81.fxcompare.utils;

/*
 * Copyright (c) 2013 http://www.loop81.com
 *
 * See the file license.txt for copying permission.
 */

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * A message bundle is a wrapper around a {@link ResourceBundle} which adds support for resolving text strings with 
 * parameters. Before any of the getString() methods can be invoked a call to 
 * {@link MessageBundle#initialize(ResourceBundle)} needs to be done otherwise the {@link ResourceBundle} will
 * be null.
 * 
 * @author Allitico
 */
public class MessageBundle {
	
	private static ResourceBundle resources;

	public static String getString(String key) {
		return resources.getString(key);
	}
	
	/**
	 * Get the string specified by the given key and resolve the parameters in the string. A valid string has the 
	 * format "mesage.key=Message {0}". Where {0} is the index which will be replaced by the first parameter. 
	 * 
	 * @param key The message key to lookup in the {@link ResourceBundle}.
	 * @param parameters The parameters which will be used to replace the index tokens in the string.
	 * @return A formatted string with the parameters being replaced by the given values.
	 */
	public static String getString(String key, Object... parameters) {
		return MessageFormat.format(getString(key), parameters);
	}
	
	public static void initialize(ResourceBundle resources) {
		MessageBundle.resources = resources;
	}
	
	public static ResourceBundle getResourceBundle() {
		return resources;
	}
}
