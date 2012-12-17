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
 */package com.jdom.services.series.actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.jdom.services.series.download.SeriesDownload;
import com.jdom.services.series.download.SeriesDownloadFinder;
import com.jdom.services.series.util.SeriesLinkFinder;
import com.jdom.services.util.ServiceLocator;
import com.jdom.tvshowdownloader.domain.Series;
import com.jdom.tvshowdownloader.ejb.ConfigurationManagerService;

public class FindLinksForSeries {

	private static final Logger LOG = Logger
			.getLogger(FindLinksForSeries.class);

	public Collection<SeriesDownload> processMessage(List<Series> entities) {

		ConfigurationManagerService configurationManager = ServiceLocator
				.getConfigurationManager();

		String[] titleExclusions = configurationManager
				.getSeriesDownloadTitleExclusions();

		SeriesLinkFinder seriesLinkFinder = configurationManager
				.getSeriesLinkFinder();

		Collection<SeriesDownload> downloads = findSeriesDownloads(entities,
				seriesLinkFinder, titleExclusions);

		return downloads;
	}

	/**
	 * This method finds any available downloads for the specified series.
	 * 
	 * @param listOfSeries
	 *            the list of series to look for links of
	 * @param seriesLinkFinder
	 *            the series link finder strategy
	 * @param titleExclusions
	 * @return the collection of series downloads
	 */
	protected Collection<SeriesDownload> findSeriesDownloads(
			List<Series> listOfSeries, SeriesLinkFinder seriesLinkFinder,
			String[] titleExclusions) {
		Collection<SeriesDownload> downloads = new HashSet<SeriesDownload>();

		// Loop over each series and try to find any downloads
		for (Series series : listOfSeries) {
			SeriesDownloadFinder seriesDownloadFinder = new SeriesDownloadFinder(
					series, seriesLinkFinder, titleExclusions);

			downloads.addAll(seriesDownloadFinder.findDownloads());
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("Found [" + downloads.size()
					+ "] nzbs ready for download.");
		}

		return downloads;
	}

}
