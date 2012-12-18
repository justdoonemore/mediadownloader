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
 */package com.jdom.services.series.download;

import java.util.Collection;
import java.util.List;

import com.jdom.mediadownloader.domain.Series;
import com.jdom.services.series.actions.DownloadNzb;
import com.jdom.services.series.actions.FindLinksForSeries;
import com.jdom.services.series.download.util.SeriesDownloadUtil;
import com.jdom.services.util.ServiceLocator;

public final class SeriesDownloadProcessor {

	private static final FindLinksForSeries FIND_LINKS_FOR_SERIES = new com.jdom.services.series.actions.FindLinksForSeries();

	private final DownloadNzb DOWNLOAD_NZB;

	public SeriesDownloadProcessor(DownloadNzb downloadNzb) {
		this.DOWNLOAD_NZB = downloadNzb;
	}

	public void process() {
		SeriesDownloadUtil.purgeExpiredSeries();

		List<Series> entities = ServiceLocator.getSeriesDAS().getAll();

		Collection<SeriesDownload> downloads = FIND_LINKS_FOR_SERIES
				.processMessage(entities);

		if (!downloads.isEmpty()) {
			DOWNLOAD_NZB.downloadNzbs(downloads);
		}
	}
}
