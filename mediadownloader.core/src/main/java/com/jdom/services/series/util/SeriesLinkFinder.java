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
package com.jdom.services.series.util;

import java.util.Collection;
import java.util.List;

import com.jdom.mediadownloader.domain.Series;
import com.jdom.services.series.download.SeriesDownload;

public interface SeriesLinkFinder {

	/**
	 * Downloads the available links to check for a series.
	 * 
	 * @param series
	 *            the series to check for links of
	 * @return the possible links
	 */
	Collection<String> getPossibleLinksForSeries(Series series);

	/**
	 * @param entities
	 * @return
	 */
	Collection<SeriesDownload> findSeriesDownloads(List<Series> entities);
}