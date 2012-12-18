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
package com.jdom.services.series.download;

import java.util.Collection;
import java.util.List;

import com.jdom.mediadownloader.domain.Series;
import com.jdom.mediadownloader.services.DasFactory;
import com.jdom.services.series.download.util.SeriesDownloadUtil;
import com.jdom.services.series.util.SeriesLinkFinder;

public final class SeriesDownloadProcessor {

	private final SeriesLinkFinder seriesLinkFinder;

	private final NzbDownloader nzbDownloader;

	private final DasFactory dasFactory;

	public SeriesDownloadProcessor(DasFactory dasFactory,
			SeriesLinkFinder seriesLinkFinder, NzbDownloader nzbDownloader) {
		this.dasFactory = dasFactory;
		this.seriesLinkFinder = seriesLinkFinder;
		this.nzbDownloader = nzbDownloader;
	}

	public void process() {
		SeriesDownloadUtil.purgeExpiredSeries();

		List<Series> entities = dasFactory.getSeriesDAS().getAll();

		Collection<SeriesDownload> downloads = seriesLinkFinder
				.findSeriesDownloads(entities);

		if (!downloads.isEmpty()) {
			nzbDownloader.downloadNzbs(downloads);
		}

		nzbDownloader.processDownloadedItems();
	}
}
