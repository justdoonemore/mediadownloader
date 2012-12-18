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
package com.jdom.mediadownloader.services;

/**
 * @author djohnson
 * 
 */
public class InjectedDasFactory implements DasFactory {

	private final SeriesDASService seriesDas;
	private final UserDASService userDas;
	private final SeriesNotificationDASService seriesNotificationDas;
	private final SeriesDownloadDASService seriesDownloadDas;

	public InjectedDasFactory(SeriesDASService seriesDas, UserDASService userDas,
			SeriesNotificationDASService seriesNotificationDas,
			SeriesDownloadDASService seriesDownloadDas) {
		this.seriesDas = seriesDas;
		this.userDas = userDas;
		this.seriesNotificationDas = seriesNotificationDas;
		this.seriesDownloadDas = seriesDownloadDas;
	}

	@Override
	public SeriesDASService getSeriesDAS() {
		return seriesDas;
	}

	@Override
	public UserDASService getUserDAS() {
		return userDas;
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
