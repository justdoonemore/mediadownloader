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
 */package com.jdom.junit.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

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
		try {
			FileUtils.deleteDirectory(dir);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		// Make the directory
		dir.mkdirs();

		return dir;
	}

}
