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

import java.io.File;

import com.jdom.util.email.Email;

/**
 * This class handles the configuration.
 * 
 * @author djohnson
 * 
 */
public interface ConfigurationManagerService {

	File getNzbDestinationDirectory();

	Email getTemplateEmail();

	String getSeriesDownloadUrl();

	File getNzbDownloadedDirectory();

	File getArchivedTvDirectory();

	File getArchivedMoviesDirectory();

	String[] getSeriesDownloadTitleExclusions();

	/**
	 * Returns the last modified time of a file in minutes for it to be able to
	 * be picked up.
	 * 
	 * @return the last modified time in minutes
	 */
	int getLastModifiedTime();

	/**
	 * Returns the time to allow a series download attempt to live in the queue,
	 * in hours.
	 * 
	 * @return the time to live in the queue for a series download, in hours.
	 */
	int getSeriesDownloadTimeToLive();
}
