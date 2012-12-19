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
package com.jdom.mediadownloader.series.download.queue;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesDownload;
import com.jdom.mediadownloader.series.services.SeriesDasFactory;
import com.jdom.mediadownloader.services.SeriesDASService;
import com.jdom.mediadownloader.services.SeriesDownloadDASService;
import com.jdom.util.time.TimeUtil;

/**
 * Manages a series download queue by placing them in a map with the time they
 * were entered.
 * 
 * @author djohnson
 */
@Service
public class SeriesDownloadDatabaseQueue implements SeriesDownloadQueueManager {

	private static final Logger LOG = Logger
			.getLogger(SeriesDownloadDatabaseQueue.class);

	private final SeriesDasFactory dasFactory;

	public SeriesDownloadDatabaseQueue(SeriesDasFactory dasFactory) {
		this.dasFactory = dasFactory;
	}

	@Override
	public boolean addSeries(Series series) {
		SeriesDownload download = new SeriesDownload(series,
				series.getSeason(), series.getEpisode(),
				TimeUtil.newImmutableDate());

		SeriesDASService seriesDas = dasFactory.getSeriesDAS();
		Series seriesEntity = seriesDas.getSeriesByName(series.getName());
		if (seriesEntity != null) {
			download.setEntity(seriesEntity);

			SeriesDownloadDASService das = dasFactory.getSeriesDownloadDAS();

			return das.addObject(download);
		} else {
			return false;
		}
	}

	@Override
	public boolean containsSeries(Series series) {
		SeriesDownload download = new SeriesDownload(series,
				series.getSeason(), series.getEpisode(), TimeUtil.newDate());

		SeriesDownloadDASService das = dasFactory.getSeriesDownloadDAS();
		SeriesDownload entity = das.getBySeriesNameSeasonAndEpisode(download);

		return entity != null;
	}

	@Override
	public boolean removeSeries(Series series) {
		SeriesDownload download = new SeriesDownload(series,
				series.getSeason(), series.getEpisode(),
				TimeUtil.newImmutableDate());

		SeriesDownloadDASService das = dasFactory.getSeriesDownloadDAS();
		SeriesDownload entity = das.getBySeriesNameSeasonAndEpisode(download);
		if (entity != null) {
			return das.deleteObject(entity);
		} else {
			LOG.warn("Unable to find series download " + download);
		}

		return false;
	}

	@Override
	public void purgeExpiredSeries(long olderThanMillis) {
		long currentTime = TimeUtil.currentTimeMillis();

		SeriesDownloadDASService das = dasFactory.getSeriesDownloadDAS();
		List<SeriesDownload> all = das.getAll();

		for (SeriesDownload entity : all) {
			long timePutInQueue = entity.getTime().getTime();

			long timeSinceAdded = currentTime - timePutInQueue;
			if (timeSinceAdded > olderThanMillis) {
				LOG.info("Purging " + entity + ", ["
						+ (timeSinceAdded - olderThanMillis)
						+ "] ms past purge time");
				if (!das.deleteObject(entity)) {
					LOG.error("Unable to delete series download " + entity);
				}
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Not purging " + entity + " still has ["
							+ (olderThanMillis - timeSinceAdded) + "] ms");
				}
			}
		}
	}
}
