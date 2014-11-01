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
package com.jdom.mediadownloader.series.download.filter;

import java.io.File;
import java.io.FileFilter;

public abstract class BaseFilter implements FileFilter {

	protected final FileFilter decorated;

	public BaseFilter(FileFilter fileFilter) {
		decorated = fileFilter;
	}

	public BaseFilter() {
		// Constructor without a decorated version,
		// by default it accepts all
		decorated = new AcceptAll();
	}

	@Override
	public boolean accept(File pathname) {
		// See if decorated filter and our filter will accept this
		// It must pass both filters
		return decorated.accept(pathname) && determineAcceptance(pathname);
	}

	/**
	 * Determines whether or not this filter will accept the path.
	 * 
	 * @param pathname
	 *            the path name
	 * @return true if the filter will accept it
	 */
	protected abstract boolean determineAcceptance(File pathname);

}
