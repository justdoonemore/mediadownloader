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

import com.jdom.mediadownloader.api.MediaProcessor;
import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesDownload;
import com.jdom.mediadownloader.series.download.NzbDownloader;
import com.jdom.mediadownloader.series.download.util.SeriesDownloadUtil;
import com.jdom.mediadownloader.series.services.SeriesDasFactory;
import com.jdom.mediadownloader.series.util.SeriesLinkFinder;

public final class SeriesDownloadProcessor implements
		MediaProcessor<Series, SeriesDownload> {

	private final SeriesLinkFinder seriesLinkFinder;

	private final NzbDownloader nzbDownloader;

	private final SeriesDasFactory dasFactory;

	public SeriesDownloadProcessor(SeriesDasFactory dasFactory,
			SeriesLinkFinder seriesLinkFinder, NzbDownloader nzbDownloader) {
		this.dasFactory = dasFactory;
		this.seriesLinkFinder = seriesLinkFinder;
		this.nzbDownloader = nzbDownloader;
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
	public void processSuccessfulDownloads() {
		nzbDownloader.processDownloadedItems();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.api.MediaProcessor#purgeFailedDownloads()
	 */
	@Override
	public void purgeFailedDownloads() {
		SeriesDownloadUtil.purgeExpiredSeries();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.api.MediaProcessor#download(java.util.Collection)
	 */
	@Override
	public void download(Collection<SeriesDownload> downloads) {
		nzbDownloader.downloadNzbs(downloads);
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
}
