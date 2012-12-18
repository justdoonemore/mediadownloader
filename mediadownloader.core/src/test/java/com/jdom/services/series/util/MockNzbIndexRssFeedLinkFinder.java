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
package com.jdom.services.series.util;

import com.jdom.mediadownloader.domain.Series;
import com.jdom.mediadownloader.services.ConfigurationManagerService;
import com.jdom.mediadownloader.services.UrlDownloadService;

public class MockNzbIndexRssFeedLinkFinder extends NzbIndexRssFeedLinkFinder {

	public MockNzbIndexRssFeedLinkFinder(
			ConfigurationManagerService configurationManagerService,
			UrlDownloadService downloadService) {
		super(configurationManagerService, downloadService);
	}

	private String searchUrl;

	/**
	 * Override the series search page to return searchUrl. setSeriesSearchPage
	 * MUST be called before this method is called.
	 */
	@Override
	protected String getSeriesSearchPage(Series series) {
		return searchUrl;
	}

	/**
	 * Method to allow setting the search url.
	 * 
	 * @param string
	 *            the url
	 */
	public void setSeriesSearchPage(String string) {
		searchUrl = string;
	}
}
