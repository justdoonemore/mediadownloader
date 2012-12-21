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

import org.apache.log4j.Logger;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesEpisodeComparator;
import com.jdom.mediadownloader.services.SeriesDASService;
import com.jdom.util.compare.CompareUtil;

/**
 * @author djohnson
 * 
 */
public class SeriesUpdater implements SeriesDownloadListener {

	private static final Logger LOG = Logger.getLogger(SeriesUpdater.class);

	private static final SeriesEpisodeComparator SERIES_EPISODE_COMPARATOR = new SeriesEpisodeComparator();

	private final SeriesDASService seriesDas;

	public SeriesUpdater(SeriesDASService seriesDas) {
		this.seriesDas = seriesDas;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.series.download.SeriesDownloadListener#downloadComplete(com.jdom.mediadownloader.series.domain.Series)
	 */
	@Override
	public void downloadComplete(Series series) {
		String name = series.getName();

		// Look up the actual series object based on the show for an
		// update
		Series seriesObj = seriesDas.getSeriesByName(name);

		if (seriesObj != null) {

			seriesObj = prepareSeriesUpdate(seriesObj, series);

			if (seriesObj != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Updating Series to " + seriesObj);
				}
				sendSeriesUpdates(seriesObj);
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Download of "
							+ series
							+ " is for an episode before than the currently configured episode.  Not updating...");
				}
			}
		} else {
			LOG.warn("Unable to find a series by name [" + name + "]");
		}

	}

	/**
	 * Prepares the series update to occur.
	 * 
	 * @param seriesObj
	 *            the current db object
	 * @param series
	 *            the downloaded series instance
	 */
	private Series prepareSeriesUpdate(Series seriesObj, Series series) {
		if (SERIES_EPISODE_COMPARATOR.compare(series, seriesObj) > CompareUtil.LESS_THAN) {
			seriesObj.setSeason(series.getSeason());
			seriesObj.setEpisode(series.getEpisode() + 1);
			return seriesObj;
		}

		return null;
	}

	private void sendSeriesUpdates(Series series) {
		seriesDas.updateObject(series);
	}

}
