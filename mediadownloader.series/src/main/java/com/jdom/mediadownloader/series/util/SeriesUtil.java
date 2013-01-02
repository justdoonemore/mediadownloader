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
package com.jdom.mediadownloader.series.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jdom.logging.api.LogFactory;
import com.jdom.logging.api.Logger;
import com.jdom.mediadownloader.series.domain.Series;

public final class SeriesUtil {

	private static final Pattern SERIES_REGEX_PATTERN = Pattern
			.compile("(.*)?(S|s)(\\d\\d)(E|e)(\\d\\d).*?");

	private static final Logger LOG = LogFactory.getLogger(SeriesUtil.class);

	private static final int SHOW_GROUP = 1;

	private static final int SEASON_GROUP = 3;

	private static final int EPISODE_GROUP = 5;

	private SeriesUtil() {

	}

	/**
	 * Parses a string into a Series object.
	 * 
	 * @param string
	 *            the string to parse
	 * @return the series object, or null if the string didn't match
	 */
	public static Series parseSeries(String string) {
		Series series = null;

		Matcher m = SERIES_REGEX_PATTERN.matcher(string);

		if (m.find()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("String [" + string + "] is a show");
			}
			String show = m.group(SHOW_GROUP).replaceAll("(\\.|_)", " ").trim();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Found show [" + show + "]");
			}

			int season = Integer.parseInt(m.group(SEASON_GROUP));
			int episode = Integer.parseInt(m.group(EPISODE_GROUP));

			series = new Series(show, season, episode);
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("String [" + string + "] is not a show");
			}
		}

		return series;
	}

}
