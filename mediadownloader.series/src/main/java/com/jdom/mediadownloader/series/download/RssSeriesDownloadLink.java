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
package com.jdom.mediadownloader.series.download;

import java.util.Random;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesDownload;
import com.jdom.mediadownloader.series.util.SeriesUtil;
import com.jdom.util.time.TimeUtil;

public class RssSeriesDownloadLink implements SeriesDownloadLink {

	private static final String NZB_FILE_EXTENSION = ".nzb";

	private final Series targetSeries;

	private final RssLink rssLink;

	private final String[] titleExclusions;

	private String nzbTitle;

	public RssSeriesDownloadLink(Series series, String link,
			String[] titleExclusions) {
		this(series, new RssLink(link), titleExclusions);
	}

	public RssSeriesDownloadLink(Series series, RssLink link,
			String[] titleExclusions) {
		this.targetSeries = series;
		this.rssLink = link;
		this.titleExclusions = titleExclusions;
	}

	@Override
	public SeriesDownload createSeriesDownload() {
		// Parse it for a series
		Series parsedSeries = SeriesUtil.parseSeries(rssLink.getDisplayName());

		int parsedSeriesSeason = parsedSeries.getSeason();
		int parsedSeriesEpisode = parsedSeries.getEpisode();

		// Create a marker series instance to track the download
		Series seriesDownloadMarker = targetSeries.clone();
		seriesDownloadMarker.setEpisode(parsedSeriesEpisode);
		seriesDownloadMarker.setSeason(parsedSeriesSeason);

		// Create a series download object
		SeriesDownload seriesToDownload = new SeriesDownload(
				seriesDownloadMarker, nzbTitle, parsedSeriesSeason,
				parsedSeriesEpisode, TimeUtil.newImmutableDate(),
				rssLink.getUrl());

		return seriesToDownload;
	}

	@Override
	public boolean matchesSeriesDownloadCriteria() {
		String displayName = rssLink.getDisplayName();
		this.nzbTitle = determineNzbTitle();

		// Skip any links without a title
		if (displayName == null || nzbTitle == null) {
			return false;
		}

		// Parse it for a series
		Series parsedSeries = SeriesUtil.parseSeries(displayName);

		// If this doesn't match a series pattern skip it,
		// or if the series name is different
		if (parsedSeries == null
				|| !Series.normalizedNameEquals(targetSeries.getName(),
						parsedSeries.getName())) {
			return false;
		}

		int targetSeason = targetSeries.getSeason();
		int targetEpisode = targetSeries.getEpisode();
		int parsedSeriesSeason = parsedSeries.getSeason();
		int parsedSeriesEpisode = parsedSeries.getEpisode();

		// Make sure the season / episode is ok
		// We will skip if the season is lower, or if season is
		// equal and episode is equal or lower
		if ((parsedSeriesSeason < targetSeason)
				|| (parsedSeriesEpisode < targetEpisode && parsedSeriesSeason == targetSeason)) {
			return false;
		}

		return true;
	}

	/**
	 * Determine the title for this NZB. It will also check for excluded strings
	 * in the title, and returns null if there is no valid title for this link.
	 * 
	 * @param series
	 *            The series we are working on
	 * @param link
	 *            The link we are working on
	 * @param titleExclusions
	 *            the title exclusions
	 * @return The found title if found on the link, a generated title if not
	 *         found on the link, or null if this title matched an exclusion
	 *         string
	 */
	private String determineNzbTitle() {
		// Determine whether there is a title on the link
		String title = rssLink.getDisplayName();

		// If there is no title
		if (title == null) {
			// Default title if we can't find it on the link
			title = targetSeries.getName() + "_S" + targetSeries.getSeason()
					+ "E" + targetSeries.getEpisode() + "_random_"
					+ new Random().nextLong();
		}

		// Verify the exclusions strings are not in the title
		for (String exclusion : titleExclusions) {

			// If it contains an excluded string, it should be skipped
			if (title.contains(exclusion)) {
				// By returning null we signal this NZB should be skipped
				return null;
			}
		}

		// Add the nzb file extension if it doesn't exist already
		if (!title.endsWith(NZB_FILE_EXTENSION)) {
			title = title + NZB_FILE_EXTENSION;
		}

		return title;
	}

	void setRssLink(String link) {
	}

}
