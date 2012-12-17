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
 */package com.jdom.services.series.util;

import java.io.File;

import org.junit.Before;

import com.jdom.junit.utils.TestUtil;

public abstract class AbstractSeriesLinkFinderTest<T extends AbstractSeriesLinkFinder> {

	protected T linkFinder;

	protected File workingDirectory;

	protected File linksFile;

	/**
	 * Set up the test.
	 * 
	 * @throws Exception
	 *             on error
	 */
	@Before
	public void setUp() throws Exception {
		linkFinder = getLinkFinder();

		workingDirectory = TestUtil.setupTestClassDir(this.getClass());
	}

	protected abstract T getLinkFinder();
}
