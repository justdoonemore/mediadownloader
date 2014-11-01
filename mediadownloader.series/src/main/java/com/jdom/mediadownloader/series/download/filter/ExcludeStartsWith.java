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
import java.util.Collection;

public class ExcludeStartsWith extends BaseFilter {

	protected Collection<String> exclusions;

	public ExcludeStartsWith(Collection<String> exclusions) {
		super();

		this.exclusions = exclusions;
	}

	public ExcludeStartsWith(FileFilter fileFilter,
			Collection<String> exclusions) {
		super(fileFilter);

		this.exclusions = exclusions;
	}

	@Override
	protected boolean determineAcceptance(File pathname) {

		// If the name starts with an exclusion, return false
		for (String exclusion : exclusions) {
			if (pathname.getName().startsWith(exclusion)) {
				return false;
			}
		}

		return true;
	}

}
