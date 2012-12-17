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
 */package com.jdom.tvshowdownloader.ejb;

import java.io.File;
import java.util.regex.Pattern;

import com.jdom.services.series.util.SeriesLinkFinder;
import com.jdom.util.email.Email;

public class ConfigurationManager implements ConfigurationManagerService {

	private static final Pattern COMMA_PATTERN = Pattern.compile(",");

	private static final File NZB_DESTINATION_DIRECTORY = new File(
			System.getProperty("nzb.destination.dir"));

	private static final String EMAIL_SERVER = System
			.getProperty("email.server");
	private static final String EMAIL_USERNAME = System
			.getProperty("email.username");
	private static final String EMAIL_PASSWORD = System
			.getProperty("email.password");

	private static final String SERIES_DOWNLOAD_URL = System
			.getProperty("series.download.url");

	private static final File NZB_DOWNLOADED_DIRECTORY = new File(
			System.getProperty("nzb.downloaded.dir"));

	private static final File ARCHIVED_TV_DIRECTORY = new File(
			System.getProperty("archived.tv.directory"));

	private static final File ARCHIVED_MOVIES_DIRECTORY = new File(
			System.getProperty("archived.movies.directory"));

	private static final int SERIES_DOWNLOAD_TIME_TO_LIVE = getIntProperty(System
			.getProperty("series.download.time.to.live"));

	private static final int FILE_PICKUP_LAST_MODIFIED_TIME = getIntProperty(System
			.getProperty("file.pickup.last.modified"));

	private volatile static String[] seriesDownloadTitleExclusions = null;

	private SeriesLinkFinder seriesLinkFinder;

	@Override
	public File getNzbDestinationDirectory() {
		return NZB_DESTINATION_DIRECTORY;
	}

	@Override
	public Email getTemplateEmail() {
		return new Email(EMAIL_SERVER, EMAIL_USERNAME, EMAIL_PASSWORD, null,
				null, null);
	}

	@Override
	public String getSeriesDownloadUrl() {
		return SERIES_DOWNLOAD_URL;
	}

	@Override
	public File getNzbDownloadedDirectory() {
		return NZB_DOWNLOADED_DIRECTORY;
	}

	@Override
	public File getArchivedMoviesDirectory() {
		return ARCHIVED_MOVIES_DIRECTORY;
	}

	@Override
	public File getArchivedTvDirectory() {
		return ARCHIVED_TV_DIRECTORY;
	}

	@Override
	public SeriesLinkFinder getSeriesLinkFinder() {
		return seriesLinkFinder;
	}

	public void setSeriesLinkFinder(SeriesLinkFinder seriesLinkFinder) {
		this.seriesLinkFinder = seriesLinkFinder;
	}

	@Override
	public int getSeriesDownloadTimeToLive() {
		return SERIES_DOWNLOAD_TIME_TO_LIVE;
	}

	@Override
	public int getLastModifiedTime() {
		return FILE_PICKUP_LAST_MODIFIED_TIME;
	}

	@Override
	public String[] getSeriesDownloadTitleExclusions() {
		String[] result = seriesDownloadTitleExclusions;
		if (result == null) {
			synchronized (ConfigurationManager.class) {
				result = seriesDownloadTitleExclusions;
				if (result == null) {
					seriesDownloadTitleExclusions = result = getStringArray(System
							.getProperty("series.download.title.exclusions", null));
				}
			}
		}
		return result;
	}

	/**
	 * Split a comma delimited string into a string array.
	 * 
	 * @param propertyValue
	 *            the property value
	 * @return the split string
	 */
	private String[] getStringArray(String propertyValue) {
		return (propertyValue == null) ? new String[0] : COMMA_PATTERN.split(propertyValue);
	}

	private static int getIntProperty(String value) {
		return Integer.parseInt(value);
	}
}
