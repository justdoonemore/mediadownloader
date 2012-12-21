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
package com.jdom.mediadownloader.series;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jdom.mediadownloader.api.MediaProcessor;
import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesDownload;
import com.jdom.mediadownloader.series.download.NzbDownloader;
import com.jdom.mediadownloader.series.download.queue.SeriesDownloadQueueManager;
import com.jdom.mediadownloader.series.services.SeriesDasFactory;
import com.jdom.mediadownloader.series.util.SeriesLinkFinder;
import com.jdom.util.time.Duration;

public final class SeriesDownloadProcessor implements
		MediaProcessor<Series, SeriesDownload> {

	private static final Duration SLEEP_TIME_BETWEEN_NZB_DOWNLOADS = Duration
			.getDuration("sleep.time.between.nzb.downloads", new Duration(2,
					TimeUnit.SECONDS));

	private static final Duration SERIES_DOWNLOAD_TIME_TO_LIVE_IN_MILLIS = Duration
			.getDuration("series.download.time.to.live", new Duration(3,
					TimeUnit.HOURS));

	private final SeriesLinkFinder seriesLinkFinder;

	private final NzbDownloader nzbDownloader;

	private final SeriesDasFactory dasFactory;

	private final SeriesDownloadQueueManager seriesDownloadQueueManager;

	public SeriesDownloadProcessor(SeriesDasFactory dasFactory,
			SeriesLinkFinder seriesLinkFinder, NzbDownloader nzbDownloader,
			SeriesDownloadQueueManager seriesDownloadQueueManager) {
		this.dasFactory = dasFactory;
		this.seriesLinkFinder = seriesLinkFinder;
		this.nzbDownloader = nzbDownloader;
		this.seriesDownloadQueueManager = seriesDownloadQueueManager;
	}

	@Override
	public Collection<SeriesDownload> findDownloads(
			List<Series> seriesCollection) {
		return seriesLinkFinder.findSeriesDownloads(seriesCollection);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.api.MediaProcessor#processSuccessfulDownloads()
	 */
	@Override
	public List<Series> processSuccessfulDownloads() {
		return nzbDownloader.processDownloadedItems();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.api.MediaProcessor#download(java.util.Collection)
	 */
	@Override
	public void download(SeriesDownload download) {
		nzbDownloader.downloadNzb(download);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.api.MediaProcessor#getEntities()
	 */
	@Override
	public List<Series> getEntities() {
		return dasFactory.getSeriesDAS().getAll();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.api.MediaProcessor#getName()
	 */
	@Override
	public String getName() {
		return "tvshowdownloader";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.api.MediaProcessor#getDownloadQueueManager()
	 */
	@Override
	public SeriesDownloadQueueManager getDownloadQueueManager() {
		return seriesDownloadQueueManager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.api.MediaProcessor#getSleepTimeBetweenDownloads()
	 */
	@Override
	public Duration getSleepTimeBetweenDownloads() {
		return SLEEP_TIME_BETWEEN_NZB_DOWNLOADS;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.api.MediaProcessor#getAllowedTimeForDownloadToLive()
	 */
	@Override
	public Duration getAllowedTimeForDownloadToLive() {
		return SERIES_DOWNLOAD_TIME_TO_LIVE_IN_MILLIS;
	}
}
