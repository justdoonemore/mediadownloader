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

import com.jdom.mediadownloader.services.Emailer;
import com.jdom.util.time.Duration;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Holds the String versions of configured properties.
 * 
 * @author djohnson
 */
public final class SeriesConfiguration {

	private SeriesConfiguration() {

	}

	private static final Pattern COMMA_PATTERN = Pattern.compile(",");

	/**
	 * Split a comma delimited string into a string array.
	 * 
	 * @param propertyValue
	 *            the property value
	 * @return the split string
	 */
	private static String[] getStringArray(String propertyValue) {
		return (propertyValue == null) ? new String[0] : COMMA_PATTERN
				.split(propertyValue);
	}

	public static final File NZB_QUEUE_DIRECTORY = new File(
			System.getProperty("nzb.destination.dir"));

	public static final File NZB_DOWNLOADED_DIRECTORY = new File(
			System.getProperty("nzb.downloaded.dir"));

	public static final File ARCHIVED_TV_DIRECTORY = new File(
			System.getProperty("archived.tv.directory"));

	public static final File ARCHIVED_MOVIES_DIRECTORY = new File(
			System.getProperty("archived.movies.directory"));

	/**
	 * The duration at which if the file last modified time is within, it won't
	 * be picked up.
	 */
	public static final Duration TIME_AGO_LAST_MODIFIED = Duration.getDuration(
			"file.pickup.last.modified", new Duration(2, TimeUnit.MINUTES));

	public static final String SERIES_SEARCH_URL = System
			.getProperty("series.download.url");

	private static final String EMAIL_SERVER = System
			.getProperty("email.server");
	private static final String EMAIL_USERNAME = System
			.getProperty("email.username");
	private static final String EMAIL_PASSWORD = System
			.getProperty("email.password");
	public static final Emailer.Email TEMPLATE_EMAIL = new Emailer.Email(EMAIL_SERVER,
			EMAIL_USERNAME, EMAIL_PASSWORD, null, null, null);

	public static final String[] SERIES_DOWNLOAD_TITLE_EXCLUSIONS;
	static {
		SERIES_DOWNLOAD_TITLE_EXCLUSIONS = SeriesConfiguration
				.getStringArray(System.getProperty(
						"series.download.title.exclusions", null));
	}
}
