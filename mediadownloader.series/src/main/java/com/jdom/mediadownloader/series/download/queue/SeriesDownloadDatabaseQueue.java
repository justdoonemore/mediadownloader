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

import org.springframework.stereotype.Service;

import com.jdom.mediadownloader.download.queue.EntityDownloadDatabaseQueue;
import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesDownload;
import com.jdom.mediadownloader.series.services.SeriesDasFactory;
import com.jdom.util.time.TimeUtil;

@Service
public class SeriesDownloadDatabaseQueue extends
		EntityDownloadDatabaseQueue<Series, SeriesDownload> implements
		SeriesDownloadQueueManager {

	private final SeriesDasFactory dasFactory;

	public SeriesDownloadDatabaseQueue(SeriesDasFactory dasFactory) {
		super(dasFactory.getSeriesDAS(), dasFactory.getSeriesDownloadDAS());
		this.dasFactory = dasFactory;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.download.queue.EntityDownloadDatabaseQueue#getDownload(com.jdom.mediadownloader.domain.AbstractEntity)
	 */
	@Override
	protected SeriesDownload getDownload(Series entity) {
		SeriesDownload download = new SeriesDownload(entity,
				entity.getSeason(), entity.getEpisode(),
				TimeUtil.newImmutableDate());
		return download;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.download.queue.EntityDownloadDatabaseQueue#checkForAlreadyDownloading(com.jdom.mediadownloader.domain.EntityDownload)
	 */
	@Override
	protected SeriesDownload checkForAlreadyDownloading(SeriesDownload download) {
		return dasFactory.getSeriesDownloadDAS()
				.getBySeriesNameSeasonAndEpisode(download);
	}
}
