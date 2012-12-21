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

import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.jdom.mediadownloader.series.domain.Series;
import com.jdom.mediadownloader.series.domain.SeriesDownload;
import com.jdom.mediadownloader.series.services.SeriesDasFactory;
import com.jdom.mediadownloader.services.UrlDownloadService;

public class SabnzbdNzbDownloader implements NzbDownloader {

	private static final Logger LOG = Logger
			.getLogger(SabnzbdNzbDownloader.class);

	protected final UrlDownloadService urlDownloadService;

	private final List<SeriesDownloadListener> seriesDownloadListeners = new CopyOnWriteArrayList<SeriesDownloadListener>();

	private final DownloadedNzbMover downloadedNzbMover;

	private final NzbAdder nzbAdder;

	/**
	 * Sets up the downloadedNzbsFilter to exclude directories with specific
	 * prefixes. Currently this is used to ignore the directories whose contents
	 * are still being unpacked.
	 */
	public SabnzbdNzbDownloader(final SeriesDasFactory dasFactory,
			final UrlDownloadService urlDownloadService,
			DownloadedNzbMover downloadedNzbMover, NzbAdder nzbAdder) {
		this.urlDownloadService = urlDownloadService;
		this.downloadedNzbMover = downloadedNzbMover;
		this.nzbAdder = nzbAdder;
	}

	@Override
	public void downloadNzb(SeriesDownload download) {
		if (addNzbForDownload(download)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Added %s to the download queue.",
						download));
			}
		} else {
			LOG.warn("Unable to add the NZB to the download queue!");
		}
	}

	/**
	 * Add an NZB for processing
	 * 
	 * @param seriesDownload
	 *            the series download
	 */
	private boolean addNzbForDownload(SeriesDownload seriesDownload) {
		URL url = seriesDownload.getLink();

		if (url == null) {
			return false;
		}

		// Get the contents of the URL
		String contents = urlDownloadService.downloadUrlContents(url
				.toExternalForm());

		return nzbAdder.addNzb(seriesDownload, contents.getBytes());
	}

	@Override
	public List<Series> processDownloadedItems() {
		List<Series> seriesList = downloadedNzbMover.handleRetrievedNzbs();

		notifySeriesDownloadListeners(seriesList);

		return seriesList;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.series.download.NzbDownloader#addSeriesDownloadListener(com.jdom.mediadownloader.series.download.SeriesDownloadListener)
	 */
	@Override
	public SeriesDownloadListener addSeriesDownloadListener(
			SeriesDownloadListener listener) {
		seriesDownloadListeners.add(listener);
		return listener;
	}

	/**
	 * Sends series updates.
	 * 
	 * @param seriesList
	 *            the list of series to update
	 */
	private void notifySeriesDownloadListeners(List<Series> seriesList) {
		for (Series series : seriesList) {
			for (SeriesDownloadListener listener : seriesDownloadListeners) {
				listener.downloadComplete(series);
			}
		}
	}
}
