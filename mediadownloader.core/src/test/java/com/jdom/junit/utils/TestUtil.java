/** 
 *  Copyright (C) 2012  Just Do One More
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jdom.junit.utils;

import com.google.common.io.Resources;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;

public final class TestUtil {

	private TestUtil() {
	}

	/**
	 * Setup the directory the test class should use.
	 * 
	 * @param testClass
	 *            The test class to create a directory for
	 * @return The directory created for the test class
	 */
	public static File setupTestClassDir(Class<?> testClass) {
		File dir = new File(System.getProperty("java.io.tmpdir"),
				testClass.getSimpleName());

		// Delete any preexisting version
		FileSystemUtils.deleteRecursively(dir);

		// Make the directory
		dir.mkdirs();

		return dir;
	}

	public static byte[] readFile(Class<?> callingClass, String resourcePath)
			throws IOException {
		return Resources.toByteArray(callingClass
					.getResource(resourcePath));
	}

	public static String readFileToString(Class<?> callingClass,
			String resourcePath) throws IOException {
		return new String(readFile(callingClass, resourcePath));
	}
}
