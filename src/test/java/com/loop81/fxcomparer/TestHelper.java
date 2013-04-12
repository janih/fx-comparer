package com.loop81.fxcomparer;

import java.io.File;
import java.net.URISyntaxException;

public class TestHelper {

	public static File getFile(Class<?> clazz, String fileName) {
		try {
			return new File(clazz.getResource(fileName).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
