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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.apache.log4j.Logger;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.services.ConfigurationManagerService;
import com.jdom.mediadownloader.services.UrlDownloadService;
import com.jdom.util.regex.RegexMatch;
import com.jdom.util.regex.RegexUtil;
import com.jdom.util.string.StringUtil;

/**
 * Provides common functionality between all link finders.
 * 
 * @author djohnson
 */
public abstract class AbstractSeriesLinkFinder implements SeriesLinkFinder {

	private static final Logger LOG = Logger
			.getLogger(AbstractSeriesLinkFinder.class);

	protected static String LINK_REGEX = "<a href=((\"|')?(.*?)(\"|'))?.*?</a>";

	protected final ConfigurationManagerService configurationManagerService;

	protected final UrlDownloadService urlDownloadService;

	public AbstractSeriesLinkFinder(
			ConfigurationManagerService configurationManager,
			UrlDownloadService urlDownloadService) {
		this.configurationManagerService = configurationManager;
		this.urlDownloadService = urlDownloadService;
	}

	/**
	 * Returns all possible links for a series from its configured download
	 * page.
	 */
	@Override
	public Collection<String> getPossibleLinksForSeries(Series series) {

		Collection<RegexMatch> linksMatches = getLinkMatches(series);

		Collection<String> possibleLinks = new HashSet<String>();

		for (RegexMatch regexMatch : linksMatches) {
			possibleLinks.add(regexMatch.getMatch());
		}

		return possibleLinks;
	}

	/**
	 * Gets all of the lines from the configured series download check page that
	 * contain links.
	 * 
	 * @param series
	 *            the series to check for
	 * @return the collection of regex matches
	 */
	protected Collection<RegexMatch> getLinkMatches(Series series) {
		Collection<RegexMatch> matches = Collections.emptyList();

		String seriesUrl = getSeriesSearchPage(series);

		if (StringUtil.isEmpty(seriesUrl)) {
			LOG.warn("Unable to find a specific url for series " + series
					+ "!  Returning empty list of matches...");
		} else {
			String pageHtml = urlDownloadService.downloadUrlContents(seriesUrl);

			matches = RegexUtil.findRegexMatches(pageHtml, LINK_REGEX);
		}

		return matches;
	}

	/**
	 * Gets the search for a series.
	 * 
	 * @param series
	 *            the series to get the search page for
	 * @return the url to search
	 */
	protected abstract String getSeriesSearchPage(Series series);
}
