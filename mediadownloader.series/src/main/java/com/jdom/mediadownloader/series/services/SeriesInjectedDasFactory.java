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
package com.jdom.mediadownloader.series.services;

import com.jdom.mediadownloader.services.DasFactory;
import com.jdom.mediadownloader.services.SeriesDASService;
import com.jdom.mediadownloader.services.SeriesDownloadDASService;
import com.jdom.mediadownloader.services.SeriesNotificationDASService;
import com.jdom.mediadownloader.services.UserDASService;

/**
 * @author djohnson
 * 
 */
public class SeriesInjectedDasFactory implements SeriesDasFactory {
	private final DasFactory dasFactory;
	private final SeriesDASService seriesDas;
	private final SeriesNotificationDASService seriesNotificationDas;
	private final SeriesDownloadDASService seriesDownloadDas;

	public SeriesInjectedDasFactory(DasFactory dasFactory,
			SeriesDASService seriesDas,
			SeriesNotificationDASService seriesNotificationDas,
			SeriesDownloadDASService seriesDownloadDas) {
		this.dasFactory = dasFactory;
		this.seriesDas = seriesDas;
		this.seriesNotificationDas = seriesNotificationDas;
		this.seriesDownloadDas = seriesDownloadDas;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.jdom.mediadownloader.services.DasFactory#getUserDAS()
	 */
	@Override
	public UserDASService getUserDAS() {
		return dasFactory.getUserDAS();
	}

	@Override
	public SeriesDASService getSeriesDAS() {
		return seriesDas;
	}

	@Override
	public SeriesNotificationDASService getSeriesNotificationDAS() {
		return seriesNotificationDas;
	}

	@Override
	public SeriesDownloadDASService getSeriesDownloadDAS() {
		return seriesDownloadDas;
	}
}
