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
 */package com.jdom.services.series.download.util;

import com.jdom.services.util.ServiceLocator;
import com.jdom.tvshowdownloader.domain.Series;
import com.jdom.tvshowdownloader.ejb.ConfigurationManagerService;
import com.jdom.util.time.TimeConstants;

public final class SeriesDownloadUtil {

	/**
	 * Adds a series to the queue.
	 * 
	 * @param series
	 *            the series
	 * @return true if the series was added
	 */
	public static boolean addSeries(Series series) {
		return ServiceLocator.getSeriesDownloadQueueManager().addSeries(series);
	}

	/**
	 * Removes the series from the queue.
	 * 
	 * @return true if the series was removed
	 */
	public static boolean removeSeries(Series series) {
		return ServiceLocator.getSeriesDownloadQueueManager().removeSeries(
				series);
	}

	/**
	 * Determine if the series is in the download queue.
	 * 
	 * @param series
	 *            the series
	 * @return true if the series is in the download queue
	 */
	public static boolean containsSeries(Series series) {
		return ServiceLocator.getSeriesDownloadQueueManager().containsSeries(
				series);
	}

	/**
	 * Removes all series that have passed the allowed purge interval.
	 * 
	 * @param olderThanMillis
	 *            the milliseconds of life allowed for a series download
	 */
	public static void purgeExpiredSeries() {
		ConfigurationManagerService configurationManager = ServiceLocator
				.getConfigurationManager();

		int seriesDownloadTimeToLiveInHours = configurationManager
				.getSeriesDownloadTimeToLive();

		long millisTime = seriesDownloadTimeToLiveInHours
				* TimeConstants.MILLIS_PER_HOUR;

		ServiceLocator.getSeriesDownloadQueueManager().purgeExpiredSeries(
				millisTime);
	}
}
